package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Visual representation of an accent palette variant.
 */
@Immutable
data class DayFlowAccentVisual(
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
  val tertiary: Color
)

/**
 * Curated professional Accent Palettes for DayFlow.
 * Maintains two theme-specific palettes (LightAccentPalette and DarkAccentPalette)
 * behind a unified and stable accent identity.
 */
enum class DayFlowAccent(
  val id: String,
  val light: DayFlowAccentVisual,
  val dark: DayFlowAccentVisual
) {
  ROSEWOOD(
    id = "rosewood",
    light = DayFlowAccentVisual(
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
      tertiary = Color(0xFF705B5C)
    ),
    dark = DayFlowAccentVisual(
      displayName = "Blush Copper",
      description = "Luminous soft copper rose on charcoal",
      primary = Color(0xFFECA9A7),
      primaryContainer = Color(0xFF573335),
      onPrimary = Color(0xFF2B1416),
      onPrimaryContainer = Color(0xFFFFDAD9),
      secondary = Color(0xFFA3A1D4),
      secondaryContainer = Color(0xFF38385A),
      onSecondary = Color(0xFF1E1E34),
      onSecondaryContainer = Color(0xFFE1DFFF),
      tertiary = Color(0xFFD4B4B5)
    )
  ),

  DUSTY_MAUVE(
    id = "dusty_mauve",
    light = DayFlowAccentVisual(
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
      tertiary = Color(0xFF6F5762)
    ),
    dark = DayFlowAccentVisual(
      displayName = "Orchid Mauve",
      description = "Radiant soft amethyst orchid on deep slate",
      primary = Color(0xFFDF9EC3),
      primaryContainer = Color(0xFF542E46),
      onPrimary = Color(0xFF2D1324),
      onPrimaryContainer = Color(0xFFFFD7ED),
      secondary = Color(0xFFBEB3DF),
      secondaryContainer = Color(0xFF433A5F),
      onSecondary = Color(0xFF251F3D),
      onSecondaryContainer = Color(0xFFE8DEFF),
      tertiary = Color(0xFFD8B8C9)
    )
  ),

  SAGE(
    id = "sage",
    light = DayFlowAccentVisual(
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
      tertiary = Color(0xFF586259)
    ),
    dark = DayFlowAccentVisual(
      displayName = "Eucalyptus Mint",
      description = "Bright botanical frosted sage & eucalyptus",
      primary = Color(0xFF9CD8AA),
      primaryContainer = Color(0xFF2B4A34),
      onPrimary = Color(0xFF122718),
      onPrimaryContainer = Color(0xFFC2F5CE),
      secondary = Color(0xFF95BDC7),
      secondaryContainer = Color(0xFF2A454D),
      onSecondary = Color(0xFF10262C),
      onSecondaryContainer = Color(0xFFD2EFF7),
      tertiary = Color(0xFFBDD2BE)
    )
  ),

  FOREST(
    id = "forest",
    light = DayFlowAccentVisual(
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
      tertiary = Color(0xFF4C6154)
    ),
    dark = DayFlowAccentVisual(
      displayName = "Nordic Pine",
      description = "Deep luminous evergreen & alpine jade",
      primary = Color(0xFF86D9B0),
      primaryContainer = Color(0xFF184631),
      onPrimary = Color(0xFF08271A),
      onPrimaryContainer = Color(0xFFB2F8D5),
      secondary = Color(0xFF91C8B4),
      secondaryContainer = Color(0xFF254B3E),
      onSecondary = Color(0xFF0C2B20),
      onSecondaryContainer = Color(0xFFD0F0E4),
      tertiary = Color(0xFFAFD5C3)
    )
  ),

  OCEAN(
    id = "ocean",
    light = DayFlowAccentVisual(
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
      tertiary = Color(0xFF4B5E68)
    ),
    dark = DayFlowAccentVisual(
      displayName = "Glacier Blue",
      description = "Luminous arctic teal & crisp coastal ocean",
      primary = Color(0xFF87D5F3),
      primaryContainer = Color(0xFF16465E),
      onPrimary = Color(0xFF052433),
      onPrimaryContainer = Color(0xFFBAE8FF),
      secondary = Color(0xFF98BFE3),
      secondaryContainer = Color(0xFF25435E),
      onSecondary = Color(0xFF0C253B),
      onSecondaryContainer = Color(0xFFD6E8FB),
      tertiary = Color(0xFFB1D1DF)
    )
  ),

  SLATE_BLUE(
    id = "slate_blue",
    light = DayFlowAccentVisual(
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
      tertiary = Color(0xFF525763)
    ),
    dark = DayFlowAccentVisual(
      displayName = "Celestial Slate",
      description = "Silvery architectural steel & periwinkle",
      primary = Color(0xFF9CB8E8),
      primaryContainer = Color(0xFF273A5C),
      onPrimary = Color(0xFF0F1E36),
      onPrimaryContainer = Color(0xFFD1E1FF),
      secondary = Color(0xFFB5AFDA),
      secondaryContainer = Color(0xFF3C385C),
      onSecondary = Color(0xFF1F1C38),
      onSecondaryContainer = Color(0xFFE5DFFF),
      tertiary = Color(0xFFBAC6DA)
    )
  ),

  INDIGO(
    id = "indigo",
    light = DayFlowAccentVisual(
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
      tertiary = Color(0xFF5C5766)
    ),
    dark = DayFlowAccentVisual(
      displayName = "Twilight Violet",
      description = "Ethereal midnight violet & cosmic lavender",
      primary = Color(0xFFB4ADF5),
      primaryContainer = Color(0xFF33305D),
      onPrimary = Color(0xFF18153B),
      onPrimaryContainer = Color(0xFFE2DFFF),
      secondary = Color(0xFFC7AEE2),
      secondaryContainer = Color(0xFF4A3561),
      onSecondary = Color(0xFF26163A),
      onSecondaryContainer = Color(0xFFF1E3FF),
      tertiary = Color(0xFFCCC5E2)
    )
  ),

  TERRACOTTA(
    id = "terracotta",
    light = DayFlowAccentVisual(
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
      tertiary = Color(0xFF6E564C)
    ),
    dark = DayFlowAccentVisual(
      displayName = "Warm Amberclay",
      description = "Sunbaked rustic clay & glowing embers",
      primary = Color(0xFFF0A787),
      primaryContainer = Color(0xFF5C311F),
      onPrimary = Color(0xFF311409),
      onPrimaryContainer = Color(0xFFFFDBD0),
      secondary = Color(0xFFD4AEA1),
      secondaryContainer = Color(0xFF543930),
      onSecondary = Color(0xFF2D1912),
      onSecondaryContainer = Color(0xFFFFE0D7),
      tertiary = Color(0xFFDDC1BA)
    )
  ),

  AMBER(
    id = "amber",
    light = DayFlowAccentVisual(
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
      tertiary = Color(0xFF675D4E)
    ),
    dark = DayFlowAccentVisual(
      displayName = "Golden Ochre",
      description = "Radiant warm golden harvest & honey amber",
      primary = Color(0xFFECC475),
      primaryContainer = Color(0xFF533C0E),
      onPrimary = Color(0xFF2B1D00),
      onPrimaryContainer = Color(0xFFFFE09E),
      secondary = Color(0xFFD7C7A3),
      secondaryContainer = Color(0xFF4E4227),
      onSecondary = Color(0xFF27200E),
      onSecondaryContainer = Color(0xFFFDF0CF),
      tertiary = Color(0xFFDDD0B8)
    )
  ),

  PLUM(
    id = "plum",
    light = DayFlowAccentVisual(
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
      tertiary = Color(0xFF645360)
    ),
    dark = DayFlowAccentVisual(
      displayName = "Velvet Amethyst",
      description = "Regal deep amethyst crystal & luminous plum",
      primary = Color(0xFFD89FE3),
      primaryContainer = Color(0xFF4A2851),
      onPrimary = Color(0xFF250F2B),
      onPrimaryContainer = Color(0xFFF9D8FF),
      secondary = Color(0xFFCEABC6),
      secondaryContainer = Color(0xFF4E3549),
      onSecondary = Color(0xFF281625),
      onSecondaryContainer = Color(0xFFF8E5F4),
      tertiary = Color(0xFFD6C0D2)
    )
  );

  fun visual(isDark: Boolean): DayFlowAccentVisual = if (isDark) dark else light

  val primary: Color get() = light.primary
  val displayName: String get() = light.displayName
  val description: String get() = light.description

  fun getDisplayName(isDark: Boolean): String = visual(isDark).displayName
  fun getDescription(isDark: Boolean): String = visual(isDark).description
  fun getPrimary(isDark: Boolean): Color = visual(isDark).primary
  fun getPrimaryContainer(isDark: Boolean): Color = visual(isDark).primaryContainer
  fun getSecondary(isDark: Boolean): Color = visual(isDark).secondary

  companion object {
    fun fromId(id: String?): DayFlowAccent {
      if (id.isNullOrBlank()) return ROSEWOOD
      return entries.firstOrNull { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) } ?: ROSEWOOD
    }

    fun fromName(name: String?): DayFlowAccent {
      if (name.isNullOrBlank()) return ROSEWOOD
      return entries.firstOrNull {
        it.light.displayName.equals(name, ignoreCase = true) ||
        it.dark.displayName.equals(name, ignoreCase = true) ||
        it.name.equals(name, ignoreCase = true)
      } ?: ROSEWOOD
    }
  }
}

val LocalDayFlowAccent = compositionLocalOf { DayFlowAccent.ROSEWOOD }
