package com.civicconnect.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicconnect.data.local.PreferenceManager
import com.civicconnect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    val isDarkMode = preferenceManager.isDarkMode.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val isNotificationEnabled = preferenceManager.isNotificationEnabled.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferenceManager.setDarkMode(enabled)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferenceManager.setNotificationEnabled(enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
