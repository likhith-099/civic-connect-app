package com.civicconnect.presentation.screens.admin.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicconnect.domain.model.Complaint
import com.civicconnect.domain.usecase.admin.AnalyzeComplaintUseCase
import com.civicconnect.domain.usecase.admin.GetAllComplaintsUseCase
import com.civicconnect.domain.usecase.admin.UpdateComplaintStatusUseCase
import com.civicconnect.domain.model.admin.AdminAnalysis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdminManagementState {
    object Loading : AdminManagementState()
    data class Success(val complaints: List<Complaint>) : AdminManagementState()
    data class Error(val message: String) : AdminManagementState()
}

@HiltViewModel
class AdminComplaintManagementViewModel @Inject constructor(
    private val getAllComplaintsUseCase: GetAllComplaintsUseCase,
    private val updateComplaintStatusUseCase: UpdateComplaintStatusUseCase,
    private val analyzeComplaintUseCase: AnalyzeComplaintUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AdminManagementState>(AdminManagementState.Loading)
    val state: StateFlow<AdminManagementState> = _state

    private val _analysisState = MutableStateFlow<Map<String, AdminAnalysis>>(emptyMap())
    val analysisState: StateFlow<Map<String, AdminAnalysis>> = _analysisState

    init {
        loadComplaints()
    }

    fun loadComplaints() {
        viewModelScope.launch {
            _state.value = AdminManagementState.Loading
            val result = getAllComplaintsUseCase()
            result.onSuccess {
                _state.value = AdminManagementState.Success(it)
            }.onFailure {
                _state.value = AdminManagementState.Error(it.message ?: "Failed to load complaints")
            }
        }
    }

    fun updateStatus(id: String, status: String) {
        viewModelScope.launch {
            val result = updateComplaintStatusUseCase(id, status)
            result.onSuccess {
                loadComplaints() // Refresh list
            }
        }
    }

    fun analyzeComplaint(id: String) {
        viewModelScope.launch {
            val result = analyzeComplaintUseCase(id)
            result.onSuccess { analysis ->
                val currentMap = _analysisState.value.toMutableMap()
                currentMap[id] = analysis
                _analysisState.value = currentMap
            }
        }
    }
}
