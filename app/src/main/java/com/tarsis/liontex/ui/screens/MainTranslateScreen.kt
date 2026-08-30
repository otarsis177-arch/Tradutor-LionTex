package com.tarsis.liontex.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarsis.liontex.domain.model.Language
import com.tarsis.liontex.ui.viewmodel.TranslationViewModel

@Composable
fun MainTranslateScreen(
  viewModel: TranslationViewModel,
  onNavigateToStudy: () -> Unit,
  modifier: Modifier = Modifier
) {
  val inputText by viewModel.inputText.collectAsState()
  val sourceLang by viewModel.sourceLanguage.collectAsState()
  val targetLang by viewModel.targetLanguage.collectAsState()
  val result by viewModel.translationResult.collectAsState()
  val feedbackMsg by viewModel.userFeedbackMessage.collectAsState()

  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current

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
      // Feedback Toast/Banner
      AnimatedVisibility(
        visible = feedbackMsg != null,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        feedbackMsg?.let { msg ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 12.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
        }
      }

      // Seletor de Idiomas (Origem <-> Destino)
      LanguageSelectorBar(
        sourceLang = sourceLang,
        targetLang = targetLang,
        onSourceSelected = { viewModel.setSourceLanguage(it) },
        onTargetSelected = { viewModel.setTargetLanguage(it) },
        onSwapLanguages = { viewModel.swapLanguages() }
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Caixa de Texto de Entrada
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_text_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          OutlinedTextField(
            value = inputText,
            onValueChange = { viewModel.updateInputText(it) },
            placeholder = { Text("Digite, cole ou capture um texto para traduzir...") },
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp)
              .testTag("translate_input_field"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            shape = RoundedCornerShape(14.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (inputText.isNotBlank()) {
                IconButton(
                  onClick = { viewModel.speakText(inputText, isEnglish = sourceLang != Language.PORTUGUESE) },
                  modifier = Modifier.testTag("speak_input_button")
                ) {
                  Icon(Icons.Default.VolumeUp, contentDescription = "Ouvir texto original", tint = MaterialTheme.colorScheme.primary)
                }

                IconButton(
                  onClick = { viewModel.updateInputText("") },
                  modifier = Modifier.testTag("clear_input_button")
                ) {
                  Icon(Icons.Default.Clear, contentDescription = "Limpar texto")
                }
              }

              IconButton(
                onClick = {
                  val androidClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = androidClipboard.primaryClip
                  if (clip != null && clip.itemCount > 0) {
                    val pasted = clip.getItemAt(0).text?.toString() ?: ""
                    if (pasted.isNotBlank()) {
                      viewModel.updateInputText(pasted)
                      viewModel.performTranslation(saveToHistory = true)
                    }
                  }
                },
                modifier = Modifier.testTag("paste_button")
              ) {
                Icon(Icons.Default.ContentPaste, contentDescription = "Colar da área de transferência")
              }
            }

            Button(
              onClick = { viewModel.performTranslation(saveToHistory = true) },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("translate_submit_button")
            ) {
              Text("Traduzir", fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Cartão de Resultado da Tradução
      result?.let { res ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("translation_result_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "TRADUÇÃO (${res.targetLang.displayName.uppercase()})",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp,
                  color = MaterialTheme.colorScheme.primary
                )
              )

              Row {
                IconButton(
                  onClick = { viewModel.speakText(res.translatedText, isEnglish = res.targetLang == Language.ENGLISH) },
                  modifier = Modifier.testTag("speak_result_button")
                ) {
                  Icon(Icons.Default.VolumeUp, contentDescription = "Ouvir tradução", tint = MaterialTheme.colorScheme.primary)
                }

                IconButton(
                  onClick = {
                    clipboardManager.setText(AnnotatedString(res.translatedText))
                  },
                  modifier = Modifier.testTag("copy_result_button")
                ) {
                  Icon(Icons.Default.ContentCopy, contentDescription = "Copiar tradução")
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = res.translatedText,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                lineHeight = 26.sp
              ),
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.testTag("translated_text_display")
            )

            if (res.contextualExplanation.isNotBlank()) {
              Spacer(modifier = Modifier.height(12.dp))
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = res.contextualExplanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão para o Modo Estudo e Flashcards
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              FilledTonalButton(
                onClick = onNavigateToStudy,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("open_study_mode_button")
              ) {
                Icon(Icons.Default.School, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Modo Estudo")
              }

              OutlinedButton(
                onClick = {
                  viewModel.saveAsFlashcard(
                    front = res.originalText,
                    back = res.translatedText,
                    hint = res.contextualExplanation
                  )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("save_flashcard_button")
              ) {
                Icon(Icons.Default.Star, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Salvar Card")
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}

@Composable
fun LanguageSelectorBar(
  sourceLang: Language,
  targetLang: Language,
  onSourceSelected: (Language) -> Unit,
  onTargetSelected: (Language) -> Unit,
  onSwapLanguages: () -> Unit
) {
  var showSourceMenu by remember { mutableStateOf(false) }
  var showTargetMenu by remember { mutableStateOf(false) }

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Origem
      Box {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
          modifier = Modifier.clickable { showSourceMenu = true }
        ) {
          Text(
            text = sourceLang.displayName,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
          )
        }

        DropdownMenu(
          expanded = showSourceMenu,
          onDismissRequest = { showSourceMenu = false }
        ) {
          Language.entries.forEach { lang ->
            DropdownMenuItem(
              text = { Text(lang.displayName) },
              onClick = {
                onSourceSelected(lang)
                showSourceMenu = false
              }
            )
          }
        }
      }

      // Botão Inverter
      IconButton(
        onClick = onSwapLanguages,
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
      ) {
        Icon(
          Icons.Default.SwapHoriz,
          contentDescription = "Inverter Idiomas",
          tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
      }

      // Destino
      Box {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
          modifier = Modifier.clickable { showTargetMenu = true }
        ) {
          Text(
            text = targetLang.displayName,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
          )
        }

        DropdownMenu(
          expanded = showTargetMenu,
          onDismissRequest = { showTargetMenu = false }
        ) {
          listOf(Language.PORTUGUESE, Language.ENGLISH, Language.SPANISH).forEach { lang ->
            DropdownMenuItem(
              text = { Text(lang.displayName) },
              onClick = {
                onTargetSelected(lang)
                showTargetMenu = false
              }
            )
          }
        }
      }
    }
  }
}
