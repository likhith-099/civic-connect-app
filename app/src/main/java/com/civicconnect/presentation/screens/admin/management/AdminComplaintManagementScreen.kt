package com.civicconnect.presentation.screens.admin.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.civicconnect.domain.model.Complaint
import com.civicconnect.presentation.components.ErrorView
import com.civicconnect.presentation.components.LoadingView
import com.civicconnect.presentation.screens.community.components.ComplaintCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminComplaintManagementScreen(
    onNavigateToDetails: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: AdminComplaintManagementViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val analyses by viewModel.analysisState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Complaints") },
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
                is AdminManagementState.Loading -> LoadingView()
                is AdminManagementState.Success -> {
                    val complaints = (state as AdminManagementState.Success).complaints
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(complaints) { complaint ->
                            AdminComplaintCard(
                                complaint = complaint,
                                analysis = analyses[complaint.id],
                                onStatusChange = { viewModel.updateStatus(complaint.id, it) },
                                onAnalyze = { viewModel.analyzeComplaint(complaint.id) },
                                onClick = { onNavigateToDetails(complaint.id) }
                            )
                        }
                    }
                }
                is AdminManagementState.Error -> {
                    ErrorView(
                        message = (state as AdminManagementState.Error).message,
                        onRetry = viewModel::loadComplaints
                    )
                }
            }
        }
    }
}

@Composable
fun AdminComplaintCard(
    complaint: Complaint,
    analysis: com.civicconnect.domain.model.admin.AdminAnalysis?,
    onStatusChange: (String) -> Unit,
    onAnalyze: () -> Unit,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            ComplaintCard(complaint = complaint, onClick = onClick)
            
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            
            Column(modifier = Modifier.padding(16.dp)) {
                if (analysis == null) {
                    Button(
                        onClick = onAnalyze,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Smart Analyze")
                    }
                } else {
                    AiAnalysisResult(analysis)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "Change Status", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusButton("Accepted", MaterialTheme.colorScheme.primaryContainer, onStatusChange)
                    StatusButton("In Progress", MaterialTheme.colorScheme.secondaryContainer, onStatusChange)
                    StatusButton("Resolved", Color(0xFFC8E6C9), onStatusChange)
                    StatusButton("Rejected", Color(0xFFFFCDD2), onStatusChange)
                }
            }
        }
    }
}

@Composable
fun AiAnalysisResult(analysis: com.civicconnect.domain.model.admin.AdminAnalysis) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.tertiary)
                Text(text = "AI Admin Analysis", modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = analysis.aiSummary, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Priority: ${analysis.priority}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Text(text = "ETA: ${analysis.estimatedResolutionTime}", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun StatusButton(label: String, color: Color, onClick: (String) -> Unit) {
    Surface(
        onClick = { onClick(label) },
        color = color,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
