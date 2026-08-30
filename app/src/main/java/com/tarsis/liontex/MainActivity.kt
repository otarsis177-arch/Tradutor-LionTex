package com.tarsis.liontex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarsis.liontex.ui.navigation.NavigationItem
import com.tarsis.liontex.ui.screens.HistoryFavoritesScreen
import com.tarsis.liontex.ui.screens.ImageOcrScreen
import com.tarsis.liontex.ui.screens.MainTranslateScreen
import com.tarsis.liontex.ui.screens.SettingsScreen
import com.tarsis.liontex.ui.screens.StudyDetailScreen
import com.tarsis.liontex.ui.theme.TradutorLiontexTheme
import com.tarsis.liontex.ui.viewmodel.TranslationViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      TradutorLiontexTheme {
        val viewModel: TranslationViewModel = viewModel()
        LionTexAppRoot(viewModel = viewModel)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LionTexAppRoot(viewModel: TranslationViewModel) {
  var currentTab by remember { mutableStateOf(NavigationItem.TRANSLATE) }
  val configuration = LocalConfiguration.current
  val isTabletLandscape = configuration.screenWidthDp >= 720 && configuration.screenWidthDp > configuration.screenHeightDp

  if (isTabletLandscape) {
    // Layout com NavigationRail para Tablet em Modo Paisagem (Galaxy Tab A9)
    Row(modifier = Modifier.fillMaxSize()) {
      NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
          Image(
            painter = painterResource(id = R.drawable.img_lion_mascot),
            contentDescription = "Mascote Liontex",
            modifier = Modifier
              .padding(vertical = 12.dp)
              .size(40.dp)
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
        }
      ) {
        NavigationItem.entries.forEach { item ->
          NavigationRailItem(
            selected = currentTab == item,
            onClick = { currentTab = item },
            icon = { Icon(item.icon, contentDescription = item.title) },
            label = { Text(item.title, style = MaterialTheme.typography.labelSmall) },
            modifier = Modifier.testTag("nav_rail_${item.route}")
          )
        }
      }

      Scaffold(
        topBar = { LionTexTopBar() },
        modifier = Modifier.weight(1f)
      ) { innerPadding ->
        ScreenContent(
          currentTab = currentTab,
          viewModel = viewModel,
          onNavigateToStudy = { currentTab = NavigationItem.STUDY },
          onNavigateToTranslate = { currentTab = NavigationItem.TRANSLATE },
          modifier = Modifier.padding(innerPadding)
        )
      }
    }
  } else {
    // Layout Padrão com NavigationBar Inferior para Modo Retrato / Telas Compactas
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = { LionTexTopBar() },
      bottomBar = {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = 4.dp
        ) {
          NavigationItem.entries.forEach { item ->
            NavigationBarItem(
              selected = currentTab == item,
              onClick = { currentTab = item },
              icon = { Icon(item.icon, contentDescription = item.title) },
              label = { Text(item.title, maxLines = 1) },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
              ),
              modifier = Modifier.testTag("nav_bottom_${item.route}")
            )
          }
        }
      }
    ) { innerPadding ->
      ScreenContent(
        currentTab = currentTab,
        viewModel = viewModel,
        onNavigateToStudy = { currentTab = NavigationItem.STUDY },
        onNavigateToTranslate = { currentTab = NavigationItem.TRANSLATE },
        modifier = Modifier.padding(innerPadding)
      )
    }
  }
}

@Composable
fun ScreenContent(
  currentTab: NavigationItem,
  viewModel: TranslationViewModel,
  onNavigateToStudy: () -> Unit,
  onNavigateToTranslate: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier.fillMaxSize()) {
    when (currentTab) {
      NavigationItem.TRANSLATE -> MainTranslateScreen(
        viewModel = viewModel,
        onNavigateToStudy = onNavigateToStudy
      )
      NavigationItem.IMAGE_OCR -> ImageOcrScreen(
        viewModel = viewModel,
        onNavigateToTranslate = onNavigateToTranslate
      )
      NavigationItem.STUDY -> StudyDetailScreen(
        viewModel = viewModel
      )
      NavigationItem.HISTORY -> HistoryFavoritesScreen(
        viewModel = viewModel,
        onOpenInTranslator = { onNavigateToTranslate() },
        onOpenInStudy = { onNavigateToStudy() }
      )
      NavigationItem.SETTINGS -> SettingsScreen(
        viewModel = viewModel
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LionTexTopBar() {
  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface,
      titleContentColor = MaterialTheme.colorScheme.onSurface,
    ),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_lion_mascot),
          contentDescription = "Mascote Liontex",
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape),
          contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = stringResource(id = R.string.app_name),
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          modifier = Modifier.testTag("app_title_text"),
        )
      }
    },
  )
}
