package com.tarsis.liontex.ui.screens

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarsis.liontex.ui.viewmodel.TranslationViewModel

@Composable
fun SettingsScreen(
  viewModel: TranslationViewModel,
  modifier: Modifier = Modifier
) {
  val currentSpeed by viewModel.ttsManager.currentSpeed.collectAsState()
  val historyList by viewModel.historyList.collectAsState()
  val flashcards by viewModel.flashcardsList.collectAsState()
  val isOnline by viewModel.isOnline.collectAsState()
  val useAiMode by viewModel.useAiMode.collectAsState()

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 680.dp)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Card Conectividade & IA Gemini
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("ai_settings_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Inteligência Artificial (Gemini)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Tradução contextual de alta fidelidade e análise gramatical",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Ativar IA para Traduções",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
              )
              Text(
                text = if (useAiMode) "Usando Gemini 3.5 Flash quando conectado à internet" else "Usando motor offline integrado",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Switch(
              checked = useAiMode,
              onCheckedChange = { viewModel.toggleAiMode(it) }
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
              contentDescription = null,
              tint = if (isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isOnline) "Conexão com a Internet: Ativa" else "Sem Internet (Fallback offline automático)",
              style = MaterialTheme.typography.bodySmall,
              color = if (isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Card Status de Compatibilidade do Dispositivo
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("device_status_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.TabletAndroid, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Dispositivo: Universal (Tablets & Smartphones)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Samsung Galaxy Tab A9 (SM-X115) e Celulares Android",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
          Spacer(modifier = Modifier.height(12.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Motor OCR: Google ML Kit (Offline / On-Device)", style = MaterialTheme.typography.bodySmall)
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Persistência: Banco Local SQLite Seguro", style = MaterialTheme.typography.bodySmall)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Card Ajustes de Voz (TTS)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("tts_settings_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Velocidade da Pronúncia (Text-To-Speech)",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Velocidade atual: ${String.format("%.2f", currentSpeed)}x",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
          )

          Slider(
            value = currentSpeed,
            onValueChange = { viewModel.ttsManager.setSpeechRate(it) },
            valueRange = 0.5f..1.5f,
            steps = 3,
            modifier = Modifier.testTag("speech_rate_slider")
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("0.5x (Lento)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("1.0x (Normal)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("1.5x (Rápido)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedButton(
            onClick = { viewModel.speakText("Testing AI pronunciation and voice speed on LionTex Translator", isEnglish = true) },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Speed, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Testar Pronúncia em Inglês")
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Card Privacidade & Dados Locais
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("privacy_settings_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Privacidade e Armazenamento",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "• ${historyList.size} traduções salvas no histórico local.\n• ${flashcards.size} cartões no deck de estudo.\n• Seus dados de estudo e histórico permanecem no seu aparelho.",
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 22.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedButton(
            onClick = { viewModel.clearAllHistory() },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Limpar Histórico Local", color = MaterialTheme.colorScheme.error)
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}
