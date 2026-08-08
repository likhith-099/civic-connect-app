package com.civicconnect.presentation.screens.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicconnect.domain.usecase.complaint.GetComplaintsUseCase
import com.civicconnect.domain.usecase.complaint.UpvoteComplaintUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val getComplaintsUseCase: GetComplaintsUseCase,
    private val upvoteComplaintUseCase: UpvoteComplaintUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CommunityState>(CommunityState.Loading)
    val state: StateFlow<CommunityState> = _state

    init {
        loadComplaints()
    }

    fun loadComplaints(isSilent: Boolean = false) {
        viewModelScope.launch {
            if (!isSilent) _state.value = CommunityState.Loading
            val result = getComplaintsUseCase()
            result.onSuccess { complaints ->
                _state.value = if (complaints.isEmpty()) {
                    CommunityState.Empty
                } else {
                    CommunityState.Success(complaints)
                }
            }.onFailure {
                _state.value = CommunityState.Error(it.message ?: "Failed to load complaints")
            }
        }
    }

    fun upvote(complaintId: String) {
        viewModelScope.launch {
            val result = upvoteComplaintUseCase(complaintId)
            result.onSuccess { updated ->
                val current = _state.value
                if (current is CommunityState.Success) {
                    val updatedList = current.complaints.map {
                        if (it.id == complaintId) updated else it
                    }
                    _state.value = CommunityState.Success(updatedList)
                }
            }
        }
    }
}
