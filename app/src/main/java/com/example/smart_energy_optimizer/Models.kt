package com.example.smart_energy_optimizer

import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val password: String
)

data class Device(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val powerRating: Double,
    val usageHours: Double,
    val category: String,
    val ownerId: String = "" // Added default value to prevent compilation issues
)

data class Schedule(
    val id: String = UUID.randomUUID().toString(),
    val deviceId: String,
    val onTime: String,
    val offTime: String,
    val isActive: Boolean = true
)

const val COST_PER_KWH = 8.0