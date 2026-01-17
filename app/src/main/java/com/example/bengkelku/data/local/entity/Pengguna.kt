package com.example.bengkelku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Pengguna entity - stores user from backend
 * PrimaryKey is from backend (not autoGenerate)
 */
@Entity(tableName = "pengguna")
data class Pengguna(

    @PrimaryKey
    val id: Int,  // From backend, not autoGenerate

    val nama: String,

    val username: String,

    val password: String,

    val role: RolePengguna
)

/**
 * Role pengguna dalam sistem BengkelKu
 * ADMIN     -> akses dashboard admin
 * PELANGGAN -> akses dashboard pelanggan
 */
enum class RolePengguna {
    ADMIN,
    PELANGGAN;
    
    companion object {
        fun fromString(value: String): RolePengguna {
            return when (value.uppercase()) {
                "ADMIN" -> ADMIN
                "CUSTOMER", "PELANGGAN" -> PELANGGAN
                else -> PELANGGAN
            }
        }
    }
}
