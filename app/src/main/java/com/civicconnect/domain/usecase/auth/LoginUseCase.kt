package com.civicconnect.domain.usecase.auth

import com.civicconnect.data.dto.auth.LoginRequest
import com.civicconnect.domain.model.User
import com.civicconnect.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: LoginRequest): Result<User> {
        return repository.login(request)
    }
}
