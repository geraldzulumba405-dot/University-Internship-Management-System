
package com.example.universityintershipapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
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
    val status: String = "pending",
    val workMode: String = "On-site",
    val createdAt: Long = System.currentTimeMillis()
)

sealed class InternshipResult {
    object Success : InternshipResult()
    data class Error(val message: String) : InternshipResult()
}

object InternshipRepository {

    private val db: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

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
            val currentUid = auth.currentUser?.uid
                ?: return InternshipResult.Error(
                    "Please log in before posting an internship."
                )

            val userDoc = db.collection("users")
                .document(currentUid)
                .get()
                .await()

            val companyName = userDoc.getString("companyName")
                ?.takeIf { it.isNotBlank() }
                ?: "Company name not provided"

            val docRef = db.collection("internships").document()

            val internshipData = hashMapOf(
                "id" to docRef.id,
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
            InternshipResult.Error(
                e.localizedMessage
                    ?: "Failed to post internship. Please check your network connection."
            )
        }
    }

    private suspend fun fetchCacheFirst(
        primarySource: Source,
        querySupplier: suspend (Source) -> com.google.firebase.firestore.QuerySnapshot,
        fallback: suspend () -> List<InternshipModel> = { emptyList() }
    ): List<InternshipModel> {
        if (primarySource == Source.CACHE) {
            return try {
                val snapshot = querySupplier(Source.CACHE)
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(InternshipModel::class.java)?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                emptyList()
            }
        }

        if (primarySource == Source.SERVER) {
            return try {
                val snapshot = querySupplier(Source.SERVER)
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(InternshipModel::class.java)?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                try {
                    val cachedSnapshot = querySupplier(Source.CACHE)
                    val cached = cachedSnapshot.documents.mapNotNull { doc ->
                        doc.toObject(InternshipModel::class.java)?.copy(id = doc.id)
                    }
                    if (cached.isNotEmpty()) cached else fallback()
                } catch (_: Exception) {
                    fallback()
                }
            }
        }

        // Default (CACHE first, then SERVER with fallback to CACHE)
        val cachedList = try {
            val snapshot = querySupplier(Source.CACHE)
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(InternshipModel::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }

        return try {
            val serverSnapshot = querySupplier(Source.SERVER)
            serverSnapshot.documents.mapNotNull { doc ->
                doc.toObject(InternshipModel::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            if (cachedList.isNotEmpty()) {
                cachedList
            } else {
                try {
                    fallback()
                } catch (_: Exception) {
                    emptyList()
                }
            }
        }
    }

    suspend fun getInternships(source: Source = Source.DEFAULT): List<InternshipModel> {
        return fetchCacheFirst(
            primarySource = source,
            querySupplier = { src ->
                db.collection("internships")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get(src)
                    .await()
            }
        )
    }

    suspend fun getPublishedInternships(source: Source = Source.DEFAULT): List<InternshipModel> {
        return fetchCacheFirst(
            primarySource = source,
            querySupplier = { src ->
                db.collection("internships")
                    .whereEqualTo("status", "published")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get(src)
                    .await()
            },
            fallback = {
                getInternships(source).filter { it.status == "published" || it.status.isBlank() }
            }
        )
    }

    suspend fun getEmployerInternships(uid: String, source: Source = Source.DEFAULT): List<InternshipModel> {
        if (uid.isBlank()) return emptyList()

        return fetchCacheFirst(
            primarySource = source,
            querySupplier = { src ->
                db.collection("internships")
                    .whereEqualTo("postedByUid", uid)
                    .get(src)
                    .await()
            }
        ).sortedByDescending { it.createdAt }
    }

    suspend fun getPendingInternships(source: Source = Source.DEFAULT): List<InternshipModel> {
        return fetchCacheFirst(
            primarySource = source,
            querySupplier = { src ->
                db.collection("internships")
                    .whereEqualTo("status", "pending")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get(src)
                    .await()
            },
            fallback = {
                getInternships(source).filter { it.status == "pending" }
            }
        )
    }

    suspend fun updateInternshipStatus(
        id: String,
        newStatus: String
    ): InternshipResult {
        return try {
            db.collection("internships")
                .document(id)
                .update("status", newStatus)
                .await()

            InternshipResult.Success

        } catch (e: Exception) {
            InternshipResult.Error(
                e.localizedMessage ?: "Failed to update internship status."
            )
        }
    }
}
