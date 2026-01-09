package com.example.bengkelku.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bengkelku.data.local.dao.BookingDao
import com.example.bengkelku.data.local.dao.KendaraanDao
import com.example.bengkelku.data.local.dao.PenggunaDao
import com.example.bengkelku.data.local.dao.ServisDao
import com.example.bengkelku.data.local.entity.Booking
import com.example.bengkelku.data.local.entity.Kendaraan
import com.example.bengkelku.data.local.entity.Pengguna
import com.example.bengkelku.data.local.entity.RolePengguna
import com.example.bengkelku.data.local.entity.Servis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Pengguna::class,
        Kendaraan::class,
        Servis::class,
        Booking::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DatabaseBengkelKu : RoomDatabase() {

    abstract fun penggunaDao(): PenggunaDao
    abstract fun kendaraanDao(): KendaraanDao
    abstract fun servisDao(): ServisDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseBengkelKu? = null

        fun getInstance(context: Context): DatabaseBengkelKu {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): DatabaseBengkelKu {
            return Room.databaseBuilder(
                context.applicationContext,
                DatabaseBengkelKu::class.java,
                "bengkelku_database"
            )
                .addCallback(DatabaseCallback())
                .build()
        }
    }

    /**
     * Callback untuk seeding data awal (ADMIN)
     */
    private class DatabaseCallback : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // Jalankan di background thread
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.penggunaDao()?.let { penggunaDao ->

                    val jumlahAdmin = penggunaDao.jumlahAdmin()

                    if (jumlahAdmin == 0) {
                        val admin = Pengguna(
                            nama = "Administrator",
                            username = "admin",
                            password = "admin",
                            role = RolePengguna.ADMIN
                        )
                        penggunaDao.insert(admin)
                    }
                }
            }
        }
    }
}
