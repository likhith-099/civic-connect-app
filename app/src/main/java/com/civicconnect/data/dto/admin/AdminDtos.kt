package com.civicconnect.data.dto.admin

import com.google.gson.annotations.SerializedName

// Matches POST /api/admin/login and POST /api/admin/register
data class AdminAuthResponse(
    val message: String,
    val token: String,
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val region: String? = null,
    @SerializedName("municipal_office") val municipalOffice: String? = null
)

data class AdminStatsDto(
    @SerializedName("total_complaints") val totalComplaints: Int,
    val pending: Int,
    @SerializedName("in_progress") val inProgress: Int,
    val resolved: Int,
    val rejected: Int,
    @SerializedName("total_users") val totalUsers: Int
)

data class AdminInsightDto(
    val success: Boolean,
    val insights: AdminInsightDataDto
)

data class AdminInsightDataDto(
    val trends: List<String>,
    val hotspots: List<String>,
    @SerializedName("urgent_issues") val urgentIssues: List<String>,
    val recommendations: List<String>
)

data class AdminAnalysisDto(
    val success: Boolean,
    val analysis: AdminAnalysisDataDto
)

data class AdminAnalysisDataDto(
    @SerializedName("ai_analysis") val aiAnalysis: AdminAiAnalysisFieldsDto,
    @SerializedName("ai_insights") val aiInsights: AdminAiInsightsDto
)

data class AdminAiAnalysisFieldsDto(
    @SerializedName("suggested_category") val suggestedCategory: String? = null,
    @SerializedName("suggested_severity") val suggestedSeverity: String? = null,
    @SerializedName("sentiment_score") val sentimentScore: Double? = null,
    @SerializedName("priority_score") val priorityScore: Int? = null,
    @SerializedName("confidence_level") val confidenceLevel: Double? = null,
    @SerializedName("key_issues") val keyIssues: List<String>? = null,
    @SerializedName("suggested_actions") val suggestedActions: List<String>? = null
)

data class AdminAiInsightsDto(
    val summary: String? = null,
    @SerializedName("urgency_reason") val urgencyReason: String? = null,
    @SerializedName("department_suggestion") val departmentSuggestion: String? = null,
    @SerializedName("estimated_resolution_time") val estimatedResolutionTime: String? = null
)

data class AdminChatRequest(
    val messages: List<AdminChatHistoryDto>,
    val sessionId: String,
    val context: Map<String, Any>
)

data class AdminChatHistoryDto(
    val role: String,
    val content: String
)

data class AdminChatResponse(
    val success: Boolean,
    val response: String? = null
)
