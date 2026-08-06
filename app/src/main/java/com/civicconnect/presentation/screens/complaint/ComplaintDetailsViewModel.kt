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
                // If it's a 404 or similar, we could show Empty, otherwise Error
                _state.value = ComplaintDetailsState.Error(it.message ?: "Failed to load complaint")
            }
        }
    }

    fun upvote() {
        viewModelScope.launch {
            val result = upvoteComplaintUseCase(complaintId)
            result.onSuccess { updatedComplaint ->
                _state.value = ComplaintDetailsState.Success(updatedComplaint)
            }.onFailure {
                _message.value = it.message ?: "Failed to upvote complaint"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
