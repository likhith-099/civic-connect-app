package com.civicconnect.data.remote

import com.civicconnect.data.dto.admin.*
import com.civicconnect.data.dto.auth.LoginRequest
import com.civicconnect.data.dto.complaint.ComplaintDto
import retrofit2.Response
import retrofit2.http.*

interface AdminApi {

    @POST("api/admin/register")
    suspend fun register(@Body request: com.civicconnect.data.dto.auth.AdminRegisterRequest): Response<AdminAuthResponse>

    @POST("api/admin/login")
    suspend fun login(@Body request: LoginRequest): Response<AdminAuthResponse>

    @GET("api/complaints")
    suspend fun getAllComplaints(): Response<List<ComplaintDto>>

    @POST("api/complaints/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: String,
        @Body statusRequest: Map<String, String>
    ): Response<ComplaintDto>

    @PUT("api/complaints/{id}")
    suspend fun updateComplaint(
        @Path("id") id: String,
        @Body complaint: ComplaintDto
    ): Response<ComplaintDto>

    @DELETE("api/complaints/{id}")
    suspend fun deleteComplaint(@Path("id") id: String): Response<Unit>

    @GET("api/ai-admin/insights")
    suspend fun getInsights(): Response<AdminInsightDto>

    @POST("api/ai-admin/analyze/{id}")
    suspend fun analyzeComplaint(@Path("id") id: String): Response<AdminAnalysisDto>

    @POST("api/ai-admin/chat")
    suspend fun chatWithAi(@Body request: AdminChatRequest): Response<AdminChatResponse>
}
