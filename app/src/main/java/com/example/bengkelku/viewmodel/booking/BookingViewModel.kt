package com.example.bengkelku.viewmodel.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.remote.model.BookingResponse
import com.example.bengkelku.data.remote.model.ServisResponse
import com.example.bengkelku.data.remote.model.SlotServisResponse
import com.example.bengkelku.data.repository.RepositoryBooking
import com.example.bengkelku.data.repository.RepositoryServis
import com.example.bengkelku.data.repository.RepositorySlotServis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingViewModel(
    private val penggunaId: Int?,  // NULLABLE - null means not logged in
    private val repositoryBooking: RepositoryBooking,
    private val repositoryServis: RepositoryServis,
    private val repositorySlotServis: RepositorySlotServis
) : ViewModel() {

    // ===== LIST STATES (API-FIRST) =====
    private val _bookingState = MutableStateFlow<ListBookingState>(ListBookingState.Loading)
    val bookingState: StateFlow<ListBookingState> = _bookingState

    private val _servisState = MutableStateFlow<ListServisState>(ListServisState.Loading)
    val servisState: StateFlow<ListServisState> = _servisState

    private val _slotState = MutableStateFlow<ListSlotState>(ListSlotState.Loading)
    val slotState: StateFlow<ListSlotState> = _slotState

    // ===== ACTION STATE =====
    private val _aksiState = MutableStateFlow<AksiBookingState>(AksiBookingState.Idle)
    val aksiState: StateFlow<AksiBookingState> = _aksiState

    init {
        loadData()
    }

    /**
     * Load all required data from API
     */
    fun loadData() {
        loadBookingCustomer()
        loadServisAktif()
        loadSlotTersedia()
    }

    private fun loadBookingCustomer() {
        if (penggunaId == null || penggunaId <= 0) {
            _bookingState.value = ListBookingState.Error("Sesi login tidak valid")
            return
        }

        viewModelScope.launch {
            _bookingState.value = ListBookingState.Loading

            val result = repositoryBooking.getBookingCustomerApi(penggunaId)
            result.fold(
                onSuccess = { data ->
                    _bookingState.value = ListBookingState.Success(data)
                },
                onFailure = { error ->
                    _bookingState.value = ListBookingState.Error(error.message ?: "Gagal memuat booking")
                }
            )
        }
    }

    private fun loadServisAktif() {
        viewModelScope.launch {
            _servisState.value = ListServisState.Loading

            val result = repositoryServis.getAllServisApi()
            result.fold(
                onSuccess = { data ->
                    // Filter only active servis
                    _servisState.value = ListServisState.Success(data.filter { it.isActive })
                },
                onFailure = { error ->
                    _servisState.value = ListServisState.Error(error.message ?: "Gagal memuat servis")
                }
            )
        }
    }

    private fun loadSlotTersedia() {
        viewModelScope.launch {
            _slotState.value = ListSlotState.Loading

            val result = repositorySlotServis.getAvailableSlotsApi()
            result.fold(
                onSuccess = { data ->
                    // Filter slots that have capacity
                    _slotState.value = ListSlotState.Success(data.filter { it.kapasitas > it.terpakai })
                },
                onFailure = { error ->
                    _slotState.value = ListSlotState.Error(error.message ?: "Gagal memuat slot")
                }
            )
        }
    }

    /**
     * Buat booking via API (PRIMARY)
     */
    fun buatBooking(
        kendaraanId: Int,
        jenisServisId: Int,
        slotServisId: Int
    ) {
        viewModelScope.launch {
            // BLOCK: penggunaId must not be null
            if (penggunaId == null || penggunaId <= 0) {
                _aksiState.value = AksiBookingState.Gagal("Sesi login tidak valid. Silakan login ulang.")
                return@launch
            }

            // BLOCK: kendaraanId must be valid
            if (kendaraanId <= 0) {
                _aksiState.value = AksiBookingState.Gagal("Pilih kendaraan terlebih dahulu")
                return@launch
            }

            // BLOCK: jenisServisId must be valid
            if (jenisServisId <= 0) {
                _aksiState.value = AksiBookingState.Gagal("Pilih jenis servis terlebih dahulu")
                return@launch
            }

            // BLOCK: slotServisId must be valid
            if (slotServisId <= 0) {
                _aksiState.value = AksiBookingState.Gagal("Pilih slot waktu terlebih dahulu")
                return@launch
            }

            _aksiState.value = AksiBookingState.Loading

            val result = repositoryBooking.createBookingApi(
                userId = penggunaId,
                kendaraanId = kendaraanId,
                jenisServisId = jenisServisId,
                slotServisId = slotServisId
            )

            result.fold(
                onSuccess = { booking ->
                    _aksiState.value = AksiBookingState.Berhasil("Booking berhasil dibuat. Antrian: ${booking.nomorAntrian}")
                    loadData()
                },
                onFailure = { error ->
                    _aksiState.value = AksiBookingState.Gagal(error.message ?: "Gagal membuat booking")
                }
            )
        }
    }

    fun resetState() {
        _aksiState.value = AksiBookingState.Idle
    }
}

/**
 * State untuk list booking
 */
sealed class ListBookingState {
    object Loading : ListBookingState()
    data class Success(val data: List<BookingResponse>) : ListBookingState()
    data class Error(val message: String) : ListBookingState()
}

/**
 * State untuk list servis
 */
sealed class ListServisState {
    object Loading : ListServisState()
    data class Success(val data: List<ServisResponse>) : ListServisState()
    data class Error(val message: String) : ListServisState()
}

/**
 * State untuk list slot
 */
sealed class ListSlotState {
    object Loading : ListSlotState()
    data class Success(val data: List<SlotServisResponse>) : ListSlotState()
    data class Error(val message: String) : ListSlotState()
}

/**
 * State untuk aksi booking
 */
sealed class AksiBookingState {
    object Idle : AksiBookingState()
    object Loading : AksiBookingState()
    data class Berhasil(val message: String) : AksiBookingState()
    data class Gagal(val pesan: String) : AksiBookingState()
}
