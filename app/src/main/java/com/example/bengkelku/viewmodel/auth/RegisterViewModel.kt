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
        if (nama.isBlank() || username.isBlank() || password.isBlank()) {
            _uiState.value = RegisterUiState.Error("Semua field wajib diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            val berhasil = repositoryAuth.register(
                nama = nama,
                username = username,
                password = password
            )

            if (berhasil) {
                _uiState.value = RegisterUiState.Success
            } else {
                _uiState.value = RegisterUiState.Error("Username sudah digunakan")
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
