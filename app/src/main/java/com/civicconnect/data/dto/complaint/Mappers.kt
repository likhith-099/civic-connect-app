package com.civicconnect.data.dto.complaint

import com.civicconnect.data.local.entities.ComplaintEntity
import com.civicconnect.domain.model.*
import com.civicconnect.utils.Constants

fun ComplaintDto.toDomain() = Complaint(
    id = id,
    title = title,
    description = description,
    category = (category.orEmpty()).replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
    severity = severity ?: "medium",
    status = status,
    imageUrl = imageUrl.resolveImageUrl(),
    location = Location(
        latitude = geo?.latitude ?: 0.0,
        longitude = geo?.longitude ?: 0.0,
        address = location.orEmpty().ifBlank { null }
    ),
    votes = votes,
    createdAt = createdAt,
    aiAnalysis = aiAnalysis?.let {
        AIAnalysis(
            summary = it.summary ?: "",
            confidence = it.confidence ?: 0.0,
            urgencyScore = it.urgencyScore ?: 0
        )
    },
    timeline = timeline?.map {
        TimelineEvent(
            status = it.status,
            description = it.description,
            timestamp = it.timestamp
        )
    }
)

fun Complaint.toEntity() = ComplaintEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    severity = severity,
    status = status,
    imageUrl = imageUrl,
    latitude = location.latitude,
    longitude = location.longitude,
    address = location.address,
    votes = votes,
    createdAt = createdAt
)

fun Complaint.toDto(userId: String) = ComplaintDto(
    id = id,
    title = title,
    description = description,
    location = location.address ?: "",
    geo = GeoDto(location.latitude, location.longitude),
    category = category.lowercase(),
    severity = severity.lowercase(),
    status = status,
    imageUrl = imageUrl,
    votes = votes,
    createdAt = createdAt,
    userId = userId
)

fun ComplaintEntity.toDomain() = Complaint(
    id = id,
    title = title,
    description = description,
    category = category.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
    severity = severity,
    status = status,
    imageUrl = imageUrl,
    location = Location(latitude, longitude, address?.ifBlank { null }),
    votes = votes,
    createdAt = createdAt
)

private fun String.capitalize() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

/**
 * Converts a potentially relative image path from the backend into an absolute URL.
 * If the URL is already absolute (starts with http/https), it is returned as-is.
 * Otherwise it is resolved against the API base URL.
 */
private fun String?.resolveImageUrl(): String? {
    if (isNullOrBlank()) return null
    return if (startsWith("http://") || startsWith("https://")) {
        this
    } else {
        val base = Constants.BASE_URL.trimEnd('/')
        val path = trimStart('/')
        "$base/$path"
    }
}
