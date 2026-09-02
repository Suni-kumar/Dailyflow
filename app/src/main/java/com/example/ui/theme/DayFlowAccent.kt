package com.example.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Curated professional Accent Palettes for DayFlow.
 * Designed to seamlessly blend with the warm off-white alabaster light background (#FAF9F6)
 * and deep charcoal dark background (#181616) with strict contrast and minimal elegance.
 */
enum class DayFlowAccent(
  val id: String,
  val displayName: String,
  val description: String,
  val primary: Color,
  val primaryContainer: Color,
  val onPrimary: Color,
  val onPrimaryContainer: Color,
  val secondary: Color,
  val secondaryContainer: Color,
  val onSecondary: Color,
  val onSecondaryContainer: Color,
  val tertiary: Color,
  val darkPrimary: Color,
  val darkPrimaryContainer: Color,
  val darkOnPrimaryContainer: Color
) {
  ROSEWOOD(
    id = "rosewood",
    displayName = "Rosewood",
    description = "Warm dusty rosewood & alabaster",
    primary = Color(0xFF7B5455),
    primaryContainer = Color(0xFFE8C5C5),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF5D3A3B),
    secondary = Color(0xFF5A5B85),
    secondaryContainer = Color(0xFFCECDFE),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF42436C),
    tertiary = Color(0xFF705B5C),
    darkPrimary = Color(0xFFECBBBA),
    darkPrimaryContainer = Color(0xFF603D3E),
    darkOnPrimaryContainer = Color(0xFFECBBBA)
  ),

  DUSTY_MAUVE(
    id = "dusty_mauve",
    displayName = "Dusty Mauve",
    description = "Sophisticated muted vintage mauve",
    primary = Color(0xFF7A4E69),
    primaryContainer = Color(0xFFE8C3D8),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF5A344C),
    secondary = Color(0xFF5E5779),
    secondaryContainer = Color(0xFFD2CBEF),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF453F5E),
    tertiary = Color(0xFF6F5762),
    darkPrimary = Color(0xFFE9B5D4),
    darkPrimaryContainer = Color(0xFF603851),
    darkOnPrimaryContainer = Color(0xFFE9B5D4)
  ),

  SAGE(
    id = "sage",
    displayName = "Sage",
    description = "Calm botanical sage & eucalyptus",
    primary = Color(0xFF4A6B53),
    primaryContainer = Color(0xFFC3DEC8),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF324F3B),
    secondary = Color(0xFF4B636B),
    secondaryContainer = Color(0xFFC7DEE5),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF334A51),
    tertiary = Color(0xFF586259),
    darkPrimary = Color(0xFFAED4B7),
    darkPrimaryContainer = Color(0xFF34523C),
    darkOnPrimaryContainer = Color(0xFFAED4B7)
  ),

  FOREST(
    id = "forest",
    displayName = "Forest",
    description = "Deep grounding pine forest green",
    primary = Color(0xFF305B48),
    primaryContainer = Color(0xFFB5DCC8),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF1E4332),
    secondary = Color(0xFF466258),
    secondaryContainer = Color(0xFFC5DFD6),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF304941),
    tertiary = Color(0xFF4C6154),
    darkPrimary = Color(0xFF98D1B3),
    darkPrimaryContainer = Color(0xFF1D4533),
    darkOnPrimaryContainer = Color(0xFF98D1B3)
  ),

  OCEAN(
    id = "ocean",
    displayName = "Ocean",
    description = "Serene nautical teal & coastal blue",
    primary = Color(0xFF326079),
    primaryContainer = Color(0xFFB7DCF0),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF1B475D),
    secondary = Color(0xFF4A5C6F),
    secondaryContainer = Color(0xFFCADAE9),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF334455),
    tertiary = Color(0xFF4B5E68),
    darkPrimary = Color(0xFF9CD1EC),
    darkPrimaryContainer = Color(0xFF1A475F),
    darkOnPrimaryContainer = Color(0xFF9CD1EC)
  ),

  SLATE_BLUE(
    id = "slate_blue",
    displayName = "Slate Blue",
    description = "Architectural muted slate & steel blue",
    primary = Color(0xFF44577C),
    primaryContainer = Color(0xFFC5D3F2),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF2D3E61),
    secondary = Color(0xFF5B5771),
    secondaryContainer = Color(0xFFD7D2EA),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF423F56),
    tertiary = Color(0xFF525763),
    darkPrimary = Color(0xFFAEC4EC),
    darkPrimaryContainer = Color(0xFF2C3E61),
    darkOnPrimaryContainer = Color(0xFFAEC4EC)
  ),

  INDIGO(
    id = "indigo",
    displayName = "Indigo",
    description = "Mindful midnight twilight indigo",
    primary = Color(0xFF514E7E),
    primaryContainer = Color(0xFFCFCCF4),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF393663),
    secondary = Color(0xFF675677),
    secondaryContainer = Color(0xFFE0D3EE),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF4C3E5B),
    tertiary = Color(0xFF5C5766),
    darkPrimary = Color(0xFFBDBAF0),
    darkPrimaryContainer = Color(0xFF3A3765),
    darkOnPrimaryContainer = Color(0xFFBDBAF0)
  ),

  TERRACOTTA(
    id = "terracotta",
    displayName = "Terracotta",
    description = "Warm rustic clay & baked earth",
    primary = Color(0xFF864F37),
    primaryContainer = Color(0xFFEEC4B2),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF653621),
    secondary = Color(0xFF7A5A4A),
    secondaryContainer = Color(0xFFE4D1C6),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF5C4133),
    tertiary = Color(0xFF6E564C),
    darkPrimary = Color(0xFFEBB49C),
    darkPrimaryContainer = Color(0xFF693822),
    darkOnPrimaryContainer = Color(0xFFEBB49C)
  ),

  AMBER(
    id = "amber",
    displayName = "Amber",
    description = "Muted golden ochre & warm amber",
    primary = Color(0xFF7D5F23),
    primaryContainer = Color(0xFFE7D1A2),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF5C4411),
    secondary = Color(0xFF6E5F48),
    secondaryContainer = Color(0xFFDED4C3),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF534633),
    tertiary = Color(0xFF675D4E),
    darkPrimary = Color(0xFFE3C588),
    darkPrimaryContainer = Color(0xFF604713),
    darkOnPrimaryContainer = Color(0xFFE3C588)
  ),

  PLUM(
    id = "plum",
    displayName = "Plum",
    description = "Muted dark amethyst & rich plum",
    primary = Color(0xFF69486E),
    primaryContainer = Color(0xFFE1C3E6),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF4C2F50),
    secondary = Color(0xFF685363),
    secondaryContainer = Color(0xFFE0D0DC),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF4E3C4A),
    tertiary = Color(0xFF645360),
    darkPrimary = Color(0xFFD9B3DE),
    darkPrimaryContainer = Color(0xFF503256),
    darkOnPrimaryContainer = Color(0xFFD9B3DE)
  );

  companion object {
    fun fromId(id: String?): DayFlowAccent {
      if (id.isNullOrBlank()) return ROSEWOOD
      return entries.firstOrNull { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) } ?: ROSEWOOD
    }

    fun fromName(name: String?): DayFlowAccent {
      if (name.isNullOrBlank()) return ROSEWOOD
      return entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) || it.name.equals(name, ignoreCase = true) } ?: ROSEWOOD
    }
  }
}

val LocalDayFlowAccent = compositionLocalOf { DayFlowAccent.ROSEWOOD }
