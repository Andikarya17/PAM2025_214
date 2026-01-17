package com.example.bengkelku.data.remote.model

import com.google.gson.annotations.SerializedName

data class PenggunaResponse(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("nama")
    val nama: String = "",

    @SerializedName("username")
    val username: String = "",

    @SerializedName("role")
    val role: String = ""
)
