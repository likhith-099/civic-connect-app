package com.civicconnect.presentation.screens.report

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicconnect.domain.usecase.ai.ClassifyImageUseCase
import com.civicconnect.domain.usecase.ai.GenerateDescriptionUseCase
import com.civicconnect.domain.usecase.complaint.ReportComplaintUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class ReportState(
    val title: String = "",
    val description: String = "",
    val category: String = "Roads", // Matches dropdown option
    val severity: String = "Medium",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val imageFile: File? = null,
    val isLoading: Boolean = false,
    val isAiClassifying: Boolean = false,
    val isAiGenerating: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportComplaintUseCase: ReportComplaintUseCase,
    private val classifyImageUseCase: ClassifyImageUseCase,
    private val generateDescriptionUseCase: GenerateDescriptionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state

    fun onTitleChange(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun onDescriptionChange(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun onCategoryChange(category: String) {
        _state.update { it.copy(category = category) }
    }

    fun onSeverityChange(severity: String) {
        _state.update { it.copy(severity = severity) }
    }

    fun onLocationUpdate(lat: Double, lng: Double, address: String) {
        _state.update { it.copy(latitude = lat, longitude = lng, address = address) }
    }

    fun onImageCaptured(file: File) {
        _state.update { it.copy(imageFile = file) }
        classifyImage(file)
    }

    fun onImageSelected(context: Context, uri: Uri) {
        viewModelScope.launch {
            val file = withContext(Dispatchers.IO) {
                uriToFile(context, uri)
            }
            if (file != null) {
                onImageCaptured(file)
            } else {
                _state.update { it.copy(error = "Failed to process selected image") }
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            null
        }
    }

    private fun classifyImage(file: File) {
        viewModelScope.launch {
            _state.update { it.copy(isAiClassifying = true, error = null) }
            val result = classifyImageUseCase(file)
            result.onSuccess { response ->
                val suggestedTitle = response.suggestedTitle ?: response.suggestedTitleSnake ?: response.title
                val rawCategory = response.category ?: ""
                
                // Match rawCategory with available options in UI
                val matchedCategory = when (rawCategory.lowercase()) {
                    "road", "roads", "pothole" -> "Roads"
                    "water", "leak", "leakage", "pipe" -> "Water"
                    "electricity", "power", "light", "wire" -> "Electricity"
                    "waste", "garbage", "trash", "rubbish" -> "Waste"
                    "health", "medical", "hospital", "sanitation", "sewage", "drainage" -> "Sanitation"
                    else -> "Other"
                }

                _state.update { currentState ->
                    currentState.copy(
                        isAiClassifying = false,
                        title = suggestedTitle ?: currentState.title,
                        category = if (rawCategory.isNotBlank()) matchedCategory else currentState.category
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(isAiClassifying = false, error = "AI Classification failed: ${e.message}") }
            }
        }
    }

    fun generateDescription() {
        val currentState = _state.value
        if (currentState.title.isBlank()) {
            _state.update { it.copy(error = "Please provide a title first") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isAiGenerating = true, error = null) }
            val result = generateDescriptionUseCase(
                title = currentState.title,
                category = currentState.category,
                severity = currentState.severity,
                location = currentState.address,
                exactLocationNote = "",
                latitude = currentState.latitude,
                longitude = currentState.longitude
            )
            result.onSuccess { description ->
                _state.update { it.copy(isAiGenerating = false, description = description) }
            }.onFailure { e ->
                _state.update { it.copy(isAiGenerating = false, error = "AI Description Generation failed: ${e.message}") }
            }
        }
    }

    fun submitReport() {
        val currentState = _state.value
        if (currentState.title.isBlank() || currentState.description.isBlank() || currentState.imageFile == null) {
            _state.update { it.copy(error = "Please fill all fields and capture an image") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = reportComplaintUseCase(
                title = currentState.title,
                description = currentState.description,
                category = currentState.category,
                severity = currentState.severity,
                latitude = currentState.latitude,
                longitude = currentState.longitude,
                address = currentState.address,
                imageFile = currentState.imageFile
            )

            result.onSuccess {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to report complaint") }
            }
        }
    }

    fun resetState() {
        _state.update { ReportState() }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
