package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.viewmodel.MentorViewModel

@Composable
fun DashboardScreen(
    viewModel: MentorViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MEMÓRIA EVOLUTIVA",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp),
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Text(
            text = "Perfil Estratégico",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        val activeProfile = profile ?: UserProfile()

        // 1. Level Card
        LevelProgressCard(activeProfile)

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Metrics (Impulsivity vs Rationality)
        MetricsBalanceCard(activeProfile)

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Behavioral Details Card
        BehavioralDetailsCard(activeProfile)

        Spacer(modifier = Modifier.height(24.dp))

        // Reset Data Button
        Button(
            onClick = { viewModel.resetData() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                contentColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("reset_data_button"),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Reiniciar", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("REINICIAR MEMÓRIA EVOLUTIVA", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun LevelProgressCard(profile: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rank",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "RANK ESTRATÉGICO",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = profile.getLevelName(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Level Progress Indicator
            LinearProgressIndicator(
                progress = { profile.level / 5.0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Short Description of the Levels
            Text(
                text = when (profile.level) {
                    1 -> "Suas decisões são reativas, motivadas por impulsos emocionais imediatos. O mentor focará em conter suas reações imediatas."
                    2 -> "Você começa a notar seus padrões de erro, mas ainda falha sob pressão. O mentor o ajudará a prever consequências."
                    3 -> "Você planeja antes de agir e calcula riscos. Suas decisões são estratégicas."
                    4 -> "Executa planos com alto nível de racionalidade e timing excelente."
                    5 -> "Maturidade de filósofo-comandante. Mantém total clareza em crises críticas."
                    else -> ""
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun MetricsBalanceCard(profile: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "EQUILÍBRIO DE TOMADA DE DECISÃO",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Balance Bar
            val impulsivityPercent = profile.impulsivity * 100
            val rationalityPercent = profile.rationality * 100

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Impulsividade: ${impulsivityPercent.toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Racionalidade: ${rationalityPercent.toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Draw a beautiful custom balance gauge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(profile.impulsivity.coerceIn(0.01f, 1f))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                        MaterialTheme.colorScheme.error
                                    )
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(profile.rationality.coerceIn(0.01f, 1f))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.tertiary,
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                                    )
                                )
                            )
                    )
                }

                // Balance point marker
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .background(Color.White)
                        .align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (profile.impulsivity > profile.rationality) {
                    "Seu perfil atual está inclinado ao erro impulsivo. O mentor recomenda aguardar 10 segundos antes de qualquer decisão e evitar responder sob o calor do momento."
                } else {
                    "Seu perfil atual demonstra racionalidade superior. Você analisa as situações friamente antes de agir. Mantenha essa prudência ativa."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun BehavioralDetailsCard(profile: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "LEITURA COMPORTAMENTAL CONTÍNUA",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileRow(
                icon = Icons.Default.Build,
                label = "Padrão de Decisão",
                value = profile.decisionPattern
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 12.dp))

            ProfileRow(
                icon = Icons.Default.Info,
                label = "Maturidade Emocional",
                value = profile.emotionalMaturity
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 12.dp))

            ProfileRow(
                icon = Icons.Default.Share,
                label = "Estilo de Comunicação",
                value = profile.communicationStyle
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 12.dp))

            ProfileRow(
                icon = Icons.Default.Warning,
                label = "Erros Recorrentes Mapeados",
                value = profile.recurrentErrors,
                valueColor = MaterialTheme.colorScheme.error
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 12.dp))

            ProfileRow(
                icon = Icons.Default.Check,
                label = "Forças Estratégicas",
                value = profile.strategicStrengths,
                valueColor = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun ProfileRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor,
                lineHeight = 18.sp
            )
        }
    }
}
