package com.civicconnect.data.dto.ai

import com.google.gson.annotations.SerializedName

data class AiClassifyResponse(
    val title: String? = null,
    val category: String? = null,
    val classified: Boolean? = null,
    @SerializedName("suggestedTitle") val suggestedTitle: String? = null,
    @SerializedName("suggested_title") val suggestedTitleSnake: String? = null,
    val source: String? = null,
    val suggestions: List<String>? = null
)

data class AiGenerateRequest(
    val title: String,
    val category: String,
    val severity: String,
    val location: String,
    @SerializedName("exactLocationNote") val exactLocationNote: String,
    val latitude: Double,
    val longitude: Double
)

data class AiGenerateResponse(
    val text: String? = null
)
