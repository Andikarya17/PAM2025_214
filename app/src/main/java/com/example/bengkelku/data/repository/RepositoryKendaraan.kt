package com.example.bengkelku.data.repository

import com.example.bengkelku.data.local.dao.KendaraanDao
import com.example.bengkelku.data.local.entity.Kendaraan
import com.example.bengkelku.data.remote.ApiService
import com.example.bengkelku.data.remote.model.KendaraanResponse
import kotlinx.coroutines.flow.Flow

class RepositoryKendaraan(
    private val kendaraanDao: KendaraanDao,
    private val apiService: ApiService
) {

    // ===== LOCAL (Room) - Secondary/Cache =====

    fun getKendaraanByPenggunaLocal(penggunaId: Int): Flow<List<Kendaraan>> {
        return kendaraanDao.getKendaraanByPengguna(penggunaId)
    }

    suspend fun getKendaraanByIdLocal(id: Int): Kendaraan? {
        return kendaraanDao.getById(id)
    }

    suspend fun upsertLocal(kendaraan: Kendaraan) {
        kendaraanDao.upsert(kendaraan)
    }

    suspend fun upsertAllLocal(kendaraanList: List<Kendaraan>) {
        kendaraanDao.upsertAll(kendaraanList)
    }

    suspend fun updateLocal(kendaraan: Kendaraan) {
        kendaraanDao.update(kendaraan)
    }

    suspend fun deleteLocal(kendaraan: Kendaraan) {
        kendaraanDao.delete(kendaraan)
    }

    suspend fun deleteByIdLocal(id: Int) {
        kendaraanDao.deleteById(id)
    }

    suspend fun isNomorPlatExistsForPengguna(penggunaId: Int, nomorPlat: String, excludeId: Int = 0): Boolean {
        return kendaraanDao.countByPenggunaAndNomorPlat(penggunaId, nomorPlat, excludeId) > 0
    }

    // ===== REMOTE (API) - Primary Source =====

    /**
     * Get list of kendaraan from API.
     * Returns pair of (success, data/errorMessage)
     */
    suspend fun getKendaraanByPenggunaApi(
        userId: Int
    ): Result<List<KendaraanResponse>> {
        return try {
            val response = apiService.getKendaraanByPengguna(userId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal memuat data kendaraan") ?: "Response kosong"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Create kendaraan via API.
     * Returns Result with success message or error.
     */
    suspend fun createKendaraanApi(
        userId: Int,
        merk: String,
        model: String,
        nomorPlat: String,
        tahun: Int?
    ): Result<String> {
        return try {
            val response = apiService.createKendaraan(userId, merk, model, nomorPlat, tahun)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.getMessageOrDefault("Kendaraan berhasil ditambahkan"))
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal menambahkan kendaraan") ?: "Response kosong"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Update kendaraan via API.
     */
    suspend fun updateKendaraanApi(
        id: Int,
        merk: String,
        model: String,
        nomorPlat: String,
        tahun: Int?
    ): Result<String> {
        return try {
            val response = apiService.updateKendaraan(id, merk, model, nomorPlat, tahun)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.getMessageOrDefault("Kendaraan berhasil diperbarui"))
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal memperbarui kendaraan") ?: "Response kosong"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Delete kendaraan via API.
     */
    suspend fun deleteKendaraanApi(id: Int): Result<String> {
        return try {
            val response = apiService.deleteKendaraan(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.getMessageOrDefault("Kendaraan berhasil dihapus"))
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal menghapus kendaraan") ?: "Response kosong"))
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
     * Convert KendaraanResponse to Kendaraan entity for local storage.
     */
    fun kendaraanResponseToEntity(response: KendaraanResponse, penggunaId: Int): Kendaraan {
        return Kendaraan(
            id = response.id,
            penggunaId = penggunaId,
            merk = response.merk,
            model = response.model,
            nomorPlat = response.nomorPlat,
            tahun = response.tahun,
            warna = response.warna
        )
    }

    /**
     * Convert list of KendaraanResponse to list of Kendaraan entities.
     */
    fun kendaraanResponseListToEntities(responses: List<KendaraanResponse>, penggunaId: Int): List<Kendaraan> {
        return responses.map { kendaraanResponseToEntity(it, penggunaId) }
    }
}
