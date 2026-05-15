package com.example.smart_energy_optimizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: EnergyViewModel = viewModel()
            AppTheme(darkMode = vm.darkMode) {
                // Main Switcher between Auth and App
                AnimatedContent(
                    targetState = vm.authState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                    },
                    label = "auth_transition"
                ) { state ->
                    when (state) {
                        AuthState.SIGN_IN -> SignInScreen(vm)
                        AuthState.SIGN_UP -> SignUpScreen(vm)
                        AuthState.AUTHENTICATED -> SmartEnergyApp(vm)
                    }
                }
            }
        }
    }
}

// ── Tabs definition ───────────────────────────────────────────────────────────
enum class AppTab(val label: String, val icon: String, val title: String) {
    DASHBOARD ("Home",     "⊞",  "Dashboard"),
    DEVICES   ("Devices",  "⚡", "Devices"),
    SCHEDULER ("Schedule", "📅", "Device Scheduler"),
    INSIGHTS  ("AI",       "💡", "Insights"),
    SETTINGS  ("Settings", "⚙️", "Settings")
}

// ── Root composable ───────────────────────────────────────────────────────────
@Composable
fun SmartEnergyApp(vm: EnergyViewModel) {
    var activeTab by remember { mutableStateOf(AppTab.DASHBOARD) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(activeTab = activeTab, onTabSelected = { activeTab = it })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Header ────────────────────────────────────────────────────────
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 6.dp)) {
                Text(
                    activeTab.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Smart Energy Optimizer",
                    fontSize = 12.sp,
                    color = DarkMuted
                )
            }

            // ── Screen content ────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        (fadeIn() + slideInVertically { 20 }).togetherWith(fadeOut() + slideOutVertically { -20 })
                    },
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        AppTab.DASHBOARD -> DashboardScreen(vm)
                        AppTab.DEVICES   -> DevicesScreen(vm)
                        AppTab.SCHEDULER -> SchedulerScreen(vm)
                        AppTab.INSIGHTS  -> InsightsScreen(vm)
                        AppTab.SETTINGS  -> SettingsScreen(vm)
                    }
                }
            }
        }
    }
}

// ── Bottom navigation bar ─────────────────────────────────────────────────────
@Composable
private fun BottomNavBar(activeTab: AppTab, onTabSelected: (AppTab) -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.92f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            AppTab.entries.forEach { tab ->
                NavItem(
                    tab = tab,
                    selected = activeTab == tab,
                    onClick = { onTabSelected(tab) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(tab: AppTab, selected: Boolean, onClick: () -> Unit) {
    val iconBg = if (selected) Purple.copy(alpha = 0.15f) else Color.Transparent
    val labelColor = if (selected) Purple else DarkMuted

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(60.dp)
            .then(Modifier.clickableNoRipple(onClick))
    ) {
        Box(
            modifier = Modifier
                .size(width = 34.dp, height = 30.dp)
                .background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) { Text(tab.icon, fontSize = 16.sp) }
        Spacer(Modifier.height(3.dp))
        Text(
            tab.label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = labelColor,
            letterSpacing = 0.5.sp
        )
    }
}

// ── No-ripple clickable helper ────────────────────────────────────────────────
@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick
        )
    )
