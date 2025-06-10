package com.daffa0050.assesment1.network

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.daffa0050.assesment1.database.PemesananDao
import com.daffa0050.assesment1.model.OpStatus
import com.daffa0050.assesment1.model.Pemesanan
import com.daffa0050.assesment1.util.NetworkUtils
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
        private val authPref: AuthPreference,
        val dao: PemesananDao,
        private val context: Context
    ) {
        val semuaPemesanan: Flow<List<Pemesanan>> = dao.getAll()

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

                            MultipartBody.Part.createFormData(
                                "image",
                                file.name,
                                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            )
                        } else {
                            MultipartBody.Part.createFormData(
                                "image",
                                "telur.jpg",
                                ByteArray(0).toRequestBody("image/*".toMediaTypeOrNull())
                            )
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

                    if (response.status == "200" && response.data != null) {
                        // Tambahkan userId ke data dari server sebelum simpan ke Room
                        val saved = response.data.copy(userId = userId)
                        dao.insert(saved)
                    } else {
                        dao.insert(pemesanan.copy(userId = userId))
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    dao.insert(pemesanan.copy(userId = userId))
                }
            } else {
                dao.insert(pemesanan.copy(userId = userId))
            }
        }
    suspend fun updatePemesananApi(
        userId: String,
        id: Int,
        customerName: String,
        customerAddress: String,
        purchaseType: String,
        amount: Int,
        total: Int,
        bitmap: Bitmap?
    ): OpStatus {

        val token = "Bearer ${authPref.getToken()}"
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
            stream.flush()
            stream.close()

            file.asRequestBody("image/jpeg".toMediaType())
                .let { body -> MultipartBody.Part.createFormData("image", file.name, body) }
        }

        return apiService.updatePemesanan(
            id = id,
            userId = userIdBody,
            token = token,
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
                // 1. Panggil API, hasilnya adalah objek ApiResponse
                val response = apiService.getPemesanan(userId)

                // 2. Ambil daftar pemesanan dari dalam properti .data
                val daftarPemesanan = response.data

                // 3. Simpan daftar yang sudah benar ini ke database
                if (daftarPemesanan.isNotEmpty()) {
                    dao.insertAll(daftarPemesanan)
                }
            } catch (e: Exception) {
                Log.e("Sinkronisasi", "Gagal sinkron dari server: ${e.message}")
            }
        }
    }
}