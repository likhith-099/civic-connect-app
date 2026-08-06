package com.civicconnect.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "complaints")
data class ComplaintEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val severity: String,
    val status: String,
    val imageUrl: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val votes: Int,
    val createdAt: String
)
