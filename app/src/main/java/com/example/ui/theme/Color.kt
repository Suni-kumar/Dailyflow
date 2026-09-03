package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Stitch Visual Design System Palette (Calm, Minimal, Warm Alabaster & Dusty Rosewood)
val DayFlowPrimary = Color(0xFF7B5455)
val DayFlowPrimaryContainer = Color(0xFFE8C5C5)
val DayFlowPrimaryFixed = Color(0xFFFFDAD9)
val DayFlowOnPrimary = Color(0xFFFFFFFF)
val DayFlowOnPrimaryContainer = Color(0xFF5D3A3B)
val DayFlowOnPrimaryFixed = Color(0xFF2F1314)

// Secondary & Accent Colors
val DayFlowSecondary = Color(0xFF5A5B85)
val DayFlowSecondaryContainer = Color(0xFFCECDFE)
val DayFlowSecondaryFixed = Color(0xFFE1DFFF)
val DayFlowOnSecondary = Color(0xFFFFFFFF)
val DayFlowOnSecondaryContainer = Color(0xFF555680)

val DayFlowTertiary = Color(0xFF5F5E5E)
val DayFlowTertiaryContainer = Color(0xFFB2B0B0)

// Dark Theme Variants (Deep Charcoal, Never Pure #000000)
val DayFlowDarkBackground = Color(0xFF141212)
val DayFlowDarkSurface = Color(0xFF1B1919)
val DayFlowDarkSurfaceLow = Color(0xFF221F1F)
val DayFlowDarkSurfaceLowest = Color(0xFF292626)
val DayFlowDarkSurfaceHigh = Color(0xFF332F2F)
val DayFlowDarkBorder = Color(0xFF3D3737)
val DayFlowDarkOnSurface = Color(0xFFF2EFEA)
val DayFlowDarkOnSurfaceVariant = Color(0xFFB5ABAB)
val DayFlowDarkOnSurfaceSubtle = Color(0xFF857D7D)
val DayFlowDarkPrimary = Color(0xFFECBBBA)

@Immutable
data class DayFlowTonalColors(
  val background: Color,
  val surface: Color,
  val surfaceContainerLow: Color,
  val surfaceContainerLowest: Color,
  val surfaceContainerHigh: Color,
  val surfaceVariant: Color,
  val onSurface: Color,
  val onSurfaceVariant: Color,
  val onSurfaceSubtle: Color,
  val outline: Color,
  val outlineVariant: Color,
  val cardBorder: Color,
  val isDark: Boolean
)

val DayFlowLightColors = DayFlowTonalColors(
  background = Color(0xFFFAF9F6),
  surface = Color(0xFFFAF9F6),
  surfaceContainerLow = Color(0xFFF4F3F1),
  surfaceContainerLowest = Color(0xFFFFFFFF),
  surfaceContainerHigh = Color(0xFFE9E8E5),
  surfaceVariant = Color(0xFFE3E2E0),
  onSurface = Color(0xFF1A1C1A),
  onSurfaceVariant = Color(0xFF504444),
  onSurfaceSubtle = Color(0xFF8A7F7F),
  outline = Color(0xFF827473),
  outlineVariant = Color(0xFFD4C2C2),
  cardBorder = Color(0xFFE3E2E0),
  isDark = false
)

val DayFlowDarkThemeColors = DayFlowTonalColors(
  background = Color(0xFF141212),
  surface = Color(0xFF1B1919),
  surfaceContainerLow = Color(0xFF221F1F),
  surfaceContainerLowest = Color(0xFF292626),
  surfaceContainerHigh = Color(0xFF332F2F),
  surfaceVariant = Color(0xFF2B2727),
  onSurface = Color(0xFFF2EFEA),
  onSurfaceVariant = Color(0xFFB5ABAB),
  onSurfaceSubtle = Color(0xFF857D7D),
  outline = Color(0xFF6B6161),
  outlineVariant = Color(0xFF3D3737),
  cardBorder = Color(0xFF2E2929),
  isDark = true
)

val LocalDayFlowColors = staticCompositionLocalOf { DayFlowLightColors }
val LocalDayFlowIsDark = staticCompositionLocalOf { false }

// Dynamic Composable Color Getters: dynamically adapts based on current theme mode
val DayFlowBackground: Color
  @Composable
  @ReadOnlyComposable
  get() = LocalDayFlowColors.current.background

val DayFlowSurface: Color
  @Composable
  @ReadOnlyComposable
  get() = LocalDayFlowColors.current.surface

val DayFlowSurfaceContainerLow: Color
  @Composable
  @ReadOnlyComposable
  get() = LocalDayFlowColors.current.surfaceContainerLow

val DayFlowSurfaceContainerLowest: Color
  @Composable
  @ReadOnlyComposable
  get() = LocalDayFlowColors.current.surfaceContainerLowest

val DayFlowSurfaceContainerHigh: Color
  @Composable
  @ReadOnlyComposable
  get() = LocalDayFlowColors.current.surfaceContainerHigh

val DayFlowSurfaceVariant: Color
  @Composable
  @ReadOnlyComposable
  get() = LocalDayFlowColors.current.surfaceVariant

val DayFlowOnSurface: Color
  @Composable
  @ReadOnlyComposable
  get() = LocalDayFlowColors.current.onSurface

val DayFlowOnSurfaceVariant: Color
  @Composable
  @ReadOnlyComposable
  get() = LocalDayFlowColors.current.onSurfaceVariant

val DayFlowOnSurfaceSubtle: Color
  @Composable
  @ReadOnlyComposable
  get() = LocalDayFlowColors.current.onSurfaceSubtle

val DayFlowOutline: Color
  @Composable
  @ReadOnlyComposable
  get() = LocalDayFlowColors.current.outline

val DayFlowOutlineVariant: Color
  @Composable
  @ReadOnlyComposable
  get() = LocalDayFlowColors.current.outlineVariant

val DayFlowCardBorder: Color
  @Composable
  @ReadOnlyComposable
  get() = LocalDayFlowColors.current.cardBorder
