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
    private val penggunaId: Int?,  // NULLABLE - null means not logged in
    private val repositoryBooking: RepositoryBooking,
    private val repositoryServis: RepositoryServis,
    private val repositorySlotServis: RepositorySlotServis
) : ViewModel() {

    // Only load if penggunaId is valid
    val bookingAktif = penggunaId?.let { id ->
        repositoryBooking.getBookingAktifPengguna(id)
    }

    val riwayatBooking = penggunaId?.let { id ->
        repositoryBooking.getRiwayatBookingPengguna(id)
    }

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
            // BLOCK: penggunaId must not be null
            if (penggunaId == null) {
                _aksiState.value = AksiBookingState.Gagal("Sesi login tidak valid. Silakan login ulang.")
                return@launch
            }

            // BLOCK: booking.penggunaId must match and be valid
            if (booking.penggunaId != penggunaId || booking.penggunaId <= 0) {
                _aksiState.value = AksiBookingState.Gagal("Data pengguna tidak valid")
                return@launch
            }

            // BLOCK: kendaraanId must be valid
            if (booking.kendaraanId <= 0) {
                _aksiState.value = AksiBookingState.Gagal("Pilih kendaraan terlebih dahulu")
                return@launch
            }

            // BLOCK: servisId must be valid
            if (booking.servisId <= 0) {
                _aksiState.value = AksiBookingState.Gagal("Pilih jenis servis terlebih dahulu")
                return@launch
            }

            // BLOCK: slotServisId must exist
            val slotAda = repositorySlotServis.getSlotById(booking.slotServisId)
            if (slotAda == null) {
                _aksiState.value = AksiBookingState.Gagal("Slot waktu tidak valid")
                return@launch
            }

            // BLOCK: slot must have capacity
            if (slotAda.terpakai >= slotAda.kapasitas) {
                _aksiState.value = AksiBookingState.Gagal("Slot waktu sudah penuh")
                return@launch
            }

            // All validations passed - safe to insert
            repositoryBooking.buatBooking(booking)
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
    data class Gagal(val pesan: String) : AksiBookingState()
}
