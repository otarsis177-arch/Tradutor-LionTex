package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
  var showDetails by remember { mutableStateOf(false) }
  var testCounter by remember { mutableIntStateOf(0) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      CenterAlignedTopAppBar(
        colors =
          TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
          ),
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
          ) {
            Box(
              modifier =
                Modifier.size(32.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center,
            ) {
              Image(
                painter = painterResource(id = R.drawable.img_lion_mascot),
                contentDescription = "Logo Mascote Leão LionTex",
                modifier = Modifier.size(28.dp).clip(CircleShape),
                contentScale = ContentScale.Crop,
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Tradutor LionTex",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              modifier = Modifier.testTag("app_title_text"),
            )
          }
        },
      )
    },
  ) { innerPadding ->
    Column(
      modifier =
        Modifier.fillMaxSize()
          .padding(innerPadding)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Mascote Card
      Card(
        modifier = Modifier.fillMaxWidth().testTag("mascot_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors =
          CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Box(
            modifier =
              Modifier.size(110.dp)
                .clip(CircleShape)
                .border(
                  width = 3.dp,
                  brush =
                    Brush.linearGradient(
                      listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                      )
                    ),
                  shape = CircleShape,
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center,
          ) {
            Image(
              painter = painterResource(id = R.drawable.img_lion_mascot),
              contentDescription = "Mascote Leão do Tradutor LionTex",
              modifier = Modifier.fillMaxSize().clip(CircleShape),
              contentScale = ContentScale.Crop,
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "Olá! Eu sou o LionTex 🦁",
            style =
              MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
              ),
            textAlign = TextAlign.Center,
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text =
              "Seu assistente inteligente para tradução contextual e aprendizado de inglês de alta precisão.",
            style =
              MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
            textAlign = TextAlign.Center,
          )

          Spacer(modifier = Modifier.height(14.dp))

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Fase 1: Projeto Base Concluído",
                style =
                  MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                  ),
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Status do Pipeline Planejado
      Text(
        text = "Estrutura do Projeto",
        style =
          MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
          ),
        modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(10.dp))

      PhaseItem(
        icon = Icons.Default.CheckCircle,
        title = "Fase 1: Configuração Base & APK",
        description = "Kotlin DSL, Jetpack Compose, Material 3 e GitHub Actions prontos.",
        isCompleted = true,
      )

      PhaseItem(
        icon = Icons.Default.PlayArrow,
        title = "Fase 2 & 3: Captura & Processamento",
        description = "Mecanismo oficial MediaProjection e pré-processamento de imagem.",
        isCompleted = false,
      )

      PhaseItem(
        icon = Icons.Default.Translate,
        title = "Fase 4 & 5: OCR & Tradução Contextual",
        description = "Reconhecimento de alta precisão, validação de erros e tradução EN ↔ PT.",
        isCompleted = false,
      )

      PhaseItem(
        icon = Icons.Default.Science,
        title = "Fase 6: Modo Estudo & Explicação",
        description = "Decomposição palavra por palavra, pronúncia e explicações gramaticais.",
        isCompleted = false,
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Teste Interativo de Funcionamento no Tablet
      Card(
        modifier = Modifier.fillMaxWidth().testTag("tablet_verification_card"),
        shape = RoundedCornerShape(16.dp),
        colors =
          CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Verificação de Execução",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text =
              "Toque no botão abaixo para validar a reatividade do Jetpack Compose no seu tablet:",
            style =
              MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Button(
              onClick = { testCounter++ },
              modifier = Modifier.testTag("test_ping_button"),
              colors =
                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
              Text("Testar Toque (${testCounter})")
            }

            OutlinedButton(
              onClick = { showDetails = !showDetails },
              modifier = Modifier.testTag("toggle_details_button"),
            ) {
              Text(if (showDetails) "Ocultar Info" else "Ver Detalhes")
            }
          }

          AnimatedVisibility(visible = showDetails) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
              Text(
                text = "• Arquitetura: Clean Architecture & Jetpack Compose",
                style = MaterialTheme.typography.bodySmall,
              )
              Text(
                text = "• Compatibilidade: Android 7.0+ (API 24 até API 36)",
                style = MaterialTheme.typography.bodySmall,
              )
              Text(
                text = "• CI/CD: Compilação remota configurada com GitHub Actions",
                style = MaterialTheme.typography.bodySmall,
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun PhaseItem(
  icon: ImageVector,
  title: String,
  description: String,
  isCompleted: Boolean,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
    shape = RoundedCornerShape(12.dp),
    colors =
      CardDefaults.cardColors(
        containerColor =
          if (isCompleted) MaterialTheme.colorScheme.surface
          else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
      ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 1.dp else 0.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint =
          if (isCompleted) MaterialTheme.colorScheme.primary
          else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.size(22.dp),
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = title,
          style =
            MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.SemiBold,
              color =
                if (isCompleted) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            ),
        )
        Text(
          text = description,
          style =
            MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
        )
      }
    }
  }
}
