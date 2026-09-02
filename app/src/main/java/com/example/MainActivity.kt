package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppThemeMode
import com.example.ui.DayFlowApp
import com.example.ui.theme.DayFlowTheme
import com.example.ui.viewmodel.DayFlowViewModel

import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.ui.screens.SplashScreen

class MainActivity : ComponentActivity() {

  private val viewModel: DayFlowViewModel by viewModels {
    DayFlowViewModel.provideFactory(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
      val accentColor by viewModel.accentColor.collectAsStateWithLifecycle()
      val systemDark = isSystemInDarkTheme()
      val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> systemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
      }
      
      var showSplash by rememberSaveable { androidx.compose.runtime.mutableStateOf(true) }

      DayFlowTheme(darkTheme = isDark, accent = accentColor) {
        androidx.compose.animation.Crossfade(
            targetState = showSplash,
            animationSpec = androidx.compose.animation.core.tween(500),
            label = "SplashTransition"
        ) { splash ->
            if (splash) {
                SplashScreen(onSplashFinished = { showSplash = false })
            } else {
                DayFlowApp(viewModel = viewModel)
            }
        }
      }
    }
  }
}


