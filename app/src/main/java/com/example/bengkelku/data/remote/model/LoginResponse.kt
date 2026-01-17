package com.example.bengkelku.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Login response data from backend.
 * All fields have safe defaults to prevent Gson deserialization crashes
 * when backend returns null or omits fields.
 */
data class LoginResponse(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("nama")
    val nama: String = "",

    @SerializedName("username")
    val username: String = "",

    @SerializedName("role")
    val role: String = ""
)
