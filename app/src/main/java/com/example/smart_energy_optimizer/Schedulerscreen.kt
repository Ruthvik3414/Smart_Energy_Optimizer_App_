package com.example.smart_energy_optimizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerScreen(vm: EnergyViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var selDeviceId by remember { mutableStateOf("") }
    var onTime      by remember { mutableStateOf("09:00") }
    var offTime     by remember { mutableStateOf("18:00") }
    var expanded    by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionLabel("AUTOMATION")
            OutlinedButton(
                onClick = { showDialog = true },
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp
                ),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Purple)
            ) {
                Text("+ New Rule", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
            if (vm.schedules.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🕐", fontSize = 40.sp, color = DarkMuted.copy(alpha = 0.3f))
                            Spacer(Modifier.height(12.dp))
                            Text("No active schedules.", fontSize = 14.sp, color = DarkMuted)
                        }
                    }
                }
            }
            items(vm.schedules, key = { it.id }) { sched ->
                val dev = vm.devices.find { it.id == sched.deviceId }
                ScheduleCard(
                    schedule = sched, deviceName = dev?.name ?: "Unknown Device",
                    onToggle = { vm.toggleSchedule(sched.id) },
                    onDelete = { vm.removeSchedule(sched.id) }
                )
            }
        }
    }

    // ── New Schedule Dialog ───────────────────────────────────────────────────
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Schedule Device", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Device picker
                    Column {
                        Text("Select Device", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkMuted)
                        Spacer(Modifier.height(4.dp))
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                            val selName = vm.devices.find { it.id == selDeviceId }?.name ?: "Choose appliance…"
                            OutlinedTextField(
                                value = selName, onValueChange = {}, readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Purple, unfocusedBorderColor = DarkBorder),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                vm.devices.forEach { d ->
                                    DropdownMenuItem(text = { Text(d.name) }, onClick = { selDeviceId = d.id; expanded = false })
                                }
                            }
                        }
                    }
                    // Time row
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TimeField("ON Time", onTime, Modifier.weight(1f)) { onTime = it }
                        TimeField("OFF Time", offTime, Modifier.weight(1f)) { offTime = it }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selDeviceId.isNotBlank()) {
                            vm.addSchedule(Schedule(id = UUID.randomUUID().toString(), deviceId = selDeviceId, onTime = onTime, offTime = offTime))
                            showDialog = false; selDeviceId = ""; onTime = "09:00"; offTime = "18:00"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Create Schedule") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel", color = DarkMuted) }
            }
        )
    }
}

@Composable
private fun ScheduleCard(schedule: Schedule, deviceName: String, onToggle: () -> Unit, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (schedule.isActive) 1f else 0.55f)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Device + toggle row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                        .background(if (schedule.isActive) PurpleFaint else Color.Gray.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) { Text(if (schedule.isActive) "⚡" else "💤", fontSize = 18.sp) }
                Column {
                    Text(deviceName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("DAILY ROUTINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkMuted, letterSpacing = 1.sp)
                }
            }
            PurpleSwitch(checked = schedule.isActive, onCheckedChange = { onToggle() })
        }

        // Time row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Purple.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                .padding(12.dp, 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("🕐", fontSize = 12.sp)
                Text(schedule.onTime, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("START", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkMuted)
            }
            HorizontalDivider(modifier = Modifier.width(28.dp), color = DarkMuted.copy(alpha = 0.3f))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(schedule.offTime, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("STOP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkMuted)
            }
        }

        // Delete
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDelete, contentPadding = PaddingValues(horizontal = 0.dp)) {
                Text("DELETE RULE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkMuted, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun TimeField(label: String, value: String, modifier: Modifier = Modifier, onChange: (String) -> Unit) {
    Column(modifier = modifier) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkMuted)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value, onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            placeholder = { Text("HH:MM", color = DarkMuted) },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Purple, unfocusedBorderColor = DarkBorder),
            shape = RoundedCornerShape(10.dp)
        )
    }
}