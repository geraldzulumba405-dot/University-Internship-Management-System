package com.example.universityintershipapp


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

object FcmTokenManager {

    fun registerTokenForCurrentUser(
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            onComplete(false, "No authenticated user found.")
            return
        }

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update(
                        "fcmTokens",
                        FieldValue.arrayUnion(token)
                    )
                    .addOnSuccessListener {
                        onComplete(true, null)
                    }
                    .addOnFailureListener { exception ->
                        onComplete(false, exception.localizedMessage)
                    }
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.localizedMessage)
            }
    }

    fun saveTokenForCurrentUser(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update(
                "fcmTokens",
                FieldValue.arrayUnion(token)
            )
    }
}
