package com.civicconnect.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("Home", Screen.Home.route, Icons.Default.Home)
    object Community : BottomNavItem("Community", "community", Icons.Default.Groups)
    object Report : BottomNavItem("Report", "report", Icons.Default.AddCircle)
    object Profile : BottomNavItem("Profile", Screen.Profile.route, Icons.Default.Person)
}
