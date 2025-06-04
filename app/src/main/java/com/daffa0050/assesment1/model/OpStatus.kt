package com.daffa0050.assesment1.model

data class OpStatus(
    var status: String,
    var message: String,
    val data: Pemesanan?
)