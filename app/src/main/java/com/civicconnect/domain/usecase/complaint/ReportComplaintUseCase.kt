package com.civicconnect.domain.usecase.complaint

import com.civicconnect.domain.model.Complaint
import com.civicconnect.domain.repository.ComplaintRepository
import java.io.File
import javax.inject.Inject

class ReportComplaintUseCase @Inject constructor(
    private val repository: ComplaintRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        category: String,
        severity: String,
        latitude: Double,
        longitude: Double,
        address: String,
        imageFile: File
    ): Result<Complaint> {
        return repository.reportComplaint(
            title, description, category, severity,
            latitude, longitude, address, imageFile
        )
    }
}
