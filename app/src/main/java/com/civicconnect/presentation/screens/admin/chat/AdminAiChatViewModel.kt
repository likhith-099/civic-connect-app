package com.civicconnect.presentation.screens.admin.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicconnect.domain.model.admin.AdminChatMessage
import com.civicconnect.domain.usecase.admin.ChatWithAiUseCase
import com.civicconnect.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminAiChatViewModel @Inject constructor(
    private val chatWithAiUseCase: ChatWithAiUseCase
) : ViewModel() {

    private val _messages = MutableStateFlow<List<AdminChatMessage>>(
        listOf(AdminChatMessage("Hello Admin! I am your CivicConnect AI Assistant. How can I help you today?", true, DateTimeUtils.getCurrentTimeString()))
    )
    val messages: StateFlow<List<AdminChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        val userMessage = AdminChatMessage(content, false, DateTimeUtils.getCurrentTimeString())
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            _isLoading.value = true
            val result = chatWithAiUseCase(content)
            _isLoading.value = false
            result.onSuccess { aiMessage ->
                _messages.value = _messages.value + aiMessage
            }.onFailure {
                val errorMessage = AdminChatMessage("Error: ${it.message ?: "Failed to get AI response"}", true, DateTimeUtils.getCurrentTimeString())
                _messages.value = _messages.value + errorMessage
            }
        }
    }
}
