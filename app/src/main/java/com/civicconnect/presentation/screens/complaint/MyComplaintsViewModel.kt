package com.civicconnect.presentation.screens.complaint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicconnect.domain.model.Complaint
import com.civicconnect.domain.usecase.complaint.GetMyComplaintsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MyComplaintsState {
    object Loading : MyComplaintsState()
    data class Success(val complaints: List<Complaint>) : MyComplaintsState()
    data class Error(val message: String) : MyComplaintsState()
    object Empty : MyComplaintsState()
}

@HiltViewModel
class MyComplaintsViewModel @Inject constructor(
    private val getMyComplaintsUseCase: GetMyComplaintsUseCase
) : ViewModel() {

    private val _allComplaints = MutableStateFlow<List<Complaint>>(emptyList())
    
    private val _filteredComplaints = MutableStateFlow<List<Complaint>>(emptyList())
    val filteredComplaints: StateFlow<List<Complaint>> = _filteredComplaints.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter

    private val _state = MutableStateFlow<MyComplaintsState>(MyComplaintsState.Loading)
    val state: StateFlow<MyComplaintsState> = _state

    init {
        loadComplaints()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterComplaints()
    }

    fun onFilterChange(filter: String) {
        _selectedFilter.value = filter
        filterComplaints()
    }

    fun loadComplaints(isSilent: Boolean = false) {
        viewModelScope.launch {
            if (!isSilent) _state.value = MyComplaintsState.Loading
            val result = getMyComplaintsUseCase()
            result.onSuccess { complaints ->
                _allComplaints.value = complaints
                filterComplaints()
            }.onFailure {
                _state.value = MyComplaintsState.Error(it.message ?: "Failed to load complaints")
            }
        }
    }

    private fun filterComplaints() {
        val query = _searchQuery.value.lowercase()
        val filter = _selectedFilter.value

        val filtered = _allComplaints.value.filter { complaint ->
            val matchesSearch = complaint.title.lowercase().contains(query) || 
                               complaint.description.lowercase().contains(query)
            val matchesFilter = if (filter == "All") true else complaint.status.equals(filter, ignoreCase = true)
            
            matchesSearch && matchesFilter
        }

        _filteredComplaints.value = filtered

        _state.value = if (filtered.isEmpty()) {
            if (_allComplaints.value.isEmpty()) MyComplaintsState.Empty else MyComplaintsState.Success(emptyList())
        } else {
            MyComplaintsState.Success(filtered)
        }
    }
}
