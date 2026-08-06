package com.civicconnect.presentation.screens.complaint

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.civicconnect.domain.model.Complaint
import com.civicconnect.domain.model.TimelineEvent
import com.civicconnect.presentation.components.EmptyStateView
import com.civicconnect.presentation.components.ErrorView
import com.civicconnect.presentation.components.LoadingView
import com.civicconnect.presentation.screens.community.components.StatusBadge

import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailsScreen(
    onBack: () -> Unit,
    viewModel: ComplaintDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val msg by viewModel.message.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(msg) {
        msg?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complaint Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                is ComplaintDetailsState.Loading -> LoadingView()
                is ComplaintDetailsState.Success -> {
                    val complaint = (state as ComplaintDetailsState.Success).complaint
                    ComplaintDetailsContent(complaint, viewModel::upvote)
                }
                is ComplaintDetailsState.Error -> {
                    ErrorView(
                        message = (state as ComplaintDetailsState.Error).message,
                        onRetry = { viewModel.loadComplaint() }
                    )
                }
                is ComplaintDetailsState.Empty -> EmptyStateView(message = "Complaint not found.")
            }
        }
    }
}

@Composable
fun ComplaintDetailsContent(
    complaint: Complaint,
    onUpvote: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    var previousStatus by remember { mutableStateOf(complaint.status) }
    val showStatusChangeAnimation = complaint.status != previousStatus
    
    LaunchedEffect(complaint.status) {
        if (complaint.status != previousStatus) {
            previousStatus = complaint.status
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            complaint.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = complaint.status)
                SeverityBadge(severity = complaint.severity)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = complaint.category,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = complaint.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp),
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = complaint.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ISSUE DESCRIPTION",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Text(
                text = complaint.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp),
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            val context = LocalContext.current
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    try {
                        val geoUri = "geo:${complaint.location.latitude},${complaint.location.longitude}?q=${complaint.location.latitude},${complaint.location.longitude}(${android.net.Uri.encode(complaint.title)})"
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(geoUri))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Safe catch if maps app isn't installed
                    }
                }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = complaint.location.address ?: "Location data unavailable",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 12.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // AI Analysis Section
            complaint.aiAnalysis?.let { ai ->
                AIAnalysisCard(ai)
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Timeline Section
            Text(
                text = "INVESTIGATION TIMELINE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            complaint.timeline?.let { events ->
                events.forEachIndexed { index, event ->
                    TimelineItem(
                        event = event,
                        isLast = (index == events.size - 1),
                        isNew = showStatusChangeAnimation && event.status == complaint.status
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Action Section
            Button(
                onClick = onUpvote,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.ThumbUp, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Support Issue (${complaint.votes})",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SeverityBadge(severity: String) {
    val color = when (severity.lowercase()) {
        "high", "critical" -> Color(0xFFD32F2F)
        "medium" -> Color(0xFFF57C00)
        else -> Color(0xFF388E3C)
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        contentColor = color,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = severity.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun AIAnalysisCard(ai: com.civicconnect.domain.model.AIAnalysis) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Smart AI Insights",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = ai.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Confidence Score:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = " ${(ai.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Urgency:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = " ${ai.urgencyScore}/10",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineItem(
    event: TimelineEvent,
    isLast: Boolean,
    isNew: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Surface(
                modifier = Modifier.size(16.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer)
            ) {}
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
        
        AnimatedVisibility(
            visible = true,
            enter = if (isNew) expandVertically(animationSpec = tween(500)) + fadeIn() else EnterTransition.None
        ) {
            Column(modifier = Modifier.padding(bottom = 24.dp, start = 8.dp)) {
                Text(
                    text = event.status,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = event.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
