package com.example.universityintershipapp

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

data class NotificationItem(
    val id: String = "",
    val recipientUserId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "", // "application", "internship", "registration", "report"
    val relatedInternshipId: String? = null,
    val relatedApplicationId: String? = null,
    val read: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

object NotificationRepository {
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    suspend fun getNotifications(userId: String): List<NotificationItem> {
        return try {
            val snapshot = db.collection("notifications")
                .whereEqualTo("recipientUserId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                val item = doc.toObject(NotificationItem::class.java)
                item?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            // Fallback if index isn't built yet
            try {
                val snapshot = db.collection("notifications")
                    .whereEqualTo("recipientUserId", userId)
                    .get()
                    .await()
                snapshot.documents.mapNotNull { doc ->
                    val item = doc.toObject(NotificationItem::class.java)
                    item?.copy(id = doc.id)
                }.sortedByDescending { it.timestamp }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    suspend fun sendNotification(
        recipientUserId: String,
        title: String,
        message: String,
        type: String,
        relatedInternshipId: String? = null,
        relatedApplicationId: String? = null
    ) {
        try {
            val docRef = db.collection("notifications").document()
            val notification = NotificationItem(
                id = docRef.id,
                recipientUserId = recipientUserId,
                title = title,
                message = message,
                type = type,
                relatedInternshipId = relatedInternshipId,
                relatedApplicationId = relatedApplicationId,
                read = false,
                timestamp = System.currentTimeMillis()
            )
            docRef.set(notification).await()
        } catch (_: Exception) {}
    }

    suspend fun markAsRead(notificationId: String) {
        try {
            db.collection("notifications").document(notificationId)
                .update("read", true)
                .await()
        } catch (_: Exception) {}
    }

    suspend fun markAllAsRead(userId: String) {
        try {
            val snapshot = db.collection("notifications")
                .whereEqualTo("recipientUserId", userId)
                .whereEqualTo("read", false)
                .get()
                .await()
            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.update(doc.reference, "read", true)
            }
            batch.commit().await()
        } catch (e: Exception) {
            try {
                val snapshot = db.collection("notifications")
                    .whereEqualTo("recipientUserId", userId)
                    .get()
                    .await()
                val batch = db.batch()
                for (doc in snapshot.documents) {
                    batch.update(doc.reference, "read", true)
                }
                batch.commit().await()
            } catch (_: Exception) {}
        }
    }
}
