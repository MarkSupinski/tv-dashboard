package com.microserve.batterytv.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microserve.batterytv.ui.theme.BatteryGreen
import com.microserve.batterytv.ui.theme.BatteryPurple
import com.microserve.batterytv.ui.theme.BatteryRed
import com.microserve.batterytv.ui.theme.GridLine

/**
 * Vertical bar chart of per-cell voltages. Bars are normalized against the
 * highest cell so small differences stay visible; a wide band marks the cell
 * average and out-of-balance cells are highlighted in purple/red.
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    Text(
                        "%.3f".format(v),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    val barFraction = ((v - minV * 0.95) / (maxV * 1.05 - minV * 0.95))
                        .toFloat()
                        .coerceIn(0.08f, 1f)
                    val delta = v - avgV
                    val barColor = when {
                        spread > 0.01 && delta > 0.004 -> BatteryPurple
                        spread > 0.01 && delta < -0.004 -> BatteryRed
                        else -> BatteryGreen
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Canvas(Modifier.fillMaxWidth()) {
                            val w = size.width
                            val h = size.height
                            val barH = h * barFraction
                            drawRoundRect(
                                color = GridLine,
                                topLeft = Offset(0f, h - barH),
                                size = Size(w, barH),
                                cornerRadius = CornerRadius(6.dp.toPx()),
                            )
                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(0f, h - barH),
                                size = Size(w, barH),
                                cornerRadius = CornerRadius(6.dp.toPx()),
                            )
                        }
                    }
                    Text(
                        "C${index + 1}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Text(
            "Cells · avg ${"%.3f".format(avgV)} V · spread ${"%.0f".format(spread * 1000)} mV",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, start = 8.dp),
        )
    }
}
