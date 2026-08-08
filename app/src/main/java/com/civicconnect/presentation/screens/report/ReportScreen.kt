package com.civicconnect.presentation.screens.report

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (locationGranted) {
            getCurrentLocation(context) { lat, lng, address ->
                viewModel.onLocationUpdate(lat, lng, address)
            }
        }
    }

    LaunchedEffect(Unit) {
        val permissions = buildList {
            add(Manifest.permission.CAMERA)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
        permissionLauncher.launch(permissions.toTypedArray())
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
                TopAppBar(
                    title = { Text("Report Civic Issue", fontWeight = FontWeight.Black) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                // Step 1: Visual Evidence
                SectionHeader(title = "1. VISUAL EVIDENCE", subtitle = "Attach photo to allow AI auto-classification")

                Spacer(modifier = Modifier.height(10.dp))

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
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
                                    color = Color.Black.copy(alpha = 0.65f)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            "AI analyzing photo...",
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AddAPhoto,
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Take or select photo of issue",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showCamera = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Take Photo", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Gallery", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(20.dp))

                // Step 2: Issue Details
                SectionHeader(title = "2. ISSUE DETAILS", subtitle = "Specify category, title, and description")

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
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(20.dp))

                // Step 3: Location Preview
                SectionHeader(title = "3. INCIDENT LOCATION", subtitle = "Auto-detected GPS position")

                Spacer(modifier = Modifier.height(12.dp))

                LocationPreview(
                    latitude = state.latitude,
                    longitude = state.longitude,
                    address = state.address
                )

                Spacer(modifier = Modifier.height(32.dp))

                CivicConnectButton(
                    text = "Submit Official Report",
                    onClick = viewModel::submitReport,
                    isLoading = state.isLoading,
                    icon = Icons.Default.Send
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (state.isSuccess) {
                    AlertDialog(
                        onDismissRequest = { viewModel.resetState(); onReportSuccess() },
                        title = { Text("Report Successfully Submitted", fontWeight = FontWeight.Bold) },
                        text = { Text("Thank you for helping keep your community safe and clean. Your report has been dispatched to municipal authorities.") },
                        confirmButton = {
                            Button(
                                onClick = { viewModel.resetState(); onReportSuccess() },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Return to Home")
                            }
                        },
                        shape = RoundedCornerShape(24.dp)
                    )
                }

                if (state.error != null) {
                    AlertDialog(
                        onDismissRequest = viewModel::clearError,
                        title = { Text("Submission Error", fontWeight = FontWeight.Bold) },
                        text = { Text(state.error!!) },
                        confirmButton = {
                            TextButton(onClick = viewModel::clearError) {
                                Text("OK")
                            }
                        },
                        shape = RoundedCornerShape(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
