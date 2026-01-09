package com.example.bengkelku.data.repository

import com.example.bengkelku.data.local.dao.KendaraanDao
import com.example.bengkelku.data.local.entity.Kendaraan
import kotlinx.coroutines.flow.Flow

class RepositoryKendaraan(
    private val kendaraanDao: KendaraanDao
) {

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
}
