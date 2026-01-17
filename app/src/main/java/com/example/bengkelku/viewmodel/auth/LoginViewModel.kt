package com.example.bengkelku.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.local.entity.Pengguna
import com.example.bengkelku.data.local.entity.RolePengguna
import com.example.bengkelku.data.repository.RepositoryAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repositoryAuth: RepositoryAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(username: String, password: String) {
        // Trim inputs to prevent whitespace mismatch
        val cleanUsername = username.trim()
        val cleanPassword = password.trim()

        if (cleanUsername.isBlank() || cleanPassword.isBlank()) {
            _uiState.value = LoginUiState.Error("Username dan password wajib diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            try {
                // ===== LOGIN VIA API (MYSQL) =====
                val response = repositoryAuth.loginApi(
                    username = cleanUsername,
                    password = cleanPassword
                )

                if (response.isSuccess && response.data != null) {
                    val data = response.data

                    // Safe role mapping with fallback
                    val role = parseRoleSafely(data.role)

                    // Mapping API -> Entity lokal
                    val pengguna = Pengguna(
                        id = data.id,
                        nama = data.nama,
                        username = data.username,
                        password = "", // password tidak disimpan
                        role = role
                    )

                    _uiState.value = LoginUiState.Success(pengguna)
                } else {
                    _uiState.value = LoginUiState.Error(
                        response.getMessageOrDefault("Username atau password salah")
                    )
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(
                    e.message ?: "Terjadi kesalahan koneksi"
                )
            }
        }
    }

    /**
     * Safely parse role string to RolePengguna enum.
     * Falls back to PELANGGAN if role is unknown or invalid.
     */
    private fun parseRoleSafely(role: String): RolePengguna {
        return try {
            val normalized = role.trim().uppercase()
            when (normalized) {
                "ADMIN" -> RolePengguna.ADMIN
                "PELANGGAN" -> RolePengguna.PELANGGAN
                else -> {
                    // Log unknown role for debugging (optional)
                    // Log.w("LoginViewModel", "Unknown role: $role, defaulting to PELANGGAN")
                    RolePengguna.PELANGGAN
                }
            }
        } catch (e: Exception) {
            RolePengguna.PELANGGAN
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}

/**
 * State untuk LoginScreen
 */
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val pengguna: Pengguna) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
