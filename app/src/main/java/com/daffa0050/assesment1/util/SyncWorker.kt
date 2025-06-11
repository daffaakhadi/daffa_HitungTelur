package com.daffa0050.assesment1.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.daffa0050.assesment1.database.PemesananDb
import com.daffa0050.assesment1.network.PemesananRepository
import com.daffa0050.assesment1.network.TelurApiService

class SyncWorker(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {

    private val repository: PemesananRepository by lazy {
        val pemesananDao = PemesananDb.getDatabase(applicationContext).pemesananDao()
        val apiService = TelurApiService.create()
        PemesananRepository(apiService, pemesananDao, applicationContext)
    }

    override suspend fun doWork(): Result {
        try {
            val unsyncedList = repository.dao.getUnsyncedPemesanan()

            if (unsyncedList.isEmpty()) {
                return Result.success()
            }

            unsyncedList.forEach { pemesananToSync ->
                val response = repository.tambahPemesananApiOnly(pemesananToSync)

                if (response.status == "200" && response.data != null) {
                    repository.dao.delete(pemesananToSync)
                    val syncedData = response.data.copy(userId = pemesananToSync.userId, isSynced = true)
                    repository.dao.insert(syncedData)
                }
            }
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }
}