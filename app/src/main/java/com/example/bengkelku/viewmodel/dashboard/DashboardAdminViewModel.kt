package com.example.bengkelku.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import com.example.bengkelku.data.repository.RepositoryBooking
import com.example.bengkelku.data.repository.RepositoryServis
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope

class DashboardAdminViewModel(
    repositoryBooking: RepositoryBooking,
    repositoryServis: RepositoryServis
) : ViewModel() {

    val semuaBooking = repositoryBooking
        .getSemuaBooking()
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
}
