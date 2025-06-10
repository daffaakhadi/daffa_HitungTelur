package com.daffa0050.assesment1.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "pemesanan")
data class Pemesanan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generate hanya digunakan saat Room insert lokal

    @Json(name = "userId") // Pastikan cocok dengan field dari API
    val userId: String,

    @Json(name = "customerName")
    val customerName: String,

    @Json(name = "customerAddress")
    val customerAddress: String,

    @Json(name = "purchaseType")
    val purchaseType: String,

    @Json(name = "amount")
    val amount: Int,

    @Json(name = "total")
    val total: Int,

    @Json(name = "eggImage")
    val image: String?,

    @Json(name = "createdAt")
    val createdAt: String? = null,

    @Json(name = "updatedAt")
    val updatedAt: String? = null
)