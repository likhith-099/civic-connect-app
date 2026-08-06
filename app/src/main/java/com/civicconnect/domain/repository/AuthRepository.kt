package com.civicconnect.domain.repository

import com.civicconnect.data.dto.auth.LoginRequest
import com.civicconnect.data.dto.auth.RegisterRequest
import com.civicconnect.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<User>
    suspend fun register(request: RegisterRequest): Result<User>
    suspend fun logout()
    fun getSession(): Flow<String?>
    fun getRole(): Flow<String?>
    suspend fun getCurrentUser(): Result<User>
    suspend fun updateProfile(name: String, email: String, municipalArea: String): Result<User>
}
