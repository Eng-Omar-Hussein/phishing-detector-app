package com.emad.data.local


import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

// @Singleton ensures we only have ONE instance of this class running
@Singleton
class SessionManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREF_NAME = "secure_email_session"
        const val KEY_AUTH_TOKEN = "auth_token"
        const val KEY_USER_NAME = "user_name"  // Optional: Cache name for header
        const val KEY_USER_EMAIL = "user_email" // Optional: Cache email for header
        const val KEY_GOOGLE_ID = "google_id"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        prefs.edit {
            putString(KEY_AUTH_TOKEN, token)
        } // Asynchronous save
    }

    /**
     * Function to fetch auth token
     */
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Save basic user details for the Navigation Header
     * (So we don't have to query DB just to show the name)
     */
    fun saveUserDetails(name: String, email: String, googleId: String) {
        prefs.edit {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_GOOGLE_ID, googleId)
        }
    }

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, "User")
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, "")

    /**
     * Clear session details (Logout)
     */
    fun clearSession() {
        prefs.edit {
            clear()
        }
    }
}