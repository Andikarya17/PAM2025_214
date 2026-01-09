package com.example.bengkelku.ui.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bengkelku.ui.view.auth.LoginScreen
import com.example.bengkelku.ui.view.auth.RegisterScreen
import com.example.bengkelku.ui.view.dashboard.DashboardAdminScreen
import com.example.bengkelku.ui.view.dashboard.DashboardPelangganScreen
import com.example.bengkelku.ui.view.kendaraan.KendaraanScreen
import com.example.bengkelku.ui.view.kendaraan.TambahKendaraanScreen
import com.example.bengkelku.viewmodel.auth.LoginViewModel
import com.example.bengkelku.viewmodel.auth.RegisterViewModel
import com.example.bengkelku.viewmodel.dashboard.DashboardAdminViewModel
import com.example.bengkelku.viewmodel.dashboard.DashboardPelangganViewModel
import com.example.bengkelku.viewmodel.kendaraan.KendaraanViewModel


@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    dashboardAdminViewModel: DashboardAdminViewModel,
    dashboardPelangganViewModel: DashboardPelangganViewModel,
    kendaraanViewModel: KendaraanViewModel,
    penggunaId: Int
) {

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
                viewModel = dashboardAdminViewModel
            )

        }

        // ===== DASHBOARD PELANGGAN =====
        composable(NavRoute.DASHBOARD_PELANGGAN) {
            DashboardPelangganScreen(
                viewModel = dashboardPelangganViewModel,
                onKelolaKendaraan = { /* navigate ke kendaraan */ },
                onBuatBooking = { /* navigate ke booking */ }
            )
        }
        composable(NavRoute.KENDARAAN) {
            KendaraanScreen(
                viewModel = kendaraanViewModel,
                onTambahKendaraan = {
                    navController.navigate(NavRoute.TAMBAH_KENDARAAN)
                }
            )
        }

        composable(NavRoute.TAMBAH_KENDARAAN) {
            TambahKendaraanScreen(
                viewModel = kendaraanViewModel,
                penggunaId = penggunaId,
                onSelesai = { navController.popBackStack() }
            )
        }

    }
}
