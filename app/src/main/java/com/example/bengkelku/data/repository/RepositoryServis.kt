package com.example.bengkelku.data.repository

import com.example.bengkelku.data.local.dao.ServisDao
import com.example.bengkelku.data.local.entity.Servis
import com.example.bengkelku.data.remote.ApiService
import com.example.bengkelku.data.remote.model.ServisResponse
import kotlinx.coroutines.flow.Flow

class RepositoryServis(
    private val servisDao: ServisDao,
    private val apiService: ApiService
) {

    // ===== LOCAL (Room) - Secondary/Cache =====

    fun getAllServisLocal(): Flow<List<Servis>> {
        return servisDao.getAllServis()
    }

    fun getServisAktifLocal(): Flow<List<Servis>> {
        return servisDao.getServisAktif()
    }

    suspend fun getServisByIdLocal(id: Int): Servis? {
        return servisDao.getById(id)
    }

    suspend fun upsertLocal(servis: Servis) {
        servisDao.upsert(servis)
    }

    suspend fun upsertAllLocal(servisList: List<Servis>) {
        servisDao.upsertAll(servisList)
    }

    suspend fun updateLocal(servis: Servis) {
        servisDao.update(servis)
    }

    suspend fun deleteLocal(servis: Servis) {
        servisDao.delete(servis)
    }

    suspend fun deleteByIdLocal(id: Int) {
        servisDao.deleteById(id)
    }

    // ===== REMOTE (API) - Primary Source =====

    /**
     * Get all servis from API.
     */
    suspend fun getAllServisApi(): Result<List<ServisResponse>> {
        return try {
            val response = apiService.getAllServis()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal memuat data servis") ?: "Response kosong"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Create servis via API.
     */
    suspend fun createServisApi(
        namaServis: String,
        harga: Int,
        deskripsi: String?
    ): Result<String> {
        return try {
            val response = apiService.createServis(namaServis, harga, deskripsi)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.getMessageOrDefault("Servis berhasil ditambahkan"))
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal menambahkan servis") ?: "Response kosong"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Update servis via API.
     */
    suspend fun updateServisApi(
        id: Int,
        namaServis: String,
        harga: Int,
        deskripsi: String?
    ): Result<String> {
        return try {
            val response = apiService.updateServis(id, namaServis, harga, deskripsi)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.getMessageOrDefault("Servis berhasil diperbarui"))
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal memperbarui servis") ?: "Response kosong"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    /**
     * Delete servis via API.
     */
    suspend fun deleteServisApi(id: Int): Result<String> {
        return try {
            val response = apiService.deleteServis(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.isSuccess) {
                    Result.success(body.getMessageOrDefault("Servis berhasil dihapus"))
                } else {
                    Result.failure(Exception(body?.getMessageOrDefault("Gagal menghapus servis") ?: "Response kosong"))
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
     * Convert ServisResponse to Servis entity for local storage.
     */
    fun servisResponseToEntity(response: ServisResponse): Servis {
        return Servis(
            id = response.id,
            namaServis = response.namaServis,
            harga = response.harga,
            deskripsi = response.deskripsi,
            isActive = response.isActive
        )
    }

    /**
     * Convert list of ServisResponse to list of Servis entities.
     */
    fun servisResponseListToEntities(responses: List<ServisResponse>): List<Servis> {
        return responses.map { servisResponseToEntity(it) }
    }
}
