package com.daffa0050.assesment1.model

import com.squareup.moshi.Json

data class ApiResponse(
    @Json(name = "data")
    val data: List<Pemesanan>
)