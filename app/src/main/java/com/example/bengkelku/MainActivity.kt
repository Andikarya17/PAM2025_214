package com.example.bengkelku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.example.bengkelku.di.ContainerApp
import com.example.bengkelku.di.ViewModelFactory
import com.example.bengkelku.ui.route.NavGraph
import com.example.bengkelku.ui.theme.BengkelKuTheme
import com.example.bengkelku.viewmodel.auth.LoginViewModel
import com.example.bengkelku.viewmodel.auth.RegisterViewModel
import com.example.bengkelku.viewmodel.dashboard.DashboardAdminViewModel
import com.example.bengkelku.viewmodel.dashboard.DashboardPelangganViewModel


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val containerApp = ContainerApp(applicationContext)

        val factoryAdmin = ViewModelFactory(containerApp)

        // Global ViewModels (tidak perlu penggunaId)
        val loginViewModel = ViewModelProvider(this, factoryAdmin)
            .get(LoginViewModel::class.java)

        val registerViewModel = ViewModelProvider(this, factoryAdmin)
            .get(RegisterViewModel::class.java)

        val dashboardAdminViewModel = ViewModelProvider(this, factoryAdmin)
            .get(DashboardAdminViewModel::class.java)

        setContent {
            BengkelKuTheme {
                // NULLABLE - null means not logged in, NEVER use 0
                var loggedInPenggunaId: Int? by remember { mutableStateOf(null) }

                // DashboardPelangganViewModel only created when logged in
                val dashboardPelangganViewModel = remember(loggedInPenggunaId) {
                    loggedInPenggunaId?.let { id ->
                        DashboardPelangganViewModel(
                            id,
                            containerApp.repositoryBooking,
                            containerApp.repositoryKendaraan
                        )
                    }
                }

                NavGraph(
                    containerApp = containerApp,
                    loginViewModel = loginViewModel,
                    registerViewModel = registerViewModel,
                    dashboardAdminViewModel = dashboardAdminViewModel,
                    dashboardPelangganViewModel = dashboardPelangganViewModel,
                    penggunaId = loggedInPenggunaId,
                    onLoginSuccess = { penggunaId ->
                        // Set actual logged in user ID
                        loggedInPenggunaId = penggunaId
                    },
                    onLogout = {
                        // Clear session
                        loggedInPenggunaId = null
                    }
                )
            }
        }
    }
}
