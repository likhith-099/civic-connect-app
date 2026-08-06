package com.civicconnect.presentation.screens.report

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.civicconnect.presentation.screens.components.CivicConnectButton
import com.civicconnect.presentation.screens.report.components.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onReportSuccess: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    var showCamera by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onImageSelected(context, it)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (cameraGranted && locationGranted) {
            getCurrentLocation(context) { lat, lng, address ->
                viewModel.onLocationUpdate(lat, lng, address)
            }
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    if (showCamera) {
        CameraCaptureView(
            onImageCaptured = { file ->
                viewModel.onImageCaptured(file)
                showCamera = false
            },
            onClose = { showCamera = false }
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Report Issue", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                // Image Selection Section
                Text(
                    text = "VISUAL EVIDENCE",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (state.imageFile != null) {
                            AsyncImage(
                                model = state.imageFile,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (state.isAiClassifying) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color.Black.copy(alpha = 0.6f)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("AI analyzing issue...", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Attach photo to start",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { showCamera = true },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Camera")
                    }
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Gallery")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                Text(
                    text = "DETAILS & DESCRIPTION",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                ReportForm(
                    title = state.title,
                    onTitleChange = viewModel::onTitleChange,
                    description = state.description,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    category = state.category,
                    onCategoryChange = viewModel::onCategoryChange,
                    severity = state.severity,
                    onSeverityChange = viewModel::onSeverityChange,
                    isAiGenerating = state.isAiGenerating,
                    onGenerateDescription = viewModel::generateDescription
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "INCIDENT LOCATION",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                LocationPreview(
                    latitude = state.latitude,
                    longitude = state.longitude,
                    address = state.address
                )

                Spacer(modifier = Modifier.height(32.dp))

                CivicConnectButton(
                    text = "Submit Professional Report",
                    onClick = viewModel::submitReport,
                    isLoading = state.isLoading
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Dialogs remain functional
                if (state.isSuccess) {
                    AlertDialog(
                        onDismissRequest = { viewModel.resetState(); onReportSuccess() },
                        title = { Text("Report Submitted") },
                        text = { Text("Thank you for contributing to a better community. Your report is now under review.") },
                        confirmButton = {
                            Button(onClick = { viewModel.resetState(); onReportSuccess() }) {
                                Text("Back to Home")
                            }
                        },
                        shape = RoundedCornerShape(28.dp)
                    )
                }

                if (state.error != null) {
                    AlertDialog(
                        onDismissRequest = viewModel::clearError,
                        title = { Text("Submission Error") },
                        text = { Text(state.error!!) },
                        confirmButton = {
                            TextButton(onClick = viewModel::clearError) {
                                Text("Got it")
                            }
                        },
                        shape = RoundedCornerShape(28.dp)
                    )
                }
            }
        }
    }
}

private fun getCurrentLocation(context: Context, onLocationResult: (Double, Double, String) -> Unit) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return
    }
    try {
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { loc: android.location.Location? ->
            loc?.let {
                onLocationResult(it.latitude, it.longitude, "${it.latitude}, ${it.longitude}")
            }
        }
    } catch (e: Exception) {
        // Safe catch
    }
}
