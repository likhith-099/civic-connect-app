package com.civicconnect.domain.usecase.complaint

import com.civicconnect.domain.model.Complaint
import com.civicconnect.domain.repository.ComplaintRepository
import javax.inject.Inject

class UpvoteComplaintUseCase @Inject constructor(
    private val repository: ComplaintRepository
) {
    suspend operator fun invoke(id: String): Result<Complaint> {
        return repository.upvoteComplaint(id)
    }
}
