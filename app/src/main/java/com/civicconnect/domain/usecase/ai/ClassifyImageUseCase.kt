package com.civicconnect.domain.usecase.ai

import com.civicconnect.data.dto.ai.AiClassifyResponse
import com.civicconnect.domain.repository.ComplaintRepository
import java.io.File
import javax.inject.Inject

class ClassifyImageUseCase @Inject constructor(
    private val repository: ComplaintRepository
) {
    suspend operator fun invoke(imageFile: File): Result<AiClassifyResponse> {
        return repository.classifyImage(imageFile)
    }
}
