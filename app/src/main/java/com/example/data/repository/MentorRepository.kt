package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.MentorDao
import com.example.data.model.Interaction
import com.example.data.model.UserProfile
import com.example.data.remote.Content
import com.example.data.remote.GeminiRequest
import com.example.data.remote.GenerationConfig
import com.example.data.remote.MentorAnalysisResponse
import com.example.data.remote.Part
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class MentorRepository(private val mentorDao: MentorDao) {

    val userProfile: Flow<UserProfile?> = mentorDao.getUserProfile()
    val allInteractions: Flow<List<Interaction>> = mentorDao.getAllInteractions()

    suspend fun getProfileOneShot(): UserProfile {
        return mentorDao.getUserProfileOneShot() ?: UserProfile()
    }

    suspend fun clearAll() {
        mentorDao.clearHistory()
        mentorDao.insertUserProfile(UserProfile())
    }

    suspend fun analyzeSituation(situation: String, isVoice: Boolean): Result<Interaction> {
        val currentProfile = getProfileOneShot()
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return Result.failure(Exception("API Key do Gemini não configurada. Por favor, adicione-a no painel de Secrets no AI Studio."))
        }

        val systemInstruction = """
            Você é um mentor pessoal estratégico de alta performance especializado em leitura de cenários humanos, tomada de decisão e evolução comportamental contínua. 
            Você atua como uma inteligência filosófica aplicada baseada em:
            - Baltasar Gracián (prudência, leitura social, sutileza estratégica)
            - Robert Greene / 48 Laws of Power (dinâmica de poder e percepção social)
            - Sun Tzu / Art of War (estratégia, timing e vantagem competitiva)
            - Maquiavel / O Príncipe (realidade de poder estrutural)

            Seu objetivo é evoluir o usuário. Você não motiva. Você estrutura pensamento e ação de forma direta, analítica e pragmática.
            
            Você deve analisar o cenário do usuário e atualizar silenciosamente o modelo de memória evolutiva dele.
            O perfil atual do usuário é:
            - Nível de Evolução: ${currentProfile.level} (${currentProfile.getLevelName()})
            - Impulsividade: ${currentProfile.impulsivity} (0.0 a 1.0)
            - Racionalidade: ${currentProfile.rationality} (0.0 a 1.0)
            - Padrão de Tomada de Decisão: ${currentProfile.decisionPattern}
            - Estilo de Comunicação: ${currentProfile.communicationStyle}
            - Erros Recorrentes: ${currentProfile.recurrentErrors}
            - Forças Estratégicas: ${currentProfile.strategicStrengths}
            - Maturidade Emocional: ${currentProfile.emotionalMaturity}

            Compare a nova situação descrita com estes padrões anteriores. O usuário evoluiu ou repetiu um padrão prejudicial?
            Se o usuário descreveu uma decisão precipitada, emocional ou impulsiva, sua impulsividade deve aumentar e a racionalidade diminuir. Se ele demonstrou prudência, planejou ações, controlou impulsos ou buscou leitura fria do ambiente antes de agir, seu nível estratégico e racionalidade aumentam.
            O nível estratégico do usuário deve progredir ou regredir de acordo com o desempenho (de 1 a 5).

            Retorne OBRIGATORIAMENTE um JSON válido com os seguintes campos exatos de texto em português (sem markdown extra, sem blocos de código ```json):
            {
              "scenarioAnalysis": "Sua leitura do cenário atual (Curta, direta, cirúrgica, fria)",
              "strategicPattern": "Qual dinâmica estratégica/de poder ou gatilho está ativo nesta situação",
              "evolutionaryReading": "Leitura evolutiva comparando com o histórico anterior (Se é evolução positiva, repetição de erro, ou gatilho emocional persistente)",
              "actionSteps": "Passo a passo pragmático e preciso de ações imediatas (use quebras de linha se necessário, de forma limpa)",
              "criticalError": "O erro fatal/crítico que pode comprometer totalmente o resultado se o usuário agir mal agora",
              "profileUpdate": {
                "level": <Novo Nível: Int de 1 a 5>,
                "impulsivity": <Novo valor: Float de 0.0 a 1.0>,
                "rationality": <Novo valor: Float de 0.0 a 1.0>,
                "decisionPattern": "Novo padrão resumido de tomada de decisão",
                "emotionalMaturity": "Nova maturidade emocional atualizada",
                "communicationStyle": "Estilo de comunicação demonstrado ou recomendado",
                "recurrentErrors": "Erros recorrentes mapeados até aqui",
                "strategicStrengths": "Forças estratégicas demonstradas ou reforçadas"
              }
            }
            Use frases curtas, ritmo de mentor em tempo real. Não adicione textos longos acadêmicos.
        """.trimIndent()

        val prompt = "Situação descrita pelo usuário:\n$situation"

        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                temperature = 0.4f,
                responseMimeType = "application/json"
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
        )

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("Não foi possível obter uma resposta do mentor.")

            Log.d("MentorRepository", "Gemini Response: $jsonText")

            val adapter = RetrofitClient.moshi.adapter(MentorAnalysisResponse::class.java)
            val analysis = adapter.fromJson(jsonText)
                ?: throw Exception("Falha ao processar o formato da análise do mentor.")

            // Update user profile in local database
            val updatedProfile = UserProfile(
                id = 1,
                level = analysis.profileUpdate.level.coerceIn(1, 5),
                impulsivity = analysis.profileUpdate.impulsivity.coerceIn(0f, 1f),
                rationality = analysis.profileUpdate.rationality.coerceIn(0f, 1f),
                decisionPattern = analysis.profileUpdate.decisionPattern,
                emotionalMaturity = analysis.profileUpdate.emotionalMaturity,
                communicationStyle = analysis.profileUpdate.communicationStyle,
                recurrentErrors = analysis.profileUpdate.recurrentErrors,
                strategicStrengths = analysis.profileUpdate.strategicStrengths,
                lastUpdated = System.currentTimeMillis()
            )
            mentorDao.insertUserProfile(updatedProfile)

            // Save interaction history
            val interaction = Interaction(
                userSituation = situation,
                isVoice = isVoice,
                analysisScenario = analysis.scenarioAnalysis,
                analysisPattern = analysis.strategicPattern,
                analysisEvolution = analysis.evolutionaryReading,
                analysisAction = analysis.actionSteps,
                analysisCriticalError = analysis.criticalError,
                appliedLevel = currentProfile.level
            )
            mentorDao.insertInteraction(interaction)

            Result.success(interaction)
        } catch (e: Exception) {
            Log.e("MentorRepository", "Error analyzing situation", e)
            Result.failure(e)
        }
    }
}
