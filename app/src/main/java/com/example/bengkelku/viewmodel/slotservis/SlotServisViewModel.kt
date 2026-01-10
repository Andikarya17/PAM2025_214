package com.example.bengkelku.viewmodel.slotservis

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.local.entity.SlotServis
import com.example.bengkelku.data.repository.RepositorySlotServis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SlotServisViewModel(
    private val repositorySlotServis: RepositorySlotServis
) : ViewModel() {

    val semuaSlot = repositorySlotServis
        .getAllSlots()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    private val _aksiState = MutableStateFlow<AksiSlotState>(AksiSlotState.Idle)
    val aksiState: StateFlow<AksiSlotState> = _aksiState

    private val _slotUntukEdit = MutableStateFlow<SlotServis?>(null)
    val slotUntukEdit: StateFlow<SlotServis?> = _slotUntukEdit

    fun tambahSlot(tanggal: String, jamMulai: String, jamSelesai: String, kapasitas: Int) {
        viewModelScope.launch {
            // Validasi waktu
            if (jamMulai >= jamSelesai) {
                _aksiState.value = AksiSlotState.Gagal("Jam mulai harus sebelum jam selesai")
                return@launch
            }

            // Cek overlap
            val overlaps = repositorySlotServis.getOverlappingSlots(
                tanggal = tanggal,
                jamMulai = jamMulai,
                jamSelesai = jamSelesai
            )

            if (overlaps.isNotEmpty()) {
                _aksiState.value = AksiSlotState.Gagal(
                    "Slot bertabrakan dengan slot lain yang sudah ada"
                )
                return@launch
            }

            val slot = SlotServis(
                tanggal = tanggal,
                jamMulai = jamMulai,
                jamSelesai = jamSelesai,
                kapasitas = kapasitas
            )
            repositorySlotServis.tambahSlot(slot)
            _aksiState.value = AksiSlotState.Berhasil("Slot berhasil ditambahkan")
        }
    }

    fun updateSlot(slot: SlotServis) {
        viewModelScope.launch {
            // Validasi waktu
            if (slot.jamMulai >= slot.jamSelesai) {
                _aksiState.value = AksiSlotState.Gagal("Jam mulai harus sebelum jam selesai")
                return@launch
            }

            // Cek overlap (exclude current slot)
            val overlaps = repositorySlotServis.getOverlappingSlots(
                tanggal = slot.tanggal,
                jamMulai = slot.jamMulai,
                jamSelesai = slot.jamSelesai,
                excludeId = slot.id
            )

            if (overlaps.isNotEmpty()) {
                _aksiState.value = AksiSlotState.Gagal(
                    "Slot bertabrakan dengan slot lain yang sudah ada"
                )
                return@launch
            }

            repositorySlotServis.updateSlot(slot)
            _aksiState.value = AksiSlotState.Berhasil("Slot berhasil diperbarui")
            _slotUntukEdit.value = null
        }
    }

    fun hapusSlot(slot: SlotServis) {
        viewModelScope.launch {
            // Pre-check: cek apakah slot digunakan dalam booking
            val bookingCount = repositorySlotServis.countBookingsForSlot(slot.id)

            if (bookingCount > 0) {
                _aksiState.value = AksiSlotState.Gagal(
                    "Tidak dapat menghapus slot yang sudah digunakan dalam booking ($bookingCount booking)"
                )
                return@launch
            }

            // Safe delete dengan try-catch untuk FK RESTRICT
            try {
                repositorySlotServis.hapusSlot(slot)
                _aksiState.value = AksiSlotState.Berhasil("Slot berhasil dihapus")
            } catch (e: SQLiteConstraintException) {
                _aksiState.value = AksiSlotState.Gagal(
                    "Tidak dapat menghapus slot karena masih digunakan dalam booking"
                )
            } catch (e: Exception) {
                _aksiState.value = AksiSlotState.Gagal(
                    "Gagal menghapus slot: ${e.message ?: "Terjadi kesalahan"}"
                )
            }
        }
    }

    fun pilihUntukEdit(slot: SlotServis) {
        _slotUntukEdit.value = slot
    }

    fun batalEdit() {
        _slotUntukEdit.value = null
    }

    fun resetState() {
        _aksiState.value = AksiSlotState.Idle
    }
}

sealed class AksiSlotState {
    object Idle : AksiSlotState()
    data class Berhasil(val pesan: String) : AksiSlotState()
    data class Gagal(val pesan: String) : AksiSlotState()
}
