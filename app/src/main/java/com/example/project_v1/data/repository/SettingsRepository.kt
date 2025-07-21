package com.example.project_v1.data.repository

import android.content.Context

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    data class Settings(val ipSuffix: String, val username: String, val filename: String)

    fun loadSettings(): Settings {
        val ip = prefs.getString(KEY_IP, DEFAULT_IP) ?: DEFAULT_IP
        val user = prefs.getString(KEY_USER, DEFAULT_USER) ?: DEFAULT_USER
        val file = prefs.getString(KEY_FILE, DEFAULT_FILE) ?: DEFAULT_FILE
        return Settings(ip, user, file)
    }

    fun saveSettings(ipSuffix: String, username: String, filename: String) {
        prefs.edit()
            .putString(KEY_IP, ipSuffix)
            .putString(KEY_USER, username)
            .putString(KEY_FILE, filename)
            .apply()
    }

    companion object {
        private const val PREF_NAME = "app_settings"
        private const val KEY_IP = "ip_suffix"
        private const val KEY_USER = "username"
        private const val KEY_FILE = "filename"

        const val DEFAULT_IP = "0.173"
        const val DEFAULT_USER = "Brak"
        const val DEFAULT_FILE = "IW2024"
    }
}
