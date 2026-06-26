package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = null,
    val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?
)

@JsonClass(generateAdapter = true)
data class MentorAnalysisResponse(
    val scenarioAnalysis: String,
    val strategicPattern: String,
    val evolutionaryReading: String,
    val actionSteps: String,
    val criticalError: String,
    val profileUpdate: ProfileUpdateJson
)

@JsonClass(generateAdapter = true)
data class ProfileUpdateJson(
    val level: Int,
    val impulsivity: Float,
    val rationality: Float,
    val decisionPattern: String,
    val emotionalMaturity: String,
    val communicationStyle: String,
    val recurrentErrors: String,
    val strategicStrengths: String
)
