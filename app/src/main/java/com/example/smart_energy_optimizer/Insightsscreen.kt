package com.example.smart_energy_optimizer

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Replace with your actual key or leave blank to use mock insights
private const val CLAUDE_API_KEY = ""

@Composable
fun InsightsScreen(vm: EnergyViewModel) {
    // Fetch on first composition
    LaunchedEffect(Unit) {
        if (vm.insights.isEmpty()) vm.fetchInsights(CLAUDE_API_KEY)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionLabel("AI PREDICTIONS")
                TextButton(
                    onClick = { vm.fetchInsights(CLAUDE_API_KEY) },
                    enabled = !vm.insightsLoading
                ) {
                    Text(
                        if (vm.insightsLoading) "↻ Loading…" else "↻ Refresh",
                        fontSize = 12.sp, color = Purple
                    )
                }
            }
        }

        // Anomaly card
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RedColor.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("⚠️", fontSize = 18.sp)
                Column {
                    Text("Anomaly Detected", fontWeight = FontWeight.Bold, color = RedColor, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Energy usage spiked 25% between 2 AM–4 AM. Check if any heavy appliance was left on.",
                        fontSize = 12.sp, color = RedColor.copy(alpha = 0.85f), lineHeight = 18.sp
                    )
                }
            }
        }

        // Insight cards or shimmer
        if (vm.insightsLoading) {
            items(4) {
                ShimmerBox()
            }
        } else {
            itemsIndexed(vm.insights) { _, insight ->
                EnergyCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(PurpleFaint),
                            contentAlignment = Alignment.Center
                        ) { Text("✨", fontSize = 18.sp) }
                        Text(insight, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Efficiency Score card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Purple, RoundedCornerShape(20.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("EFFICIENCY SCORE", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.75f), letterSpacing = 1.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("82", fontSize = 52.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(99.dp))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text("Great Performance", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Better than 75% of similar households in your area.",
                        fontSize = 11.sp, color = Color.White.copy(alpha = 0.65f),
                        lineHeight = 16.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.widthIn(max = 200.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ShimmerBox() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 1f, targetValue = 0.4f, label = "alpha",
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Purple.copy(alpha = 0.08f * alpha), RoundedCornerShape(16.dp))
    )
}