package com.daffa0050.assesment1.network

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.daffa0050.assesment1.database.PemesananDao
import com.daffa0050.assesment1.model.OpStatus
import com.daffa0050.assesment1.model.Pemesanan
import com.daffa0050.assesment1.util.NetworkUtils
import com.daffa0050.assesment1.util.SyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

    class PemesananRepository(
        private val apiService: TelurApiService,
        val dao: PemesananDao,
        private val context: Context
    ) {

        suspend fun tambahPemesanan(pemesanan: Pemesanan, userId: String, bitmap: Bitmap?) {

            if (NetworkUtils.isOnline(context)) {
                try {
                    val imagePart = withContext(Dispatchers.IO) {
                        if (bitmap != null) {
                            val file = File(context.cacheDir, "upload.jpg")
                            val outputStream = FileOutputStream(file)
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            outputStream.write(stream.toByteArray())
                            MultipartBody.Part.createFormData("image", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull()))
                        } else {
                            MultipartBody.Part.createFormData("image", "telur.jpg", ByteArray(0).toRequestBody("image/*".toMediaTypeOrNull()))
                        }
                    }

                    val response = apiService.postPemesanan(
                        userId = userId.toRequestBody(),
                        customerName = pemesanan.customerName.toRequestBody(),
                        customerAddress = pemesanan.customerAddress.toRequestBody(),
                        purchaseType = pemesanan.purchaseType.toRequestBody(),
                        amount = pemesanan.amount.toString().toRequestBody(),
                        total = pemesanan.total.toString().toRequestBody(),
                        image = imagePart
                    )

                    if ((response.status == "200" || response.status == "201" ) && response.data != null) {
                        val serverPesanan = response.data.copy(userId = userId, isSynced = true)
                        dao.insert(serverPesanan)
                    } else {
                        throw Exception("Gagal menyimpan pesanan di server: ${response.message}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw e
                }
            } else {
                val localPemesanan = pemesanan.copy(userId = userId, isSynced = false)
                dao.insert(localPemesanan)
                scheduleSync()
            }
        }
    suspend fun updatePemesananApi(
        id: Int,
        userId: String,
        customerName: String,
        customerAddress: String,
        purchaseType: String,
        amount: Int,
        total: Int,
        bitmap: Bitmap?
    ): OpStatus {

        if (!NetworkUtils.isOnline(context)) {
            return OpStatus(
                status = "400",
                message = "Tidak ada koneksi internet untuk melakukan update.",
                data = null
            )
        }

        try {
            val userIdBody = userId.toRequestBody()
            val customerNameBody = customerName.toRequestBody()
            val customerAddressBody = customerAddress.toRequestBody()
            val purchaseTypeBody = purchaseType.toRequestBody()
            val amountBody = amount.toString().toRequestBody()
            val totalBody = total.toString().toRequestBody()
            val imagePart = bitmap?.let {
                val file = createTempFile("upload", ".jpg")
                val stream = FileOutputStream(file)
                it.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush(); stream.close()
                file.asRequestBody("image/jpeg".toMediaType())
                    .let { body -> MultipartBody.Part.createFormData("image", file.name, body) }
            }
            val response = apiService.updatePemesanan(
                id, userIdBody, customerNameBody, customerAddressBody, purchaseTypeBody, amountBody, totalBody, imagePart
            )

            if (response.status == "200" && response.data != null) {
                val updatedPemesananFromServer = response.data.copy(isSynced = true)

                dao.update(updatedPemesananFromServer)
            }

            return response

        } catch (e: Exception) {
            e.printStackTrace()
            return OpStatus(
                status = "500",
                message = "Terjadi error saat update: ${e.message}",
                data = null
            )
        }
    }
        suspend fun tambahPemesananApiOnly(pemesanan: Pemesanan): OpStatus {
            val mediaType = "text/plain".toMediaType()

            val userIdBody = pemesanan.userId.toRequestBody(mediaType)
            val customerNameBody = pemesanan.customerName.toRequestBody(mediaType)
            val customerAddressBody = pemesanan.customerAddress.toRequestBody(mediaType)
            val purchaseTypeBody = pemesanan.purchaseType.toRequestBody(mediaType)
            val amountBody = pemesanan.amount.toString().toRequestBody(mediaType)
            val totalBody = pemesanan.total.toString().toRequestBody(mediaType)

            val imagePart = MultipartBody.Part.createFormData(
                "image",
                "no_image.jpg",
                ByteArray(0).toRequestBody("image/*".toMediaTypeOrNull())
            )


            return apiService.postPemesanan(
                userId = userIdBody,
                customerName = customerNameBody,
                customerAddress = customerAddressBody,
                purchaseType = purchaseTypeBody,
                amount = amountBody,
                total = totalBody,
                image = imagePart
            )
        }


    suspend fun sinkronDariServer(userId: String) {
        if (NetworkUtils.isOnline(context)) {
            try {
                val response = apiService.getPemesanan(userId)

                val daftarPemesanan = response.data

                if (daftarPemesanan.isNotEmpty()) {
                    dao.syncFromServer(userId,daftarPemesanan)
                }
            } catch (e: Exception) {
                Log.e("Sinkronisasi", "Gagal sinkron dari server: ${e.message}")
            }
        }
    }
        fun getPemesananByUserId(userId: String): Flow<List<Pemesanan>> {
            return dao.getPemesananByUserId(userId)
        }
        private fun scheduleSync() {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync_pemesanan_data",
                ExistingWorkPolicy.KEEP,
                syncWorkRequest
            )
            Log.d("PemesananRepository", "Sinkronisasi dijadwalkan.")
        }
    }