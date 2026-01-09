package com.example.bengkelku.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bengkelku.viewmodel.auth.LoginViewModel
import com.example.bengkelku.viewmodel.auth.RegisterViewModel
import com.example.bengkelku.viewmodel.booking.BookingViewModel
import com.example.bengkelku.viewmodel.dashboard.DashboardAdminViewModel
import com.example.bengkelku.viewmodel.dashboard.DashboardPelangganViewModel
import com.example.bengkelku.viewmodel.kendaraan.KendaraanViewModel

class ViewModelFactory(
    private val containerApp: ContainerApp,
    private val penggunaId: Int? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(containerApp.repositoryAuth) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(containerApp.repositoryAuth) as T
            }

            modelClass.isAssignableFrom(DashboardAdminViewModel::class.java) -> {
                DashboardAdminViewModel(
                    containerApp.repositoryBooking,
                    containerApp.repositoryServis
                ) as T
            }

            modelClass.isAssignableFrom(DashboardPelangganViewModel::class.java) -> {
                requireNotNull(penggunaId) {
                    "penggunaId wajib untuk DashboardPelangganViewModel"
                }
                DashboardPelangganViewModel(
                    penggunaId,
                    containerApp.repositoryBooking,
                    containerApp.repositoryKendaraan
                ) as T
            }

            modelClass.isAssignableFrom(KendaraanViewModel::class.java) -> {
                requireNotNull(penggunaId) {
                    "penggunaId wajib untuk KendaraanViewModel"
                }
                KendaraanViewModel(
                    penggunaId,
                    containerApp.repositoryKendaraan
                ) as T
            }

            modelClass.isAssignableFrom(BookingViewModel::class.java) -> {
                requireNotNull(penggunaId) {
                    "penggunaId wajib untuk BookingViewModel"
                }
                BookingViewModel(
                    penggunaId,
                    containerApp.repositoryBooking
                ) as T
            }

            else -> throw IllegalArgumentException(
                "ViewModel tidak dikenal: ${modelClass.name}"
            )
        }
    }
}
