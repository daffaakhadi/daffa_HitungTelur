package com.daffa0050.assesment1.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daffa0050.assesment1.database.PemesananDao
import com.daffa0050.assesment1.database.PemesananDb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PemesananViewModel(application: Application) : AndroidViewModel(application) {
    private val pemesananDao: PemesananDao = PemesananDb.getDatabase(application).pemesananDao()

    val allPemesanan = pemesananDao.getAll()
        .map { it.sortedByDescending { p -> p.id } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalEceran = pemesananDao.getTotalEceran()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val totalGrosir = pemesananDao.getTotalGrosir()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun getPemesananById(id: Int): Flow<Pemesanan?> {
        return pemesananDao.getPemesananById(id)
    }

    suspend fun updatePemesanan(pemesanan: Pemesanan) {
        pemesananDao.updatePemesanan(pemesanan)
    }


    fun tambahPemesanan(pemesanan: Pemesanan) {
        viewModelScope.launch {
            pemesananDao.insert(pemesanan)
        }
    }
}
