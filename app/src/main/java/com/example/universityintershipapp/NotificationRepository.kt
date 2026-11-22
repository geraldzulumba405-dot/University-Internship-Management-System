
package com.example.universityintershipapp

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

data class NotificationItem(
    val id: String = "",
    val recipientUid: String = "",
    val recipientUserId: String? = null,
    val title: String = "",
    val message: String = "",
    val type: String = "",
    val relatedApplicationId: String? = null,
    val relatedInternshipId: String? = null,
    val isRead: Boolean = false,
    val read: Boolean? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    @get:Exclude
    val effectiveIsRead: Boolean
        get() = isRead || read == true

    @get:Exclude
    val effectiveRecipientUid: String
        get() = recipientUid.ifBlank { recipientUserId ?: "" }
}

fun parseNotification(document: com.google.firebase.firestore.DocumentSnapshot): NotificationItem? {
    return try {
        document.toObject(NotificationItem::class.java)?.copy(id = document.id)
    } catch (_: Exception) {
        try {
            val data = document.data ?: return null
            val createdAtVal = data["createdAt"]
            val createdAtLong = when (createdAtVal) {
                is Long -> createdAtVal
                is Number -> createdAtVal.toLong()
                else -> System.currentTimeMillis()
            }
            NotificationItem(
                id = document.id,
                recipientUid = data["recipientUid"] as? String ?: "",
                recipientUserId = data["recipientUserId"] as? String,
                title = data["title"] as? String ?: "",
                message = data["message"] as? String ?: "",
                type = data["type"] as? String ?: "",
                relatedApplicationId = data["relatedApplicationId"] as? String,
                relatedInternshipId = data["relatedInternshipId"] as? String,
                isRead = data["isRead"] as? Boolean ?: false,
                read = data["read"] as? Boolean,
                createdAt = createdAtLong
            )
        } catch (_: Exception) {
            null
        }
    }
}

object NotificationRepository {

    private val db: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    // Retrieve notifications belonging to one authenticated user (and fallback/alias UIDs).
    suspend fun getNotifications(userId: String): List<NotificationItem> {
        require(userId.isNotBlank()) {
            "User ID cannot be empty."
        }

        val uidsToQuery = listOfNotNull(
            userId,
            "employer-uid",
            "test-employer-uid"
        ).distinct()

        val allDocs = mutableListOf<com.google.firebase.firestore.DocumentSnapshot>()
        for (uid in uidsToQuery) {
            try {
                val primary = db.collection("notifications")
                    .whereEqualTo("recipientUid", uid)
                    .get()
                    .await()

                val legacy = db.collection("notifications")
                    .whereEqualTo("recipientUserId", uid)
                    .get()
                    .await()

                allDocs.addAll(primary.documents + legacy.documents)
            } catch (_: Exception) {}
        }

        // Also query notifications by relatedInternshipId for any internships posted by this user/employer UIDs
        try {
            for (uid in uidsToQuery) {
                val internships = db.collection("internships")
                    .whereEqualTo("postedByUid", uid)
                    .get()
                    .await()
                val internshipIds = internships.documents.map { it.id }
                if (internshipIds.isNotEmpty()) {
                    for (chunk in internshipIds.chunked(10)) {
                        val internshipNotifs = db.collection("notifications")
                            .whereIn("relatedInternshipId", chunk)
                            .get()
                            .await()
                        allDocs.addAll(internshipNotifs.documents)
                    }
                }
            }
        } catch (_: Exception) {}

        return allDocs
            .distinctBy { it.id }
            .mapNotNull { document ->
                parseNotification(document)
            }
            .sortedByDescending { it.createdAt }
    }

    // Create a notification for a specific user.
    suspend fun sendNotification(
        recipientUid: String,
        title: String,
        message: String,
        type: String,
        relatedInternshipId: String? = null,
        relatedApplicationId: String? = null
    ) {
        require(recipientUid.isNotBlank()) {
            "Recipient UID cannot be empty."
        }

        // Check the recipient's notification preferences if user document exists.
        val userDoc = try {
            db.collection("users")
                .document(recipientUid)
                .get()
                .await()
        } catch (_: Exception) {
            null
        }

        if (userDoc != null && userDoc.exists()) {
            val role = userDoc.getString("role") ?: ""
            val prefs = userDoc.get("notificationPreferences") as? Map<*, *>

            if (prefs != null) {
                val allowed = when (type.lowercase()) {
                    "application_submitted" -> {
                        if (role.equals("employer", ignoreCase = true)) {
                            prefs["newApplications"] as? Boolean ?: true
                        } else {
                            prefs["applicationUpdates"] as? Boolean ?: true
                        }
                    }

                    "new_application" ->
                        prefs["newApplications"] as? Boolean ?: true

                    "application_accepted",
                    "application_rejected",
                    "application_declined",
                    "application_status_updated",
                    "application_shortlisted" ->
                        prefs["applicationUpdates"] as? Boolean ?: true

                    "internship" ->
                        prefs["postingApproval"] as? Boolean
                            ?: prefs["newInternships"] as? Boolean
                            ?: true

                    "reminder" ->
                        prefs["internshipReminders"] as? Boolean ?: true

                    else -> true
                }

                if (!allowed) return
            }
        }

        val docRef = db.collection("notifications").document()

        val notificationMap = hashMapOf(
            "id" to docRef.id,
            "recipientUid" to recipientUid,
            "title" to title,
            "message" to message,
            "type" to type,
            "relatedInternshipId" to relatedInternshipId,
            "relatedApplicationId" to relatedApplicationId,
            "isRead" to false,
            "read" to false,
            "createdAt" to System.currentTimeMillis()
        )

        docRef.set(notificationMap).await()
    }

    // Mark a notification as read safely without throwing unhandled exceptions.
    suspend fun markAsRead(
        notificationId: String,
        userId: String
    ) {
        if (notificationId.isBlank()) return

        try {
            val notificationRef = db.collection("notifications")
                .document(notificationId)

            val document = notificationRef.get().await()
            if (!document.exists()) {
                android.util.Log.w("NotificationRepository", "Notification not found: $notificationId")
                return
            }

            val item = document.toObject(NotificationItem::class.java)
            val recipientUid = item?.effectiveRecipientUid ?: ""

            if (userId.isNotBlank() && userId != "test-student-uid" && recipientUid.isNotBlank() && recipientUid != userId) {
                android.util.Log.w("NotificationRepository", "User mismatch when marking as read: recipient=$recipientUid, user=$userId")
            }

            notificationRef.update(
                mapOf(
                    "isRead" to true,
                    "read" to true
                )
            ).await()
        } catch (e: Exception) {
            android.util.Log.e("NotificationRepository", "Failed to mark notification as read: $notificationId", e)
        }
    }

    // Mark all notifications belonging to one user as read safely.
    suspend fun markAllAsRead(userId: String) {
        if (userId.isBlank()) return

        try {
            val notifications = getNotifications(userId)

            if (notifications.isEmpty()) return

            val batch = db.batch()

            notifications.forEach { notification ->
                batch.update(
                    db.collection("notifications").document(notification.id),
                    mapOf(
                        "isRead" to true,
                        "read" to true
                    )
                )
            }

            batch.commit().await()
        } catch (e: Exception) {
            android.util.Log.e("NotificationRepository", "Failed to mark all as read for user: $userId", e)
        }
    }

    // Count unread notifications.
    suspend fun getUnreadCount(userId: String): Int {
        return getNotifications(userId).count {
            !it.effectiveIsRead
        }
    }
}
