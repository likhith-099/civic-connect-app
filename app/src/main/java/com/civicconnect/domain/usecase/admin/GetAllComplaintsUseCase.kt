package com.civicconnect.domain.usecase.admin

import com.civicconnect.domain.model.Complaint
import com.civicconnect.domain.repository.AdminRepository
import javax.inject.Inject

class GetAllComplaintsUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(): Result<List<Complaint>> {
        return repository.getAllComplaints()
    }
}
