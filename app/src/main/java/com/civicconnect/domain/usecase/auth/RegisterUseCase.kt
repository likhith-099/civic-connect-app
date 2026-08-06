package com.civicconnect.domain.usecase.auth

import com.civicconnect.data.dto.auth.RegisterRequest
import com.civicconnect.domain.model.User
import com.civicconnect.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest): Result<User> {
        return repository.register(request)
    }
}
