package com.example.bengkelku.data.repository

import com.example.bengkelku.data.local.dao.BookingDao
import com.example.bengkelku.data.local.dao.BookingWithDetails
import com.example.bengkelku.data.local.entity.Booking
import com.example.bengkelku.data.remote.ApiResponse
import com.example.bengkelku.data.remote.ApiService
import com.example.bengkelku.data.remote.model.BookingResponse
import kotlinx.coroutines.flow.Flow

class RepositoryBooking(
    private val bookingDao: BookingDao,
    private val apiService: ApiService
) {

    // ===== LOCAL (Room) =====

    suspend fun buatBooking(booking: Booking) {
        bookingDao.insert(booking)
    }

    suspend fun updateBooking(booking: Booking) {
        bookingDao.update(booking)
    }

    suspend fun hapusBooking(booking: Booking) {
        bookingDao.delete(booking)
    }

    fun getBookingAktifPengguna(penggunaId: Int): Flow<List<Booking>> {
        return bookingDao.getBookingAktif(penggunaId)
    }

    fun getRiwayatBookingPengguna(penggunaId: Int): Flow<List<Booking>> {
        return bookingDao.getRiwayatBooking(penggunaId)
    }

    fun getSemuaBooking(): Flow<List<Booking>> {
        return bookingDao.getAllBooking()
    }

    fun getSemuaBookingWithDetails(): Flow<List<BookingWithDetails>> {
        return bookingDao.getAllBookingWithDetails()
    }

    suspend fun getBookingById(bookingId: Int): Booking? {
        return bookingDao.getBookingById(bookingId)
    }

    // ===== REMOTE (API) =====

    suspend fun getBookingAktifApi(penggunaId: Int): ApiResponse<List<BookingResponse>> {
        return apiService.getBookingAktif(penggunaId)
    }

    suspend fun getRiwayatBookingApi(penggunaId: Int): ApiResponse<List<BookingResponse>> {
        return apiService.getRiwayatBooking(penggunaId)
    }

    suspend fun getAllBookingApi(): ApiResponse<List<BookingResponse>> {
        return apiService.getAllBooking()
    }

    suspend fun createBookingApi(
        penggunaId: Int,
        kendaraanId: Int,
        servisId: Int,
        slotServisId: Int,
        tanggalServis: String,
        jamServis: String,
        nomorAntrian: String,
        totalBiaya: Int
    ): ApiResponse<BookingResponse> {
        return apiService.createBooking(
            penggunaId,
            kendaraanId,
            servisId,
            slotServisId,
            tanggalServis,
            jamServis,
            nomorAntrian,
            totalBiaya
        )
    }

    suspend fun updateBookingStatusApi(
        id: Int,
        status: String
    ): ApiResponse<BookingResponse> {
        return apiService.updateBookingStatus(id, status)
    }
}
