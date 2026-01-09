package com.example.bengkelku.data.repository

import com.example.bengkelku.data.local.dao.ServisDao
import com.example.bengkelku.data.local.entity.Servis
import kotlinx.coroutines.flow.Flow

class RepositoryServis(
    private val servisDao: ServisDao
) {

    // Admin
    fun getAllServis(): Flow<List<Servis>> {
        return servisDao.getAllServis()
    }

    // Pelanggan
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
}
