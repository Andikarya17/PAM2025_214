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
import com.example.bengkelku.data.local.dao.SlotServisDao
import com.example.bengkelku.data.local.entity.Booking
import com.example.bengkelku.data.local.entity.Kendaraan
import com.example.bengkelku.data.local.entity.Pengguna
import com.example.bengkelku.data.local.entity.RolePengguna
import com.example.bengkelku.data.local.entity.Servis
import com.example.bengkelku.data.local.entity.SlotServis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Pengguna::class,
        Kendaraan::class,
        Servis::class,
        SlotServis::class,
        Booking::class
    ],
    version = 2,
    exportSchema = false
)
abstract class DatabaseBengkelKu : RoomDatabase() {

    abstract fun penggunaDao(): PenggunaDao
    abstract fun kendaraanDao(): KendaraanDao
    abstract fun servisDao(): ServisDao
    abstract fun slotServisDao(): SlotServisDao
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
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    /**
     * Callback untuk seeding data awal (ADMIN)
     * Menggunakan onOpen agar admin selalu di-seed jika belum ada,
     * termasuk setelah destructive migration.
     */
    private class DatabaseCallback : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)

            // Cek apakah admin sudah ada
            val cursor = db.query("SELECT COUNT(*) FROM pengguna WHERE role = 'ADMIN'")
            var adminCount = 0
            if (cursor.moveToFirst()) {
                adminCount = cursor.getInt(0)
            }
            cursor.close()

            // Insert admin jika belum ada
            if (adminCount == 0) {
                db.execSQL("""
                    INSERT INTO pengguna (nama, username, password, role)
                    VALUES ('Administrator', 'admin', 'admin', 'ADMIN')
                """)
            }
        }
    }

}