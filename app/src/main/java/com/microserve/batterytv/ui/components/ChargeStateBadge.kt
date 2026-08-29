package com.microserve.batterytv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microserve.batterytv.data.BatteryStatus
import com.microserve.batterytv.data.ChargeState
import com.microserve.batterytv.ui.theme.BatteryGreen
import com.microserve.batterytv.ui.theme.BatteryTeal
import com.microserve.batterytv.ui.theme.TextSecondary

/** A small pill showing whether the battery is charging, discharging, or idle. */
@Composable
fun ChargeStateBadge(battery: BatteryStatus) {
    val current = battery.current
    val (label, color) = when (battery.chargeState) {
        ChargeState.CHARGING ->
            ("Charging · ${"%.1f".format(current ?: 0.0)} A") to BatteryGreen
        ChargeState.DISCHARGING ->
            ("Discharging · ${"%.1f".format(current ?: 0.0)} A") to BatteryTeal
        ChargeState.IDLE -> "Idle" to TextSecondary
    }
    Text(
        text = label,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 14.dp, vertical = 5.dp),
    )
}