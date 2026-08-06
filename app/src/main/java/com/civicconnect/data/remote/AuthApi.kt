package com.civicconnect.data.remote

import com.civicconnect.data.dto.auth.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getCurrentUser(): Response<UserDto>

    @PUT("api/auth/profile")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): Response<ProfileUpdateResponse>
}
