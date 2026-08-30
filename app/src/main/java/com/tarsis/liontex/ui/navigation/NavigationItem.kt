package com.tarsis.liontex.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationItem(
  val title: String,
  val icon: ImageVector,
  val route: String
) {
  TRANSLATE("Tradutor", Icons.Default.Translate, "route_translate"),
  IMAGE_OCR("Imagem / OCR", Icons.Default.Image, "route_image_ocr"),
  STUDY("Modo Estudo", Icons.Default.AutoStories, "route_study"),
  HISTORY("Histórico", Icons.Default.History, "route_history"),
  SETTINGS("Ajustes", Icons.Default.Settings, "route_settings")
}
