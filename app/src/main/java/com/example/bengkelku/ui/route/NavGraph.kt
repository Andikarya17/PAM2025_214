package com.example.bengkelku.ui.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bengkelku.di.ContainerApp
import com.example.bengkelku.di.ViewModelFactory
import com.example.bengkelku.ui.view.auth.LoginScreen
import com.example.bengkelku.ui.view.auth.RegisterScreen
import com.example.bengkelku.ui.view.booking.BookingScreen
import com.example.bengkelku.ui.view.booking.BuatBookingScreen
import com.example.bengkelku.ui.view.dashboard.DashboardAdminScreen
import com.example.bengkelku.ui.view.dashboard.DashboardPelangganScreen
import com.example.bengkelku.ui.view.kendaraan.KendaraanScreen
import com.example.bengkelku.ui.view.kendaraan.TambahKendaraanScreen
import com.example.bengkelku.ui.view.servis.KelolaServisScreen
import com.example.bengkelku.ui.view.slotservis.KelolaSlotServisScreen
import com.example.bengkelku.viewmodel.auth.LoginViewModel
import com.example.bengkelku.viewmodel.auth.RegisterViewModel
import com.example.bengkelku.viewmodel.booking.BookingViewModel
import com.example.bengkelku.viewmodel.dashboard.DashboardAdminViewModel
import com.example.bengkelku.viewmodel.dashboard.DashboardPelangganViewModel
import com.example.bengkelku.viewmodel.kendaraan.KendaraanViewModel
import com.example.bengkelku.viewmodel.servis.ServisAdminViewModel
import com.example.bengkelku.viewmodel.slotservis.SlotServisViewModel


@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    containerApp: ContainerApp,
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    dashboardAdminViewModel: DashboardAdminViewModel,
    dashboardPelangganViewModel: DashboardPelangganViewModel?,
    penggunaId: Int?,  // NULLABLE - null means not logged in
    onLoginSuccess: (penggunaId: Int) -> Unit,
    onLogout: () -> Unit
) {
    // Feature-specific factories
    val factoryAdmin = remember { ViewModelFactory(containerApp) }
    
    // Factory untuk user - only created when penggunaId is valid
    val factoryUser = remember(penggunaId) {
        penggunaId?.let { id ->
            ViewModelFactory(containerApp, id)
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoute.LOGIN
    ) {

        // ===== LOGIN =====
        composable(NavRoute.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { loggedInPenggunaId, isAdmin ->
                    // Update penggunaId via callback ke MainActivity
                    onLoginSuccess(loggedInPenggunaId)
                    
                    if (isAdmin) {
                        navController.navigate(NavRoute.DASHBOARD_ADMIN) {
                            popUpTo(NavRoute.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(NavRoute.DASHBOARD_PELANGGAN) {
                            popUpTo(NavRoute.LOGIN) { inclusive = true }
                        }
                    }
                },
                onNavigateRegister = {
                    navController.navigate(NavRoute.REGISTER)
                }
            )
        }

        // ===== REGISTER =====
        composable(NavRoute.REGISTER) {
            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ===== DASHBOARD ADMIN =====
        composable(NavRoute.DASHBOARD_ADMIN) {
            DashboardAdminScreen(
                viewModel = dashboardAdminViewModel,
                onKelolaServis = {
                    navController.navigate(NavRoute.KELOLA_SERVIS)
                },
                onKelolaSlotServis = {
                    navController.navigate(NavRoute.KELOLA_SLOT_SERVIS)
                }
            )
        }

        // ===== KELOLA SERVIS (Admin) =====
        composable(NavRoute.KELOLA_SERVIS) {
            val servisAdminViewModel: ServisAdminViewModel = viewModel(factory = factoryAdmin)
            KelolaServisScreen(
                viewModel = servisAdminViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ===== KELOLA SLOT SERVIS (Admin) =====
        composable(NavRoute.KELOLA_SLOT_SERVIS) {
            val slotServisViewModel: SlotServisViewModel = viewModel(factory = factoryAdmin)
            KelolaSlotServisScreen(
                viewModel = slotServisViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ===== DASHBOARD PELANGGAN =====
        composable(NavRoute.DASHBOARD_PELANGGAN) {
            // BLOCK: Must be logged in
            if (penggunaId == null || dashboardPelangganViewModel == null) {
                // Redirect to login
                navController.navigate(NavRoute.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
                return@composable
            }

            DashboardPelangganScreen(
                viewModel = dashboardPelangganViewModel,
                onKelolaKendaraan = {
                    navController.navigate(NavRoute.KENDARAAN)
                },
                onBuatBooking = {
                    navController.navigate(NavRoute.BOOKING)
                }
            )
        }

        // ===== KENDARAAN =====
        composable(NavRoute.KENDARAAN) {
            // BLOCK: Must be logged in
            if (penggunaId == null || factoryUser == null) {
                navController.navigate(NavRoute.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
                return@composable
            }

            val kendaraanViewModel: KendaraanViewModel = viewModel(factory = factoryUser)
            KendaraanScreen(
                viewModel = kendaraanViewModel,
                onTambahKendaraan = {
                    navController.navigate(NavRoute.TAMBAH_KENDARAAN)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ===== TAMBAH KENDARAAN =====
        composable(NavRoute.TAMBAH_KENDARAAN) {
            // BLOCK: Must be logged in
            if (penggunaId == null || factoryUser == null) {
                navController.navigate(NavRoute.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
                return@composable
            }

            val kendaraanViewModel: KendaraanViewModel = viewModel(factory = factoryUser)
            TambahKendaraanScreen(
                viewModel = kendaraanViewModel,
                penggunaId = penggunaId,
                onSelesai = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        // ===== BOOKING =====
        composable(NavRoute.BOOKING) {
            // BLOCK: Must be logged in
            if (penggunaId == null || factoryUser == null) {
                navController.navigate(NavRoute.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
                return@composable
            }

            val bookingViewModel: BookingViewModel = viewModel(factory = factoryUser)
            BookingScreen(
                viewModel = bookingViewModel,
                onBuatBooking = {
                    navController.navigate(NavRoute.BUAT_BOOKING)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ===== BUAT BOOKING =====
        composable(NavRoute.BUAT_BOOKING) {
            // BLOCK: Must be logged in
            if (penggunaId == null || factoryUser == null) {
                navController.navigate(NavRoute.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
                return@composable
            }

            val bookingViewModel: BookingViewModel = viewModel(factory = factoryUser)
            val kendaraanViewModel: KendaraanViewModel = viewModel(factory = factoryUser)
            BuatBookingScreen(
                bookingViewModel = bookingViewModel,
                kendaraanViewModel = kendaraanViewModel,
                penggunaId = penggunaId,
                onSelesai = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

    }
}
