package com.civicconnect.presentation.screens.complaint

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicconnect.domain.model.Complaint
import com.civicconnect.domain.usecase.complaint.GetComplaintByIdUseCase
import com.civicconnect.domain.usecase.complaint.UpvoteComplaintUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ComplaintDetailsState {
    object Loading : ComplaintDetailsState()
    data class Success(val complaint: Complaint) : ComplaintDetailsState()
    data class Error(val message: String) : ComplaintDetailsState()
    object Empty : ComplaintDetailsState()
}

@HiltViewModel
class ComplaintDetailsViewModel @Inject constructor(
    private val getComplaintByIdUseCase: GetComplaintByIdUseCase,
    private val upvoteComplaintUseCase: UpvoteComplaintUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<ComplaintDetailsState>(ComplaintDetailsState.Loading)
    val state: StateFlow<ComplaintDetailsState> = _state

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Separate loading flag for upvote so the main state doesn't flicker to Loading
    private val _isUpvoting = MutableStateFlow(false)
    val isUpvoting: StateFlow<Boolean> = _isUpvoting

    private val complaintId: String = checkNotNull(savedStateHandle["complaintId"])

    init {
        loadComplaint()
    }

    fun loadComplaint() {
        viewModelScope.launch {
            _state.value = ComplaintDetailsState.Loading
            val result = getComplaintByIdUseCase(complaintId)
            result.onSuccess {
                _state.value = ComplaintDetailsState.Success(it)
            }.onFailure {
                _state.value = ComplaintDetailsState.Error(it.message ?: "Failed to load complaint")
            }
        }
    }

    fun upvote() {
        // Guard: do nothing if already upvoting
        if (_isUpvoting.value) return

        viewModelScope.launch {
            _isUpvoting.value = true
            val result = upvoteComplaintUseCase(complaintId)
            result.onSuccess { updatedComplaint ->
                _state.value = ComplaintDetailsState.Success(updatedComplaint)
                _message.value = "✓ Your support has been recorded!"
            }.onFailure { error ->
                val msg = when {
                    error.message?.contains("already", ignoreCase = true) == true ->
                        "You've already supported this issue."
                    error.message?.contains("401", ignoreCase = true) == true ||
                    error.message?.contains("unauthorized", ignoreCase = true) == true ->
                        "Please log in to support issues."
                    else -> error.message ?: "Failed to record your support. Try again."
                }
                _message.value = msg
            }
            _isUpvoting.value = false
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
