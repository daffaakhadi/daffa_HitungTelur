package com.daffa0050.assesment1.network

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.daffa0050.assesment1.database.PemesananDao
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

    suspend fun tambahPemesanan(pemesanan: Pemesanan, bitmap: Bitmap?) {
        if (NetworkUtils.isOnline(context)) {
            try {
                val userId = "guest"

                // Siapkan imagePart
                val imagePart = withContext(Dispatchers.IO) {
                    if (bitmap != null) {
                        val file = File(context.cacheDir, "upload.jpg")
                        val outputStream = FileOutputStream(file)
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        val imageByteArray = stream.toByteArray()

                        outputStream.use {
                            it.write(imageByteArray)
                        }

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

                // Kirim ke API
                val response = apiService.postPemesanan(
                    userId = userId,
                    customerName = pemesanan.customerName.toRequestBody("text/plain".toMediaType()),
                    customerAddress = pemesanan.customerAddress.toRequestBody("text/plain".toMediaType()),
                    purchaseType = pemesanan.purchaseType.toRequestBody("text/plain".toMediaType()),
                    amount = pemesanan.amount.toString().toRequestBody("text/plain".toMediaType()),
                    total = pemesanan.total.toString().toRequestBody("text/plain".toMediaType()),
                    image = imagePart
                )

                if (response.status == "200" && response.data != null) {
                    Log.d("Repo", "eggImage dari server: ${response.data.image}")
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
    suspend fun updatePemesananApi(
        id: Int,
        customerName: String,
        customerAddress: String,
        purchaseType: String,
        amount: Int,
        total: Int,
        bitmap: Bitmap?
    ) {
        val token = "Bearer ${authPref.getToken()}"

        val customerNameBody = customerName.toRequestBody("text/plain".toMediaType())
        val customerAddressBody = customerAddress.toRequestBody("text/plain".toMediaType())
        val purchaseTypeBody = purchaseType.toRequestBody("text/plain".toMediaType())
        val amountBody = amount.toString().toRequestBody("text/plain".toMediaType())
        val totalBody = total.toString().toRequestBody("text/plain".toMediaType())
        val methodBody = "PUT".toRequestBody("text/plain".toMediaType())

        val imagePart = bitmap?.let {
            val file = createTempFile("upload", ".jpg")
            val stream = FileOutputStream(file)
            it.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

            file.asRequestBody("image/jpeg".toMediaType())
                .let { body -> MultipartBody.Part.createFormData("image", file.name, body) }
        }

        apiService.updatePemesanan(
            id = id,
            token = token,
            customerName = customerNameBody,
            customerAddress = customerAddressBody,
            purchaseType = purchaseTypeBody,
            amount = amountBody,
            total = totalBody,
            method = methodBody,
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
