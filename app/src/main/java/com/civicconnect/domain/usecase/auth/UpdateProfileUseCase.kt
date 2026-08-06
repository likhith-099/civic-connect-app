package com.civicconnect.domain.usecase.auth

import com.civicconnect.domain.model.User
import com.civicconnect.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, municipalArea: String): Result<User> {
        return repository.updateProfile(name, email, municipalArea)
    }
}
