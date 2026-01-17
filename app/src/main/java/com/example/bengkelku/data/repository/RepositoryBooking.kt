package com.example.bengkelku.data.repository

import android.util.Log
import com.example.bengkelku.data.local.dao.BookingDao
import com.example.bengkelku.data.local.dao.BookingWithDetails
import com.example.bengkelku.data.local.entity.Booking
import com.example.bengkelku.data.local.entity.StatusBooking
import com.example.bengkelku.data.remote.ApiService
import com.example.bengkelku.data.remote.model.BookingResponse
import kotlinx.coroutines.flow.Flow

private const val TAG = "RepositoryBooking"

class RepositoryBooking(
    private val bookingDao: BookingDao,
    private val apiService: ApiService
) {

    // ===== LOCAL (Room) - Secondary/Cache =====

    suspend fun upsertLocal(booking: Booking) {
        bookingDao.upsert(booking)
    }

    suspend fun upsertAllLocal(bookings: List<Booking>) {
        bookingDao.upsertAll(bookings)
    }

    suspend fun updateLocal(booking: Booking) {
        bookingDao.update(booking)
    }

    suspend fun deleteLocal(booking: Booking) {
        bookingDao.delete(booking)
    }

    fun getBookingAktifPenggunaLocal(penggunaId: Int): Flow<List<Booking>> {
        return bookingDao.getBookingAktif(penggunaId)
    }

    fun getRiwayatBookingPenggunaLocal(penggunaId: Int): Flow<List<Booking>> {
        return bookingDao.getRiwayatBooking(penggunaId)
    }

    fun getBookingByPenggunaLocal(penggunaId: Int): Flow<List<Booking>> {
        return bookingDao.getBookingByPengguna(penggunaId)
    }

    fun getSemuaBookingLocal(): Flow<List<Booking>> {
        return bookingDao.getAllBooking()
    }

    fun getSemuaBookingWithDetailsLocal(): Flow<List<BookingWithDetails>> {
        return bookingDao.getAllBookingWithDetails()
    }

    suspend fun getBookingByIdLocal(bookingId: Int): Booking? {
        return bookingDao.getBookingById(bookingId)
    }

    suspend fun deleteAllLocal() {
        bookingDao.deleteAll()
    }

    // ===== REMOTE (API) - Primary Source =====

    /**
     * Get customer bookings from API.
     */
    suspend fun getBookingCustomerApi(userId: Int): Result<List<BookingResponse>> {
        return try {
            val response = apiService.getBookingCustomer(userId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal memuat booking") ?: "Response kosong"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Get all bookings (admin) from API.
     */
    suspend fun getAllBookingApi(): Result<List<BookingResponse>> {
        return try {
            val response = apiService.getAllBooking()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal memuat booking") ?: "Response kosong"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Create booking via API.
     */
    suspend fun createBookingApi(
        userId: Int,
        kendaraanId: Int,
        jenisServisId: Int,
        slotServisId: Int
    ): Result<BookingResponse> {
        Log.d(TAG, "createBookingApi called: userId=$userId, kendaraanId=$kendaraanId, jenisServisId=$jenisServisId, slotServisId=$slotServisId")
        return try {
            val response = apiService.createBooking(
                userId,
                kendaraanId,
                jenisServisId,
                slotServisId
            )
            Log.d(TAG, "createBookingApi response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess && body.data != null) {
                    Log.d(TAG, "createBookingApi SUCCESS: bookingId=${body.data.id}, status=${body.data.status}")
                    Result.success(body.data)
                } else {
                    val errorMsg = body?.getMessageOrDefault("Gagal membuat booking") ?: "Response kosong"
                    Log.e(TAG, "createBookingApi FAILED: $errorMsg")
                    Result.failure(Exception(errorMsg))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "No error body"
                Log.e(TAG, "createBookingApi HTTP ERROR: ${response.code()} - $errorBody")
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "createBookingApi EXCEPTION: ${e.message}", e)
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Update booking status via API.
     */
    suspend fun updateBookingStatusApi(id: Int, status: String): Result<String> {
        return try {
            val response = apiService.updateBookingStatus(id, status)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.getMessageOrDefault("Status berhasil diperbarui"))
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal memperbarui status") ?: "Response kosong"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    // ===== CONVERTERS =====

    /**
     * Convert BookingResponse to Booking entity for local storage.
     */
    fun bookingResponseToEntity(response: BookingResponse): Booking {
        return Booking(
            id = response.id,
            penggunaId = response.userId,
            kendaraanId = response.kendaraanId,
            servisId = response.jenisServisId,
            slotServisId = response.slotServisId,
            tanggalServis = response.tanggalServis,
            jamServis = response.jamServis,
            nomorAntrian = response.nomorAntrian,
            status = StatusBooking.fromString(response.status),
            totalBiaya = response.totalBiaya
        )
    }

    /**
     * Convert list of BookingResponse to list of Booking entities.
     */
    fun bookingResponseListToEntities(responses: List<BookingResponse>): List<Booking> {
        return responses.map { bookingResponseToEntity(it) }
    }
}
