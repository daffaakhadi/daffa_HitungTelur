package com.daffa0050.assesment1.model

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daffa0050.assesment1.database.PemesananDb
import com.daffa0050.assesment1.network.AuthPreference
import com.daffa0050.assesment1.network.PemesananRepository
import com.daffa0050.assesment1.network.TelurApiService
import com.daffa0050.assesment1.util.SettingsDataStore
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
    val currentUserId: StateFlow<UserData?> = SettingsDataStore(application).userFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
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
            val userId = currentUserId.value?.email
            if (userId != null) {
                repository.tambahPemesanan(pemesanan, userId, bitmap)
            }
        }
    }

    fun sinkronisasi(userId: String) {
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
        userId: String,
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
                // Panggil fungsi repository yang sudah diubah untuk return OpStatus
                val response = repository.updatePemesananApi(
                    userId = userId,
                    id = id,
                    customerName = customerName,
                    customerAddress = customerAddress,
                    purchaseType = purchaseType,
                    amount = amount,
                    total = total,
                    bitmap = image
                )

                if (response.status == "200") {
                    onSuccess()
                    sinkronisasi(userId) // ambil data terbaru dari server dan update DB
                } else {
                    onError(response.message ?: "Update gagal")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Terjadi kesalahan yang tidak diketahui")
            }
        }
    }


    fun getPemesananById(id: Int): StateFlow<Pemesanan?> {
        return repository.dao.getPemesananById(id)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)
    }
}