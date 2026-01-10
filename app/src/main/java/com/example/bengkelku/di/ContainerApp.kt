package com.example.bengkelku.di

import android.content.Context
import com.example.bengkelku.data.local.database.DatabaseBengkelKu
import com.example.bengkelku.data.repository.RepositoryAuth
import com.example.bengkelku.data.repository.RepositoryBooking
import com.example.bengkelku.data.repository.RepositoryKendaraan
import com.example.bengkelku.data.repository.RepositoryServis
import com.example.bengkelku.data.repository.RepositorySlotServis

class ContainerApp(context: Context) {

    // ===== DATABASE =====
    private val database: DatabaseBengkelKu =
        DatabaseBengkelKu.getInstance(context)

    // ===== REPOSITORY =====
    val repositoryAuth: RepositoryAuth by lazy {
        RepositoryAuth(database.penggunaDao())
    }

    val repositoryKendaraan: RepositoryKendaraan by lazy {
        RepositoryKendaraan(database.kendaraanDao())
    }

    val repositoryServis: RepositoryServis by lazy {
        RepositoryServis(database.servisDao())
    }

    val repositorySlotServis: RepositorySlotServis by lazy {
        RepositorySlotServis(database.slotServisDao())
    }

    val repositoryBooking: RepositoryBooking by lazy {
        RepositoryBooking(database.bookingDao())
    }
}
