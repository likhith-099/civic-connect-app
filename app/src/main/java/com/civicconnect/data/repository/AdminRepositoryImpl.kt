package com.civicconnect.data.repository

import com.civicconnect.data.dto.admin.*
import com.civicconnect.data.dto.auth.LoginRequest
import com.civicconnect.data.dto.auth.toDomain
import com.civicconnect.data.dto.complaint.toDomain
import com.civicconnect.data.dto.complaint.toDto
import com.civicconnect.data.local.TokenManager
import com.civicconnect.data.remote.AdminApi
import com.civicconnect.domain.model.Complaint
import com.civicconnect.domain.model.User
import com.civicconnect.domain.model.admin.*
import com.civicconnect.domain.repository.AdminRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val api: AdminApi,
    private val tokenManager: TokenManager
) : AdminRepository {

    override suspend fun login(request: LoginRequest): Result<User> {
        return try {
            val response = api.login(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveSession(authResponse.token, authResponse.role)
                Result.success(authResponse.toDomain())
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(request: com.civicconnect.data.dto.auth.AdminRegisterRequest): Result<User> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveSession(authResponse.token, authResponse.role)
                Result.success(authResponse.toDomain())
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllComplaints(): Result<List<Complaint>> {
        return try {
            val response = api.getAllComplaints()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStatus(id: String, status: String): Result<Complaint> {
        return try {
            val response = api.updateStatus(id, mapOf("status" to status))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateComplaint(id: String, complaint: Complaint): Result<Complaint> {
        return try {
            val response = api.updateComplaint(id, complaint.toDto(userId = "")) 
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteComplaint(id: String): Result<Unit> {
        return try {
            val response = api.deleteComplaint(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAdminStats(): Result<AdminStats> {
        return try {
            val result = getAllComplaints()
            if (result.isSuccess) {
                val complaints = result.getOrNull() ?: emptyList()
                Result.success(AdminStats(
                    totalComplaints = complaints.size,
                    pending = complaints.count { it.status.lowercase() == "pending" },
                    inProgress = complaints.count { it.status.lowercase() == "in progress" },
                    resolved = complaints.count { it.status.lowercase() == "resolved" },
                    rejected = complaints.count { it.status.lowercase() == "rejected" },
                    totalUsers = 0 
                ))
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getInsights(): Result<AdminInsight> {
        return try {
            val response = api.getInsights()
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!.insights
                Result.success(AdminInsight(
                    trends = dto.trends.map { TrendPoint(it, 0) },
                    hotspots = dto.hotspots.map { hotspotStr ->
                        val parts = hotspotStr.split(":")
                        val area = parts.firstOrNull()?.trim() ?: hotspotStr
                        val count = parts.getOrNull(1)?.replace(Regex("[^0-9]"), "")?.toIntOrNull() ?: 1
                        Hotspot(area, count, 0.0, 0.0)
                    },
                    urgentIssues = dto.urgentIssues.size,
                    recommendations = dto.recommendations
                ))
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun analyzeComplaint(id: String): Result<AdminAnalysis> {
        return try {
            val response = api.analyzeComplaint(id)
            if (response.isSuccessful && response.body() != null) {
                val analysis = response.body()!!.analysis
                val aiAnalysis = analysis.aiAnalysis
                val aiInsights = analysis.aiInsights
                Result.success(AdminAnalysis(
                    aiSummary = aiInsights.summary ?: "No summary generated",
                    priority = aiAnalysis.suggestedSeverity ?: "medium",
                    recommendedAction = aiAnalysis.suggestedActions?.joinToString(", ") ?: "No recommended action",
                    estimatedResolutionTime = aiInsights.estimatedResolutionTime ?: "3-5 days"
                ))
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun chatWithAi(message: String): Result<AdminChatMessage> {
        return try {
            // Simplified chat for now - sending only last message
            val request = AdminChatRequest(
                messages = listOf(AdminChatHistoryDto(role = "user", content = message)),
                sessionId = "mobile-session-" + UUID.randomUUID().toString(),
                context = emptyMap()
            )
            val response = api.chatWithAi(request)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Result.success(AdminChatMessage(
                    content = dto.response ?: "No response",
                    isFromAi = true,
                    timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                ))
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
