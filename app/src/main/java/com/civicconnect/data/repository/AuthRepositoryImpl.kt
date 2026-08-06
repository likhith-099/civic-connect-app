package com.civicconnect.data.repository

import com.civicconnect.data.dto.auth.*
import com.civicconnect.data.local.TokenManager
import com.civicconnect.data.remote.AuthApi
import com.civicconnect.domain.model.User
import com.civicconnect.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

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
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(request: RegisterRequest): Result<User> {
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
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearToken()
    }

    override fun getSession(): Flow<String?> = tokenManager.token

    override fun getRole(): Flow<String?> = tokenManager.role

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = api.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(name: String, email: String, municipalArea: String): Result<User> {
        return try {
            val response = api.updateProfile(ProfileUpdateRequest(name = name, email = email, municipalArea = municipalArea))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
