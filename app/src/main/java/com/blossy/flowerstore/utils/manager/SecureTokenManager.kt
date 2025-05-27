package com.blossy.flowerstore.utils.manager

import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureTokenManager @Inject constructor(
    private val encryptionManager: EncryptionManager,
    private val sharedPreferences: SharedPreferences
) {
    init {
        Log.d(TAG, "SecureTokenManager initialized")
    }

    fun saveAccessToken(token: String) {
        Log.d(TAG, "Original token: $token")

        val encryptedToken = encryptionManager.encrypt(token)
        Log.d(TAG, "Encrypted token: $encryptedToken")

        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, encryptedToken).apply()
        Log.d(TAG, "Token saved to SharedPreferences")
    }

    val accessToken: String?
        get() {
            val encryptedToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
            Log.d(
                TAG,
                "Retrieved encrypted token from SharedPreferences: $encryptedToken"
            )

            if (encryptedToken != null) {
                val decryptedToken = encryptionManager.decrypt(encryptedToken)
                Log.d(
                    TAG,
                    "Decrypted token: $decryptedToken"
                ) // Log token đã giải mã
                return decryptedToken
            } else {
                Log.d(TAG, "No token found in SharedPreferences")
                return null
            }
        }

    fun clearTokens() {
        Log.d(TAG, "Clearing tokens from SharedPreferences")
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .apply()
        Log.d(TAG, "Tokens cleared")
    }

    companion object {
        private const val TAG = "SecureTokenManager"
        private const val KEY_ACCESS_TOKEN = "access_token"
    }
}