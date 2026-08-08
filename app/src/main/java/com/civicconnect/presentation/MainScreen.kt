package com.civicconnect.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.civicconnect.domain.usecase.auth.GetSessionUseCase
import com.civicconnect.domain.usecase.auth.GetUserRoleUseCase
import com.civicconnect.presentation.components.ConnectivityStatus
import com.civicconnect.presentation.navigation.BottomNavItem
import com.civicconnect.presentation.navigation.NavGraph
import com.civicconnect.presentation.navigation.Screen
import com.civicconnect.utils.ConnectivityObserver

@Composable
fun MainScreen(
    getSessionUseCase: GetSessionUseCase,
    getUserRoleUseCase: GetUserRoleUseCase,
    connectivityObserver: ConnectivityObserver
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val networkStatus by connectivityObserver.observe().collectAsState(initial = ConnectivityObserver.Status.Unavailable)

    val bottomBarScreens = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Community.route,
        BottomNavItem.Report.route,
        BottomNavItem.Profile.route
    )
    val showBottomBar = currentDestination?.route in bottomBarScreens

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                NavigationBar(
                    tonalElevation = 8.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Community,
                        BottomNavItem.Report,
                        BottomNavItem.Profile
                    )
                    items.forEach { item ->
                        val route = item.route
                        val selected = currentDestination?.hierarchy?.any { it.route == route } == true
                        
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                ) 
                            },
                            label = { 
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                                ) 
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ConnectivityStatus(isOnline = networkStatus == ConnectivityObserver.Status.Available)
            Surface(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.background
            ) {
                NavGraph(
                    navController = navController,
                    getSessionUseCase = getSessionUseCase,
                    getUserRoleUseCase = getUserRoleUseCase
                )
            }
        }
    }
}
