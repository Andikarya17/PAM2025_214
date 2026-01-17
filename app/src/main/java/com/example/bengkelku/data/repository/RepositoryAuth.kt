package com.example.bengkelku.data.repository

import com.example.bengkelku.data.local.dao.PenggunaDao
import com.example.bengkelku.data.local.entity.Pengguna
import com.example.bengkelku.data.local.entity.RolePengguna
import com.example.bengkelku.data.remote.ApiResponse
import com.example.bengkelku.data.remote.ApiService
import com.example.bengkelku.data.remote.model.LoginResponse
import com.example.bengkelku.data.remote.model.PenggunaResponse

class RepositoryAuth(
    private val penggunaDao: PenggunaDao,
    private val apiService: ApiService
) {

    // ===== LOCAL (Room) =====

    /**
     * Get pengguna by username from local database.
     * Used for checking login state.
     */
    suspend fun getPenggunaByUsername(username: String): Pengguna? {
        return penggunaDao.getByUsername(username)
    }

    /**
     * Get pengguna by id from local database.
     */
    suspend fun getPenggunaById(id: Int): Pengguna? {
        return penggunaDao.getById(id)
    }

    /**
     * Save pengguna to local database (upsert).
     */
    suspend fun savePenggunaLocal(pengguna: Pengguna) {
        penggunaDao.upsert(pengguna)
    }

    /**
     * Check if pengguna is admin.
     */
    fun isAdmin(pengguna: Pengguna): Boolean {
        return pengguna.role == RolePengguna.ADMIN
    }

    // ===== REMOTE (API) =====

    /**
     * Login via API with proper HTTP error handling.
     * @throws Exception with error message if login fails
     */
    suspend fun loginApi(
        username: String,
        password: String
    ): ApiResponse<LoginResponse> {
        val response = apiService.login(username.trim(), password.trim())

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody) ?: "Login gagal"
            throw Exception(errorMessage)
        }
    }

    /**
     * Convert LoginResponse to Pengguna entity for local storage.
     */
    fun loginResponseToPengguna(loginResponse: LoginResponse): Pengguna {
        return Pengguna(
            id = loginResponse.id,
            nama = loginResponse.nama,
            username = loginResponse.username,
            password = "",  // Don't store password locally
            role = RolePengguna.fromString(loginResponse.role)
        )
    }

    /**
     * Register via API with proper HTTP error handling.
     * Now uses Response wrapper for consistent error handling.
     * @throws Exception with error message if registration fails
     */
    suspend fun registerApi(
        nama: String,
        username: String,
        password: String
    ): ApiResponse<PenggunaResponse> {
        val response = apiService.register(
            nama = nama.trim(),
            username = username.trim(),
            password = password.trim()
        )

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody) ?: "Register gagal"
            throw Exception(errorMessage)
        }
    }

    /**
     * Convert PenggunaResponse to Pengguna entity for local storage.
     */
    fun penggunaResponseToPengguna(response: PenggunaResponse): Pengguna {
        return Pengguna(
            id = response.id,
            nama = response.nama,
            username = response.username,
            password = "",  // Don't store password locally
            role = RolePengguna.fromString(response.role)
        )
    }

    /**
     * Attempt to parse error message from JSON error body.
     * Returns null if parsing fails.
     */
    private fun parseErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null

        return try {
            // Try to parse as JSON and extract message
            val gson = com.google.gson.Gson()
            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
            errorResponse.message?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            // If parsing fails, return raw error body (truncated)
            errorBody.take(100)
        }
    }
}
