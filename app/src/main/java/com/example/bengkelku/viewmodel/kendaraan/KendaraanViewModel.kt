package com.example.bengkelku.viewmodel.kendaraan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.local.entity.Kendaraan
import com.example.bengkelku.data.repository.RepositoryKendaraan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KendaraanViewModel(
    private val penggunaId: Int,
    private val repositoryKendaraan: RepositoryKendaraan
) : ViewModel() {

    val daftarKendaraan = repositoryKendaraan
        .getKendaraanByPengguna(penggunaId)

    private val _aksiState = MutableStateFlow<AksiKendaraanState>(AksiKendaraanState.Idle)
    val aksiState: StateFlow<AksiKendaraanState> = _aksiState

    fun tambahKendaraan(kendaraan: Kendaraan) {
        viewModelScope.launch {
            // Block if penggunaId invalid
            if (kendaraan.penggunaId <= 0) {
                _aksiState.value = AksiKendaraanState.Gagal("Sesi login tidak valid")
                return@launch
            }

            // Check duplicate per customer
            val sudahAda = repositoryKendaraan.isNomorPlatExistsForPengguna(
                kendaraan.penggunaId,
                kendaraan.nomorPlat
            )
            if (sudahAda) {
                _aksiState.value = AksiKendaraanState.Gagal("Nomor plat sudah terdaftar untuk akun Anda")
                return@launch
            }

            repositoryKendaraan.tambahKendaraan(kendaraan)
            _aksiState.value = AksiKendaraanState.Berhasil
        }
    }

    fun updateKendaraan(kendaraan: Kendaraan) {
        viewModelScope.launch {
            repositoryKendaraan.updateKendaraan(kendaraan)
            _aksiState.value = AksiKendaraanState.Berhasil
        }
    }

    fun hapusKendaraan(kendaraan: Kendaraan) {
        viewModelScope.launch {
            repositoryKendaraan.hapusKendaraan(kendaraan)
            _aksiState.value = AksiKendaraanState.Berhasil
        }
    }

    fun resetState() {
        _aksiState.value = AksiKendaraanState.Idle
    }
}

sealed class AksiKendaraanState {
    object Idle : AksiKendaraanState()
    object Berhasil : AksiKendaraanState()
    data class Gagal(val pesan: String) : AksiKendaraanState()
}
