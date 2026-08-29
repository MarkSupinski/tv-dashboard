package com.microserve.batterytv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microserve.batterytv.data.BatteryRepository
import com.microserve.batterytv.data.BatteryStatus
import com.microserve.batterytv.data.HistoryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Screens reachable from the TV remote. */
sealed interface Screen {
    data object Dashboard : Screen
    data class Detail(val address: String, val name: String) : Screen
}

/** Shared app state: dashboard data, navigation and history. */
class AppViewModel : ViewModel() {

    private val repository = BatteryRepository()
    private val refreshIntervalMs = 60_000L // capture cadence of the server

    private val _screen = MutableStateFlow<Screen>(Screen.Dashboard)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _batteries = MutableStateFlow<List<BatteryStatus>>(emptyList())
    val batteries: StateFlow<List<BatteryStatus>> = _batteries.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _range = MutableStateFlow("hour")
    val range: StateFlow<String> = _range.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryPoint>>(emptyList())
    val history: StateFlow<List<HistoryPoint>> = _history.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                refreshBatteries()
                refreshHistoryIfVisible()
                delay(refreshIntervalMs)
            }
        }
    }

    fun selectBattery(battery: BatteryStatus) {
        _screen.value = Screen.Detail(battery.address, battery.name)
        refreshHistoryIfVisible()
    }

    fun backToDashboard() {
        _screen.value = Screen.Dashboard
    }

    fun setRange(range: String) {
        _range.value = range
        refreshHistoryIfVisible()
    }

    fun refreshBatteries() {
        viewModelScope.launch {
            _loading.value = _batteries.value.isEmpty()
            try {
                _batteries.value = repository.getBatteries()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Cannot reach battery server"
            }
            _loading.value = false
        }
    }

    private fun refreshHistoryIfVisible() {
        val detail = _screen.value as? Screen.Detail ?: return
        viewModelScope.launch {
            try {
                _history.value = repository.getHistory(detail.address, _range.value)
            } catch (e: Exception) {
                // Keep the previous history; the dashboard refresh reports errors.
            }
        }
    }
}
