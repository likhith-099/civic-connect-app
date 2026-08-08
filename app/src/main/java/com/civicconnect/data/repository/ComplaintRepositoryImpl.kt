package com.civicconnect.data.repository

import com.civicconnect.data.dto.ai.AiClassifyResponse
import com.civicconnect.data.dto.ai.AiGenerateRequest
import com.civicconnect.data.dto.complaint.toDomain
import com.civicconnect.data.dto.complaint.toEntity
import com.civicconnect.data.local.dao.ComplaintDao
import com.civicconnect.data.local.entities.OfflineComplaintEntity
import com.civicconnect.data.remote.AiApi
import com.civicconnect.data.remote.ComplaintApi
import com.civicconnect.domain.model.Complaint
import com.civicconnect.domain.repository.ComplaintRepository
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ComplaintRepositoryImpl @Inject constructor(
    private val api: ComplaintApi,
    private val aiApi: AiApi,
    private val complaintDao: ComplaintDao
) : ComplaintRepository {

    override suspend fun getComplaints(): Result<List<Complaint>> {
        return try {
            val response = api.getComplaints()
            if (response.isSuccessful && response.body() != null) {
                val complaintsDto = response.body()!!
                val complaints = complaintsDto.map { it.toDomain() }
                // Use REPLACE strategy instead of clearing everything to preserve local-only state if any
                complaintDao.insertComplaints(complaints.map { it.toEntity() })
                Result.success(complaints)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            val localComplaints = try {
                complaintDao.getAllComplaints().first().map { it.toDomain() }
            } catch (ex: Exception) {
                emptyList<Complaint>()
            }
            if (localComplaints.isNotEmpty()) {
                Result.success(localComplaints)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getComplaintById(id: String): Result<Complaint> {
        return try {
            val response = api.getComplaintById(id)
            if (response.isSuccessful && response.body() != null) {
                val complaint = response.body()!!.toDomain()
                complaintDao.insertComplaints(listOf(complaint.toEntity()))
                Result.success(complaint)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun upvoteComplaint(id: String): Result<Complaint> {
        return try {
            val response = api.upvoteComplaint(id)
            when {
                response.isSuccessful -> {
                    // Some backends return 200 with the updated complaint body,
                    // others return 200 with an empty or non-standard body.
                    val body = response.body()
                    if (body != null) {
                        val complaint = body.toDomain()
                        complaintDao.insertComplaints(listOf(complaint.toEntity()))
                        Result.success(complaint)
                    } else {
                        // Body is null — re-fetch the complaint to get the updated vote count
                        val refetch = api.getComplaintById(id)
                        if (refetch.isSuccessful && refetch.body() != null) {
                            val complaint = refetch.body()!!.toDomain()
                            complaintDao.insertComplaints(listOf(complaint.toEntity()))
                            Result.success(complaint)
                        } else {
                            Result.failure(Exception("Upvoted, but failed to refresh: ${refetch.message()}"))
                        }
                    }
                }
                else -> {
                    // Extract JSON error message if available
                    val errorBody = response.errorBody()?.string()
                    val msg = when {
                        !errorBody.isNullOrBlank() && errorBody.contains("message") -> {
                            try {
                                org.json.JSONObject(errorBody).optString("message", response.message())
                            } catch (_: Exception) { response.message() }
                        }
                        else -> response.message()
                    }
                    Result.failure(Exception(msg))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyComplaints(): Result<List<Complaint>> {
        return try {
            val response = api.getMyComplaints()
            if (response.isSuccessful && response.body() != null) {
                val complaints = response.body()!!.map { it.toDomain() }
                complaintDao.insertComplaints(complaints.map { it.toEntity() })
                Result.success(complaints)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reportComplaint(
        title: String,
        description: String,
        category: String,
        severity: String,
        latitude: Double,
        longitude: Double,
        address: String,
        imageFile: File
    ): Result<Complaint> {
        return try {
            val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryPart = category.toRequestBody("text/plain".toMediaTypeOrNull())
            val severityPart = severity.toRequestBody("text/plain".toMediaTypeOrNull())
            val latitudePart = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val longitudePart = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val addressPart = address.toRequestBody("text/plain".toMediaTypeOrNull())

            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            val response = api.reportComplaint(
                titlePart, descriptionPart, categoryPart, severityPart,
                latitudePart, longitudePart, addressPart, imagePart
            )

            if (response.isSuccessful && response.body() != null) {
                val complaintDto = response.body()!!
                val complaint = complaintDto.toDomain()
                // Store in local DB immediately so it appears in feed
                complaintDao.insertComplaints(listOf(complaint.toEntity()))
                Result.success(complaint)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            complaintDao.insertOfflineComplaint(
                OfflineComplaintEntity(
                    title = title,
                    description = description,
                    category = category,
                    severity = severity,
                    latitude = latitude,
                    longitude = longitude,
                    address = address,
                    imagePath = imageFile.absolutePath
                )
            )
            Result.failure(Exception("Offline: Saved locally. Will sync when online."))
        }
    }

    override suspend fun classifyImage(imageFile: File): Result<AiClassifyResponse> {
        return try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
            val response = aiApi.classifyImage(imagePart)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateDescription(
        title: String,
        category: String,
        severity: String,
        location: String,
        exactLocationNote: String,
        latitude: Double,
        longitude: Double
    ): Result<String> {
        return try {
            val request = AiGenerateRequest(
                title = title,
                category = category,
                severity = severity,
                location = location,
                exactLocationNote = exactLocationNote,
                latitude = latitude,
                longitude = longitude
            )
            val response = aiApi.generateDescription(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.text ?: "")
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
