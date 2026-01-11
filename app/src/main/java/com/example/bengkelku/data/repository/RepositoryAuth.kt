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

    suspend fun login(
        username: String,
        password: String
    ): Pengguna? {
        return penggunaDao.login(username, password)
    }

    suspend fun register(
        nama: String,
        username: String,
        password: String
    ): Boolean {
        val penggunaBaru = Pengguna(
            nama = nama,
            username = username,
            password = password,
            role = RolePengguna.PELANGGAN
        )

        return try {
            penggunaDao.insert(penggunaBaru)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isAdmin(pengguna: Pengguna): Boolean {
        return pengguna.role == RolePengguna.ADMIN
    }

    // ===== REMOTE (API) =====

    suspend fun loginApi(
        username: String,
        password: String
    ): ApiResponse<LoginResponse> {
        return apiService.login(username, password)
    }

    suspend fun registerApi(
        nama: String,
        username: String,
        password: String
    ): ApiResponse<PenggunaResponse> {
        return apiService.register(nama, username, password)
    }
}
