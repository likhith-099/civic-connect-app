package com.civicconnect.domain.usecase.complaint

import com.civicconnect.domain.model.Complaint
import com.civicconnect.domain.repository.ComplaintRepository
import javax.inject.Inject

class GetMyComplaintsUseCase @Inject constructor(
    private val repository: ComplaintRepository
) {
    suspend operator fun invoke(): Result<List<Complaint>> {
        return repository.getMyComplaints()
    }
}
