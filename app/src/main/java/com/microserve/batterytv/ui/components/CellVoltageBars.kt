package com.microserve.batterytv.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microserve.batterytv.ui.theme.BatteryGreen
import com.microserve.batterytv.ui.theme.BatteryPurple
import com.microserve.batterytv.ui.theme.BatteryRed
import kotlin.math.roundToInt

private const val CHARGE_BARS = 4

/**
 * Per-cell voltages shown as battery icons whose horizontal bars reflect the
 * cell's charge level. Bars are normalized against the pack's min/max voltage
 * so small differences stay visible; out-of-balance cells are tinted
 * purple (high) or red (low).
 */
@Composable
fun CellVoltageBars(
    cells: List<Double>,
    modifier: Modifier = Modifier,
) {
    if (cells.isEmpty()) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text("No cell data", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }
    val maxV = cells.maxOrNull() ?: 3.3
    val minV = cells.minOrNull() ?: 3.3
    val avgV = cells.average()
    val spread = maxV - minV

    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            cells.forEachIndexed { index, v ->
                CellIcon(
                    voltage = v,
                    label = "C${index + 1}",
                    fraction = ((v - minV * 0.95) / (maxV * 1.05 - minV * 0.95))
                        .toFloat()
                        .coerceIn(0f, 1f),
                    color = when {
                        spread > 0.01 && v - avgV > 0.004 -> BatteryPurple
                        spread > 0.01 && v - avgV < -0.004 -> BatteryRed
                        else -> BatteryGreen
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Text(
            text = "Cells · avg ${"%.3f".format(avgV)} V · spread ${"%.0f".format(spread * 1000)} mV",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, start = 8.dp),
        )
    }
}

/** One cell: voltage label on top, battery icon in the middle, C-number below. */
@Composable
private fun CellIcon(
    voltage: Double,
    label: String,
    fraction: Float,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxHeight(),
    ) {
        Text(
            text = "%.3f".format(voltage),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        BatteryIcon(
            fraction = fraction,
            color = color,
            modifier = Modifier
                .width(54.dp)
                .heightIn(min = 70.dp, max = 130.dp)
                .weight(1f),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
/**
 * A battery-shaped glyph: rounded body with a terminal nub on top and
 * [CHARGE_BARS] horizontal charge bars inside, filled bottom-up.
 */
@Composable
private fun BatteryIcon(
    fraction: Float,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val nubH = h * 0.10f
        val nubW = w * 0.34f
        val radius = (w * 0.10f).coerceAtMost(6f)
        val strokeW = 2.dp.toPx()

        // Terminal nub on top.
        drawRoundRect(
            color = color,
            topLeft = Offset(w / 2f - nubW / 2f, 0f),
            size = Size(nubW, nubH),
            cornerRadius = CornerRadius(radius),
        )

        // Battery body outline.
        val bodyTop = nubH
        val bodyH = h - nubH
        drawRoundRect(
            color = color,
            topLeft = Offset(strokeW / 2f, bodyTop + strokeW / 2f),
            size = Size(w - strokeW, bodyH - strokeW),
            cornerRadius = CornerRadius(radius),
            style = Stroke(width = strokeW),
        )

        // Horizontal charge bars (bottom-up fill).
        val lit = if (fraction <= 0f) 0
        else (fraction * CHARGE_BARS).roundToInt().coerceIn(1, CHARGE_BARS)
        val padX = w * 0.13f
        val innerTop = bodyTop + bodyH * 0.10f
        val innerBottom = bodyTop + bodyH * 0.90f
        val innerH = innerBottom - innerTop
        val gap = innerH * 0.05f
        val barH = (innerH - gap * (CHARGE_BARS - 1)) / CHARGE_BARS
        val barW = w - 2 * padX

        for (i in 0 until CHARGE_BARS) {
            val y = innerBottom - barH - i * (barH + gap)
            drawRoundRect(
                color = if (i < lit) color else color.copy(alpha = 0.15f),
                topLeft = Offset(padX, y),
                size = Size(barW, barH),
                cornerRadius = CornerRadius(barH * 0.35f),
            )
        }
    }
}
