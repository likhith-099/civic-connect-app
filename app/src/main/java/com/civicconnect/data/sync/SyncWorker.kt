package com.civicconnect.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker
import com.civicconnect.data.local.dao.ComplaintDao
import com.civicconnect.domain.repository.ComplaintRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val complaintDao: ComplaintDao,
    private val repository: ComplaintRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): ListenableWorker.Result {
        val pendingComplaints = complaintDao.getPendingComplaints()
        
        var allSuccess = true
        for (pending in pendingComplaints) {
            val file = File(pending.imagePath)
            if (!file.exists()) {
                complaintDao.deleteOfflineComplaint(pending)
                continue
            }
            
            val result = repository.reportComplaint(
                title = pending.title,
                description = pending.description,
                category = pending.category,
                severity = pending.severity,
                latitude = pending.latitude,
                longitude = pending.longitude,
                address = pending.address,
                imageFile = file
            )
            
            if (result.isSuccess) {
                complaintDao.deleteOfflineComplaint(pending)
            } else {
                allSuccess = false
            }
        }
        
        return if (allSuccess) ListenableWorker.Result.success() else ListenableWorker.Result.retry()
    }
}
