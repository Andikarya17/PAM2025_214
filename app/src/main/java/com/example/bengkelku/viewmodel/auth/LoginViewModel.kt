package com.example.bengkelku.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.local.entity.Pengguna
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
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Username dan password wajib diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            val pengguna = repositoryAuth.login(username, password)

            if (pengguna != null) {
                _uiState.value = LoginUiState.Success(pengguna)
            } else {
                _uiState.value = LoginUiState.Error("Username atau password salah")
            }
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
