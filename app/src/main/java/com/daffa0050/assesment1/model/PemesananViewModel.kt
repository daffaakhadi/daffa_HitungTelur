package com.daffa0050.assesment1.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daffa0050.assesment1.database.PemesananDb
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PemesananViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = PemesananDb.getDatabase(application).pemesananDao()

    val allPemesanan = dao.getAll()
        .map { it.sortedByDescending { p -> p.id } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalEceran = dao.getTotalEceran()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val totalGrosir = dao.getTotalGrosir()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun tambahPemesanan(pemesanan: Pemesanan) {
        viewModelScope.launch {
            dao.insert(pemesanan)
        }
    }
}
