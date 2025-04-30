package com.daffa0050.assesment1.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class PemesananViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "app_database"
    ).build()

    private val dao = db.pemesananDao()
    val allPemesanan = dao.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val totalEceran = dao.getTotalEceran().stateIn(viewModelScope, SharingStarted.Lazily, 0)
    val totalGrosir = dao.getTotalGrosir().stateIn(viewModelScope, SharingStarted.Lazily, 0)
}
