package com.example.bengkelku.ui.view.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.ui.view.components.BaseScaffold
import com.example.bengkelku.viewmodel.booking.BookingViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun BookingScreen(
    viewModel: BookingViewModel,
    onBuatBooking: () -> Unit,
    onBack: () -> Unit
) {
    // Safely handle nullable Flow - use empty flow as fallback
    val bookingAktifFlow = viewModel.bookingAktif ?: flowOf(emptyList())
    val riwayatBookingFlow = viewModel.riwayatBooking ?: flowOf(emptyList())

    val bookingAktif by bookingAktifFlow.collectAsState(initial = emptyList())
    val riwayatBooking by riwayatBookingFlow.collectAsState(initial = emptyList())

    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Aktif", "Riwayat")

    BaseScaffold(
        title = "Booking Servis",
        showBack = true,
        onBack = onBack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Button(
                onClick = onBuatBooking,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buat Booking Baru")
            }

            Spacer(Modifier.height(16.dp))

            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            when (tabIndex) {
                0 -> {
                    LazyColumn {
                        items(bookingAktif) { booking ->
                            BookingItem(booking)
                        }
                    }
                }
                1 -> {
                    LazyColumn {
                        items(riwayatBooking) { booking ->
                            BookingItem(booking)
                        }
                    }
                }
            }
        }
    }
}
