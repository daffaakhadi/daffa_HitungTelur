package com.daffa0050.assesment1.model

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daffa0050.assesment1.database.PemesananDb
import com.daffa0050.assesment1.network.AuthPreference
import com.daffa0050.assesment1.network.PemesananRepository
import com.daffa0050.assesment1.network.TelurApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PemesananViewModel(application: Application) : AndroidViewModel(application) {
    private val pemesananDao = PemesananDb.getDatabase(application).pemesananDao()
    private val apiService = TelurApiService.create()
    private val authPref = AuthPreference(application)
    private val repository = PemesananRepository(apiService, authPref,pemesananDao, application)

    val status = MutableStateFlow(TelurApiService.Companion.ApiStatus.SUCCESS)

    val allPemesanan = repository.semuaPemesanan
        .map { it.sortedByDescending { p -> p.id } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalEceran = allPemesanan.map {
        it.filter { p -> p.purchaseType == "Eceran" }.sumOf { p -> p.total }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val totalGrosir = allPemesanan.map {
        it.filter { p -> p.purchaseType == "Grosir" }.sumOf { p -> p.total }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun tambahPemesanan(pemesanan: Pemesanan, bitmap: Bitmap?) {
        viewModelScope.launch {
            repository.tambahPemesanan(pemesanan, bitmap)
        }
    }

    fun sinkronisasi(userId: String = "guest") {
        viewModelScope.launch {
            status.value = TelurApiService.Companion.ApiStatus.LOADING
            try {
                repository.sinkronDariServer(userId)
                status.value = TelurApiService.Companion.ApiStatus.SUCCESS
            } catch (e: Exception) {
                status.value = TelurApiService.Companion.ApiStatus.ERROR
            }
        }
    }


    fun deletePemesanan(userId: String, id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.deletePemesanan("Bearer $userId", id)
                if (response.status.toIntOrNull() == 200) {
                    repository.dao.deletePemesanan(id.toInt())
                    onSuccess()
                    sinkronisasi(userId)
                } else {
                    onError("Gagal menghapus data di server: ${response.message}")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    fun updatePemesananWithImage(
        id: Int,
        customerName: String,
        customerAddress: String,
        purchaseType: String,
        amount: Int,
        total: Int,
        image: Bitmap?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Panggil fungsi di repository yang akan menangani logika API
                repository.updatePemesananApi(
                    id = id,
                    customerName = customerName,
                    customerAddress = customerAddress,
                    purchaseType = purchaseType,
                    amount = amount,
                    total = total,
                    bitmap = image
                )
                // Jika berhasil, panggil callback onSuccess untuk navigasi kembali
                onSuccess()
                // Lakukan sinkronisasi ulang untuk memastikan data di UI terupdate
                sinkronisasi()
            } catch (e: Exception) {
                // Jika gagal, panggil callback onError untuk menampilkan pesan di UI
                onError(e.message ?: "Terjadi kesalahan yang tidak diketahui")
            }
        }
    }

    fun getPemesananById(id: Int): StateFlow<Pemesanan?> {
        return repository.dao.getPemesananById(id)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)
    }
}
