package com.example.bengkelku.data.repository

import com.example.bengkelku.data.local.dao.PenggunaDao
import com.example.bengkelku.data.local.entity.Pengguna
import com.example.bengkelku.data.local.entity.RolePengguna

/**
 * Repository untuk autentikasi:
 * - Login admin & pelanggan
 * - Register pelanggan
 *
 * Catatan:
 * - Admin TIDAK bisa register
 * - Admin sudah di-seed di database saat pertama kali install
 */
class RepositoryAuth(
    private val penggunaDao: PenggunaDao
) {

    /**
     * Login untuk semua role.
     * Mengembalikan Pengguna jika sukses, null jika gagal.
     */
    suspend fun login(
        username: String,
        password: String
    ): Pengguna? {
        return penggunaDao.login(username, password)
    }

    /**
     * Register hanya untuk pelanggan.
     * Return:
     * - true  -> register berhasil
     * - false -> username sudah dipakai / role tidak valid
     */
    suspend fun register(
        nama: String,
        username: String,
        password: String
    ): Boolean {
        // Cegah register admin (aturan bisnis)
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
            // Biasanya karena username duplikat (unique constraint)
            false
        }
    }

    /**
     * Helper untuk cek apakah pengguna adalah admin.
     * Dipakai di ViewModel / Navigation.
     */
    fun isAdmin(pengguna: Pengguna): Boolean {
        return pengguna.role == RolePengguna.ADMIN
    }
}
