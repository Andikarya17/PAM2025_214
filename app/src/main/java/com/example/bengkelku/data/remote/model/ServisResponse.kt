package com.example.bengkelku.data.remote.model

import com.google.gson.annotations.SerializedName

data class ServisResponse(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("nama_servis")
    val namaServis: String = "",

    @SerializedName("harga")
    val harga: Int = 0,

    @SerializedName("deskripsi")
    val deskripsi: String? = null,

    @SerializedName("is_active")
    val isActive: Boolean = true
)
