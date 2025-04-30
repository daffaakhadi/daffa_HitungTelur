package com.daffa0050.assesment1.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AppViewModelProvider(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PemesananViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PemesananViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
