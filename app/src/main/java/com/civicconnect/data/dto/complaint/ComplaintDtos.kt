package com.civicconnect.data.dto.complaint

import com.google.gson.annotations.SerializedName

data class ComplaintDto(
    @SerializedName("_id") val id: String,
    val title: String,
    val description: String,
    val location: String,
    val geo: GeoDto? = null,
    val category: String,
    val severity: String? = "medium",
    val status: String,
    @SerializedName("image") val imageUrl: String? = null,
    val votes: Int = 0,
    val createdAt: String,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("ai_analysis") val aiAnalysis: AIAnalysisDto? = null,
    val timeline: List<TimelineDto>? = null
)

data class GeoDto(
    val latitude: Double,
    val longitude: Double
)

data class AIAnalysisDto(
    @SerializedName("aiSummary") val summary: String? = null,
    val confidence: Double? = null,
    @SerializedName("urgencyScore") val urgencyScore: Int? = null,
    @SerializedName("key_issues") val keyIssues: List<String>? = null,
    @SerializedName("suggested_actions") val suggestedActions: List<String>? = null
)

data class TimelineDto(
    val status: String,
    val description: String,
    val timestamp: String
)
