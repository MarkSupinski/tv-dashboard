package com.microserve.batterytv.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.microserve.batterytv.ui.dashboard.DashboardScreen
import com.microserve.batterytv.ui.detail.DetailScreen

/** Root composable: renders the active screen from the shared ViewModel. */
@Composable
fun BatteryApp(viewModel: AppViewModel) {
    val screen by viewModel.screen.collectAsState()
    when (val current = screen) {
        is Screen.Dashboard -> DashboardScreen(viewModel)
        is Screen.Detail -> DetailScreen(
            viewModel = viewModel,
            address = current.address,
            name = current.name,
        )
    }
}
