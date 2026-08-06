package com.civicconnect.domain.repository

import com.civicconnect.domain.model.Complaint

interface ComplaintRepository {
    suspend fun getComplaints(): Result<List<Complaint>>
    suspend fun getComplaintById(id: String): Result<Complaint>
    suspend fun upvoteComplaint(id: String): Result<Complaint>
    suspend fun getMyComplaints(): Result<List<Complaint>>
    suspend fun reportComplaint(
        title: String,
        description: String,
        category: String,
        severity: String,
        latitude: Double,
        longitude: Double,
        address: String,
        imageFile: java.io.File
    ): Result<Complaint>

    suspend fun classifyImage(imageFile: java.io.File): Result<com.civicconnect.data.dto.ai.AiClassifyResponse>
    suspend fun generateDescription(
        title: String,
        category: String,
        severity: String,
        location: String,
        exactLocationNote: String,
        latitude: Double,
        longitude: Double
    ): Result<String>
}
