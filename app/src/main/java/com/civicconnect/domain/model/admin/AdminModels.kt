package com.civicconnect.domain.model.admin

data class AdminStats(
    val totalComplaints: Int,
    val pending: Int,
    val inProgress: Int,
    val resolved: Int,
    val rejected: Int,
    val totalUsers: Int
)

data class AdminInsight(
    val trends: List<TrendPoint>,
    val hotspots: List<Hotspot>,
    val urgentIssues: Int,
    val recommendations: List<String>
)

data class TrendPoint(
    val date: String,
    val count: Int
)

data class Hotspot(
    val area: String,
    val complaintCount: Int,
    val latitude: Double,
    val longitude: Double
)

data class AdminAnalysis(
    val aiSummary: String,
    val priority: String,
    val recommendedAction: String,
    val estimatedResolutionTime: String
)

data class AdminChatMessage(
    val content: String,
    val isFromAi: Boolean,
    val timestamp: String
)
