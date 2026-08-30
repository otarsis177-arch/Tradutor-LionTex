package com.tarsis.liontex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarsis.liontex.domain.model.FlashcardItem
import com.tarsis.liontex.domain.model.WordBreakdown
import com.tarsis.liontex.ui.viewmodel.TranslationViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudyDetailScreen(
  viewModel: TranslationViewModel,
  modifier: Modifier = Modifier
) {
  val studyBreakdown by viewModel.studyBreakdown.collectAsState()
  val flashcards by viewModel.flashcardsList.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) }
  var selectedWord by remember { mutableStateOf<WordBreakdown?>(null) }

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
      // Abas: Análise Gramatical vs Praticar Flashcards
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Análise da Frase", fontWeight = FontWeight.SemiBold) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Flashcards (${flashcards.size})", fontWeight = FontWeight.SemiBold) }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (selectedTab == 0) {
        // Tab 1: Análise Gramatical
        if (studyBreakdown == null) {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(
              modifier = Modifier.padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.height(12.dp))
              Text("Traduza uma frase no Tradutor para ver o estudo detalhado aqui.", textAlign = TextAlign.Center)
            }
          }
        } else {
          val study = studyBreakdown!!

          // Card Frase & Tradução
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("study_sentence_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(modifier = Modifier.padding(20.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.primaryContainer
                ) {
                  Text(
                    text = study.tenseIdentified,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                  )
                }

                IconButton(onClick = { viewModel.speakText(study.fullSentence, isEnglish = true) }) {
                  Icon(Icons.Default.VolumeUp, contentDescription = "Ouvir frase em inglês", tint = MaterialTheme.colorScheme.primary)
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = study.fullSentence,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = study.fullTranslation,
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
              )

              Spacer(modifier = Modifier.height(14.dp))
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
              Spacer(modifier = Modifier.height(12.dp))

              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Estrutura: ${study.grammarStructure}",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = study.studyTip,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Dicionário por Toque (Palavras da Frase)
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("interactive_words_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TouchApp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Dicionário por Toque (Selecione uma palavra):",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                study.words.forEach { wordItem ->
                  val isSelected = selectedWord?.word?.equals(wordItem.word, ignoreCase = true) == true
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { selectedWord = wordItem }
                  ) {
                    Text(
                      text = wordItem.word,
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                      color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                  }
                }
              }

              // Detalhe da Palavra Selecionada
              selectedWord?.let { word ->
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedCard(
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                          text = word.word,
                          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                          color = MaterialTheme.colorScheme.primary
                        )
                        if (word.phonetic.isNotBlank()) {
                          Spacer(modifier = Modifier.width(8.dp))
                          Text(
                            text = word.phonetic,
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.secondary
                          )
                        }
                      }

                      IconButton(onClick = { viewModel.speakText(word.word, isEnglish = true) }) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Pronunciar palavra", tint = MaterialTheme.colorScheme.primary)
                      }
                    }

                    Text(
                      text = "Tradução: ${word.translation} • (${word.partOfSpeech})",
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )

                    if (word.grammarNote.isNotBlank()) {
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = "Uso: ${word.grammarNote}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }

                    if (word.exampleEn.isNotBlank()) {
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                        text = "Exemplo: \"${word.exampleEn}\" -> \"${word.examplePt}\"",
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                }
              }
            }
          }
        }
      } else {
        // Tab 2: Flashcards Trainer
        FlashcardStudyDeck(
          flashcards = flashcards,
          onSpeak = { text -> viewModel.speakText(text, isEnglish = true) },
          onReview = { id, mastered -> viewModel.reviewFlashcard(id, mastered) }
        )
      }

      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}

@Composable
fun FlashcardStudyDeck(
  flashcards: List<FlashcardItem>,
  onSpeak: (String) -> Unit,
  onReview: (Long, Boolean) -> Unit
) {
  var currentIndex by remember { mutableIntStateOf(0) }
  var isFlipped by remember { mutableStateOf(false) }

  if (flashcards.isEmpty()) {
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "Nenhum Flashcard cadastrado ainda.\nSalve frases traduzidas para praticar a memorização!",
          textAlign = TextAlign.Center
        )
      }
    }
  } else {
    val safeIndex = currentIndex.coerceIn(0, flashcards.size - 1)
    val card = flashcards[safeIndex]

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "Card ${safeIndex + 1} de ${flashcards.size}",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.secondary
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Cartão Principal Interativo (Frente / Verso)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
          .clip(RoundedCornerShape(24.dp))
          .clickable { isFlipped = !isFlipped }
          .testTag("flashcard_interactive_item"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isFlipped) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = if (isFlipped) "TRADUÇÃO EM PORTUGUÊS" else "INGLÊS (FRENTE)",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = if (isFlipped) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.primary
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = if (isFlipped) card.backText else card.frontText,
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
              ),
              color = if (isFlipped) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )

            if (card.hint.isNotBlank()) {
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "💡 ${card.hint}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "Toque no card para virar 🔄",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Botões de Resposta & Navegação
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        FilledTonalButton(
          onClick = { onSpeak(card.frontText) },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.VolumeUp, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Ouvir")
        }

        Button(
          onClick = {
            onReview(card.id, true)
            isFlipped = false
            if (currentIndex < flashcards.size - 1) currentIndex++ else currentIndex = 0
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Check, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Já Sei!")
        }
      }
    }
  }
}
