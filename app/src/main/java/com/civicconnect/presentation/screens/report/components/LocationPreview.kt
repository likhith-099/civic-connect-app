package com.civicconnect.presentation.screens.report.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun LocationPreview(
    latitude: Double,
    longitude: Double,
    address: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Location",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            val currentLatLng = LatLng(latitude, longitude)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
            }
            
            LaunchedEffect(latitude, longitude) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                Marker(
                    state = rememberMarkerState(position = currentLatLng),
                    title = "Complaint Location"
                )
            }
        }
        Text(
            text = address.ifBlank { "Detecting location..." },
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
