package com.daffa0050.assesment1.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daffa0050.assesment1.database.PemesananDb
import com.daffa0050.assesment1.network.PemesananRepository
import com.daffa0050.assesment1.network.TelurApiService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PemesananViewModel(application: Application) : AndroidViewModel(application) {
    private val pemesananDao = PemesananDb.getDatabase(application).pemesananDao()
    private val apiService = TelurApiService.create()
    private val repository = PemesananRepository(apiService, pemesananDao, application)

    val allPemesanan = repository.semuaPemesanan
        .map { it.sortedByDescending { p -> p.id } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalEceran = allPemesanan.map {
        it.filter { p -> p.purchaseType == "Eceran" }.sumOf { p -> p.total }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val totalGrosir = allPemesanan.map {
        it.filter { p -> p.purchaseType == "Grosir" }.sumOf { p -> p.total }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun tambahPemesanan(pemesanan: Pemesanan) {
        viewModelScope.launch {
            repository.tambahPemesanan(pemesanan)
        }
    }

    fun sinkronisasi(userId: String = "guest") {
        viewModelScope.launch {
            repository.sinkronDariServer(userId)
        }
    }

    suspend fun updatePemesanan(pemesanan: Pemesanan) {
        repository.dao.updatePemesanan(pemesanan)
    }

    suspend fun deletePemesanan(id: Int) {
        repository.dao.deletePemesanan(id)
    }

    fun getPemesananById(id: Int): Flow<Pemesanan?> {
        return repository.dao.getPemesananById(id)
    }
}

