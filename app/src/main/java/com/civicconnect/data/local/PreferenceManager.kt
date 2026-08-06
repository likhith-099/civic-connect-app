package com.civicconnect.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("app_settings")

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val notificationKey = booleanPreferencesKey("notifications_enabled")

    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[darkModeKey] ?: false }
    val isNotificationEnabled: Flow<Boolean> = dataStore.data.map { it[notificationKey] ?: true }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[darkModeKey] = enabled }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { it[notificationKey] = enabled }
    }
}
