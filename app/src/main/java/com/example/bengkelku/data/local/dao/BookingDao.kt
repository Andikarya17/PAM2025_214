package com.example.bengkelku.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bengkelku.data.local.entity.Booking
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    @Insert
    suspend fun insert(booking: Booking)

    @Update
    suspend fun update(booking: Booking)

    @Delete
    suspend fun delete(booking: Booking)

    // Booking aktif pelanggan
    @Query("""
        SELECT * FROM booking
        WHERE penggunaId = :penggunaId
          AND status IN ('MENUNGGU', 'DIPROSES')
        ORDER BY tanggalServis ASC, jamServis ASC
    """)
    fun getBookingAktif(penggunaId: Int): Flow<List<Booking>>

    // Riwayat booking pelanggan
    @Query("""
        SELECT * FROM booking
        WHERE penggunaId = :penggunaId
          AND status IN ('SELESAI', 'DIAMBIL')
        ORDER BY tanggalServis DESC
    """)
    fun getRiwayatBooking(penggunaId: Int): Flow<List<Booking>>

    // Semua booking (Admin)
    @Query("""
        SELECT * FROM booking
        ORDER BY tanggalServis DESC, jamServis DESC
    """)
    fun getAllBooking(): Flow<List<Booking>>
}
