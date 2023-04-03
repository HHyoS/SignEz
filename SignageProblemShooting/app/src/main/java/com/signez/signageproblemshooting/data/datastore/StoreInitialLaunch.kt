package com.signez.signageproblemshooting.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreInitialLaunch(private val context: Context) {

    // to make sure there is only one instance
    companion object{
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("InitialLaunch")
        val INITIAL_LAUNCH_KEY = booleanPreferencesKey("initial_launch")
    }

    // to get the initial launch
    val getInitialLaunch: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[INITIAL_LAUNCH_KEY] ?: true
    }

    // to save the
    suspend fun saveInitialLaunch(isInitialLaunch: Boolean){
        context.dataStore.edit { preferences ->
            preferences[INITIAL_LAUNCH_KEY] = isInitialLaunch
        }
    }

}