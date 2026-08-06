package com.civicconnect.data.remote

import com.civicconnect.data.dto.ai.AiClassifyResponse
import com.civicconnect.data.dto.ai.AiGenerateRequest
import com.civicconnect.data.dto.ai.AiGenerateResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface AiApi {

    @Multipart
    @POST("api/ai/classify")
    suspend fun classifyImage(
        @Part image: MultipartBody.Part
    ): Response<AiClassifyResponse>

    @POST("api/ai/generate")
    suspend fun generateDescription(
        @Body request: AiGenerateRequest
    ): Response<AiGenerateResponse>
}
