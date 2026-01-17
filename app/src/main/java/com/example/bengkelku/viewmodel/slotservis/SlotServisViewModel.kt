package com.example.bengkelku.viewmodel.slotservis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.remote.model.SlotServisResponse
import com.example.bengkelku.data.repository.RepositorySlotServis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SlotServisViewModel(
    private val repositorySlotServis: RepositorySlotServis
) : ViewModel() {

    // ===== LIST STATE (API-FIRST) =====
    private val _listState = MutableStateFlow<ListSlotState>(ListSlotState.Loading)
    val listState: StateFlow<ListSlotState> = _listState

    // ===== ACTION STATE =====
    private val _aksiState = MutableStateFlow<AksiSlotState>(AksiSlotState.Idle)
    val aksiState: StateFlow<AksiSlotState> = _aksiState

    // ===== EDIT STATE =====
    private val _slotUntukEdit = MutableStateFlow<SlotServisResponse?>(null)
    val slotUntukEdit: StateFlow<SlotServisResponse?> = _slotUntukEdit

    init {
        loadSlots()
    }

    /**
     * Load all slots from API (PRIMARY SOURCE)
     */
    fun loadSlots() {
        viewModelScope.launch {
            _listState.value = ListSlotState.Loading

            val result = repositorySlotServis.getAllSlotsApi()
            result.fold(
                onSuccess = { data ->
                    _listState.value = ListSlotState.Success(data)
                },
                onFailure = { error ->
                    _listState.value = ListSlotState.Error(error.message ?: "Terjadi kesalahan")
                }
            )
        }
    }

    /**
     * Tambah slot via API (PRIMARY)
     */
    fun tambahSlot(tanggal: String, jamMulai: String, jamSelesai: String, kapasitas: Int) {
        viewModelScope.launch {
            // Validasi waktu
            if (jamMulai >= jamSelesai) {
                _aksiState.value = AksiSlotState.Gagal("Jam mulai harus sebelum jam selesai")
                return@launch
            }

            if (tanggal.isBlank() || jamMulai.isBlank() || jamSelesai.isBlank()) {
                _aksiState.value = AksiSlotState.Gagal("Data slot tidak lengkap")
                return@launch
            }

            if (kapasitas <= 0) {
                _aksiState.value = AksiSlotState.Gagal("Kapasitas harus lebih dari 0")
                return@launch
            }

            _aksiState.value = AksiSlotState.Loading

            val result = repositorySlotServis.createSlotApi(
                tanggal = tanggal,
                jamMulai = jamMulai,
                jamSelesai = jamSelesai,
                kapasitas = kapasitas
            )

            result.fold(
                onSuccess = { message ->
                    _aksiState.value = AksiSlotState.Berhasil(message)
                    loadSlots()
                },
                onFailure = { error ->
                    _aksiState.value = AksiSlotState.Gagal(error.message ?: "Gagal menambahkan slot")
                }
            )
        }
    }

    /**
     * Update slot via API (PRIMARY)
     */
    fun updateSlot(id: Int, tanggal: String, jamMulai: String, jamSelesai: String, kapasitas: Int) {
        viewModelScope.launch {
            // Validasi waktu
            if (jamMulai >= jamSelesai) {
                _aksiState.value = AksiSlotState.Gagal("Jam mulai harus sebelum jam selesai")
                return@launch
            }

            _aksiState.value = AksiSlotState.Loading

            val result = repositorySlotServis.updateSlotApi(
                id = id,
                tanggal = tanggal,
                jamMulai = jamMulai,
                jamSelesai = jamSelesai,
                kapasitas = kapasitas
            )

            result.fold(
                onSuccess = { message ->
                    _aksiState.value = AksiSlotState.Berhasil(message)
                    _slotUntukEdit.value = null
                    loadSlots()
                },
                onFailure = { error ->
                    _aksiState.value = AksiSlotState.Gagal(error.message ?: "Gagal memperbarui slot")
                }
            )
        }
    }

    /**
     * Hapus slot via API (PRIMARY)
     */
    fun hapusSlot(id: Int) {
        viewModelScope.launch {
            _aksiState.value = AksiSlotState.Loading

            val result = repositorySlotServis.deleteSlotApi(id)

            result.fold(
                onSuccess = { message ->
                    _aksiState.value = AksiSlotState.Berhasil(message)
                    loadSlots()
                },
                onFailure = { error ->
                    _aksiState.value = AksiSlotState.Gagal(error.message ?: "Gagal menghapus slot")
                }
            )
        }
    }

    fun pilihUntukEdit(slot: SlotServisResponse) {
        _slotUntukEdit.value = slot
    }

    fun batalEdit() {
        _slotUntukEdit.value = null
    }

    fun resetState() {
        _aksiState.value = AksiSlotState.Idle
    }
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
 * State untuk aksi CRUD
 */
sealed class AksiSlotState {
    object Idle : AksiSlotState()
    object Loading : AksiSlotState()
    data class Berhasil(val pesan: String) : AksiSlotState()
    data class Gagal(val pesan: String) : AksiSlotState()
}
