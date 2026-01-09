package com.example.bengkelku.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.repository.RepositoryBooking
import com.example.bengkelku.data.repository.RepositoryKendaraan
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class DashboardPelangganViewModel(
    penggunaId: Int,
    repositoryBooking: RepositoryBooking,
    repositoryKendaraan: RepositoryKendaraan
) : ViewModel() {

    val bookingAktif = repositoryBooking
        .getBookingAktifPengguna(penggunaId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val kendaraanSaya = repositoryKendaraan
        .getKendaraanByPengguna(penggunaId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )
}
