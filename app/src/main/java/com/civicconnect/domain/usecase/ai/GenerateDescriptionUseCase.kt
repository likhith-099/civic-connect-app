package com.civicconnect.domain.usecase.ai

import com.civicconnect.domain.repository.ComplaintRepository
import javax.inject.Inject

class GenerateDescriptionUseCase @Inject constructor(
    private val repository: ComplaintRepository
) {
    suspend operator fun invoke(
        title: String,
        category: String,
        severity: String,
        location: String,
        exactLocationNote: String,
        latitude: Double,
        longitude: Double
    ): Result<String> {
        return repository.generateDescription(title, category, severity, location, exactLocationNote, latitude, longitude)
    }
}
