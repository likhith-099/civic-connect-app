package com.civicconnect.presentation.screens.admin.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.civicconnect.presentation.components.ErrorView
import com.civicconnect.presentation.components.LoadingView
import com.civicconnect.presentation.screens.admin.management.AdminManagementState
import com.civicconnect.presentation.screens.admin.management.AdminComplaintManagementViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMapScreen(
    onNavigateToDetails: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: AdminComplaintManagementViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complaint Hotspots", fontWeight = FontWeight.Bold) },
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
                    
                    val initialCenter = LatLng(1.3521, 103.8198) // Placeholder center
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(initialCenter, 11f)
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        complaints.forEach { complaint ->
                            Marker(
                                state = rememberMarkerState(position = LatLng(complaint.location.latitude, complaint.location.longitude)),
                                title = complaint.title,
                                snippet = "Status: ${complaint.status}",
                                onClick = {
                                    onNavigateToDetails(complaint.id)
                                    true
                                }
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
