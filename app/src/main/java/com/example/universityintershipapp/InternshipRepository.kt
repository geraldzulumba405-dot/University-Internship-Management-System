package com.example.universityintershipapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

data class InternshipModel(
    val id: String = "",
    val title: String = "",
    val fieldOfStudy: String = "",
    val location: String = "",
    val duration: String = "",
    val deadline: String = "",
    val description: String = "",
    val requirements: String = "",
    val skills: List<String> = emptyList(),
    val reqTranscript: Boolean = true,
    val reqResume: Boolean = true,
    val reqPortfolio: Boolean = true,
    val companyName: String = "",
    val postedByUid: String = "",
    val status: String = "pending", // "pending", "published", "rejected", "removed"
    val workMode: String = "On-site",
    val createdAt: Long = System.currentTimeMillis()
)

sealed class InternshipResult {
    object Success : InternshipResult()
    data class Error(val message: String) : InternshipResult()
}

object InternshipRepository {
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    suspend fun postInternship(
        title: String,
        fieldOfStudy: String,
        location: String,
        duration: String,
        deadline: String,
        description: String,
        requirements: String,
        skills: List<String>,
        reqTranscript: Boolean,
        reqResume: Boolean,
        reqPortfolio: Boolean,
        workMode: String
    ): InternshipResult {
        return try {
            val docRef = db.collection("internships").document()
            val id = docRef.id
            val currentUid = auth.currentUser?.uid ?: "employer-uid"

            var companyName = "Apex Tech Solutions"
            if (auth.currentUser != null) {
                try {
                    val userDoc = db.collection("users").document(currentUid).get().await()
                    companyName = userDoc.getString("companyName")
                        ?: userDoc.getString("fullName")
                        ?: "Apex Tech Solutions"
                } catch (_: Exception) {}
            }

            val internshipData = hashMapOf(
                "id" to id,
                "title" to title.trim(),
                "fieldOfStudy" to fieldOfStudy.trim(),
                "location" to location.trim(),
                "duration" to duration.trim(),
                "deadline" to deadline.trim(),
                "description" to description.trim(),
                "requirements" to requirements.trim(),
                "skills" to skills,
                "reqTranscript" to reqTranscript,
                "reqResume" to reqResume,
                "reqPortfolio" to reqPortfolio,
                "companyName" to companyName,
                "postedByUid" to currentUid,
                "status" to "pending",
                "workMode" to workMode.trim(),
                "createdAt" to System.currentTimeMillis()
            )

            docRef.set(internshipData).await()
            InternshipResult.Success
        } catch (e: Exception) {
            InternshipResult.Error(e.localizedMessage ?: "Failed to post internship. Please check network connection.")
        }
    }

    suspend fun getInternships(): List<InternshipModel> {
        return try {
            val snapshot = db.collection("internships")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(InternshipModel::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPublishedInternships(): List<InternshipModel> {
        return try {
            val snapshot = db.collection("internships")
                .whereEqualTo("status", "published")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(InternshipModel::class.java)
            }
        } catch (e: Exception) {
            // Fallback if index missing: fetch all and filter in memory
            try {
                getInternships().filter { it.status == "published" || it.status.isBlank() }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getEmployerInternships(uid: String): List<InternshipModel> {
        return try {
            val snapshot = db.collection("internships")
                .whereEqualTo("postedByUid", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(InternshipModel::class.java)
            }
        } catch (e: Exception) {
            try {
                getInternships().filter { it.postedByUid == uid }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getPendingInternships(): List<InternshipModel> {
        return try {
            val snapshot = db.collection("internships")
                .whereEqualTo("status", "pending")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(InternshipModel::class.java)
            }
        } catch (e: Exception) {
            try {
                getInternships().filter { it.status == "pending" || it.status.isBlank() }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    suspend fun updateInternshipStatus(id: String, newStatus: String): InternshipResult {
        return try {
            db.collection("internships").document(id)
                .update("status", newStatus)
                .await()
            InternshipResult.Success
        } catch (e: Exception) {
            InternshipResult.Error(e.localizedMessage ?: "Failed to update internship status.")
        }
    }
}
