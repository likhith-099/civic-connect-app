package com.civicconnect.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("auth_prefs")

@Singleton
class TokenManager @Inject constructor(private val context: Context) {

    private val tokenKey = stringPreferencesKey("jwt_token")
    private val roleKey = stringPreferencesKey("user_role")

    val token: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[tokenKey]
    }

    val role: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[roleKey]
    }

    suspend fun saveSession(token: String, role: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token
            preferences[roleKey] = role
        }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(tokenKey)
            preferences.remove(roleKey)
        }
    }
}
