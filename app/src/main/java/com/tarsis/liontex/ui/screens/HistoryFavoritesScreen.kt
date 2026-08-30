package com.tarsis.liontex.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarsis.liontex.domain.model.HistoryItem
import com.tarsis.liontex.ui.viewmodel.TranslationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryFavoritesScreen(
  viewModel: TranslationViewModel,
  onOpenInTranslator: (String) -> Unit,
  onOpenInStudy: () -> Unit,
  modifier: Modifier = Modifier
) {
  val historyList by viewModel.historyList.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var filterCategory by remember { mutableIntStateOf(0) } // 0 = Todos, 1 = Favoritos, 2 = Imagens
  var showClearDialog by remember { mutableStateOf(false) }

  val clipboardManager = LocalClipboardManager.current
  val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

  val filteredList = historyList.filter { item ->
    val matchesSearch = item.originalText.contains(searchQuery, ignoreCase = true) ||
      item.translatedText.contains(searchQuery, ignoreCase = true)

    val matchesCategory = when (filterCategory) {
      1 -> item.isFavorite
      2 -> item.sourceMode == "image" || item.sourceMode == "screen"
      else -> true
    }

    matchesSearch && matchesCategory
  }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 680.dp)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Barra de Busca
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Pesquisar no histórico...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Limpar busca")
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("history_search_input"),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Filtros em Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FilterChip(
            selected = filterCategory == 0,
            onClick = { filterCategory = 0 },
            label = { Text("Todos (${historyList.size})") }
          )
          FilterChip(
            selected = filterCategory == 1,
            onClick = { filterCategory = 1 },
            label = { Text("Favoritos") },
            leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp)) }
          )
          FilterChip(
            selected = filterCategory == 2,
            onClick = { filterCategory = 2 },
            label = { Text("Imagens") }
          )
        }

        if (historyList.isNotEmpty()) {
          IconButton(onClick = { showClearDialog = true }) {
            Icon(Icons.Default.DeleteSweep, contentDescription = "Limpar Histórico", tint = MaterialTheme.colorScheme.error)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Lista de Itens
      if (filteredList.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = if (searchQuery.isNotBlank()) "Nenhum resultado para \"$searchQuery\"." else "Nenhuma tradução no histórico.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredList, key = { it.id }) { item ->
            HistoryCardItem(
              item = item,
              dateFormat = dateFormat,
              onSpeak = { text -> viewModel.speakText(text, isEnglish = item.sourceLangCode == "en") },
              onCopy = { text -> clipboardManager.setText(AnnotatedString(text)) },
              onToggleFavorite = { viewModel.toggleFavorite(item) },
              onDelete = { viewModel.deleteHistoryItem(item.id) },
              onOpenTranslator = {
                viewModel.updateInputText(item.originalText)
                viewModel.performTranslation(saveToHistory = false)
                onOpenInTranslator(item.originalText)
              },
              onOpenStudy = {
                viewModel.updateInputText(item.originalText)
                viewModel.performTranslation(saveToHistory = false)
                onOpenInStudy()
              }
            )
          }
        }
      }
    }
  }

  // Diálogo para Limpar Tudo
  if (showClearDialog) {
    AlertDialog(
      onDismissRequest = { showClearDialog = false },
      title = { Text("Limpar Histórico") },
      text = { Text("Deseja realmente apagar todas as traduções salvas no histórico local?") },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.clearAllHistory()
            showClearDialog = false
          }
        ) {
          Text("Apagar Tudo", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearDialog = false }) {
          Text("Cancelar")
        }
      }
    )
  }
}

@Composable
fun HistoryCardItem(
  item: HistoryItem,
  dateFormat: SimpleDateFormat,
  onSpeak: (String) -> Unit,
  onCopy: (String) -> Unit,
  onToggleFavorite: () -> Unit,
  onDelete: () -> Unit,
  onOpenTranslator: () -> Unit,
  onOpenStudy: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("history_item_${item.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = "${item.sourceLangCode.uppercase()} → ${item.targetLangCode.uppercase()} • ${item.sourceMode.uppercase()}",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Text(
          text = dateFormat.format(Date(item.timestamp)),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = item.originalText,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = item.translatedText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row {
          IconButton(onClick = { onSpeak(item.originalText) }, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.VolumeUp, contentDescription = "Ouvir", modifier = Modifier.size(18.dp))
          }
          IconButton(onClick = { onCopy(item.translatedText) }, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", modifier = Modifier.size(18.dp))
          }
          IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
            Icon(
              if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
              contentDescription = "Favoritar",
              tint = if (item.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
          IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Excluir", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          TextButton(onClick = onOpenStudy) {
            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Estudar", style = MaterialTheme.typography.labelSmall)
          }
        }
      }
    }
  }
}
