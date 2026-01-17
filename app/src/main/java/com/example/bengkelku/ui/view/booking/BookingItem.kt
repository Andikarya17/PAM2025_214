package com.example.bengkelku.ui.view.booking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.remote.model.BookingResponse
import java.text.NumberFormat
import java.util.*

@Composable
fun BookingItem(
    booking: BookingResponse
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Antrian: ${booking.nomorAntrian}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Tanggal: ${booking.tanggalServis} ${booking.jamServis}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Show servis name if available
            booking.namaServis?.let { nama ->
                Text(
                    text = "Servis: $nama",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Status with color
            val statusColor = when (booking.status.uppercase()) {
                "MENUNGGU" -> MaterialTheme.colorScheme.tertiary
                "DIPROSES" -> MaterialTheme.colorScheme.primary
                "SELESAI" -> MaterialTheme.colorScheme.secondary
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
