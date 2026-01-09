package com.example.bengkelku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pengguna")
data class Pengguna(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

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
    PELANGGAN
}
