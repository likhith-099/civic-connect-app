package com.civicconnect.domain.usecase.admin

import com.civicconnect.domain.model.admin.AdminAnalysis
import com.civicconnect.domain.repository.AdminRepository
import javax.inject.Inject

class AnalyzeComplaintUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(id: String): Result<AdminAnalysis> {
        return repository.analyzeComplaint(id)
    }
}
