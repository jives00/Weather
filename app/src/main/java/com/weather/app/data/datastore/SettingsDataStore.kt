package com.weather.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.weather.app.domain.model.Units
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    private val UNITS_KEY = stringPreferencesKey("units")

    val units: Flow<Units> = context.settingsDataStore.data.map { prefs ->
        Units.valueOf(prefs[UNITS_KEY] ?: Units.IMPERIAL.name)
    }

    suspend fun setUnits(units: Units) {
        context.settingsDataStore.edit { prefs ->
            prefs[UNITS_KEY] = units.name
        }
    }
}
