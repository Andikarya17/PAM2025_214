package com.example.bengkelku.viewmodel.servis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.remote.model.ServisResponse
import com.example.bengkelku.data.repository.RepositoryServis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ServisAdminViewModel(
    private val repositoryServis: RepositoryServis
) : ViewModel() {

    // ===== LIST STATE (API-FIRST) =====
    private val _listState = MutableStateFlow<ListServisState>(ListServisState.Loading)
    val listState: StateFlow<ListServisState> = _listState

    // ===== ACTION STATE =====
    private val _aksiState = MutableStateFlow<AksiServisState>(AksiServisState.Idle)
    val aksiState: StateFlow<AksiServisState> = _aksiState

    // ===== EDIT STATE =====
    private val _servisUntukEdit = MutableStateFlow<ServisResponse?>(null)
    val servisUntukEdit: StateFlow<ServisResponse?> = _servisUntukEdit

    init {
        loadServis()
    }

    /**
     * Load all servis from API (PRIMARY SOURCE)
     */
    fun loadServis() {
        viewModelScope.launch {
            _listState.value = ListServisState.Loading

            val result = repositoryServis.getAllServisApi()
            result.fold(
                onSuccess = { data ->
                    _listState.value = ListServisState.Success(data)
                },
                onFailure = { error ->
                    _listState.value = ListServisState.Error(error.message ?: "Terjadi kesalahan")
                }
            )
        }
    }

    /**
     * Tambah servis via API (PRIMARY)
     */
    fun tambahServis(namaServis: String, harga: Int, deskripsi: String?) {
        viewModelScope.launch {
            if (namaServis.isBlank()) {
                _aksiState.value = AksiServisState.Gagal("Nama servis wajib diisi")
                return@launch
            }

            _aksiState.value = AksiServisState.Loading

            val result = repositoryServis.createServisApi(
                namaServis = namaServis.trim(),
                harga = harga,
                deskripsi = deskripsi?.trim()?.ifBlank { null }
            )

            result.fold(
                onSuccess = { message ->
                    _aksiState.value = AksiServisState.Berhasil(message)
                    loadServis()
                },
                onFailure = { error ->
                    _aksiState.value = AksiServisState.Gagal(error.message ?: "Gagal menambahkan servis")
                }
            )
        }
    }

    /**
     * Update servis via API (PRIMARY)
     */
    fun updateServis(id: Int, namaServis: String, harga: Int, deskripsi: String?) {
        viewModelScope.launch {
            _aksiState.value = AksiServisState.Loading

            val result = repositoryServis.updateServisApi(
                id = id,
                namaServis = namaServis.trim(),
                harga = harga,
                deskripsi = deskripsi?.trim()?.ifBlank { null }
            )

            result.fold(
                onSuccess = { message ->
                    _aksiState.value = AksiServisState.Berhasil(message)
                    _servisUntukEdit.value = null
                    loadServis()
                },
                onFailure = { error ->
                    _aksiState.value = AksiServisState.Gagal(error.message ?: "Gagal memperbarui servis")
                }
            )
        }
    }

    /**
     * Hapus servis via API (PRIMARY)
     */
    fun hapusServis(id: Int) {
        viewModelScope.launch {
            _aksiState.value = AksiServisState.Loading

            val result = repositoryServis.deleteServisApi(id)

            result.fold(
                onSuccess = { message ->
                    _aksiState.value = AksiServisState.Berhasil(message)
                    loadServis()
                },
                onFailure = { error ->
                    _aksiState.value = AksiServisState.Gagal(error.message ?: "Gagal menghapus servis")
                }
            )
        }
    }

    fun pilihUntukEdit(servis: ServisResponse) {
        _servisUntukEdit.value = servis
    }

    fun batalEdit() {
        _servisUntukEdit.value = null
    }

    fun resetState() {
        _aksiState.value = AksiServisState.Idle
    }
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
 * State untuk aksi CRUD
 */
sealed class AksiServisState {
    object Idle : AksiServisState()
    object Loading : AksiServisState()
    data class Berhasil(val pesan: String) : AksiServisState()
    data class Gagal(val pesan: String) : AksiServisState()
}
