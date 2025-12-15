package com.example.projectfinalpaseador.data.repository

import android.content.Context
import android.content.SharedPreferences

class TokenRepository(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "walker_prefs"
        private const val TOKEN_KEY = "auth_token"
        private const val USER_NAME_KEY = "user_name"
        private const val USER_EMAIL_KEY = "user_email"
        private const val USER_PHOTO_KEY = "user_photo"
    }

    fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString(TOKEN_KEY, token)
            .apply()
    }

    fun saveUserInfo(name: String?, email: String?, photo: String? = null) {
        sharedPreferences.edit()
            .putString(USER_NAME_KEY, name)
            .putString(USER_EMAIL_KEY, email)
            .putString(USER_PHOTO_KEY, photo)
            .apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun getUserName(): String? = sharedPreferences.getString(USER_NAME_KEY, null)
    fun getUserEmail(): String? = sharedPreferences.getString(USER_EMAIL_KEY, null)
    fun getUserPhoto(): String? = sharedPreferences.getString(USER_PHOTO_KEY, null)

    fun clearToken() {
        sharedPreferences.edit()
            .remove(TOKEN_KEY)
            .remove(USER_NAME_KEY)
            .remove(USER_EMAIL_KEY)
            .remove(USER_PHOTO_KEY)
            .apply()
    }

    fun hasToken(): Boolean {
        return getToken() != null
    }
}
