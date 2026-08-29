package com.microserve.batterytv.ui.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microserve.batterytv.data.BatteryStatus
import com.microserve.batterytv.ui.AppViewModel
import com.microserve.batterytv.ui.components.CellVoltageBars
import com.microserve.batterytv.ui.components.MetricCard
import com.microserve.batterytv.ui.components.SegmentedControl
import com.microserve.batterytv.ui.components.SocHistoryChart
import com.microserve.batterytv.ui.components.SocRing
import com.microserve.batterytv.ui.theme.SurfaceFocus
import com.microserve.batterytv.ui.theme.TextSecondary

private val HISTORY_RANGES = listOf("hour", "day", "week")

/** Battery detail page: full telemetry, cell voltages and SOC history charts. */
@Composable
fun DetailScreen(
    viewModel: AppViewModel,
    address: String,
    name: String,
) {
    val batteries by viewModel.batteries.collectAsState()
    val history by viewModel.history.collectAsState()
    val range by viewModel.range.collectAsState()

    val battery = batteries.firstOrNull { it.address == address }

    BackHandler { viewModel.backToDashboard() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 36.dp, vertical = 24.dp),
    ) {
        // Header with a "back" hint.
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackChip(onClick = { viewModel.backToDashboard() })
            Spacer(Modifier.width(20.dp))
            Column {
                Text(
                    text = name,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                if (battery != null) {
                    Text(
                        text = battery.lastUpdated.replace('T', ' ').take(19),
                        fontSize = 14.sp,
                        color = TextSecondary,
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        if (battery == null) {
            Text(
                "Battery offline or not found — it will reappear once the server captures it.",
                color = TextSecondary,
                fontSize = 16.sp,
            )
            return
        }

        // Top: SOC ring + key electrical metrics.
        Row(verticalAlignment = Alignment.CenterVertically) {
            SocRing(soc = battery.soc, size = 170.dp, strokeWidth = 18.dp)
            Spacer(Modifier.width(28.dp))
            Column(Modifier.weight(1f)) {
                Row {
                    MetricCard("Voltage", "%.2f V".format(battery.voltage ?: 0.0), Modifier.weight(1f))
                    MetricCard("Current", "%.2f A".format(battery.current ?: 0.0), Modifier.weight(1f))
                    MetricCard("Power", "%.0f W".format(battery.powerW ?: 0.0), Modifier.weight(1f))
                }
                Row {
                    MetricCard("Temperature", "%.1f °C".format(battery.temperature ?: 0.0), Modifier.weight(1f))
                    MetricCard("Capacity", "%.1f Ah".format(battery.capacityAh ?: 0.0), Modifier.weight(1f))
                    MetricCard("Health", "%.0f %%".format(battery.health ?: battery.soh ?: 0.0), Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Cell voltages.
        Text(
            text = "Cell Voltages",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        CellVoltageBars(
            cells = battery.cells,
            modifier = Modifier.fillMaxWidth().height(190.dp),
        )

        Spacer(Modifier.height(24.dp))

        // SOC history with range selector.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "State of Charge History",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
            )
            SegmentedControl(
                options = HISTORY_RANGES,
                selected = range,
                onSelect = { viewModel.setRange(it) },
            )
        }
        Spacer(Modifier.height(10.dp))
        SocHistoryChart(
            points = history.map { it.soc },
            startLabel = history.firstOrNull()?.ts?.replace('T', ' ')?.take(16) ?: "",
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                .padding(10.dp),
        )

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun BackChip(onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Text(
        text = "‹ Back",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (focused) MaterialTheme.colorScheme.primary else TextSecondary,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (focused) SurfaceFocus else MaterialTheme.colorScheme.surface)
            .focusable()
            .onFocusChanged { focused = it.isFocused }
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
    )
}

