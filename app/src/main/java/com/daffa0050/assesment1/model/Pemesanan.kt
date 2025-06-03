package com.daffa0050.assesment1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pemesanan")
data class Pemesanan(
    @PrimaryKey val id: Int = 0,
    val customerName: String,
    val customerAddress: String,
    val purchaseType: String,
    val amount: Int,
    val total: Int
)
