package com.civicconnect.domain.usecase.admin

import com.civicconnect.domain.model.Complaint
import com.civicconnect.domain.repository.AdminRepository
import javax.inject.Inject

class UpdateComplaintStatusUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(id: String, status: String): Result<Complaint> {
        return repository.updateStatus(id, status)
    }
}
