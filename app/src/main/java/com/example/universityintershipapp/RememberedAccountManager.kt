package com.example.universityintershipapp

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

data class RememberedAccount(
    val email: String,
    val passwordEncryptedOrStored: String,
    val fullName: String,
    val role: String,
    val uid: String
)

object RememberedAccountManager {
    private const val PREFS_NAME = "uniintern_secure_prefs"
    private const val KEY_EMAIL = "remembered_email"
    private const val KEY_PASSWORD = "remembered_password"
    private const val KEY_NAME = "remembered_name"
    private const val KEY_ROLE = "remembered_role"
    private const val KEY_UID = "remembered_uid"
    private const val KEY_IS_REMEMBERED = "is_remembered"

    private fun getPrefs(context: Context): android.content.SharedPreferences? {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            null
        }
    }

    fun saveRememberedAccount(context: Context, email: String, password: String, fullName: String, role: String, uid: String) {
        if (role.trim().lowercase() == "admin") return
        val prefs = getPrefs(context) ?: return
        prefs.edit()
            .putString(KEY_EMAIL, email.trim())
            .putString(KEY_PASSWORD, password)
            .putString(KEY_NAME, fullName.trim())
            .putString(KEY_ROLE, role.trim().lowercase())
            .putString(KEY_UID, uid)
            .putBoolean(KEY_IS_REMEMBERED, true)
            .apply()
    }

    fun getRememberedAccount(context: Context): RememberedAccount? {
        val prefs = getPrefs(context) ?: return null
        val isRemembered = prefs.getBoolean(KEY_IS_REMEMBERED, false)
        if (!isRemembered) return null

        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        val password = prefs.getString(KEY_PASSWORD, null) ?: return null
        val name = prefs.getString(KEY_NAME, "") ?: ""
        val role = prefs.getString(KEY_ROLE, "") ?: ""
        val uid = prefs.getString(KEY_UID, "") ?: ""

        if (role == "admin" || email.isBlank() || password.isBlank()) return null

        return RememberedAccount(email, password, name, role, uid)
    }

    fun clearRememberedAccount(context: Context) {
        val prefs = getPrefs(context) ?: return
        prefs.edit().clear().apply()
    }
}
