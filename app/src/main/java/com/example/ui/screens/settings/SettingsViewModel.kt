package com.example.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    KINYARWANDA("rw", "Kinyarwanda"),
    FRENCH("fr", "Français")
}

class SettingsViewModel : ViewModel() {

    private val _isDarkMode = MutableStateFlow(com.example.ui.theme.AppThemeState.isDarkMode.value)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _cacheSizeMb = MutableStateFlow(128)
    val cacheSizeMb: StateFlow<Int> = _cacheSizeMb.asStateFlow()

    private val _isClearingCache = MutableStateFlow(false)
    val isClearingCache: StateFlow<Boolean> = _isClearingCache.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        com.example.ui.theme.AppThemeState.isDarkMode.value = enabled
    }

    fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
        _toastMessage.value = "Language changed to ${language.displayName}"
    }

    fun clearCache() {
        viewModelScope.launch {
            _isClearingCache.value = true
            delay(1000)
            _cacheSizeMb.value = 0
            _isClearingCache.value = false
            _toastMessage.value = "Cache cleaned successfully!"
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
