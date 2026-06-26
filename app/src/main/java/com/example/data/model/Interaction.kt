package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interactions")
data class Interaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val userSituation: String,
    val isVoice: Boolean = false,
    val analysisScenario: String,
    val analysisPattern: String,
    val analysisEvolution: String,
    val analysisAction: String,
    val analysisCriticalError: String,
    val appliedLevel: Int
)
