package com.example.bengkelku.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.remote.model.BookingResponse
import com.example.bengkelku.data.remote.model.BookingStatus
import com.example.bengkelku.data.remote.model.KendaraanResponse
import com.example.bengkelku.data.repository.RepositoryBooking
import com.example.bengkelku.data.repository.RepositoryKendaraan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardPelangganViewModel(
    private val penggunaId: Int,
    private val repositoryBooking: RepositoryBooking,
    private val repositoryKendaraan: RepositoryKendaraan
) : ViewModel() {

    // ===== KENDARAAN STATE =====
    private val _kendaraanState = MutableStateFlow<DashboardKendaraanState>(DashboardKendaraanState.Loading)
    val kendaraanState: StateFlow<DashboardKendaraanState> = _kendaraanState

    // ===== BOOKING STATE =====
    private val _bookingState = MutableStateFlow<DashboardBookingState>(DashboardBookingState.Loading)
    val bookingState: StateFlow<DashboardBookingState> = _bookingState

    init {
        loadData()
    }

    fun loadData() {
        loadKendaraan()
        loadBookingAktif()
    }

    private fun loadKendaraan() {
        viewModelScope.launch {
            _kendaraanState.value = DashboardKendaraanState.Loading

            val result = repositoryKendaraan.getKendaraanByPenggunaApi(penggunaId)
            result.fold(
                onSuccess = { data ->
                    _kendaraanState.value = DashboardKendaraanState.Success(data)
                },
                onFailure = { error ->
                    _kendaraanState.value = DashboardKendaraanState.Error(error.message ?: "Gagal memuat kendaraan")
                }
            )
        }
    }

    private fun loadBookingAktif() {
        viewModelScope.launch {
            _bookingState.value = DashboardBookingState.Loading

            val result = repositoryBooking.getBookingCustomerApi(penggunaId)
            result.fold(
                onSuccess = { data ->
                    // Filter active bookings using enum (case-insensitive)
                    val aktif = data.filter { booking ->
                        val status = BookingStatus.fromString(booking.status)
                        BookingStatus.isActive(status)
                    }
                    _bookingState.value = DashboardBookingState.Success(aktif)
                },
                onFailure = { error ->
                    _bookingState.value = DashboardBookingState.Error(error.message ?: "Gagal memuat booking")
                }
            )
        }
    }
}

sealed class DashboardKendaraanState {
    object Loading : DashboardKendaraanState()
    data class Success(val data: List<KendaraanResponse>) : DashboardKendaraanState()
    data class Error(val message: String) : DashboardKendaraanState()
}

sealed class DashboardBookingState {
    object Loading : DashboardBookingState()
    data class Success(val data: List<BookingResponse>) : DashboardBookingState()
    data class Error(val message: String) : DashboardBookingState()
}
