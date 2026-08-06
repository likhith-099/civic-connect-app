package com.civicconnect.data.remote

import com.civicconnect.data.dto.complaint.ComplaintDto
import retrofit2.Response
import retrofit2.http.*

interface ComplaintApi {

    @GET("api/complaints")
    suspend fun getComplaints(): Response<List<ComplaintDto>>

    @GET("api/complaints/{id}")
    suspend fun getComplaintById(@Path("id") id: String): Response<ComplaintDto>

    @POST("api/complaints/{id}/upvote")
    suspend fun upvoteComplaint(@Path("id") id: String): Response<ComplaintDto>

    @GET("api/complaints/my-complaints")
    suspend fun getMyComplaints(): Response<List<ComplaintDto>>

    @Multipart
    @POST("api/complaints")
    suspend fun reportComplaint(
        @Part("title") title: okhttp3.RequestBody,
        @Part("description") description: okhttp3.RequestBody,
        @Part("category") category: okhttp3.RequestBody,
        @Part("severity") severity: okhttp3.RequestBody,
        @Part("latitude") latitude: okhttp3.RequestBody,
        @Part("longitude") longitude: okhttp3.RequestBody,
        @Part("location") location: okhttp3.RequestBody,
        @Part image: okhttp3.MultipartBody.Part
    ): Response<ComplaintDto>
}
