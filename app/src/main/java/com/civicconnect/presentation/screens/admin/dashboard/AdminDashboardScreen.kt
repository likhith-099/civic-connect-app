package com.civicconnect.presentation.screens.admin.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.civicconnect.presentation.components.ErrorView
import com.civicconnect.presentation.components.LoadingView
import com.civicconnect.presentation.screens.home.StatCard
import com.civicconnect.presentation.screens.home.StatItem

import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToManagement: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Dashboard", fontWeight = FontWeight.Black) },
                actions = {
                    IconButton(onClick = onNavigateToChat) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI Assistant")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (state) {
                is AdminDashboardState.Loading -> LoadingView()
                is AdminDashboardState.Success -> {
                    val stats = (state as AdminDashboardState.Success).stats
                    AdminDashboardContent(
                        stats = stats,
                        onManage = onNavigateToManagement,
                        onInsights = onNavigateToInsights,
                        onMap = onNavigateToMap
                    )
                }
                is AdminDashboardState.Error -> {
                    ErrorView(
                        message = (state as AdminDashboardState.Error).message,
                        onRetry = viewModel::loadDashboard
                    )
                }
            }
        }
    }
}

@Composable
fun AdminDashboardContent(
    stats: com.civicconnect.domain.model.admin.AdminStats,
    onManage: () -> Unit,
    onInsights: () -> Unit,
    onMap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val statItems = listOf(
            StatItem("Total", stats.totalComplaints, Icons.Default.Assessment, MaterialTheme.colorScheme.primary),
            StatItem("Pending", stats.pending, Icons.Default.Pending, Color(0xFFF57C00)),
            StatItem("Active", stats.inProgress, Icons.Default.Autorenew, Color(0xFF1976D2)),
            StatItem("Solved", stats.resolved, Icons.Default.CheckCircle, Color(0xFF388E3C)),
            StatItem("Rejected", stats.rejected, Icons.Default.Cancel, Color(0xFFD32F2F)),
            StatItem("Users", stats.totalUsers, Icons.Default.People, MaterialTheme.colorScheme.tertiary)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(statItems) { item ->
                StatCard(item)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdminActionButton(
                label = "Complaints",
                icon = Icons.Default.List,
                onClick = onManage,
                modifier = Modifier.weight(1f)
            )
            AdminActionButton(
                label = "Insights",
                icon = Icons.Default.BarChart,
                onClick = onInsights,
                modifier = Modifier.weight(1f)
            )
            AdminActionButton(
                label = "Map",
                icon = Icons.Default.Map,
                onClick = onMap,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AdminActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}
