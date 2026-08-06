package com.civicconnect.domain.usecase.admin

import com.civicconnect.domain.model.admin.AdminInsight
import com.civicconnect.domain.repository.AdminRepository
import javax.inject.Inject

class GetAdminInsightsUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(): Result<AdminInsight> {
        return repository.getInsights()
    }
}
