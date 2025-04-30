package com.daffa0050.assesment1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pemesanan")
data class Pemesanan (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val alamat: String,
    val jenis: String,
    val jumlahKg: Int,
    val totalHarga: Int
)