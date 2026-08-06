package com.civicconnect.domain.model

data class Complaint(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val severity: String,
    val status: String,
    val imageUrl: String? = null,
    val location: Location,
    val votes: Int,
    val createdAt: String,
    val aiAnalysis: AIAnalysis? = null,
    val timeline: List<TimelineEvent>? = null
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)

data class AIAnalysis(
    val summary: String,
    val confidence: Double,
    val urgencyScore: Int
)

data class TimelineEvent(
    val status: String,
    val description: String,
    val timestamp: String
)
