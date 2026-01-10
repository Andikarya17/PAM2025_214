package com.example.bengkelku.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bengkelku.data.local.dao.BookingWithDetails
import com.example.bengkelku.data.local.entity.Booking
import com.example.bengkelku.data.local.entity.StatusBooking
import com.example.bengkelku.data.repository.RepositoryBooking
import com.example.bengkelku.data.repository.RepositoryServis
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardAdminViewModel(
    private val repositoryBooking: RepositoryBooking,
    repositoryServis: RepositoryServis
) : ViewModel() {

    // Semua booking dengan detail untuk Admin
    val semuaBookingWithDetails = repositoryBooking
        .getSemuaBookingWithDetails()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val semuaServis = repositoryServis
        .getAllServis()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    // Update status booking
    fun updateBookingStatus(bookingWithDetails: BookingWithDetails, newStatus: StatusBooking) {
        viewModelScope.launch {
            val booking = repositoryBooking.getBookingById(bookingWithDetails.id)
            booking?.let {
                val updatedBooking = it.copy(status = newStatus)
                repositoryBooking.updateBooking(updatedBooking)
            }
        }
    }
}
