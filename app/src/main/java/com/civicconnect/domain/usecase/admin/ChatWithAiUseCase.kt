package com.civicconnect.domain.usecase.admin

import com.civicconnect.domain.model.admin.AdminChatMessage
import com.civicconnect.domain.repository.AdminRepository
import javax.inject.Inject

class ChatWithAiUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(message: String): Result<AdminChatMessage> {
        return repository.chatWithAi(message)
    }
}
