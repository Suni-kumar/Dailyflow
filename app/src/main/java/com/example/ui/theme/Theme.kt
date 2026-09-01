package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = DayFlowDarkPrimary,
  onPrimary = Color(0xFF472627),
  primaryContainer = Color(0xFF603D3E),
  onPrimaryContainer = DayFlowDarkPrimary,
  secondary = DayFlowSecondaryContainer,
  onSecondary = Color(0xFF16173E),
  secondaryContainer = Color(0xFF42436C),
  onSecondaryContainer = DayFlowSecondaryContainer,
  tertiary = DayFlowTertiaryContainer,
  onTertiary = Color(0xFF1B1C1C),
  background = DayFlowDarkBackground,
  onBackground = DayFlowDarkOnSurface,
  surface = DayFlowDarkSurface,
  onSurface = DayFlowDarkOnSurface,
  surfaceVariant = DayFlowDarkSurfaceLow,
  onSurfaceVariant = DayFlowDarkOnSurfaceVariant,
  outline = DayFlowDarkBorder
)

private val LightColorScheme = lightColorScheme(
  primary = DayFlowPrimary,
  onPrimary = DayFlowOnPrimary,
  primaryContainer = DayFlowPrimaryContainer,
  onPrimaryContainer = DayFlowOnPrimaryContainer,
  secondary = DayFlowSecondary,
  onSecondary = DayFlowOnSecondary,
  secondaryContainer = DayFlowSecondaryContainer,
  onSecondaryContainer = DayFlowOnSecondaryContainer,
  tertiary = DayFlowTertiary,
  onTertiary = Color.White,
  background = DayFlowBackground,
  onBackground = DayFlowOnSurface,
  surface = DayFlowSurface,
  onSurface = DayFlowOnSurface,
  surfaceVariant = DayFlowSurfaceVariant,
  onSurfaceVariant = DayFlowOnSurfaceVariant,
  outline = DayFlowOutlineVariant
)

@Composable
fun DayFlowTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent crafted Stitch branding
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
