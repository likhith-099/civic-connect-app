package com.civicconnect.presentation.screens.community

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.civicconnect.presentation.components.EmptyStateView
import com.civicconnect.presentation.components.ErrorView
import com.civicconnect.presentation.components.ShimmerItem
import com.civicconnect.presentation.screens.community.components.ComplaintCard
import com.civicconnect.presentation.screens.complaint.MyComplaintsViewModel
import com.civicconnect.presentation.screens.complaint.MyComplaintsState
import com.civicconnect.presentation.screens.complaint.MyComplaintCard
import com.civicconnect.presentation.screens.complaint.EmptyMyComplaintsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onNavigateToDetails: (String) -> Unit,
    communityViewModel: CommunityViewModel = hiltViewModel(),
    myComplaintsViewModel: MyComplaintsViewModel = hiltViewModel()
) {
    val communityState by communityViewModel.state.collectAsStateWithLifecycle()
    val myComplaintsState by myComplaintsViewModel.state.collectAsStateWithLifecycle()
    val searchQuery by myComplaintsViewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by myComplaintsViewModel.selectedFilter.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Community Feed", "My Reports")

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                communityViewModel.loadComplaints(isSilent = true)
                myComplaintsViewModel.loadComplaints(isSilent = true)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Community & Reports",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { 
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (selectedTab == 0) {
                // Community Feed Tab
                val isRefreshing = communityState is CommunityState.Loading
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { communityViewModel.loadComplaints() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (val state = communityState) {
                        is CommunityState.Loading -> {
                            CommunityShimmer()
                        }
                        is CommunityState.Success -> {
                            val complaints = state.complaints
                            if (complaints.isEmpty()) {
                                EmptyStateView(
                                    message = "No active complaints in your community right now."
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(complaints) { complaint ->
                                        ComplaintCard(
                                            complaint = complaint,
                                            onClick = { onNavigateToDetails(complaint.id) }
                                        )
                                    }
                                }
                            }
                        }
                        is CommunityState.Error -> {
                            ErrorView(
                                message = state.message,
                                onRetry = { communityViewModel.loadComplaints() }
                            )
                        }
                        is CommunityState.Empty -> {
                            EmptyStateView(
                                message = "No active complaints in your community right now."
                            )
                        }
                    }
                }
            } else {
                // My Reports Tab
                val isRefreshing = myComplaintsState is MyComplaintsState.Loading
                val filters = listOf("All", "Pending", "In Progress", "Resolved", "Rejected")

                Column(modifier = Modifier.fillMaxSize()) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = myComplaintsViewModel::onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Search my reports...") },
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
                                onClick = { myComplaintsViewModel.onFilterChange(filter) },
                                label = { Text(filter) }
                            )
                        }
                    }

                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = { myComplaintsViewModel.loadComplaints() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (val state = myComplaintsState) {
                            is MyComplaintsState.Loading -> {
                                CommunityShimmer()
                            }
                            is MyComplaintsState.Success -> {
                                val complaints = state.complaints
                                if (complaints.isEmpty() && searchQuery.isBlank() && selectedFilter == "All") {
                                    EmptyMyComplaintsState()
                                } else if (complaints.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No reports matching your criteria.")
                                    }
                                } else {
                                    LazyColumn(
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(complaints) { complaint ->
                                            MyComplaintCard(
                                                complaint = complaint,
                                                onClick = { onNavigateToDetails(complaint.id) }
                                            )
                                        }
                                    }
                                }
                            }
                            is MyComplaintsState.Error -> {
                                ErrorView(
                                    message = state.message,
                                    onRetry = { myComplaintsViewModel.loadComplaints() }
                                )
                            }
                            is MyComplaintsState.Empty -> {
                                EmptyMyComplaintsState()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        repeat(3) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column {
                    ShimmerItem(modifier = Modifier.fillMaxWidth(), height = 200.dp)
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ShimmerItem(width = 80.dp)
                            ShimmerItem(width = 60.dp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        ShimmerItem(modifier = Modifier.fillMaxWidth(), height = 24.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                        ShimmerItem(modifier = Modifier.fillMaxWidth(0.7f))
                        Spacer(modifier = Modifier.height(16.dp))
                        ShimmerItem(width = 120.dp)
                    }
                }
            }
        }
    }
}
