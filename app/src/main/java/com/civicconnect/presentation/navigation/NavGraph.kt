package com.civicconnect.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.civicconnect.presentation.screens.auth.AuthViewModel
import com.civicconnect.presentation.screens.community.CommunityScreen
import com.civicconnect.presentation.screens.admin.chat.AdminAiChatScreen
import com.civicconnect.presentation.screens.admin.dashboard.AdminDashboardScreen
import com.civicconnect.presentation.screens.admin.insights.AdminAiInsightsScreen
import com.civicconnect.presentation.screens.admin.login.AdminLoginScreen
import com.civicconnect.presentation.screens.admin.management.AdminComplaintManagementScreen
import com.civicconnect.presentation.screens.admin.map.AdminMapScreen
import com.civicconnect.presentation.screens.complaint.ComplaintDetailsScreen
import com.civicconnect.presentation.screens.complaint.MyComplaintsScreen
import com.civicconnect.presentation.screens.home.HomeScreen
import com.civicconnect.presentation.screens.login.LoginScreen
import com.civicconnect.presentation.screens.profile.ProfileScreen
import com.civicconnect.presentation.screens.settings.SettingsScreen
import com.civicconnect.presentation.screens.register.RegisterScreen
import com.civicconnect.presentation.screens.report.ReportScreen
import com.civicconnect.presentation.screens.splash.SplashScreen
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.civicconnect.domain.usecase.auth.GetSessionUseCase
import com.civicconnect.domain.usecase.auth.GetUserRoleUseCase
import kotlinx.coroutines.flow.first

@Composable
fun NavGraph(
    navController: NavHostController,
    getSessionUseCase: GetSessionUseCase,
    getUserRoleUseCase: GetUserRoleUseCase
) {
    val session by getSessionUseCase().collectAsState(initial = null)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen()
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                val currentSession = getSessionUseCase().first()
                val role = getUserRoleUseCase().first()
                
                if (!currentSession.isNullOrBlank()) {
                    if (role?.lowercase() == "admin") {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                } else {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToAdmin = { navController.navigate(Screen.AdminLogin.route) }
            )
        }
        composable(Screen.AdminLogin.route) {
            AdminLoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.AdminLogin.route) { inclusive = true }
                    }
                },
                onNavigateToCitizenLogin = { navController.popBackStack() }
            )
        }
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onNavigateToManagement = { navController.navigate(Screen.AdminComplaints.route) },
                onNavigateToInsights = { navController.navigate(Screen.AdminInsights.route) },
                onNavigateToChat = { navController.navigate(Screen.AdminChat.route) },
                onNavigateToMap = { navController.navigate(Screen.AdminMap.route) }
            )
        }
        composable(Screen.AdminComplaints.route) {
            AdminComplaintManagementScreen(
                onNavigateToDetails = { complaintId ->
                    navController.navigate(Screen.ComplaintDetails.createRoute(complaintId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdminInsights.route) {
            AdminAiInsightsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.AdminChat.route) {
            AdminAiChatScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.AdminMap.route) {
            AdminMapScreen(
                onNavigateToDetails = { complaintId ->
                    navController.navigate(Screen.ComplaintDetails.createRoute(complaintId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(onProfileClick = { navController.navigate(Screen.Profile.route) })
        }
        composable(Screen.Community.route) {
            CommunityScreen(
                onNavigateToDetails = { complaintId ->
                    navController.navigate(Screen.ComplaintDetails.createRoute(complaintId))
                }
            )
        }
        composable(Screen.Community.route + "_my") {
             MyComplaintsScreen(
                onNavigateToDetails = { complaintId ->
                    navController.navigate(Screen.ComplaintDetails.createRoute(complaintId))
                }
            )
        }
        composable(
            route = Screen.ComplaintDetails.route,
            arguments = listOf(navArgument("complaintId") { type = NavType.StringType })
        ) {
            ComplaintDetailsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Report.route) {
            ReportScreen(
                onReportSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Report.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
