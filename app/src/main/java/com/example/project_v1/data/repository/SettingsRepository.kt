package com.example.project_v1.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

data class SettingsData(
    val ipSuffix: String = "0.173",
    val username: String = "Brak",
    val filename: String = "IW2024"
)

class SettingsRepository(private val context: Context) {
    companion object {
        val KEY_IP = stringPreferencesKey("ip_suffix")
        val KEY_USER = stringPreferencesKey("username")
        val KEY_FILE = stringPreferencesKey("filename")
    }

    val settings: Flow<SettingsData> = context.dataStore.data.map { prefs ->
        SettingsData(
            ipSuffix = prefs[KEY_IP] ?: "0.173",
            username = prefs[KEY_USER] ?: "Brak",
            filename = prefs[KEY_FILE] ?: "IW2024"
        )
    }

    suspend fun save(data: SettingsData) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IP] = data.ipSuffix
            prefs[KEY_USER] = data.username
            prefs[KEY_FILE] = data.filename
        }
    }
}
