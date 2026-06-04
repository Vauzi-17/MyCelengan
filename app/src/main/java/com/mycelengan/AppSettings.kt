package com.mycelengan

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appSettingsDataStore by preferencesDataStore(name = "app_settings")

object AppSettings {
    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val notificationKey = booleanPreferencesKey("target_reminder_notification")

    fun darkMode(context: Context): Flow<Boolean?> {
        return context.appSettingsDataStore.data.map { preferences ->
            preferences[darkModeKey]
        }
    }

    fun targetReminderNotification(context: Context): Flow<Boolean> {
        return context.appSettingsDataStore.data.map { preferences ->
            preferences[notificationKey] ?: false
        }
    }

    suspend fun setDarkMode(context: Context, enabled: Boolean) {
        context.appSettingsDataStore.edit { preferences ->
            preferences[darkModeKey] = enabled
        }
    }

    suspend fun setTargetReminderNotification(context: Context, enabled: Boolean) {
        context.appSettingsDataStore.edit { preferences ->
            preferences[notificationKey] = enabled
        }
    }
}
