package com.example.bengkelku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

        // Contoh: penggunaId hasil login (sementara dummy)
        val penggunaId = 1

        val factoryAdmin = ViewModelFactory(containerApp)
        val factoryUser = ViewModelFactory(containerApp, penggunaId)

        val loginViewModel = ViewModelProvider(this, factoryAdmin)
            .get(LoginViewModel::class.java)

        val registerViewModel = ViewModelProvider(this, factoryAdmin)
            .get(RegisterViewModel::class.java)

        val dashboardAdminViewModel = ViewModelProvider(this, factoryAdmin)
            .get(DashboardAdminViewModel::class.java)

        val dashboardPelangganViewModel = ViewModelProvider(this, factoryUser)
            .get(DashboardPelangganViewModel::class.java)

        setContent {
            BengkelKuTheme {
                NavGraph(
                    loginViewModel = loginViewModel,
                    registerViewModel = registerViewModel,
                    dashboardAdminViewModel = dashboardAdminViewModel,
                    dashboardPelangganViewModel = dashboardPelangganViewModel
                )
            }
        }
    }
}

