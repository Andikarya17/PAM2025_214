package com.example.bengkelku.ui.view.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.local.entity.Booking
import com.example.bengkelku.data.local.entity.Servis
import com.example.bengkelku.viewmodel.dashboard.DashboardAdminViewModel

@Composable
fun DashboardAdminScreen(
    viewModel: DashboardAdminViewModel
) {
    val semuaBooking by viewModel.semuaBooking.collectAsState()
    val semuaServis by viewModel.semuaServis.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Dashboard Admin",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        Text("Booking Masuk", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(semuaBooking) { booking ->
                BookingItemAdmin(booking)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Daftar Servis", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(semuaServis) { servis ->
                ServisItemAdmin(servis)
            }
        }
    }
}

@Composable
private fun BookingItemAdmin(booking: Booking) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Antrian: ${booking.nomorAntrian}")
            Text("Tanggal: ${booking.tanggalServis} ${booking.jamServis}")
            Text("Status: ${booking.status.name}")
        }
    }
}

@Composable
private fun ServisItemAdmin(servis: Servis) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(servis.namaServis, style = MaterialTheme.typography.titleMedium)
            Text("Harga: Rp ${servis.harga}")
            Text(if (servis.aktif) "Aktif" else "Nonaktif")
        }
    }
}
