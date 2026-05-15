package com.example.smart_energy_optimizer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape

// ── Weekly Area Chart ─────────────────────────────────────────────────────────
@Composable
fun WeeklyAreaChart(data: List<Pair<String, Double>>) {
    val textMeasurer = rememberTextMeasurer()
    val labelColor   = DarkMuted
    val lineColor    = Purple
    val fillTop      = Purple.copy(alpha = 0.35f)
    val fillBottom   = Purple.copy(alpha = 0.02f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        if (data.isEmpty()) return@Canvas

        val padL = 36f; val padR = 12f; val padT = 10f; val padB = 32f
        val w = size.width - padL - padR
        val h = size.height - padT - padB
        val max = data.maxOf { it.second } * 1.1

        val xs = data.indices.map { i -> padL + (i.toFloat() / (data.size - 1).coerceAtLeast(1)) * w }
        val ys = data.map { (_, v) -> padT + (1f - (v / max).toFloat()) * h }

        // Gradient fill path
        val fillPath = Path().apply {
            moveTo(xs[0], ys[0])
            for (i in 1 until xs.size) lineTo(xs[i], ys[i])
            lineTo(xs.last(), padT + h)
            lineTo(xs.first(), padT + h)
            close()
        }
        drawPath(
            fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(fillTop, fillBottom),
                startY = padT, endY = padT + h
            )
        )

        // Line
        val linePath = Path().apply {
            moveTo(xs[0], ys[0])
            for (i in 1 until xs.size) lineTo(xs[i], ys[i])
        }
        drawPath(linePath, color = lineColor, style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // X labels
        data.forEachIndexed { i, (day, _) ->
            val measured = textMeasurer.measure(day, style = TextStyle(fontSize = 9.sp, color = labelColor))
            drawText(measured, topLeft = Offset(xs[i] - measured.size.width / 2f, padT + h + 6f))
        }

        // Y labels (3 ticks)
        listOf(0f, 0.5f, 1f).forEach { t ->
            val v = (t * max)
            val y = padT + (1f - t) * h
            val label = String.format("%.1f", v)
            val measured = textMeasurer.measure(label, style = TextStyle(fontSize = 9.sp, color = labelColor))
            drawText(measured, topLeft = Offset(padL - measured.size.width - 4f, y - measured.size.height / 2f))
            // Dashed grid line
            drawLine(
                color = labelColor.copy(alpha = 0.15f),
                start = Offset(padL, y), end = Offset(padL + w, y),
                strokeWidth = 1f
            )
        }
    }
}

// ── Horizontal Bar Chart (Top Consumers) ─────────────────────────────────────
@Composable
fun TopConsumersChart(items: List<Pair<String, Double>>) {
    val barColors = listOf(Purple, PurpleLight, Color(0xFFC4B5FD))
    val max = items.maxOfOrNull { it.second }?.coerceAtLeast(0.01) ?: 0.01

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        items.forEachIndexed { i, (name, kwh) ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Rank circle
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(barColors[i.coerceAtMost(2)], shape = androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("${i + 1}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            "${String.format("%.2f", kwh)} kWh",
                            fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Purple
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    // Bar track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Purple.copy(alpha = 0.10f), RoundedCornerShape(99.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((kwh / max).toFloat().coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .background(barColors[i.coerceAtMost(2)], RoundedCornerShape(99.dp))
                        )
                    }
                    Spacer(Modifier.height(3.dp))
                    Text("₹${String.format("%.2f", kwh * COST_PER_KWH)}/day", fontSize = 10.sp, color = DarkMuted)
                }
            }
        }
    }
}