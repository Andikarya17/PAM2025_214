package com.example.bengkelku.ui.view.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bengkelku.data.local.entity.Booking
import com.example.bengkelku.data.local.entity.Kendaraan
import com.example.bengkelku.viewmodel.dashboard.DashboardPelangganViewModel

@Composable
fun DashboardPelangganScreen(
    viewModel: DashboardPelangganViewModel,
    onKelolaKendaraan: () -> Unit,
    onBuatBooking: () -> Unit
) {
    val kendaraanSaya by viewModel.kendaraanSaya.collectAsState()
    val bookingAktif by viewModel.bookingAktif.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Dashboard Pelanggan",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

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

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(kendaraanSaya) { kendaraan ->
                KendaraanItemPelanggan(kendaraan)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Booking Aktif", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(bookingAktif) { booking ->
                BookingItemPelanggan(booking)
            }
        }
    }
}

@Composable
private fun KendaraanItemPelanggan(kendaraan: Kendaraan) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("${kendaraan.merk} ${kendaraan.model}")
            Text("Plat: ${kendaraan.nomorPlat}")
        }
    }
}

@Composable
private fun BookingItemPelanggan(booking: Booking) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Antrian: ${booking.nomorAntrian}")
            Text("Tanggal: ${booking.tanggalServis}")
            Text("Status: ${booking.status.name}")
        }
    }
}
