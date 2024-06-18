package com.example.rentstyle.model.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingDataStore: DataStore<Preferences> by preferencesDataStore(name = "setting")
class SettingPref private constructor(private val dataStore: DataStore<Preferences>){

    companion object {
        @Volatile
        private var INSTANCE: SettingPref? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPref {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPref(dataStore)
                INSTANCE = instance
                instance
            }
        }

        val THEME_KEY = booleanPreferencesKey("theme_setting")
    }

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }
}