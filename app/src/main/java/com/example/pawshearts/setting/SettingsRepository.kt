package com.example.pawshearts.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// kho lưu trữ
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SettingsRepository(private val context: Context) {

    private val IS_DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode") // tên của chìa khóa

    val isDarkModeFlow: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[IS_DARK_MODE_KEY] ?: false // mặc định là sáng
        }
        // hàm ghi dữ liệu của người dùng
    suspend fun setTheme(isDarkMode: Boolean) {
        context.settingsDataStore.edit { settings ->
            settings[IS_DARK_MODE_KEY] = isDarkMode
        }
    }
}
