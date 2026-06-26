package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Interaction
import com.example.ui.theme.CardBg
import com.example.ui.viewmodel.MentorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorChatScreen(
    viewModel: MentorViewModel,
    modifier: Modifier = Modifier
) {
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val latestAnalysis by viewModel.latestAnalysis.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var textInput by remember { mutableStateOf("") }
    var voiceModeActive by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Scroll to bottom when analysis arrives
    LaunchedEffect(latestAnalysis) {
        if (latestAnalysis != null) {
            scope.launch {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (voiceModeActive) {
            // High-fidelity Voice Interaction screen
            VoiceInteractionScreen(
                onClose = { voiceModeActive = false },
                onAnalyze = { spokenText ->
                    viewModel.analyzeSituation(spokenText, isVoice = true)
                    voiceModeActive = false
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Top Header with Multi-Prompts Quick Modes
                Text(
                    text = "MENTOR FILOSÓFICO",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Aconselhamento de Alta Performance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Quick Action Modes: Crisis, Negotiation, Critical Decision
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickModeChip(
                        label = "Modo Crise",
                        icon = Icons.Default.Warning,
                        onClick = {
                            textInput = "SITUAÇÃO DE CRISE: Estou sob forte pressão emocional porque..."
                        }
                    )
                    QuickModeChip(
                        label = "Negociação",
                        icon = Icons.Default.Share,
                        onClick = {
                            textInput = "NEGOCIAÇÃO: Preciso fechar um acordo estratégico com..."
                        }
                    )
                    QuickModeChip(
                        label = "Decisão Crítica",
                        icon = Icons.Default.Info,
                        onClick = {
                            textInput = "DECISÃO CRÍTICA: Tenho duas opções difíceis e preciso decidir sobre..."
                        }
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))

                // Scrollable main content (either Quote dashboard or Analysis output)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(vertical = 16.dp)
                ) {
                    if (latestAnalysis == null && !isAnalyzing) {
                        // Quotes and welcome Dashboard
                        WelcomeDashboard { scenarioText ->
                            textInput = scenarioText
                        }
                    } else {
                        // Analysis Results
                        latestAnalysis?.let { analysis ->
                            AnalysisDisplay(analysis)
                        }

                        if (isAnalyzing) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Consultando Gracián, Maquiavel e Sun Tzu...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }

                        errorMessage?.let { error ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = "Erro",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodyMedium
                                        )
                                }
                            }
                        }
                    }
                }

                // Input Bar at bottom
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Voice mode toggle
                        IconButton(
                            onClick = { voiceModeActive = true },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("voice_mode_button")
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Modo de Voz",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Text field
                        TextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = {
                                Text(
                                    "Descreva uma situação real de conflito, decisão ou negociação...",
                                    fontSize = 13.sp
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input"),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // Clear input button
                        if (textInput.isNotEmpty()) {
                            IconButton(onClick = { textInput = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar")
                            }
                        }

                        // Send button
                        IconButton(
                            onClick = {
                                if (textInput.trim().isNotEmpty()) {
                                    viewModel.analyzeSituation(textInput, isVoice = false)
                                    textInput = ""
                                }
                            },
                            enabled = textInput.trim().isNotEmpty() && !isAnalyzing,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("send_button")
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Enviar",
                                tint = if (textInput.trim().isNotEmpty()) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickModeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun WelcomeDashboard(onSelectPreset: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "A ARTE DA PRUDÊNCIA",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"Não se mostre vulnerável de imediato. A sutileza e o silêncio são as maiores armas do estrategista maduro.\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "— Baltasar Gracián",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Selecione uma situação exemplo para testar:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        val presets = listOf(
            "Um colega de equipe tentou levar os créditos por um projeto meu em uma reunião geral. Quero responder sem parecer mesquinho, mantendo autoridade.",
            "Estou sob pressão do meu chefe para entregar uma decisão financeira crítica em 1 hora, mas ainda não tenho todos os dados seguros. Devo aceitar o risco?",
            "Um parceiro comercial está tentando alterar os termos do nosso contrato na véspera do fechamento. Ele está usando táticas de intimidação de timing."
        )

        presets.forEach { preset ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable { onSelectPreset(preset) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Selecionar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = preset,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AnalysisDisplay(interaction: Interaction) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "SITUAÇÃO REGISTRADA",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = interaction.userSituation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 1. Leitura do Cenário
        AnalysisCard(
            title = "1. Leitura do Cenário",
            content = interaction.analysisScenario,
            icon = Icons.Default.Info,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Padrão Estratégico
        AnalysisCard(
            title = "2. Padrão Estratégico",
            content = interaction.analysisPattern,
            icon = Icons.Default.Share,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Leitura Evolutiva
        AnalysisCard(
            title = "3. Leitura Evolutiva",
            content = interaction.analysisEvolution,
            icon = Icons.Default.Star,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Direção de Ação
        AnalysisCard(
            title = "4. Direção de Ação",
            content = interaction.analysisAction,
            icon = Icons.Default.Check,
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Erro Crítico
        AnalysisCard(
            title = "5. Erro Crítico",
            content = interaction.analysisCriticalError,
            icon = Icons.Default.Warning,
            color = MaterialTheme.colorScheme.error,
            isError = true
        )
    }
}

@Composable
fun AnalysisCard(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isError: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.05f)
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun VoiceInteractionScreen(
    onClose: () -> Unit,
    onAnalyze: (String) -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }
    var recordingFinished by remember { mutableStateOf(false) }
    var spokenResultText by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition()
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val preConfiguredCrisis = listOf(
        "Eu acabei de descobrir que meu principal fornecedor vai atrasar a entrega e o cliente está me cobrando agressivamente. Estou prestes a responder de forma ríspida.",
        "Meu sócio quer vender a empresa imediatamente por um valor baixo por medo da crise de mercado, mas eu acredito no potencial de longo prazo.",
        "Um competidor direto lançou uma campanha copiando exatamente nossa estratégia de marketing. Minha equipe quer processar imediatamente."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        CardBg
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.White)
            }
            Text(
                text = "MODO DE VOZ ESTRATÉGICO",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Large Mic Ripple Button
        Box(
            modifier = Modifier
                .size(200.dp)
                .clickable {
                    if (!isRecording && !recordingFinished) {
                        isRecording = true
                    } else if (isRecording) {
                        isRecording = false
                        recordingFinished = true
                        spokenResultText = preConfiguredCrisis.random()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (isRecording) {
                // Outer Ripple
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(waveScale)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                )
                // Inner Ripple
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(waveScale * 0.8f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                )
            }

            // Central Button
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Refresh else Icons.Default.PlayArrow,
                    contentDescription = "Microfone",
                    tint = Color.Black,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = when {
                isRecording -> "Escutando sua fala... Clique no botão para finalizar."
                recordingFinished -> "Transcrição de voz concluída."
                else -> "Clique no botão para falar sua situação em tempo real."
            },
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (recordingFinished) {
            // Display simulated transcript
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TRANSCRIÇÃO DE VOZ:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = spokenResultText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onAnalyze(spokenResultText) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("PROCESSAR ANÁLISE DO MENTOR", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        } else {
            // Strategic recommendation
            Text(
                text = "Fale de forma pausada e direta sobre conflitos, reações emocionais sob pressão ou crises que você enfrenta para atualizar sua Memória Evolutiva.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.weight(1.5f))
    }
}
