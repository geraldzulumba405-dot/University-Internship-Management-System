package com.example.universityintershipapp

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

data class NotificationItem(
    val id: String = "",
    val recipientUid: String = "",
    val recipientUserId: String? = null,
    val title: String = "",
    val message: String = "",
    val type: String = "", // application_submitted, new_application, application_status_updated, application_shortlisted, application_accepted, application_rejected
    val relatedApplicationId: String? = null,
    val relatedInternshipId: String? = null,
    val isRead: Boolean = false,
    val read: Boolean? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val effectiveIsRead: Boolean get() = isRead || (read == true)
    val effectiveRecipientUid: String get() = recipientUid.ifBlank { recipientUserId ?: "" }
}

object NotificationRepository {
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    suspend fun getNotifications(userId: String): List<NotificationItem> {
        return try {
            val actualUserId = if (userId.isBlank()) "test-student-uid" else userId
            val ids = listOf(actualUserId, "test-student-uid").distinct()
            val allDocs = mutableListOf<com.google.firebase.firestore.DocumentSnapshot>()
            for (id in ids) {
                val s1 = db.collection("notifications").whereEqualTo("recipientUid", id).get().await()
                val s2 = db.collection("notifications").whereEqualTo("recipientUserId", id).get().await()
                allDocs.addAll(s1.documents + s2.documents)
            }

            allDocs.distinctBy { it.id }.mapNotNull { doc ->
                val item = doc.toObject(NotificationItem::class.java)
                item?.copy(id = doc.id)
            }.sortedByDescending { it.createdAt }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun sendNotification(
        recipientUid: String = "",
        recipientUserId: String? = null,
        title: String,
        message: String,
        type: String,
        relatedInternshipId: String? = null,
        relatedApplicationId: String? = null
    ) {
        try {
            val actualUid = recipientUid.ifBlank { recipientUserId?.ifBlank { "test-student-uid" } ?: "test-student-uid" }
            if (actualUid.isBlank()) return

            val userDoc = db.collection("users").document(actualUid).get().await()
            if (userDoc.exists()) {
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
                        "new_application" -> prefs["newApplications"] as? Boolean ?: true
                        "application_accepted", "application_rejected", "application_status_updated", "application_shortlisted" -> prefs["applicationUpdates"] as? Boolean ?: true
                        "internship" -> prefs["postingApproval"] as? Boolean ?: prefs["newInternships"] as? Boolean ?: true
                        "reminder" -> prefs["internshipReminders"] as? Boolean ?: true
                        else -> true
                    }
                    if (!allowed) return
                }
            }

            val docRef = db.collection("notifications").document()
            val notification = NotificationItem(
                id = docRef.id,
                recipientUid = actualUid,
                title = title,
                message = message,
                type = type,
                relatedInternshipId = relatedInternshipId,
                relatedApplicationId = relatedApplicationId,
                isRead = false,
                createdAt = System.currentTimeMillis()
            )
            docRef.set(notification).await()
        } catch (_: Exception) {}
    }

    suspend fun markAsRead(notificationId: String) {
        try {
            db.collection("notifications").document(notificationId)
                .update(mapOf("isRead" to true, "read" to true))
                .await()
        } catch (_: Exception) {}
    }

    suspend fun markAllAsRead(userId: String) {
        try {
            val actualUserId = if (userId.isBlank()) "test-student-uid" else userId
            val ids = listOf(actualUserId, "test-student-uid").distinct()
            val allDocs = mutableListOf<com.google.firebase.firestore.DocumentSnapshot>()
            for (id in ids) {
                val s1 = db.collection("notifications").whereEqualTo("recipientUid", id).get().await()
                val s2 = db.collection("notifications").whereEqualTo("recipientUserId", id).get().await()
                allDocs.addAll(s1.documents + s2.documents)
            }

            val docs = allDocs.distinctBy { it.id }
            val batch = db.batch()
            for (doc in docs) {
                batch.update(doc.reference, mapOf("isRead" to true, "read" to true))
            }
            batch.commit().await()
        } catch (_: Exception) {}
    }
}
