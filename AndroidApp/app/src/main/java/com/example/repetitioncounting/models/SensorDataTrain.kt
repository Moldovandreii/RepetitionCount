package com.example.repetitioncounting.models

data class SensorDataTrain(
        val acc_x: Float,
        val acc_y: Float,
        val acc_z: Float,
        val timestamp: Long,
        val type: String,
        val descriptionId: Int
)