package com.example.smart_energy_optimizer

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(vm: EnergyViewModel) {
    val context = LocalContext.current
    val user = vm.currentUser
    var showProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
    ) {
        // ── Profile ───────────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Purple.copy(alpha = 0.07f), RoundedCornerShape(20.dp))
                    .clickable { showProfileDialog = true }
                    .padding(14.dp, 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Purple),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = user?.firstName?.take(1) ?: "?"
                    Text(initial, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("${user?.firstName ?: "Guest"} ${user?.lastName ?: ""}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(user?.email ?: "No email set", fontSize = 12.sp, color = DarkMuted)
                }
                Text("›", fontSize = 18.sp, color = DarkMuted)
            }
        }

        // ── Preferences ───────────────────────────────────────────────────────
        item {
            SectionLabel("PREFERENCES")
            Spacer(Modifier.height(10.dp))
            SettingsGroupCard {
                ToggleRow(
                    icon = "🌿", label = "Eco Mode", sub = "Save 15% energy automatically",
                    iconBg = GreenColor.copy(alpha = 0.15f),
                    checked = vm.ecoMode, onToggle = { vm.toggleEcoMode() }
                )
                HorizontalDivider(color = DarkBorder)
                ToggleRow(
                    icon = if (vm.darkMode) "🌙" else "☀️",
                    label = "Dark Appearance", sub = "Easier on the eyes",
                    iconBg = BlueColor.copy(alpha = 0.15f),
                    checked = vm.darkMode, onToggle = { vm.toggleDarkMode() }
                )
                HorizontalDivider(color = DarkBorder)
                ToggleRow(
                    icon = "🔔", label = "High Usage Alerts", sub = "Notify when limit exceeds",
                    iconBg = OrangeColor.copy(alpha = 0.15f),
                    checked = vm.alertsEnabled, onToggle = { vm.toggleAlerts() }
                )
            }
        }

        // ── Data & Security ───────────────────────────────────────────────────
        item {
            SectionLabel("DATA & SECURITY")
            Spacer(Modifier.height(10.dp))
            SettingsGroupCard {
                ActionRow(
                    icon = "📄", label = "Export Usage Report (PDF)",
                    iconBg = Purple.copy(alpha = 0.15f)
                ) { PdfExporter.exportUsagePdf(context, vm) }
                HorizontalDivider(color = DarkBorder)
                ActionRow(
                    icon = "🛡️", label = "Privacy Policy",
                    iconBg = CyanColor.copy(alpha = 0.15f)
                ) { openPrivacyPolicy(context) }
            }
        }

        // ── Sign Out ──────────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RedColor.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .border(1.dp, RedColor.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                    .clickable { vm.logout() }
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🚪 ", fontSize = 16.sp)
                Text("Sign Out", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RedColor)
            }
        }

        // ── Version ───────────────────────────────────────────────────────────
        item {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("VERSION 1.0.4 (BETA)", fontSize = 10.sp, color = DarkMuted, letterSpacing = 1.5.sp)
            }
        }
    }

    // ── Profile Details Dialog ────────────────────────────────────────────────
    if (showProfileDialog && user != null) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Account Details", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfileDetailItem("Full Name", "${user.firstName} ${user.lastName}")
                    ProfileDetailItem("Email", user.email)
                    ProfileDetailItem("Phone", user.phone)
                    ProfileDetailItem("User ID", user.id.take(8).uppercase())
                }
            },
            confirmButton = {
                Button(onClick = { showProfileDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Purple)) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun ProfileDetailItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkMuted, letterSpacing = 1.sp)
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(4.dp))
        HorizontalDivider(color = DarkBorder.copy(alpha = 0.05f))
    }
}

// ── Reusable setting rows ─────────────────────────────────────────────────────
@Composable
private fun SettingsGroupCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
    ) { content() }
}

@Composable
private fun ToggleRow(icon: String, label: String, sub: String, iconBg: Color, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) { Text(icon, fontSize = 16.sp) }
            Column {
                Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(sub, fontSize = 11.sp, color = DarkMuted)
            }
        }
        PurpleSwitch(checked = checked, onCheckedChange = { onToggle() })
    }
}

@Composable
private fun ActionRow(icon: String, label: String, iconBg: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) { Text(icon, fontSize = 16.sp) }
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Text("›", fontSize = 18.sp, color = DarkMuted)
    }
}

private fun openPrivacyPolicy(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com/privacy-policy"))
    context.startActivity(intent)
}