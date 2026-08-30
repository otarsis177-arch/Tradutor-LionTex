package com.tarsis.liontex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tarsis.liontex.ui.theme.TradutorLiontexTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      TradutorLiontexTheme {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          topBar = { TopBar() }
        ) { innerPadding ->
          MainScreen(modifier = Modifier.padding(innerPadding))
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
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

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
  var touchCount by remember { mutableIntStateOf(0) }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 620.dp) // Otimizado para telas de tablets (evita layout esticado)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Card Principal com Mascote
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("mascot_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          // Imagem do mascote com borda gradiente
          Box(
            modifier = Modifier
              .size(120.dp)
              .clip(CircleShape)
              .border(
                width = 3.dp,
                brush = Brush.linearGradient(
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
              contentDescription = "Mascote Leão do Tradutor Liontex",
              modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
              contentScale = ContentScale.Crop,
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          Text(
            text = "Tradutor Liontex",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
            ),
            textAlign = TextAlign.Center,
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Assistente de Tradução e Estudo de Inglês",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center,
          )

          Spacer(modifier = Modifier.height(16.dp))

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
          ) {
            Text(
              text = "Fase 1: Base Pronta para Tablet Samsung A9",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
              ),
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Card de Teste Interativo para o Tablet
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("tablet_test_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Teste de Toque & Responsividade",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Valide a renderização fluida no seu Galaxy Tab A9:",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = { touchCount++ },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("test_touch_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              text = if (touchCount == 0) "Toque aqui para testar" else "Toques registrados: $touchCount",
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
fun TabletLandscapePreview() {
  TradutorLiontexTheme {
    MainScreen()
  }
}
