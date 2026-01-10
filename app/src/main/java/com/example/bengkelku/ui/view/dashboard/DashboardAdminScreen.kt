package com.example.bengkelku.ui.view.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.local.dao.BookingWithDetails
import com.example.bengkelku.data.local.entity.StatusBooking
import com.example.bengkelku.ui.view.components.BaseScaffold
import com.example.bengkelku.viewmodel.dashboard.DashboardAdminViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun DashboardAdminScreen(
    viewModel: DashboardAdminViewModel,
    onKelolaServis: () -> Unit,
    onKelolaSlotServis: () -> Unit
) {
    val semuaBookingWithDetails by viewModel.semuaBookingWithDetails.collectAsState()

    BaseScaffold(
        title = "Dashboard Admin",
        showBack = false
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Admin Management Buttons
            Text("Manajemen", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onKelolaServis,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Kelola Servis")
                }

                OutlinedButton(
                    onClick = onKelolaSlotServis,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Kelola Slot")
                }
            }

            Spacer(Modifier.height(24.dp))

            // Booking Monitoring Section
            Text(
                "Monitoring Booking (${semuaBookingWithDetails.size})",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            if (semuaBookingWithDetails.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Belum ada booking masuk",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(semuaBookingWithDetails) { booking ->
                        BookingItemAdmin(
                            booking = booking,
                            onStatusChange = { newStatus ->
                                viewModel.updateBookingStatus(booking, newStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingItemAdmin(
    booking: BookingWithDetails,
    onStatusChange: (StatusBooking) -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    var expanded by remember { mutableStateOf(false) }

    // Status options as per requirement
    val statusOptions = when (booking.status) {
        StatusBooking.MENUNGGU -> listOf(
            StatusBooking.MENUNGGU,
            StatusBooking.DIPROSES
        )
        StatusBooking.DIPROSES -> listOf(
            StatusBooking.DIPROSES,
            StatusBooking.SELESAI
        )
        StatusBooking.SELESAI -> listOf(
            StatusBooking.SELESAI
        )
        else -> listOf(booking.status)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            // Customer name
            Text(
                "Customer: ${booking.namaPengguna}",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(Modifier.height(4.dp))

            // Vehicle info
            Text(
                "Kendaraan: ${booking.merkKendaraan} ${booking.modelKendaraan} - ${booking.nomorPlat}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Service name
            Text(
                "Servis: ${booking.namaServis}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Date & Time
            Text(
                "Jadwal: ${booking.tanggalServis} ${booking.jamServis}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Queue number
            Text(
                "Antrian: ${booking.nomorAntrian}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Total
            Text(
                "Total: ${currencyFormat.format(booking.totalBiaya)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            // Status dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Status:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alignByBaseline()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    OutlinedTextField(
                        value = booking.status.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = MaterialTheme.typography.bodySmall,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = when (booking.status) {
                                StatusBooking.MENUNGGU -> MaterialTheme.colorScheme.tertiaryContainer
                                StatusBooking.DIPROSES -> MaterialTheme.colorScheme.primaryContainer
                                StatusBooking.SELESAI -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statusOptions.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name) },
                                onClick = {
                                    onStatusChange(status)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
