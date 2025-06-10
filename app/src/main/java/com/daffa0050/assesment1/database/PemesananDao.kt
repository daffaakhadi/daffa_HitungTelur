package com.daffa0050.assesment1.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.daffa0050.assesment1.model.Pemesanan
import kotlinx.coroutines.flow.Flow

@Dao
interface PemesananDao {
    @Query("SELECT * FROM pemesanan")
    fun getAll(): Flow<List<Pemesanan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pemesanan: Pemesanan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<Pemesanan>) // untuk sinkronisasi

    @Query("SELECT SUM(total) FROM pemesanan WHERE purchaseType = 'Eceran'")
    fun getTotalEceran(): Flow<Int?>

    @Query("SELECT SUM(total) FROM pemesanan WHERE purchaseType = 'Grosir'")
    fun getTotalGrosir(): Flow<Int?>

    @Query("SELECT * FROM pemesanan WHERE id = :id")
    fun getPemesananById(id: Int): Flow<Pemesanan?>

    @Query("DELETE FROM pemesanan WHERE id = :id")
    suspend fun deletePemesanan(id: Int)

    @Update
    suspend fun updatePemesanan(pemesanan: Pemesanan)

    @Delete
    suspend fun delete(pemesanan: Pemesanan)

    @Query("DELETE FROM pemesanan WHERE userId = :userId")
    suspend fun clearByUserId(userId: String)

    @Query("SELECT * FROM pemesanan WHERE userId = :userId ORDER BY id DESC")
    suspend fun getByUserId(userId: String): List<Pemesanan>
}