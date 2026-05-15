package com.example.smart_energy_optimizer

import androidx.compose.runtime.mutableStateListOf

object MockDatabase {
    val registeredUsers = mutableStateListOf<User>()
    val allDevices = mutableStateListOf<Device>()
    val allSchedules = mutableStateListOf<Schedule>()

    // Optional: Pre-fill with a test user if needed
    init {
        // registeredUsers.add(User(firstName = "Ruthvik", lastName = "R", email = "ruthvik@example.com", phone = "1234567890", password = "password"))
    }
}