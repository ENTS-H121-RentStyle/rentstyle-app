package com.example.rentstyle.model.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class LoginSession private constructor (private val dataStore: DataStore<Preferences>) {
    companion object {
        @Volatile
        private var INSTANCE: LoginSession? = null

        fun getInstance(dataStore: DataStore<Preferences>): LoginSession {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginSession(dataStore)
                INSTANCE = instance
                instance
            }
        }

        val USER_ID = stringPreferencesKey("user_id")
        val SESSION_TOKEN = stringPreferencesKey("session_token")
        val FIRST_TIME_LOGIN = booleanPreferencesKey("first_login")
        val PREF_CHECK = booleanPreferencesKey("pref_check")
    }

    suspend fun setUserIdAndToken (id: String, token: String) {
        dataStore.edit {  preferences ->
            preferences[USER_ID] = id
            preferences[SESSION_TOKEN] = token
        }
    }

    fun getUserId(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID]
        }
    }

    fun getSessionToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[SESSION_TOKEN]
        }
    }

    suspend fun setFirstLoginSession () {
        dataStore.edit { preferences->
            preferences[FIRST_TIME_LOGIN] = true
        }
    }

    fun getFirstLoginSession(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[FIRST_TIME_LOGIN] ?: false
        }
    }

    suspend fun setPrefCheck () {
        dataStore.edit { preferences->
            preferences[PREF_CHECK] = true
        }
    }

    fun getPrefCheck(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PREF_CHECK] ?: false
        }
    }
}