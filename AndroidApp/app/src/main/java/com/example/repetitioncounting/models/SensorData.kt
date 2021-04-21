package com.example.repetitioncounting.models

data class SensorData(
    val acc_x: Float,
    val acc_y: Float,
    val acc_z: Float,
    val timestamp: Long,
    val type: String
    )