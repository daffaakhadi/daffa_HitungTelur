package com.daffa0050.assesment1.model

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daffa0050.assesment1.database.PemesananDb
import com.daffa0050.assesment1.network.PemesananRepository
import com.daffa0050.assesment1.network.TelurApiService
import com.daffa0050.assesment1.util.SettingsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PemesananViewModel(application: Application) : AndroidViewModel(application) {
    private val pemesananDao = PemesananDb.getDatabase(application).pemesananDao()
    private val apiService = TelurApiService.create()
    private val repository = PemesananRepository(apiService, pemesananDao, application)

    val status = MutableStateFlow(TelurApiService.Companion.ApiStatus.SUCCESS)
    val currentUserId: StateFlow<UserData?> = SettingsDataStore(application).userFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val pesananMilikUser: StateFlow<List<Pemesanan>> = currentUserId.flatMapLatest { user ->
        val userId = user?.email

        if (userId.isNullOrBlank()) {
            flowOf(emptyList())
        } else {
            repository.getPemesananByUserId(userId)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    val totalEceran = pesananMilikUser.map {
        it.filter { p -> p.purchaseType == "Eceran" }.sumOf { p -> p.total }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val totalGrosir = pesananMilikUser.map {
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
        id: Int,
        userId: String,
        customerName: String,
        customerAddress: String,
        purchaseType: String,
        amount: Int,
        total: Int,
        image: Bitmap?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updatePemesananApi( // Panggil fungsi yang sudah diperbaiki
                id = id,
                userId = userId,
                customerName = customerName,
                customerAddress = customerAddress,
                purchaseType = purchaseType,
                amount = amount,
                total = total,
                bitmap = image
            )
            withContext(Dispatchers.Main) {
                if (result.status == "200") {
                    onSuccess()
                } else {
                    onError(result.message ?: "Gagal memperbarui data.")
                }
            }
        }
    }

    fun getPemesananById(id: Int): StateFlow<Pemesanan?> {
        return repository.dao.getPemesananById(id)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)
    }
}