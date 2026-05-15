package com.example.smart_energy_optimizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(vm: EnergyViewModel) {
    val dailyKWh    = vm.getDailyKWh()
    val monthlyCost = vm.getMonthlyCost()
    val topDevices  = vm.getTopDevices()
    val chartData   = vm.getDailyUsageData()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
    ) {
        // ── Two summary cards ────────────────────────────────────────────────
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                // Usage card (purple fill)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Purple, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("⚡ USAGE", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.75f), letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = androidx.compose.ui.Alignment.Bottom) {
                        Text(
                            String.format("%.1f", dailyKWh),
                            fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("kWh", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                    Text("Today's consumption", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                }

                // Cost card
                EnergyCard(modifier = Modifier.weight(1f)) {
                    Text("₹ COST", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = DarkMuted, letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "₹${monthlyCost.toInt()}",
                        fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text("Est. monthly bill", fontSize = 10.sp, color = DarkMuted)
                }
            }
        }

        // ── Weekly chart ─────────────────────────────────────────────────────
        item {
            EnergyCard {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text("📈 ", fontSize = 13.sp)
                    Text("Weekly Usage Trend", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Purple)
                }
                Spacer(Modifier.height(10.dp))
                WeeklyAreaChart(data = chartData)
            }
        }

        // ── Top consumers ────────────────────────────────────────────────────
        item {
            EnergyCard {
                SectionLabel("TOP CONSUMERS")
                Spacer(Modifier.height(14.dp))
                TopConsumersChart(items = topDevices)
            }
        }
    }
}