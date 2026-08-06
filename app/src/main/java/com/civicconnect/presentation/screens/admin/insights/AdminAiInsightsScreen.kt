package com.civicconnect.presentation.screens.admin.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.civicconnect.domain.model.admin.AdminInsight
import com.civicconnect.presentation.components.ErrorView
import com.civicconnect.presentation.components.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAiInsightsScreen(
    onBack: () -> Unit,
    viewModel: AdminAiInsightsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Intelligence Insights") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (state) {
                is AdminInsightsState.Loading -> LoadingView()
                is AdminInsightsState.Success -> {
                    AdminInsightsContent((state as AdminInsightsState.Success).insights)
                }
                is AdminInsightsState.Error -> {
                    ErrorView(
                        message = (state as AdminInsightsState.Error).message,
                        onRetry = viewModel::loadInsights
                    )
                }
            }
        }
    }
}

@Composable
fun AdminInsightsContent(insights: AdminInsight) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hotspot Highlight
        UrgentIssuesCard(insights.urgentIssues)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(text = "Area Hotspots", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        
        insights.hotspots.forEach { hotspot ->
            HotspotItem(hotspot)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(text = "AI Smart Recommendations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        
        insights.recommendations.forEach { recommendation ->
            RecommendationCard(recommendation)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun UrgentIssuesCard(count: Int) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.error,
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.PriorityHigh, contentDescription = null, modifier = Modifier.padding(12.dp), tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "$count Urgent Issues", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onErrorContainer)
                Text(text = "Immediate intervention recommended", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun HotspotItem(hotspot: com.civicconnect.domain.model.admin.Hotspot) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = hotspot.area, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                Text(text = "${hotspot.complaintCount} reports")
            }
        }
    }
}

@Composable
fun RecommendationCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
