package com.civicconnect.domain.usecase.auth

import com.civicconnect.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<String?> {
        return repository.getSession()
    }
}
