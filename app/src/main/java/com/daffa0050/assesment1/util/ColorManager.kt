package com.daffa0050.assesment1.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.daffa0050.assesment1.screen.PreferenceKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ColorManager(private val context: Context) {
    val themeColor: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.THEME_COLOR] ?: "Coklat"
        }

    suspend fun saveThemePreference(colorName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_COLOR] = colorName
        }
    }
}