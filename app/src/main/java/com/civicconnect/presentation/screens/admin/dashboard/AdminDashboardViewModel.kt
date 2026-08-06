package com.civicconnect.presentation.screens.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicconnect.domain.model.admin.AdminStats
import com.civicconnect.domain.usecase.admin.GetAdminStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdminDashboardState {
    object Loading : AdminDashboardState()
    data class Success(val stats: AdminStats) : AdminDashboardState()
    data class Error(val message: String) : AdminDashboardState()
}

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val getAdminStatsUseCase: GetAdminStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AdminDashboardState>(AdminDashboardState.Loading)
    val state: StateFlow<AdminDashboardState> = _state

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = AdminDashboardState.Loading
            val result = getAdminStatsUseCase()
            result.onSuccess {
                _state.value = AdminDashboardState.Success(it)
            }.onFailure {
                _state.value = AdminDashboardState.Error(it.message ?: "Failed to load dashboard")
            }
        }
    }
}
