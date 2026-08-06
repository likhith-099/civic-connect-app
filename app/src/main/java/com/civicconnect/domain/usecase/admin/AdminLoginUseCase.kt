package com.civicconnect.domain.usecase.admin

import com.civicconnect.data.dto.auth.LoginRequest
import com.civicconnect.domain.model.User
import com.civicconnect.domain.repository.AdminRepository
import javax.inject.Inject

class AdminLoginUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(request: LoginRequest): Result<User> {
        return repository.login(request)
    }
}
