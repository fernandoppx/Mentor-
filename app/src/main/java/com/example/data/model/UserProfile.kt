package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val level: Int = 1, // Nível 1 a 5
    val impulsivity: Float = 0.5f, // 0.0 to 1.0
    val rationality: Float = 0.5f, // 0.0 to 1.0
    val decisionPattern: String = "Padrão inicial não estabelecido. Registre interações para mapear.",
    val emotionalMaturity: String = "Avaliação em andamento",
    val communicationStyle: String = "Não identificado",
    val recurrentErrors: String = "Ainda não mapeado",
    val strategicStrengths: String = "Ainda não mapeado",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun getLevelName(): String {
        return when (level) {
            1 -> "Nível 1: Reativo (Emocional/Impulsivo)"
            2 -> "Nível 2: Consciente (Percebe Padrões)"
            3 -> "Nível 3: Estratégico (Planeja antes de agir)"
            4 -> "Nível 4: Consistente (Executa com controle)"
            5 -> "Nível 5: Estável sob Pressão (Alta Performance)"
            else -> "Nível $level"
        }
    }
}
