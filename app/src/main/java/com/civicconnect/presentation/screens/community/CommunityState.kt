package com.civicconnect.presentation.screens.community

import com.civicconnect.domain.model.Complaint

sealed class CommunityState {
    object Loading : CommunityState()
    data class Success(val complaints: List<Complaint>) : CommunityState()
    data class Error(val message: String) : CommunityState()
    object Empty : CommunityState()
}
