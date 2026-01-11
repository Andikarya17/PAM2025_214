package com.example.bengkelku.di

import android.content.Context
import com.example.bengkelku.data.local.database.DatabaseBengkelKu
import com.example.bengkelku.data.remote.ApiConfig
import com.example.bengkelku.data.remote.ApiService
import com.example.bengkelku.data.repository.RepositoryAuth
import com.example.bengkelku.data.repository.RepositoryBooking
import com.example.bengkelku.data.repository.RepositoryKendaraan
import com.example.bengkelku.data.repository.RepositoryServis
import com.example.bengkelku.data.repository.RepositorySlotServis

class ContainerApp(context: Context) {

    // ===== DATABASE =====
    private val database: DatabaseBengkelKu =
        DatabaseBengkelKu.getInstance(context)

    // ===== API SERVICE =====
    private val apiService: ApiService by lazy {
        ApiConfig.apiService
    }

    // ===== REPOSITORY =====
    val repositoryAuth: RepositoryAuth by lazy {
        RepositoryAuth(database.penggunaDao(), apiService)
    }

    val repositoryKendaraan: RepositoryKendaraan by lazy {
        RepositoryKendaraan(database.kendaraanDao(), apiService)
    }

    val repositoryServis: RepositoryServis by lazy {
        RepositoryServis(database.servisDao(), apiService)
    }

    val repositorySlotServis: RepositorySlotServis by lazy {
        RepositorySlotServis(database.slotServisDao(), apiService)
    }

    val repositoryBooking: RepositoryBooking by lazy {
        RepositoryBooking(database.bookingDao(), apiService)
    }
}
