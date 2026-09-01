package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val leftBarHeight = remember { Animatable(0f) }
    val middleBarHeight = remember { Animatable(0f) }
    val rightBarHeight = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(16f) }

    LaunchedEffect(Unit) {
        // Phase 1: Logo Reveal
        // Left bar: 0.00s -> 0.25s
        // Middle bar: 0.20s -> 0.50s
        // Right bar: 0.40s -> 0.70s
        
        launch {
            leftBarHeight.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
            )
        }
        
        launch {
            delay(200)
            middleBarHeight.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        }
        
        launch {
            delay(400)
            rightBarHeight.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        }
        
        // 0.70s -> 1.00s logo settles
        delay(1000)

        // Phase 4: DayFlow Text Reveal (1.00s -> 1.45s)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
            )
        }
        launch {
            textOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
            )
        }
        
        // 1.45s -> 1.75s Final Brand Moment
        // Wait 450ms for text reveal + 300ms for stable moment = 750ms total
        delay(750)
        
        // Phase 6: Transition to App
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Container
            Row(
                modifier = Modifier.height(100.dp), // Fixed height to allow bars to grow up
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left bar
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height((48 * leftBarHeight.value).dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp))
                )
                // Middle bar
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height((74 * middleBarHeight.value).dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp))
                )
                // Right bar
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height((100 * rightBarHeight.value).dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp))
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Text
            Text(
                text = "DayFlow",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .offset(y = textOffsetY.value.dp)
                    .alpha(textAlpha.value)
            )
        }
    }
}
