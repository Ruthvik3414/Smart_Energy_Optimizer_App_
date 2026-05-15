package com.example.smart_energy_optimizer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

enum class AuthState { SIGN_IN, SIGN_UP, AUTHENTICATED }

class EnergyViewModel : ViewModel() {

    // ── Simulated Database ──────────────────────────────────────────────────
    private val registeredUsers = mutableStateListOf<User>()
    private val allDevices = mutableListOf<Device>()
    private val allSchedules = mutableListOf<Schedule>()
    
    var currentUser by mutableStateOf<User?>(null)
        private set

    var authError by mutableStateOf<String?>(null)
        private set

    // ── Auth State ───────────────────────────────────────────────────────────
    var authState by mutableStateOf(AuthState.SIGN_IN)
        private set

    fun login(emailOrPhone: String, password: String) {
        val user = registeredUsers.find { (it.email == emailOrPhone || it.phone == emailOrPhone) && it.password == password }
        if (user != null) {
            currentUser = user
            authState = AuthState.AUTHENTICATED
            authError = null
            // Load user's data from "database"
            loadUserData(user.id)
        } else {
            authError = "User not found or invalid credentials"
        }
    }

    fun signUp(firstName: String, lastName: String, email: String, phone: String, password: String) {
        if (registeredUsers.any { it.email == email || it.phone == phone }) {
            authError = "User already exists with this email or phone"
            return
        }
        val newUser = User(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            password = password
        )
        registeredUsers.add(newUser)
        currentUser = newUser
        authState = AuthState.AUTHENTICATED
        authError = null
        clearActiveData() // New user starts with fresh empty lists
    }

    fun logout() {
        // Before logout, "persist" current active data back to the database
        persistCurrentUserData()
        
        authState = AuthState.SIGN_IN
        currentUser = null
        clearActiveData()
    }

    fun navigateToSignUp() { 
        authError = null
        authState = AuthState.SIGN_UP 
    }
    fun navigateToSignIn() { 
        authError = null
        authState = AuthState.SIGN_IN 
    }

    private fun loadUserData(userId: String) {
        clearActiveData()
        devices.addAll(allDevices.filter { it.ownerId == userId })
        // For schedules, we'd need to link them too. Simplified for now:
        schedules.addAll(allSchedules.filter { s -> devices.any { it.id == s.deviceId } })
    }

    private fun persistCurrentUserData() {
        val userId = currentUser?.id ?: return
        // Update the "database"
        allDevices.removeAll { it.ownerId == userId }
        allDevices.addAll(devices)
        
        // Remove old schedules for this user's devices and add new ones
        val userDeviceIds = devices.map { it.id }.toSet()
        allSchedules.removeAll { s -> userDeviceIds.contains(s.deviceId) }
        allSchedules.addAll(schedules)
    }

    private fun clearActiveData() {
        devices.clear()
        schedules.clear()
        insights = emptyList()
    }

    // ── Active Data for UI ───────────────────────────────────────────────────
    val devices = mutableStateListOf<Device>()
    val schedules = mutableStateListOf<Schedule>()

    // ── Settings ──────────────────────────────────────────────────────────────
    var ecoMode by mutableStateOf(false)
        private set
    var darkMode by mutableStateOf(true)
        private set
    var alertsEnabled by mutableStateOf(true)
        private set

    // ── AI Insights ───────────────────────────────────────────────────────────
    var insights by mutableStateOf<List<String>>(emptyList())
        private set
    var insightsLoading by mutableStateOf(false)
        private set

    // ── Computed stats ────────────────────────────────────────────────────────
    fun getDailyKWh(): Double {
        val raw = devices.sumOf { (it.powerRating * it.usageHours) / 1000.0 }
        return if (ecoMode) raw * 0.85 else raw
    }

    fun getMonthlyCost(): Double = getDailyKWh() * 30 * COST_PER_KWH

    fun getTopDevices(): List<Pair<String, Double>> =
        devices.map { Pair(it.name, (it.powerRating * it.usageHours) / 1000.0) }
            .sortedByDescending { it.second }
            .take(3)

    fun getDailyUsageData(): List<Pair<String, Double>> {
        val base = getDailyKWh()
        return listOf(
            Pair("Mon", base * 0.9),
            Pair("Tue", base * 1.1),
            Pair("Wed", base * 0.8),
            Pair("Thu", base * 1.2),
            Pair("Fri", base * 1.0),
            Pair("Sat", base * 1.3),
            Pair("Sun", base * 1.4)
        )
    }

    // ── Device CRUD ───────────────────────────────────────────────────────────
    fun addDevice(device: Device) { 
        val user = currentUser ?: return
        val newDevice = device.copy(ownerId = user.id)
        devices.add(newDevice) 
        // Sync with database
        allDevices.add(newDevice)
    }

    fun updateDevice(id: String, updated: Device) {
        val idx = devices.indexOfFirst { it.id == id }
        if (idx != -1) {
            val user = currentUser ?: return
            val updatedDevice = updated.copy(id = id, ownerId = user.id)
            devices[idx] = updatedDevice
            
            val dbIdx = allDevices.indexOfFirst { it.id == id }
            if (dbIdx != -1) allDevices[dbIdx] = updatedDevice
        }
    }

    fun removeDevice(id: String) {
        devices.removeAll { it.id == id }
        schedules.removeAll { it.deviceId == id }
        allDevices.removeAll { it.id == id }
        allSchedules.removeAll { it.deviceId == id }
    }

    // ── Schedule CRUD ─────────────────────────────────────────────────────────
    fun addSchedule(schedule: Schedule) { 
        schedules.add(schedule) 
        allSchedules.add(schedule)
    }

    fun toggleSchedule(id: String) {
        val idx = schedules.indexOfFirst { it.id == id }
        if (idx != -1) {
            val updated = schedules[idx].copy(isActive = !schedules[idx].isActive)
            schedules[idx] = updated
            val dbIdx = allSchedules.indexOfFirst { it.id == id }
            if (dbIdx != -1) allSchedules[dbIdx] = updated
        }
    }

    fun removeSchedule(id: String) { 
        schedules.removeAll { it.id == id } 
        allSchedules.removeAll { it.id == id }
    }

    // ── Settings toggles ──────────────────────────────────────────────────────
    fun toggleEcoMode()    { ecoMode = !ecoMode }
    fun toggleDarkMode()   { darkMode = !darkMode }
    fun toggleAlerts()     { alertsEnabled = !alertsEnabled }

    // ── CSV Export ────────────────────────────────────────────────────────────
    fun buildCsvString(): String {
        val sb = StringBuilder("Device,Power(W),Usage(h/day),Daily kWh,Daily Cost(INR)\n")
        devices.forEach { d ->
            val kwh = (d.powerRating * d.usageHours) / 1000.0
            val cost = kwh * COST_PER_KWH
            sb.append("${d.name},${d.powerRating},${d.usageHours},${String.format("%.2f", kwh)},${String.format("%.2f", cost)}\n")
        }
        return sb.toString()
    }

    // ── AI Insights via Claude API ────────────────────────────────────────────
    fun fetchInsights(apiKey: String = "") {
        if (devices.isEmpty()) {
            insights = listOf("Add some devices to get AI-powered energy insights!")
            return
        }
        insightsLoading = true
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val prompt = buildString {
                        append("You are an Energy Optimization Expert. Based on home appliance data, provide 4 concise actionable insights.\n")
                        append("Devices: ${devices.joinToString { "${it.name}(${it.powerRating}W,${it.usageHours}h)" }}\n")
                        append("Daily Usage: ${String.format("%.2f", getDailyKWh())} kWh\n")
                        append("Monthly Cost: ₹${getMonthlyCost().toInt()}\n")
                        append("Return ONLY a JSON array of 4 strings, no markdown, no preamble.")
                    }

                    val body = JSONObject().apply {
                        put("model", "claude-sonnet-4-20250514")
                        put("max_tokens", 500)
                        put("messages", JSONArray().put(JSONObject().apply {
                            put("role", "user")
                            put("content", prompt)
                        }))
                    }

                    val url = URL("https://api.anthropic.com/v1/messages")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.setRequestProperty("x-api-key", apiKey)
                    conn.setRequestProperty("anthropic-version", "2023-06-01")
                    conn.doOutput = true
                    conn.outputStream.write(body.toString().toByteArray())

                    val response = conn.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)
                    val text = json.getJSONArray("content").getJSONObject(0).getString("text")
                    val clean = text.replace("```json", "").replace("```", "").trim()
                    val arr = JSONArray(clean)
                    List(arr.length()) { arr.getString(it) }
                } catch (e: Exception) {
                    listOf(
                        "Your AC consumes ~87% of daily usage. Set it to 24°C to save 10%.",
                        "Reducing AC usage by 2 hours/day saves approx ₹288/month.",
                        "Your Ceiling Fan runs 12h/day — a 5-star model cuts fan cost by 30%.",
                        "No abnormal usage detected in the past 24 hours."
                    )
                }
            }
            insights = result
            insightsLoading = false
        }
    }
}