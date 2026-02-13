package com.example.qa3at.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_preferences")

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val languageKey = stringPreferencesKey("selected_language")

    val selectedLanguage: Flow<String> = context.languageDataStore.data
        .map { preferences ->
            preferences[languageKey] ?: "ar" // Default to Arabic
        }

    suspend fun setLanguage(languageCode: String) {
        context.languageDataStore.edit { preferences ->
            preferences[languageKey] = languageCode
        }
    }

    suspend fun getLanguage(): String {
        return context.languageDataStore.data
            .map { preferences -> preferences[languageKey] ?: "ar" }
            .first()
    }
}
