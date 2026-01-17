package com.example.bengkelku.ui.view.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.bengkelku.data.remote.model.BookingResponse
import com.example.bengkelku.data.remote.model.KendaraanResponse
import com.example.bengkelku.ui.view.components.BaseScaffold
import com.example.bengkelku.viewmodel.dashboard.DashboardBookingState
import com.example.bengkelku.viewmodel.dashboard.DashboardKendaraanState
import com.example.bengkelku.viewmodel.dashboard.DashboardPelangganViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun DashboardPelangganScreen(
    viewModel: DashboardPelangganViewModel,
    onKelolaKendaraan: () -> Unit,
    onBuatBooking: () -> Unit
) {
    val kendaraanState by viewModel.kendaraanState.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()
    
    // Auto-refresh when screen becomes visible (after returning from other screens)
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            viewModel.loadData()
        }
    }

    BaseScaffold(
        title = "Dashboard Pelanggan",
        showBack = false
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Button(
                onClick = onKelolaKendaraan,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kelola Kendaraan")
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onBuatBooking,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buat Booking Servis")
            }

            Spacer(Modifier.height(16.dp))

            Text("Kendaraan Saya", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // Kendaraan Section
            when (val state = kendaraanState) {
                is DashboardKendaraanState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
                is DashboardKendaraanState.Success -> {
                    if (state.data.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada kendaraan",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(state.data, key = { it.id }) { kendaraan ->
                                KendaraanItemPelanggan(kendaraan)
                            }
                        }
                    }
                }
                is DashboardKendaraanState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        TextButton(onClick = { viewModel.loadData() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Booking Aktif", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // Booking Section
            when (val state = bookingState) {
                is DashboardBookingState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
                is DashboardBookingState.Success -> {
                    if (state.data.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak ada booking aktif",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(state.data, key = { it.id }) { booking ->
                                BookingItemPelanggan(booking)
                            }
                        }
                    }
                }
                is DashboardBookingState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        TextButton(onClick = { viewModel.loadData() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KendaraanItemPelanggan(kendaraan: KendaraanResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = "${kendaraan.merk} ${kendaraan.model}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Plat: ${kendaraan.nomorPlat}",
                style = MaterialTheme.typography.bodyMedium
            )
            kendaraan.tahun?.let {
                Text(
                    text = "Tahun: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BookingItemPelanggan(booking: BookingResponse) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = "Antrian: ${booking.nomorAntrian}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Tanggal: ${booking.tanggalServis}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Status with color
            val statusColor = when (booking.status.uppercase()) {
                "MENUNGGU" -> MaterialTheme.colorScheme.tertiary
                "DIPROSES" -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface
            }
            Text(
                text = "Status: ${booking.status}",
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor
            )
            
            Text(
                text = "Total: ${currencyFormat.format(booking.totalBiaya)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
