package com.example.bengkelku.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Generic API response wrapper matching PHP backend format.
 * All fields are null-safe to prevent Gson deserialization crashes.
 */
data class ApiResponse<T>(
    @SerializedName("status")
    val status: String = "",

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null
) {
    /**
     * Check if response indicates success.
     * Case-insensitive comparison for robustness.
     */
    val isSuccess: Boolean
        get() = status.equals("success", ignoreCase = true)

    /**
     * Get message with fallback for null/blank cases.
     */
    fun getMessageOrDefault(default: String): String {
        return message?.takeIf { it.isNotBlank() } ?: default
    }
}
