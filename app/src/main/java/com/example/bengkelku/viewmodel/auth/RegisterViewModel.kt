package com.example.bengkelku.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.repository.RepositoryAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repositoryAuth: RepositoryAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun register(
        nama: String,
        username: String,
        password: String
    ) {
        // Trim inputs to prevent whitespace issues
        val cleanNama = nama.trim()
        val cleanUsername = username.trim()
        val cleanPassword = password.trim()

        // Validasi input
        if (cleanNama.isBlank() || cleanUsername.isBlank() || cleanPassword.isBlank()) {
            _uiState.value = RegisterUiState.Error("Semua field wajib diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            try {
                // ===== REGISTER VIA API (MYSQL) =====
                val response = repositoryAuth.registerApi(
                    nama = cleanNama,
                    username = cleanUsername,
                    password = cleanPassword
                )

                if (response.isSuccess) {
                    _uiState.value = RegisterUiState.Success
                } else {
                    _uiState.value = RegisterUiState.Error(
                        response.getMessageOrDefault("Register gagal")
                    )
                }
            } catch (e: Exception) {
                _uiState.value = RegisterUiState.Error(
                    e.message ?: "Terjadi kesalahan koneksi"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}

/**
 * State untuk RegisterScreen
 */
sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}
