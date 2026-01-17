package com.example.bengkelku.viewmodel.kendaraan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.remote.model.KendaraanResponse
import com.example.bengkelku.data.repository.RepositoryKendaraan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KendaraanViewModel(
    private val penggunaId: Int,
    private val repositoryKendaraan: RepositoryKendaraan
) : ViewModel() {

    // ===== LIST STATE (API-FIRST) =====
    private val _listState = MutableStateFlow<ListKendaraanState>(ListKendaraanState.Loading)
    val listState: StateFlow<ListKendaraanState> = _listState

    // ===== ACTION STATE =====
    private val _aksiState = MutableStateFlow<AksiKendaraanState>(AksiKendaraanState.Idle)
    val aksiState: StateFlow<AksiKendaraanState> = _aksiState

    init {
        loadKendaraan()
    }

    /**
     * Load kendaraan from API (PRIMARY SOURCE)
     */
    fun loadKendaraan() {
        viewModelScope.launch {
            _listState.value = ListKendaraanState.Loading

            val result = repositoryKendaraan.getKendaraanByPenggunaApi(penggunaId)
            result.fold(
                onSuccess = { data ->
                    _listState.value = ListKendaraanState.Success(data)
                },
                onFailure = { error ->
                    _listState.value = ListKendaraanState.Error(error.message ?: "Terjadi kesalahan")
                }
            )
        }
    }

    /**
     * Tambah kendaraan via API (PRIMARY)
     */
    fun tambahKendaraan(
        merk: String,
        model: String,
        nomorPlat: String,
        tahun: Int?
    ) {
        viewModelScope.launch {
            // Validate input
            if (penggunaId <= 0) {
                _aksiState.value = AksiKendaraanState.Gagal("Sesi login tidak valid")
                return@launch
            }

            if (merk.isBlank() || model.isBlank() || nomorPlat.isBlank()) {
                _aksiState.value = AksiKendaraanState.Gagal("Data kendaraan tidak lengkap")
                return@launch
            }

            _aksiState.value = AksiKendaraanState.Loading

            val result = repositoryKendaraan.createKendaraanApi(
                userId = penggunaId,
                merk = merk.trim(),
                model = model.trim(),
                nomorPlat = nomorPlat.trim().uppercase(),
                tahun = tahun
            )

            result.fold(
                onSuccess = { message ->
                    _aksiState.value = AksiKendaraanState.Berhasil(message)
                    // Reload list after success
                    loadKendaraan()
                },
                onFailure = { error ->
                    _aksiState.value = AksiKendaraanState.Gagal(error.message ?: "Gagal menambahkan kendaraan")
                }
            )
        }
    }

    /**
     * Update kendaraan via API (PRIMARY)
     */
    fun updateKendaraan(
        id: Int,
        merk: String,
        model: String,
        nomorPlat: String,
        tahun: Int?
    ) {
        viewModelScope.launch {
            _aksiState.value = AksiKendaraanState.Loading

            val result = repositoryKendaraan.updateKendaraanApi(
                id = id,
                merk = merk.trim(),
                model = model.trim(),
                nomorPlat = nomorPlat.trim().uppercase(),
                tahun = tahun
            )

            result.fold(
                onSuccess = { message ->
                    _aksiState.value = AksiKendaraanState.Berhasil(message)
                    loadKendaraan()
                },
                onFailure = { error ->
                    _aksiState.value = AksiKendaraanState.Gagal(error.message ?: "Gagal memperbarui kendaraan")
                }
            )
        }
    }

    /**
     * Hapus kendaraan via API (PRIMARY)
     */
    fun hapusKendaraan(id: Int) {
        viewModelScope.launch {
            _aksiState.value = AksiKendaraanState.Loading

            val result = repositoryKendaraan.deleteKendaraanApi(id)

            result.fold(
                onSuccess = { message ->
                    _aksiState.value = AksiKendaraanState.Berhasil(message)
                    loadKendaraan()
                },
                onFailure = { error ->
                    _aksiState.value = AksiKendaraanState.Gagal(error.message ?: "Gagal menghapus kendaraan")
                }
            )
        }
    }

    fun resetState() {
        _aksiState.value = AksiKendaraanState.Idle
    }
}

/**
 * State untuk list kendaraan
 */
sealed class ListKendaraanState {
    object Loading : ListKendaraanState()
    data class Success(val data: List<KendaraanResponse>) : ListKendaraanState()
    data class Error(val message: String) : ListKendaraanState()
}

/**
 * State untuk aksi CRUD
 */
sealed class AksiKendaraanState {
    object Idle : AksiKendaraanState()
    object Loading : AksiKendaraanState()
    data class Berhasil(val message: String) : AksiKendaraanState()
    data class Gagal(val pesan: String) : AksiKendaraanState()
}
