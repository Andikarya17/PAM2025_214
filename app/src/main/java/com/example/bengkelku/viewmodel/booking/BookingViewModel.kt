package com.example.bengkelku.viewmodel.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.local.entity.Booking
import com.example.bengkelku.data.repository.RepositoryBooking
import com.example.bengkelku.data.repository.RepositoryServis
import com.example.bengkelku.data.repository.RepositorySlotServis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingViewModel(
    private val penggunaId: Int,
    private val repositoryBooking: RepositoryBooking,
    private val repositoryServis: RepositoryServis,
    private val repositorySlotServis: RepositorySlotServis
) : ViewModel() {

    val bookingAktif = repositoryBooking
        .getBookingAktifPengguna(penggunaId)

    val riwayatBooking = repositoryBooking
        .getRiwayatBookingPengguna(penggunaId)

    // Daftar servis aktif untuk customer
    val daftarServis = repositoryServis
        .getServisAktif()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    // Daftar slot tersedia untuk customer
    val daftarSlot = repositorySlotServis
        .getAvailableSlots(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    private val _aksiState = MutableStateFlow<AksiBookingState>(AksiBookingState.Idle)
    val aksiState: StateFlow<AksiBookingState> = _aksiState

    fun buatBooking(booking: Booking) {
        viewModelScope.launch {
            repositoryBooking.buatBooking(booking)
            // Increment slot terpakai
            repositorySlotServis.incrementTerpakai(booking.slotServisId)
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
