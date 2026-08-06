package com.civicconnect.data.dto.auth

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val profilePicture: String? = null,
    @SerializedName("municipal_area") val municipalArea: String? = null
)

// Matches POST /api/auth/login and POST /api/auth/register
data class AuthResponse(
    @SerializedName("id") val id: String,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("municipal_area") val municipalArea: String? = null,
    val token: String
)

// Matches PUT /api/auth/profile
data class ProfileUpdateResponse(
    val message: String,
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("municipal_area") val municipalArea: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("municipal_area") val municipalArea: String? = null
)

data class AdminRegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("municipal_office") val municipalOffice: String,
    val region: String
)

data class ProfileUpdateRequest(
    val name: String? = null,
    val email: String? = null,
    @SerializedName("municipal_area") val municipalArea: String? = null
)
