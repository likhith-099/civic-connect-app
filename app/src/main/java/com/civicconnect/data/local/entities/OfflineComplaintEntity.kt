package com.civicconnect.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_complaints")
data class OfflineComplaintEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val severity: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val imagePath: String, // Store local file path
    val timestamp: Long = System.currentTimeMillis()
)
