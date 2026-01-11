package com.example.bengkelku.data.repository

import com.example.bengkelku.data.local.dao.ServisDao
import com.example.bengkelku.data.local.entity.Servis
import com.example.bengkelku.data.remote.ApiResponse
import com.example.bengkelku.data.remote.ApiService
import com.example.bengkelku.data.remote.model.ServisResponse
import kotlinx.coroutines.flow.Flow

class RepositoryServis(
    private val servisDao: ServisDao,
    private val apiService: ApiService
) {

    // ===== LOCAL (Room) =====

    fun getAllServis(): Flow<List<Servis>> {
        return servisDao.getAllServis()
    }

    fun getServisAktif(): Flow<List<Servis>> {
        return servisDao.getServisAktif()
    }

    suspend fun tambahServis(servis: Servis) {
        servisDao.insert(servis)
    }

    suspend fun updateServis(servis: Servis) {
        servisDao.update(servis)
    }

    suspend fun hapusServis(servis: Servis) {
        servisDao.delete(servis)
    }

    // ===== REMOTE (API) =====

    suspend fun getAllServisApi(): ApiResponse<List<ServisResponse>> {
        return apiService.getAllServis()
    }

    suspend fun getServisAktifApi(): ApiResponse<List<ServisResponse>> {
        return apiService.getServisAktif()
    }

    suspend fun createServisApi(
        namaServis: String,
        harga: Int,
        deskripsi: String?
    ): ApiResponse<ServisResponse> {
        return apiService.createServis(namaServis, harga, deskripsi)
    }

    suspend fun updateServisApi(
        id: Int,
        namaServis: String,
        harga: Int,
        deskripsi: String?,
        aktif: Boolean
    ): ApiResponse<ServisResponse> {
        return apiService.updateServis(id, namaServis, harga, deskripsi, aktif)
    }

    suspend fun deleteServisApi(id: Int): ApiResponse<Unit> {
        return apiService.deleteServis(id)
    }
}
