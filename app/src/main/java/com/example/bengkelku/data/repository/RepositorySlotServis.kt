package com.example.bengkelku.data.repository

import com.example.bengkelku.data.local.dao.SlotServisDao
import com.example.bengkelku.data.local.entity.SlotServis
import com.example.bengkelku.data.remote.ApiService
import com.example.bengkelku.data.remote.model.SlotServisResponse
import kotlinx.coroutines.flow.Flow

class RepositorySlotServis(
    private val slotServisDao: SlotServisDao,
    private val apiService: ApiService
) {

    // ===== LOCAL (Room) - Secondary/Cache =====

    fun getAllSlotsLocal(): Flow<List<SlotServis>> {
        return slotServisDao.getAllSlots()
    }

    fun getSlotsByDateLocal(tanggal: String): Flow<List<SlotServis>> {
        return slotServisDao.getSlotsByDate(tanggal)
    }

    fun getAvailableSlotsLocal(today: String): Flow<List<SlotServis>> {
        return slotServisDao.getAvailableSlots(today)
    }

    suspend fun getSlotByIdLocal(slotId: Int): SlotServis? {
        return slotServisDao.getSlotById(slotId)
    }

    suspend fun upsertLocal(slot: SlotServis) {
        slotServisDao.upsert(slot)
    }

    suspend fun upsertAllLocal(slotList: List<SlotServis>) {
        slotServisDao.upsertAll(slotList)
    }

    suspend fun updateLocal(slot: SlotServis) {
        slotServisDao.update(slot)
    }

    suspend fun deleteLocal(slot: SlotServis) {
        slotServisDao.delete(slot)
    }

    suspend fun deleteByIdLocal(id: Int) {
        slotServisDao.deleteById(id)
    }

    suspend fun getOverlappingSlotsLocal(
        tanggal: String,
        jamMulai: String,
        jamSelesai: String,
        excludeId: Int
    ): List<SlotServis> {
        return slotServisDao.getOverlappingSlots(tanggal, jamMulai, jamSelesai, excludeId)
    }

    suspend fun countBookingsForSlotLocal(slotId: Int): Int {
        return slotServisDao.countBookingsForSlot(slotId)
    }

    suspend fun incrementTerpakaiLocal(slotId: Int) {
        slotServisDao.incrementTerpakai(slotId)
    }

    suspend fun decrementTerpakaiLocal(slotId: Int) {
        slotServisDao.decrementTerpakai(slotId)
    }

    // ===== REMOTE (API) - Primary Source =====

    /**
     * Get available slots from API.
     */
    suspend fun getAvailableSlotsApi(): Result<List<SlotServisResponse>> {
        return try {
            val response = apiService.getAvailableSlots()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal memuat slot") ?: "Response kosong"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Get all slots from API.
     */
    suspend fun getAllSlotsApi(): Result<List<SlotServisResponse>> {
        return try {
            val response = apiService.getAllSlots()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal memuat slot") ?: "Response kosong"))
                }
            } else {
                val errorMsg = when (response.code()) {
                    404 -> "Endpoint tidak ditemukan (404). Periksa konfigurasi server."
                    500 -> "Server error (500). Periksa log PHP."
                    else -> "HTTP ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Create slot via API.
     */
    suspend fun createSlotApi(
        tanggal: String,
        jamMulai: String,
        jamSelesai: String,
        kapasitas: Int
    ): Result<String> {
        return try {
            val response = apiService.createSlot(tanggal, jamMulai, jamSelesai, kapasitas)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.getMessageOrDefault("Slot berhasil ditambahkan"))
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal menambahkan slot") ?: "Response kosong"))
                }
            } else {
                // Handle specific HTTP error codes with clear messages
                val errorMsg = when (response.code()) {
                    404 -> "Endpoint tidak ditemukan (404). Periksa konfigurasi server."
                    500 -> "Server error (500). Periksa log PHP."
                    else -> "HTTP ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Update slot via API.
     */
    suspend fun updateSlotApi(
        id: Int,
        tanggal: String,
        jamMulai: String,
        jamSelesai: String,
        kapasitas: Int
    ): Result<String> {
        return try {
            val response = apiService.updateSlot(id, tanggal, jamMulai, jamSelesai, kapasitas)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.getMessageOrDefault("Slot berhasil diperbarui"))
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal memperbarui slot") ?: "Response kosong"))
                }
            } else {
                val errorMsg = when (response.code()) {
                    404 -> "Endpoint tidak ditemukan (404). Periksa konfigurasi server."
                    500 -> "Server error (500). Periksa log PHP."
                    else -> "HTTP ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Delete slot via API.
     */
    suspend fun deleteSlotApi(id: Int): Result<String> {
        return try {
            val response = apiService.deleteSlot(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.getMessageOrDefault("Slot berhasil dihapus"))
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal menghapus slot") ?: "Response kosong"))
                }
            } else {
                val errorMsg = when (response.code()) {
                    404 -> "Endpoint tidak ditemukan (404). Periksa konfigurasi server."
                    500 -> "Server error (500). Periksa log PHP."
                    else -> "HTTP ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    // ===== CONVERTERS =====

    /**
     * Convert SlotServisResponse to SlotServis entity for local storage.
     */
    fun slotServisResponseToEntity(response: SlotServisResponse): SlotServis {
        return SlotServis(
            id = response.id,
            tanggal = response.tanggal,
            jamMulai = response.jamMulai,
            jamSelesai = response.jamSelesai,
            kapasitas = response.kapasitas,
            terpakai = response.terpakai,
            status = response.status
        )
    }

    /**
     * Convert list of SlotServisResponse to list of SlotServis entities.
     */
    fun slotServisResponseListToEntities(responses: List<SlotServisResponse>): List<SlotServis> {
        return responses.map { slotServisResponseToEntity(it) }
    }
}
