package com.example.bengkelku.ui.view.booking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.local.entity.Booking

@Composable
fun BookingItem(
    booking: Booking
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text("Antrian: ${booking.nomorAntrian}")
            Text("Tanggal: ${booking.tanggalServis} ${booking.jamServis}")
            Text("Status: ${booking.status.name}")
            Text("Total: Rp ${booking.totalBiaya}")
        }
    }
}
