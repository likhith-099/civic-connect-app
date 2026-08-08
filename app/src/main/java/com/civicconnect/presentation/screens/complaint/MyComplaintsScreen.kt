package com.civicconnect.presentation.screens.complaint

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.civicconnect.domain.model.Complaint
import com.civicconnect.presentation.components.CommunityShimmer
import com.civicconnect.presentation.components.EmptyStateView
import com.civicconnect.presentation.screens.community.components.ComplaintCard

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
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            placeholder = { Text("Search your reports...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        // Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { viewModel.onFilterChange(filter) },
                    label = { Text(filter, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(50.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
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
                CommunityShimmer()
            } else {
                if (filteredComplaints.isEmpty() && searchQuery.isBlank() && selectedFilter == "All") {
                    EmptyMyComplaintsState()
                } else if (filteredComplaints.isEmpty()) {
                    EmptyStateView(message = "No reports matching criteria.")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredComplaints) { complaint ->
                            MyComplaintCard(
                                complaint = complaint,
                                onClick = { onNavigateToDetails(complaint.id) },
                                onUpvote = { viewModel.upvote(complaint.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyComplaintCard(complaint: Complaint, onClick: () -> Unit, onUpvote: (() -> Unit)? = null) {
    ComplaintCard(complaint = complaint, onClick = onClick, onUpvote = onUpvote)
}

@Composable
fun EmptyMyComplaintsState() {
    EmptyStateView(
        message = "You haven't reported any civic issues yet. Tap the report button on the home screen to submit your first report!"
    )
}
