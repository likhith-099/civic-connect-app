package com.civicconnect.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.civicconnect.data.local.dao.ComplaintDao
import com.civicconnect.data.local.entities.ComplaintEntity
import com.civicconnect.data.local.entities.OfflineComplaintEntity

@Database(entities = [ComplaintEntity::class, OfflineComplaintEntity::class], version = 1, exportSchema = false)
abstract class CivicConnectDatabase : RoomDatabase() {
    abstract val complaintDao: ComplaintDao
}
