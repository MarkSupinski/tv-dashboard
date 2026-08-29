package com.microserve.batterytv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.microserve.batterytv.ui.AppViewModel
import com.microserve.batterytv.ui.BatteryApp
import com.microserve.batterytv.ui.theme.BatteryTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BatteryTheme {
                BatteryApp(viewModel = viewModel)
            }
        }
    }
}
