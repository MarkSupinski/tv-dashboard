package com.microserve.batterytv.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microserve.batterytv.ui.theme.BatteryGreen
import com.microserve.batterytv.ui.theme.BatteryTeal
import com.microserve.batterytv.ui.theme.GridLine

/**
 * An attractive SOC history area chart. Values are expected in percent (0..100);
 * the y-axis is fixed at 0..100 so different ranges are comparable. Gridlines,
 * a gradient fill, the trace, and a "now" dot are drawn with a single Canvas.
 */
@Composable
fun SocHistoryChart(
    points: List<Double?>,
    modifier: Modifier = Modifier,
    showAxisLabels: Boolean = true,
    startLabel: String = "",
) {
    if (points.isEmpty()) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text(
                "No history yet — collecting samples every 60s…",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
            )
        }
        return
    }

    Canvas(modifier) {
        val padX = 8.dp.toPx()
        val padTop = 12.dp.toPx()
        val padBottom = if (showAxisLabels) 22.dp.toPx() else 10.dp.toPx()
        val w = size.width
        val h = size.height
        val plotLeft = padX
        val plotRight = w - padX
        val plotTop = padTop
        val plotBottom = h - padBottom

        fun yFor(v: Double): Float {
            val clamped = v.coerceIn(0.0, 100.0) / 100.0
            return plotBottom - (clamped * (plotBottom - plotTop)).toFloat()
        }

        // Horizontal gridlines at 0/25/50/75/100 %.
        for (pct in intArrayOf(0, 25, 50, 75, 100)) {
            val y = yFor(pct.toDouble())
            drawLine(
                color = GridLine,
                start = Offset(plotLeft, y),
                end = Offset(plotRight, y),
                strokeWidth = 1.dp.toPx(),
            )
            if (showAxisLabels) {
                drawContext.canvas.nativeCanvas.drawText(
                    "$pct%",
                    plotLeft,
                    y - 4.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = GridLine.toArgb()
                        textSize = 10.sp.toPx()
                    },
                )
            }
        }

        val n = points.size
        if (n == 1) {
            // Single point: draw a marker on the baseline.
            val x = plotLeft + (plotRight - plotLeft) / 2f
            val y = yFor(points[0] ?: 0.0)
            drawCircle(color = BatteryGreen, radius = 5.dp.toPx(), center = Offset(x, y))
            return@Canvas
        }

        val stepX = (plotRight - plotLeft) / (n - 1)
        val linePath = Path()
        val fillPath = Path()
        var first = true
        points.forEachIndexed { i, p ->
            val x = plotLeft + i * stepX
            val y = yFor(p ?: 0.0)
            if (first) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, plotBottom)
                fillPath.lineTo(x, y)
                first = false
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        fillPath.lineTo(plotLeft + (n - 1) * stepX, plotBottom)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    BatteryGreen.copy(alpha = 0.35f),
                    BatteryGreen.copy(alpha = 0.04f),
                ),
                startY = plotTop,
                endY = plotBottom,
            ),
        )
        drawPath(
            path = linePath,
            color = BatteryTeal,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
        )

        // "Now" dot.
        val lastX = plotLeft + (n - 1) * stepX
        val lastY = yFor(points.last() ?: 0.0)
        drawCircle(color = BatteryTeal, radius = 6.dp.toPx(), center = Offset(lastX, lastY))
        drawCircle(
            color = BatteryGreen,
            radius = 3.dp.toPx(),
            center = Offset(lastX, lastY),
        )

        if (showAxisLabels) {
            val axisPaint = android.graphics.Paint().apply {
                color = GridLine.toArgb()
                textSize = 11.sp.toPx()
            }
            drawContext.canvas.nativeCanvas.drawText("now", lastX - 20.dp.toPx(), plotBottom + 16.dp.toPx(), axisPaint)
            if (startLabel.isNotEmpty()) {
                drawContext.canvas.nativeCanvas.drawText(startLabel, plotLeft, plotBottom + 16.dp.toPx(), axisPaint)
            }
        }
    }
}
