package com.example.bengkelku.data.remote.model

import com.google.gson.annotations.SerializedName

data class ServisResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nama_servis")
    val namaServis: String,

    @SerializedName("harga")
    val harga: Int,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("aktif")
    val aktif: Boolean
)
