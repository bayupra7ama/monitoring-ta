package com.tugas.data.repository


import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    val dataStore = context.dataStore // expose ke luar
    val roleFlow: Flow<String?> get() = context.dataStore.data.map { it[ROLE_KEY] }

    companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val ROLE_KEY = stringPreferencesKey("role")
    }



    suspend fun saveAuth(token: String, role: String) {
        Log.d("UserPreferences", "Saving token=$token, role=$role")
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[ROLE_KEY] = role
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data.map { it[TOKEN_KEY] ?: "" }.first()
    }

    suspend fun getRole(): String? {
        return context.dataStore.data.map { it[ROLE_KEY] ?: "" }.first()
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
