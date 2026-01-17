package com.example.bengkelku.data.remote.model

/**
 * Enum for booking status with safe parsing.
 * Handles both uppercase and lowercase from backend.
 */
enum class BookingStatus(val value: String) {
    MENUNGGU("MENUNGGU"),
    DIPROSES("DIPROSES"),
    SELESAI("SELESAI"),
    DIBATALKAN("DIBATALKAN"),
    UNKNOWN("UNKNOWN");

    companion object {
        /**
         * Safely parse status string from backend.
         * Case-insensitive, returns UNKNOWN for unrecognized values.
         */
        fun fromString(status: String?): BookingStatus {
            if (status.isNullOrBlank()) return UNKNOWN
            
            return when (status.uppercase().trim()) {
                "MENUNGGU" -> MENUNGGU
                "DIPROSES", "DALAM_PROSES" -> DIPROSES
                "SELESAI" -> SELESAI
                "DIBATALKAN", "BATAL" -> DIBATALKAN
                else -> UNKNOWN
            }
        }

        /**
         * Check if status is considered "active" (not finished)
         */
        fun isActive(status: BookingStatus): Boolean {
            return status == MENUNGGU || status == DIPROSES
        }

        /**
         * Check if status is considered "history" (finished)
         */
        fun isHistory(status: BookingStatus): Boolean {
            return status == SELESAI || status == DIBATALKAN
        }
    }
}
