package com.civicconnect.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Community : Screen("community")
    object Report : Screen("report")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object AdminLogin : Screen("admin_login")
    object AdminDashboard : Screen("admin_dashboard")
    object AdminComplaints : Screen("admin_complaints")
    object AdminInsights : Screen("admin_insights")
    object AdminChat : Screen("admin_chat")
    object AdminMap : Screen("admin_map")
    object ComplaintDetails : Screen("complaint_details/{complaintId}") {
        fun createRoute(complaintId: String) = "complaint_details/$complaintId"
    }
}
