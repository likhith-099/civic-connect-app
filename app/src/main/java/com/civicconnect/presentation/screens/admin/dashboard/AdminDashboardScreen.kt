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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.civicconnect.presentation.components.ErrorView
import com.civicconnect.presentation.components.LoadingView
import com.civicconnect.presentation.screens.home.StatCard
import com.civicconnect.presentation.screens.home.StatItem
import com.civicconnect.presentation.theme.InProgressColor
import com.civicconnect.presentation.theme.PendingColor
import com.civicconnect.presentation.theme.RejectedColor
import com.civicconnect.presentation.theme.ResolvedColor

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
            TopAppBar(
                title = { Text("Admin Control Center", fontWeight = FontWeight.Black) },
                actions = {
                    IconButton(onClick = onNavigateToChat) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI Assistant", tint = MaterialTheme.colorScheme.tertiary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
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
            .padding(18.dp)
    ) {
        val statItems = listOf(
            StatItem("Total Reports", stats.totalComplaints, Icons.Default.Assessment, MaterialTheme.colorScheme.primary),
            StatItem("Pending Review", stats.pending, Icons.Default.Schedule, PendingColor),
            StatItem("Active Work", stats.inProgress, Icons.Default.Autorenew, InProgressColor),
            StatItem("Resolved", stats.resolved, Icons.Default.CheckCircle, ResolvedColor),
            StatItem("Rejected", stats.rejected, Icons.Default.Cancel, RejectedColor),
            StatItem("Registered Citizens", stats.totalUsers, Icons.Default.People, MaterialTheme.colorScheme.tertiary)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(statItems) { item ->
                StatCard(item)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "OPERATIONS & ANALYTICS Shortcuts",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp),
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdminActionButton(
                label = "Management",
                icon = Icons.Default.List,
                onClick = onManage,
                modifier = Modifier.weight(1f)
            )
            AdminActionButton(
                label = "AI Insights",
                icon = Icons.Default.BarChart,
                onClick = onInsights,
                modifier = Modifier.weight(1f)
            )
            AdminActionButton(
                label = "Live Map",
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
    ElevatedButton(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}
