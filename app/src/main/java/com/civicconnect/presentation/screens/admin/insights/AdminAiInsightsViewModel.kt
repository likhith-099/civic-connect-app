package com.civicconnect.presentation.screens.admin.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicconnect.domain.model.admin.AdminInsight
import com.civicconnect.domain.usecase.admin.GetAdminInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdminInsightsState {
    object Loading : AdminInsightsState()
    data class Success(val insights: AdminInsight) : AdminInsightsState()
    data class Error(val message: String) : AdminInsightsState()
}

@HiltViewModel
class AdminAiInsightsViewModel @Inject constructor(
    private val getAdminInsightsUseCase: GetAdminInsightsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AdminInsightsState>(AdminInsightsState.Loading)
    val state: StateFlow<AdminInsightsState> = _state

    init {
        loadInsights()
    }

    fun loadInsights() {
        viewModelScope.launch {
            _state.value = AdminInsightsState.Loading
            val result = getAdminInsightsUseCase()
            result.onSuccess {
                _state.value = AdminInsightsState.Success(it)
            }.onFailure {
                _state.value = AdminInsightsState.Error(it.message ?: "Failed to load insights")
            }
        }
    }
}
