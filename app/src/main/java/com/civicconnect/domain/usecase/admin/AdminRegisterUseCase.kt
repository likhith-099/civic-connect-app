package com.civicconnect.domain.usecase.admin

import com.civicconnect.data.dto.auth.AdminRegisterRequest
import com.civicconnect.domain.model.User
import com.civicconnect.domain.repository.AdminRepository
import javax.inject.Inject

class AdminRegisterUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(request: AdminRegisterRequest): Result<User> {
        return repository.register(request)
    }
}
