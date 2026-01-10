package com.example.bengkelku.viewmodel.servis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.local.entity.Servis
import com.example.bengkelku.data.repository.RepositoryServis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServisAdminViewModel(
    private val repositoryServis: RepositoryServis
) : ViewModel() {

    val semuaServis = repositoryServis
        .getAllServis()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    private val _aksiState = MutableStateFlow<AksiServisState>(AksiServisState.Idle)
    val aksiState: StateFlow<AksiServisState> = _aksiState

    private val _servisUntukEdit = MutableStateFlow<Servis?>(null)
    val servisUntukEdit: StateFlow<Servis?> = _servisUntukEdit

    fun tambahServis(namaServis: String, harga: Int, deskripsi: String?) {
        viewModelScope.launch {
            val servis = Servis(
                namaServis = namaServis,
                harga = harga,
                deskripsi = deskripsi,
                aktif = true
            )
            repositoryServis.tambahServis(servis)
            _aksiState.value = AksiServisState.Berhasil("Servis berhasil ditambahkan")
        }
    }

    fun updateServis(servis: Servis) {
        viewModelScope.launch {
            repositoryServis.updateServis(servis)
            _aksiState.value = AksiServisState.Berhasil("Servis berhasil diperbarui")
            _servisUntukEdit.value = null
        }
    }

    fun hapusServis(servis: Servis) {
        viewModelScope.launch {
            try {
                repositoryServis.hapusServis(servis)
                _aksiState.value = AksiServisState.Berhasil("Servis berhasil dihapus")
            } catch (e: Exception) {
                _aksiState.value = AksiServisState.Gagal(
                    "Tidak dapat menghapus servis yang sedang digunakan dalam booking"
                )
            }
        }
    }

    fun toggleAktif(servis: Servis) {
        viewModelScope.launch {
            val updated = servis.copy(aktif = !servis.aktif)
            repositoryServis.updateServis(updated)
            _aksiState.value = AksiServisState.Berhasil(
                if (updated.aktif) "Servis diaktifkan" else "Servis dinonaktifkan"
            )
        }
    }

    fun pilihUntukEdit(servis: Servis) {
        _servisUntukEdit.value = servis
    }

    fun batalEdit() {
        _servisUntukEdit.value = null
    }

    fun resetState() {
        _aksiState.value = AksiServisState.Idle
    }
}

sealed class AksiServisState {
    object Idle : AksiServisState()
    data class Berhasil(val pesan: String) : AksiServisState()
    data class Gagal(val pesan: String) : AksiServisState()
}
