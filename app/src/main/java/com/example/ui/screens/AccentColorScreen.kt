package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DayFlowAccent
import com.example.ui.theme.DayFlowBackground
import com.example.ui.theme.DayFlowCardBorder
import com.example.ui.theme.DayFlowOnPrimary
import com.example.ui.theme.DayFlowOnSurface
import com.example.ui.theme.DayFlowOnSurfaceVariant
import com.example.ui.theme.DayFlowOutlineVariant
import com.example.ui.theme.DayFlowSurface
import com.example.ui.theme.DayFlowSurfaceContainerLow
import com.example.ui.theme.DayFlowSurfaceVariant
import com.example.ui.theme.LocalDayFlowIsDark

@Composable
fun AccentColorScreen(
  currentAccent: DayFlowAccent,
  onSelectAccent: (DayFlowAccent) -> Unit,
  onNavigateBack: () -> Unit
) {
  Scaffold(
    containerColor = DayFlowBackground,
    modifier = Modifier
      .fillMaxSize()
      .testTag("accent_color_screen")
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 1. Top Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .height(60.dp)
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier
            .size(40.dp)
            .testTag("accent_color_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to Settings",
            tint = DayFlowOnSurfaceVariant
          )
        }

        Text(
          text = "Accent Color",
          style = MaterialTheme.typography.titleLarge.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal
          ),
          color = DayFlowOnSurface
        )

        Box(modifier = Modifier.size(40.dp))
      }

      // 2. Subtitle Section
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 6.dp)
      ) {
        Text(
          text = "Choose an accent that fits your flow.",
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp
          ),
          color = DayFlowOnSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3. 10 Curated Palettes List
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(DayFlowAccent.entries, key = { it.id }) { accent ->
          val isSelected = accent == currentAccent

          AccentOptionCard(
            accent = accent,
            isSelected = isSelected,
            onClick = { onSelectAccent(accent) }
          )
        }

        item {
          Spacer(modifier = Modifier.height(32.dp))
        }
      }
    }
  }
}

@Composable
private fun AccentOptionCard(
  accent: DayFlowAccent,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val isDark = LocalDayFlowIsDark.current
  val activePrimary = if (isDark) accent.darkPrimary else accent.primary

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .testTag("accent_option_${accent.id}"),
    shape = RoundedCornerShape(16.dp),
    color = if (isSelected) DayFlowSurfaceContainerLow else DayFlowSurface,
    border = BorderStroke(
      width = if (isSelected) 1.5.dp else 1.dp,
      color = if (isSelected) activePrimary else DayFlowCardBorder
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp, vertical = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.weight(1f)
      ) {
        // Palette Swatch preview (Primary dot + secondary tone)
        PaletteSwatchPreview(accent = accent)

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = accent.displayName,
            style = MaterialTheme.typography.titleMedium.copy(
              fontSize = 16.sp,
              fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = if (isSelected) activePrimary else DayFlowOnSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = accent.description,
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 12.sp,
              lineHeight = 16.sp
            ),
            color = DayFlowOnSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Checkmark Selection Indicator
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(if (isSelected) activePrimary else Color.Transparent)
          .then(
            if (!isSelected) {
              Modifier.background(DayFlowSurfaceVariant.copy(alpha = 0.5f))
            } else Modifier
          ),
        contentAlignment = Alignment.Center
      ) {
        if (isSelected) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Selected",
            tint = DayFlowOnPrimary,
            modifier = Modifier.size(15.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun PaletteSwatchPreview(accent: DayFlowAccent) {
  val isDark = LocalDayFlowIsDark.current
  val primaryTone = if (isDark) accent.darkPrimary else accent.primary
  val containerTone = if (isDark) accent.darkPrimaryContainer else accent.primaryContainer

  Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Primary Tone
    Box(
      modifier = Modifier
        .size(22.dp)
        .clip(CircleShape)
        .background(primaryTone)
    )

    // Primary Container Tone
    Box(
      modifier = Modifier
        .size(16.dp)
        .clip(CircleShape)
        .background(containerTone)
    )

    // Secondary Accent Tone
    Box(
      modifier = Modifier
        .size(12.dp)
        .clip(CircleShape)
        .background(accent.secondary)
    )
  }
}
