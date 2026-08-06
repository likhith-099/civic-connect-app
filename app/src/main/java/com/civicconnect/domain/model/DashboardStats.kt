package com.civicconnect.domain.model

data class DashboardStats(
    val totalComplaints: Int,
    val pendingComplaints: Int,
    val inProgressComplaints: Int,
    val resolvedComplaints: Int
)
