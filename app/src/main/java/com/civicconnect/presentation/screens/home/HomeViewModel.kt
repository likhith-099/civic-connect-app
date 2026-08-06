package com.civicconnect.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicconnect.domain.model.DashboardStats
import com.civicconnect.domain.model.User
import com.civicconnect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.civicconnect.domain.usecase.complaint.GetComplaintsUseCase

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getComplaintsUseCase: GetComplaintsUseCase
) : ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState

    private val _statsState = MutableStateFlow(
        DashboardStats(
            totalComplaints = 0,
            pendingComplaints = 0,
            inProgressComplaints = 0,
            resolvedComplaints = 0
        )
    )
    val statsState: StateFlow<DashboardStats> = _statsState

    init {
        loadUserProfile()
        loadStats()
    }

    fun refresh() {
        loadUserProfile()
        loadStats()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            authRepository.getCurrentUser().onSuccess {
                _userState.value = it
            }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            getComplaintsUseCase().onSuccess { complaints ->
                _statsState.value = DashboardStats(
                    totalComplaints = complaints.size,
                    pendingComplaints = complaints.count { it.status.lowercase() == "pending" },
                    inProgressComplaints = complaints.count { it.status.lowercase() == "in progress" },
                    resolvedComplaints = complaints.count { it.status.lowercase() == "resolved" }
                )
            }
        }
    }
}
