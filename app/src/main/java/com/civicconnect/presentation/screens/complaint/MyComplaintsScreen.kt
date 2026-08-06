package com.civicconnect.presentation.screens.complaint

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.civicconnect.domain.model.Complaint
import com.civicconnect.presentation.screens.community.components.ComplaintCard

import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyComplaintsScreen(
    onNavigateToDetails: (String) -> Unit,
    viewModel: MyComplaintsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val isRefreshing = state is MyComplaintsState.Loading
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadComplaints(isSilent = true)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val filters = listOf("All", "Pending", "In Progress", "Resolved", "Rejected")

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search my complaints...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        // Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { viewModel.onFilterChange(filter) },
                    label = { Text(filter) }
                )
            }
        }

        val filteredComplaints by viewModel.filteredComplaints.collectAsStateWithLifecycle()

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.loadComplaints() },
            modifier = Modifier.fillMaxSize()
        ) {
            if (state is MyComplaintsState.Loading && filteredComplaints.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (filteredComplaints.isEmpty() && searchQuery.isBlank() && selectedFilter == "All") {
                    EmptyMyComplaintsState()
                } else if (filteredComplaints.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No results matching your criteria.")
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredComplaints) { complaint ->
                            MyComplaintCard(
                                complaint = complaint,
                                onClick = { onNavigateToDetails(complaint.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyComplaintCard(complaint: Complaint, onClick: () -> Unit) {
    // We can reuse ComplaintCard or create a specialized one. 
    // The requirement mentions specific status colors.
    ComplaintCard(complaint = complaint, onClick = onClick)
}

@Composable
fun EmptyMyComplaintsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "You haven't reported any complaints yet.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
