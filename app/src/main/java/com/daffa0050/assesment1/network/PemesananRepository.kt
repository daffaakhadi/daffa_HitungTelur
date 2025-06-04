package com.daffa0050.assesment1.network

import android.content.Context
import com.daffa0050.assesment1.database.PemesananDao
import com.daffa0050.assesment1.model.Pemesanan
import com.daffa0050.assesment1.util.NetworkUtils
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class PemesananRepository(
    private val apiService: TelurApiService,
    val dao: PemesananDao,
    private val context: Context
) {
    val semuaPemesanan: Flow<List<Pemesanan>> = dao.getAll()

    suspend fun tambahPemesanan(pemesanan: Pemesanan) {
        if (NetworkUtils.isOnline(context)) {
            try {
                val userId = "guest"
                val emptyImage = MultipartBody.Part.createFormData(
                    "image",
                    "telur.jpg",
                    ByteArray(0).toRequestBody("image/*".toMediaTypeOrNull())
                )

                val response = apiService.postPemesanan(
                    userId = userId,
                    customerName = pemesanan.customerName.toRequestBody("text/plain".toMediaType()),
                    customerAddress = pemesanan.customerAddress.toRequestBody("text/plain".toMediaType()),
                    purchaseType = pemesanan.purchaseType.toRequestBody("text/plain".toMediaType()),
                    amount = pemesanan.amount.toString().toRequestBody("text/plain".toMediaType()),
                    total = pemesanan.total.toString().toRequestBody("text/plain".toMediaType()),
                    image = emptyImage
                )

                if (response.status == "200" && response.data != null) {
                    dao.insert(response.data)
                } else {
                    dao.insert(pemesanan)
                }

            } catch (e: Exception) {
                dao.insert(pemesanan)
            }
        } else {
            dao.insert(pemesanan)
        }
    }

    suspend fun sinkronDariServer(userId: String) {
        if (NetworkUtils.isOnline(context)) {
            try {
                val dataDariServer = apiService.getPemesanan(userId)
                dao.insertAll(dataDariServer)
            } catch (_: Exception) {
                // Gagal sinkron, bisa diabaikan atau beri log nanti
            }
        }
    }
}
