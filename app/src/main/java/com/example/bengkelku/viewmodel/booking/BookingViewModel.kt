package com.example.bengkelku.viewmodel.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.local.entity.Booking
import com.example.bengkelku.data.repository.RepositoryBooking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingViewModel(
    private val penggunaId: Int,
    private val repositoryBooking: RepositoryBooking
) : ViewModel() {

    val bookingAktif = repositoryBooking
        .getBookingAktifPengguna(penggunaId)

    val riwayatBooking = repositoryBooking
        .getRiwayatBookingPengguna(penggunaId)

    private val _aksiState = MutableStateFlow<AksiBookingState>(AksiBookingState.Idle)
    val aksiState: StateFlow<AksiBookingState> = _aksiState

    fun buatBooking(booking: Booking) {
        viewModelScope.launch {
            repositoryBooking.buatBooking(booking)
            _aksiState.value = AksiBookingState.Berhasil
        }
    }

    fun resetState() {
        _aksiState.value = AksiBookingState.Idle
    }
}

sealed class AksiBookingState {
    object Idle : AksiBookingState()
    object Berhasil : AksiBookingState()
}
