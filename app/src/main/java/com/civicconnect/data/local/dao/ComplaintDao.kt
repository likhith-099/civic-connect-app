package com.civicconnect.data.local.dao

import androidx.room.*
import com.civicconnect.data.local.entities.ComplaintEntity
import com.civicconnect.data.local.entities.OfflineComplaintEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplaintDao {
    @Query("SELECT * FROM complaints ORDER BY createdAt DESC")
    fun getAllComplaints(): Flow<List<ComplaintEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaints(complaints: List<ComplaintEntity>)

    @Query("DELETE FROM complaints")
    suspend fun clearComplaints()

    @Insert
    suspend fun insertOfflineComplaint(complaint: OfflineComplaintEntity)

    @Query("SELECT * FROM offline_complaints ORDER BY timestamp ASC")
    suspend fun getPendingComplaints(): List<OfflineComplaintEntity>

    @Delete
    suspend fun deleteOfflineComplaint(complaint: OfflineComplaintEntity)
}
