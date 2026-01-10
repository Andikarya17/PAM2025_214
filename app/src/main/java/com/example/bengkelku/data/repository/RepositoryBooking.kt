package com.example.bengkelku.data.repository

import com.example.bengkelku.data.local.dao.BookingDao
import com.example.bengkelku.data.local.dao.BookingWithDetails
import com.example.bengkelku.data.local.entity.Booking
import kotlinx.coroutines.flow.Flow

class RepositoryBooking(
    private val bookingDao: BookingDao
) {

    suspend fun buatBooking(booking: Booking) {
        bookingDao.insert(booking)
    }

    suspend fun updateBooking(booking: Booking) {
        bookingDao.update(booking)
    }

    suspend fun hapusBooking(booking: Booking) {
        bookingDao.delete(booking)
    }

    // Pelanggan - booking aktif
    fun getBookingAktifPengguna(penggunaId: Int): Flow<List<Booking>> {
        return bookingDao.getBookingAktif(penggunaId)
    }

    // Pelanggan - riwayat
    fun getRiwayatBookingPengguna(penggunaId: Int): Flow<List<Booking>> {
        return bookingDao.getRiwayatBooking(penggunaId)
    }

    // Admin - semua booking
    fun getSemuaBooking(): Flow<List<Booking>> {
        return bookingDao.getAllBooking()
    }

    // Admin - semua booking dengan detail
    fun getSemuaBookingWithDetails(): Flow<List<BookingWithDetails>> {
        return bookingDao.getAllBookingWithDetails()
    }

    // Get booking by ID
    suspend fun getBookingById(bookingId: Int): Booking? {
        return bookingDao.getBookingById(bookingId)
    }
}
