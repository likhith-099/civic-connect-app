package com.civicconnect.domain.repository

import com.civicconnect.data.dto.auth.LoginRequest
import com.civicconnect.domain.model.Complaint
import com.civicconnect.domain.model.User
import com.civicconnect.domain.model.admin.AdminAnalysis
import com.civicconnect.domain.model.admin.AdminInsight
import com.civicconnect.domain.model.admin.AdminStats
import com.civicconnect.domain.model.admin.AdminChatMessage

interface AdminRepository {
    suspend fun login(request: LoginRequest): Result<User>
    suspend fun register(request: com.civicconnect.data.dto.auth.AdminRegisterRequest): Result<User>
    suspend fun getAllComplaints(): Result<List<Complaint>>
    suspend fun updateStatus(id: String, status: String): Result<Complaint>
    suspend fun updateComplaint(id: String, complaint: Complaint): Result<Complaint>
    suspend fun deleteComplaint(id: String): Result<Unit>
    suspend fun getAdminStats(): Result<AdminStats>
    suspend fun getInsights(): Result<AdminInsight>
    suspend fun analyzeComplaint(id: String): Result<AdminAnalysis>
    suspend fun chatWithAi(message: String): Result<AdminChatMessage>
}
