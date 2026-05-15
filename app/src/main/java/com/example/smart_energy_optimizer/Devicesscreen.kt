package com.example.smart_energy_optimizer


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(vm: EnergyViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var editingDevice by remember { mutableStateOf<Device?>(null) }

    // Form state
    var formName     by remember { mutableStateOf("") }
    var formWatts    by remember { mutableStateOf("") }
    var formHours    by remember { mutableStateOf("") }
    var formCategory by remember { mutableStateOf("Other") }

    fun openAdd() {
        editingDevice = null; formName = ""; formWatts = ""; formHours = ""; formCategory = "Other"
        showDialog = true
    }

    fun openEdit(d: Device) {
        editingDevice = d; formName = d.name; formWatts = d.powerRating.toString()
        formHours = d.usageHours.toString(); formCategory = d.category; showDialog = true
    }

    fun save() {
        val w = formWatts.toDoubleOrNull() ?: return
        val h = formHours.toDoubleOrNull() ?: 0.0
        val userId = vm.currentUser?.id ?: ""
        if (formName.isBlank()) return
        val ed = editingDevice
        if (ed != null) {
            vm.updateDevice(ed.id, ed.copy(name = formName, powerRating = w, usageHours = h, category = formCategory))
        } else {
            vm.addDevice(Device(id = UUID.randomUUID().toString(), name = formName, powerRating = w, usageHours = h, category = formCategory, ownerId = userId))
        }
        showDialog = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionLabel("CONNECTED APPLIANCES")
            Button(
                onClick = { openAdd() },
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("+ Add Device", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
            if (vm.devices.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⚡", fontSize = 40.sp, color = DarkMuted.copy(alpha = 0.3f))
                            Spacer(Modifier.height(12.dp))
                            Text("No devices added yet.", fontSize = 14.sp, color = DarkMuted)
                        }
                    }
                }
            }
            items(vm.devices, key = { it.id }) { device ->
                DeviceCard(device, onEdit = { openEdit(it) }, onDelete = { vm.removeDevice(it.id) })
            }
        }
    }

    // ── Add / Edit Dialog ─────────────────────────────────────────────────────
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = { Text(if (editingDevice != null) "Edit Appliance" else "Add New Appliance", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DialogField("Device Name", formName, "e.g. Microwave") { formName = it }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(Modifier.weight(1f)) {
                            DialogField("Power (Watts)", formWatts, "750", KeyboardType.Number) { formWatts = it }
                        }
                        Column(Modifier.weight(1f)) {
                            DialogField("Hours / Day", formHours, "2", KeyboardType.Number) { formHours = it }
                        }
                    }
                    // Category dropdown
                    val cats = listOf("AC", "Fan", "Light", "Heater", "Electronics", "Other")
                    var expanded by remember { mutableStateOf(false) }
                    Column {
                        Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkMuted)
                        Spacer(Modifier.height(4.dp))
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                            OutlinedTextField(
                                value = formCategory, onValueChange = {},
                                readOnly = true, modifier = Modifier.fillMaxWidth().menuAnchor(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Purple, unfocusedBorderColor = DarkBorder),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                cats.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { formCategory = it; expanded = false }) }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { save() }, colors = ButtonDefaults.buttonColors(containerColor = Purple), shape = RoundedCornerShape(10.dp)) {
                    Text("Save Device")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel", color = DarkMuted) }
            }
        )
    }
}

@Composable
private fun DeviceCard(device: Device, onEdit: (Device) -> Unit, onDelete: (Device) -> Unit) {
    val dailyCost = (device.powerRating * device.usageHours / 1000.0) * COST_PER_KWH
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
    ) {
        // Main row
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp, 14.dp, 14.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                IconBox(emoji = categoryEmoji(device.category))
                Column {
                    Text(device.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("${device.powerRating.toInt()}W · ${device.usageHours.toInt()}h/day",
                        fontSize = 11.sp, color = DarkMuted)
                }
            }
            Row {
                IconButton(onClick = { onEdit(device) }, modifier = Modifier.size(32.dp)) {
                    Text("✏️", fontSize = 14.sp)
                }
                IconButton(onClick = { onDelete(device) }, modifier = Modifier.size(32.dp)) {
                    Text("🗑️", fontSize = 14.sp)
                }
            }
        }
        // Footer bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Purple.copy(alpha = 0.05f), RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("DAILY COST", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkMuted, letterSpacing = 1.sp)
            Text("₹${String.format("%.2f", dailyCost)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Purple)
        }
    }
}

@Composable
private fun DialogField(
    label: String, value: String, placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit
) {
    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkMuted)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value, onValueChange = onChange, placeholder = { Text(placeholder, color = DarkMuted) },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Purple, unfocusedBorderColor = DarkBorder),
            shape = RoundedCornerShape(10.dp)
        )
    }
}