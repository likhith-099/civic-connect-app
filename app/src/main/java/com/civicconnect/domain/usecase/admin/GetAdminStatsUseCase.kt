package com.civicconnect.domain.usecase.admin

import com.civicconnect.domain.model.admin.AdminStats
import com.civicconnect.domain.repository.AdminRepository
import javax.inject.Inject

class GetAdminStatsUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(): Result<AdminStats> {
        return repository.getAdminStats()
    }
}
