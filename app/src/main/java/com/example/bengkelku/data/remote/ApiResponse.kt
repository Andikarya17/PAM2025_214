package com.example.bengkelku.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Generic API response wrapper matching PHP backend format
 */
data class ApiResponse<T>(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T?
) {
    val isSuccess: Boolean
        get() = status == "success"
}
