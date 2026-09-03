package com.example

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.DayFlowAccent
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testAccentPaletteCount() {
    assertEquals(10, DayFlowAccent.entries.size)
  }

  @Test
  fun testLightAndDarkPalettesAreDistinct() {
    for (accent in DayFlowAccent.entries) {
      val lightVisual = accent.light
      val darkVisual = accent.dark

      // Verify names and descriptions are crafted per theme
      assertNotNull(lightVisual.displayName)
      assertNotNull(darkVisual.displayName)
      assertNotNull(lightVisual.description)
      assertNotNull(darkVisual.description)

      // Verify visual primary colors differ between light and dark
      assertNotEquals(
        "Accent ${accent.id} primary color should differ between light and dark palettes",
        lightVisual.primary,
        darkVisual.primary
      )

      // Verify contrast tones
      assertNotEquals(Color.Transparent, lightVisual.primary)
      assertNotEquals(Color.Transparent, darkVisual.primary)
      assertNotEquals(Color.Transparent, lightVisual.primaryContainer)
      assertNotEquals(Color.Transparent, darkVisual.primaryContainer)
    }
  }

  @Test
  fun testAll10AccentsHaveUniqueColorsInBothThemes() {
    val lightPrimaries = DayFlowAccent.entries.map { it.light.primary }.toSet()
    val darkPrimaries = DayFlowAccent.entries.map { it.dark.primary }.toSet()

    assertEquals("All 10 light accents must have unique primary colors", 10, lightPrimaries.size)
    assertEquals("All 10 dark accents must have unique primary colors", 10, darkPrimaries.size)
  }

  @Test
  fun testAccentIdentityResolution() {
    for (accent in DayFlowAccent.entries) {
      assertEquals(accent, DayFlowAccent.fromId(accent.id))
      assertEquals(accent, DayFlowAccent.fromName(accent.light.displayName))
      assertEquals(accent, DayFlowAccent.fromName(accent.dark.displayName))
    }
    // Fallback default
    assertEquals(DayFlowAccent.ROSEWOOD, DayFlowAccent.fromId("unknown"))
  }
}
