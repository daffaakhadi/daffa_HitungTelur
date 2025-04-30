package com.daffa0050.assesment1.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daffa0050.assesment1.model.Pemesanan
import kotlinx.coroutines.flow.Flow

@Dao
interface PemesananDao {
    @Query("SELECT * FROM pemesanan")
    fun getAll(): Flow<List<Pemesanan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pemesanan: Pemesanan)

    @Query("SELECT SUM(totalHarga) FROM pemesanan WHERE jenis = 'eceran'")
    fun getTotalEceran(): Flow<Int?>

    @Query("SELECT SUM(totalHarga) FROM pemesanan WHERE jenis = 'grosir'")
    fun getTotalGrosir(): Flow<Int?>
}
