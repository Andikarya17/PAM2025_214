package com.example.bengkelku.ui.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    dashboardPelangganViewModel: DashboardPelangganViewModel,
    penggunaId: Int
) {
    // Feature-specific factories
    val factoryAdmin = remember { ViewModelFactory(containerApp) }
    val factoryUser = remember { ViewModelFactory(containerApp, penggunaId) }

    NavHost(
        navController = navController,
        startDestination = NavRoute.LOGIN
    ) {

        // ===== LOGIN =====
        composable(NavRoute.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { _, isAdmin ->
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

        composable(NavRoute.KENDARAAN) {
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


        composable(NavRoute.TAMBAH_KENDARAAN) {
            val kendaraanViewModel: KendaraanViewModel = viewModel(factory = factoryUser)
            TambahKendaraanScreen(
                viewModel = kendaraanViewModel,
                penggunaId = penggunaId,
                onSelesai = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoute.BOOKING) {
            val bookingViewModel: BookingViewModel = viewModel(factory = factoryUser)
            BookingScreen(
                viewModel = bookingViewModel,
                onBuatBooking = {
                    navController.navigate(NavRoute.BUAT_BOOKING)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoute.BUAT_BOOKING) {
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
