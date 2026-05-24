package com.example.elite_fitness_app.presentation.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.elite_fitness_app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isLoggedIn: Boolean,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    // Animation states
    val brandAlpha = remember { Animatable(0f) }
    val brandTranslateY = remember { Animatable(20f) }
    var progressValue by remember { mutableFloatStateOf(0.05f) }
    var loadingText by remember { mutableStateOf("Optimizing your fitness data") }

    val pulseScale = rememberInfiniteTransition(label = "pulse")
    val glowScale = pulseScale.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow_pulse",
    )

    // Animated gradient background
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val gradientOffset = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "gradient_offset",
    )

    LaunchedEffect(Unit) {
        // Brand entrance animation
        brandAlpha.animateTo(1f, tween(1000, easing = EaseOut))
        brandTranslateY.animateTo(0f, tween(1000, easing = EaseOut))

        // Progress stages
        delay(400)
        progressValue = 0.25f; loadingText = "Syncing biometrics..."
        delay(600 + (Math.random() * 400).toLong())
        progressValue = 0.55f; loadingText = "Analyzing activity trends..."
        delay(600 + (Math.random() * 400).toLong())
        progressValue = 0.85f; loadingText = "Generating daily goals..."
        delay(500)
        progressValue = 1f; loadingText = "Ready to move."

        delay(500)
        if (isLoggedIn) onNavigateToHome() else onNavigateToLogin()
    }

    val animatedProgress = animateFloatAsState(
        targetValue = progressValue,
        animationSpec = tween(700, easing = EaseOut),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Surface,
                        PrimaryFixed.copy(alpha = 0.3f),
                        SurfaceContainerLow,
                        PrimaryFixedDim.copy(alpha = 0.2f),
                    )
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Main content
        Column(
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .offset(y = brandTranslateY.value.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Icon wrapper with glassmorphic background
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(glowScale.value)
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurfaceContainerHighest.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Logo",
                    modifier = Modifier.size(48.dp),
                    tint = Primary,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Typography
            Text(
                text = "Elite Fitness Pro",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = Primary,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your health, precision-engineered.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                ),
                color = OnSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading text
            Text(
                text = loadingText,
                style = MaterialTheme.typography.labelLarge,
                color = OnSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Primary,
                trackColor = SurfaceContainerHighest.copy(alpha = 0.5f),
            )
        }
    }
}
