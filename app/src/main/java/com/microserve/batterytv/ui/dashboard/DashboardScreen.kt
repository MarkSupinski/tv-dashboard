package com.microserve.batterytv.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microserve.batterytv.data.BatteryStatus
import com.microserve.batterytv.ui.AppViewModel
import com.microserve.batterytv.ui.components.SocRing
import com.microserve.batterytv.ui.theme.SurfaceFocus
import com.microserve.batterytv.ui.theme.TextSecondary

/** TV home screen: every battery as a SOC ring card, navigated with the D-pad. */
@Composable
fun DashboardScreen(viewModel: AppViewModel) {
    val batteries by viewModel.batteries.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp, vertical = 32.dp),
    ) {
        Text(
            text = "Battery Dashboard",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(6.dp))
        val statusLine = when {
            error != null && batteries.isEmpty() -> "Server unreachable — check the battery server"
            error != null -> "Last update may be stale · $error"
            batteries.isEmpty() -> "No batteries reported yet"
            else -> "${batteries.size} battery(ies) · refresh every 60s"
        }
        Text(
            text = statusLine,
            fontSize = 16.sp,
            color = TextSecondary,
        )
        Spacer(Modifier.height(28.dp))

        when {
            loading && batteries.isEmpty() -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            batteries.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No batteries found.\nStart the battery server, then press OK to retry.",
                        color = TextSecondary,
                        fontSize = 18.sp,
                    )
                }
            }
            else -> {
                LazyRow(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                ) {
                    items(batteries, key = { it.address }) { battery ->
                        BatteryCard(
                            battery = battery,
                            onClick = { viewModel.selectBattery(battery) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BatteryCard(
    battery: BatteryStatus,
    onClick: () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.05f else 1f, label = "cardScale")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(300.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                color = if (focused) SurfaceFocus else MaterialTheme.colorScheme.surface,
            )
            .focusable()
            .onFocusChanged { focused = it.isFocused }
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 28.dp),
    ) {
        SocRing(soc = battery.soc, size = 150.dp, strokeWidth = 15.dp)
        Spacer(Modifier.height(20.dp))
        Text(
            text = battery.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(6.dp))
        val sub = buildString {
            append(if (battery.voltage != null) "%.2f V".format(battery.voltage) else "--")
            if (battery.temperature != null) append("  ·  %.1f°C".format(battery.temperature))
        }
        Text(
            text = sub,
            fontSize = 16.sp,
            color = TextSecondary,
        )
    }
}
