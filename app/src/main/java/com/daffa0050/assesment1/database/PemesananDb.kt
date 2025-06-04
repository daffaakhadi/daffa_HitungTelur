package com.daffa0050.assesment1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.daffa0050.assesment1.model.Pemesanan

@Database(entities = [Pemesanan::class], version = 1)
abstract class PemesananDb : RoomDatabase() {
    abstract fun pemesananDao(): PemesananDao

    companion object {
        @Volatile
        private var INSTANCE: PemesananDb? = null

        fun getDatabase(context: Context): PemesananDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PemesananDb::class.java,
                    "datatelur_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
