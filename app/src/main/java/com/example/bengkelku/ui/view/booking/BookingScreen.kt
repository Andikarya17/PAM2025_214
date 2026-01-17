package com.example.bengkelku.ui.view.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.remote.model.BookingStatus
import com.example.bengkelku.ui.view.components.BaseScaffold
import com.example.bengkelku.viewmodel.booking.BookingViewModel
import com.example.bengkelku.viewmodel.booking.ListBookingState

@Composable
fun BookingScreen(
    viewModel: BookingViewModel,
    onBuatBooking: () -> Unit,
    onBack: () -> Unit
) {
    val bookingState by viewModel.bookingState.collectAsState()

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

            when (val state = bookingState) {
                is ListBookingState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ListBookingState.Success -> {
                    // Filter based on tab using BookingStatus enum (case-insensitive)
                    val bookings = when (tabIndex) {
                        0 -> state.data.filter { booking ->
                            val status = BookingStatus.fromString(booking.status)
                            BookingStatus.isActive(status)
                        }
                        else -> state.data.filter { booking ->
                            val status = BookingStatus.fromString(booking.status)
                            BookingStatus.isHistory(status)
                        }
                    }

                    if (bookings.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (tabIndex == 0) "Tidak ada booking aktif" else "Belum ada riwayat",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn {
                            items(bookings, key = { it.id }) { booking ->
                                BookingItem(booking)
                            }
                        }
                    }
                }
                is ListBookingState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadData() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }
    }
}
