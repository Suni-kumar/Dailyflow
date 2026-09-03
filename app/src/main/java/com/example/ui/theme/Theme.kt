package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun buildDarkColorScheme(accent: DayFlowAccent): ColorScheme {
  val visual = accent.dark
  return darkColorScheme(
    primary = visual.primary,
    onPrimary = visual.onPrimary,
    primaryContainer = visual.primaryContainer,
    onPrimaryContainer = visual.onPrimaryContainer,
    secondary = visual.secondary,
    onSecondary = visual.onSecondary,
    secondaryContainer = visual.secondaryContainer,
    onSecondaryContainer = visual.onSecondaryContainer,
    tertiary = visual.tertiary,
    onTertiary = Color(0xFF1B1C1C),
    background = DayFlowDarkThemeColors.background,
    onBackground = DayFlowDarkThemeColors.onSurface,
    surface = DayFlowDarkThemeColors.surface,
    onSurface = DayFlowDarkThemeColors.onSurface,
    surfaceVariant = DayFlowDarkThemeColors.surfaceVariant,
    onSurfaceVariant = DayFlowDarkThemeColors.onSurfaceVariant,
    surfaceContainer = DayFlowDarkThemeColors.surface,
    surfaceContainerLow = DayFlowDarkThemeColors.surfaceContainerLow,
    surfaceContainerLowest = DayFlowDarkThemeColors.surfaceContainerLowest,
    surfaceContainerHigh = DayFlowDarkThemeColors.surfaceContainerHigh,
    outline = DayFlowDarkThemeColors.outline,
    outlineVariant = DayFlowDarkThemeColors.outlineVariant
  )
}

fun buildLightColorScheme(accent: DayFlowAccent): ColorScheme {
  val visual = accent.light
  return lightColorScheme(
    primary = visual.primary,
    onPrimary = visual.onPrimary,
    primaryContainer = visual.primaryContainer,
    onPrimaryContainer = visual.onPrimaryContainer,
    secondary = visual.secondary,
    onSecondary = visual.onSecondary,
    secondaryContainer = visual.secondaryContainer,
    onSecondaryContainer = visual.onSecondaryContainer,
    tertiary = visual.tertiary,
    onTertiary = Color.White,
    background = DayFlowLightColors.background,
    onBackground = DayFlowLightColors.onSurface,
    surface = DayFlowLightColors.surface,
    onSurface = DayFlowLightColors.onSurface,
    surfaceVariant = DayFlowLightColors.surfaceVariant,
    onSurfaceVariant = DayFlowLightColors.onSurfaceVariant,
    surfaceContainer = DayFlowLightColors.surface,
    surfaceContainerLow = DayFlowLightColors.surfaceContainerLow,
    surfaceContainerLowest = DayFlowLightColors.surfaceContainerLowest,
    surfaceContainerHigh = DayFlowLightColors.surfaceContainerHigh,
    outline = DayFlowLightColors.outline,
    outlineVariant = DayFlowLightColors.outlineVariant
  )
}

@Composable
fun DayFlowTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  accent: DayFlowAccent = DayFlowAccent.ROSEWOOD,
  dynamicColor: Boolean = false, // Keep consistent crafted Stitch branding
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> buildDarkColorScheme(accent)
    else -> buildLightColorScheme(accent)
  }

  CompositionLocalProvider(
    LocalDayFlowAccent provides accent,
    LocalDayFlowColors provides if (darkTheme) DayFlowDarkThemeColors else DayFlowLightColors,
    LocalDayFlowIsDark provides darkTheme
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}

