package com.example.bengkelku.data.repository

import com.example.bengkelku.data.local.dao.KendaraanDao
import com.example.bengkelku.data.local.entity.Kendaraan
import com.example.bengkelku.data.remote.ApiResponse
import com.example.bengkelku.data.remote.ApiService
import com.example.bengkelku.data.remote.model.KendaraanResponse
import kotlinx.coroutines.flow.Flow

class RepositoryKendaraan(
    private val kendaraanDao: KendaraanDao,
    private val apiService: ApiService
) {

    // ===== LOCAL (Room) =====

    fun getKendaraanByPengguna(penggunaId: Int): Flow<List<Kendaraan>> {
        return kendaraanDao.getKendaraanByPengguna(penggunaId)
    }

    suspend fun getKendaraanById(id: Int): Kendaraan? {
        return kendaraanDao.getById(id)
    }

    suspend fun tambahKendaraan(kendaraan: Kendaraan) {
        kendaraanDao.insert(kendaraan)
    }

    suspend fun updateKendaraan(kendaraan: Kendaraan) {
        kendaraanDao.update(kendaraan)
    }

    suspend fun hapusKendaraan(kendaraan: Kendaraan) {
        kendaraanDao.delete(kendaraan)
    }

    suspend fun isNomorPlatExistsForPengguna(penggunaId: Int, nomorPlat: String): Boolean {
        return kendaraanDao.countByPenggunaAndNomorPlat(penggunaId, nomorPlat) > 0
    }

    // ===== REMOTE (API) =====

    suspend fun getKendaraanByPenggunaApi(penggunaId: Int): ApiResponse<List<KendaraanResponse>> {
        return apiService.getKendaraanByPengguna(penggunaId)
    }

    suspend fun createKendaraanApi(
        penggunaId: Int,
        merk: String,
        model: String,
        nomorPlat: String,
        tahun: Int?
    ): ApiResponse<KendaraanResponse> {
        return apiService.createKendaraan(penggunaId, merk, model, nomorPlat, tahun)
    }

    suspend fun updateKendaraanApi(
        id: Int,
        merk: String,
        model: String,
        nomorPlat: String,
        tahun: Int?
    ): ApiResponse<KendaraanResponse> {
        return apiService.updateKendaraan(id, merk, model, nomorPlat, tahun)
    }

    suspend fun deleteKendaraanApi(id: Int): ApiResponse<Unit> {
        return apiService.deleteKendaraan(id)
    }
}
