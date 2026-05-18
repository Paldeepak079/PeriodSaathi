package com.deepak.periodsaathi.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.MascotEmotion
import com.deepak.periodsaathi.ui.components.SaathiMascot
import com.deepak.periodsaathi.ui.theme.*
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────
//  Splash Screen
//  Stitch reference: ethereal_companion (mascot drop)
//  Background: Warm cream (#FFF8F2) with pulsing blobs
//  Navigation: auto-routes after 2.5s via SplashViewModel
// ─────────────────────────────────────────────
@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToLock: () -> Unit = {},
    viewModel: SplashViewModel = hiltViewModel()
) {
    val splashState by viewModel.splashState.collectAsState()

    LaunchedEffect(splashState) {
        when (val state = splashState) {
            is SplashState.NavigateTo -> {
                when (state.destination) {
                    "Onboarding" -> onNavigateToOnboarding()
                    "LockScreen" -> onNavigateToLock()
                    "Home" -> onNavigateToHome()
                }
            }
            is SplashState.Loading -> {}
        }
    }

    // Mascot drop animation
    var mascotDropped by remember { mutableStateOf(false) }
    val mascotY by animateFloatAsState(
        targetValue = if (mascotDropped) 0f else -200f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "mascotDrop"
    )
    val mascotAlpha by animateFloatAsState(
        targetValue = if (mascotDropped) 1f else 0f,
        animationSpec = tween(400),
        label = "mascotAlpha"
    )

    // Typewriter app name
    var displayedText by remember { mutableStateOf("") }
    val fullText = "Period Saathi"

    LaunchedEffect(Unit) {
        delay(300)
        mascotDropped = true
        delay(600)
        fullText.forEach { char ->
            displayedText += char
            delay(70)
        }
    }

    // Pulsing background blobs
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val blobScale1 by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1"
    )
    val blobScale2 by infiniteTransition.animateFloat(
        initialValue = 1.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        // Animated blobs
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-80).dp, y = (-80).dp)
                .scale(blobScale1)
                .size(350.dp)
                .background(
                    Brush.radialGradient(
                        listOf(PrimaryContainer.copy(0.5f), Color.Transparent)
                    ),
                    CircleShape
                )
                .blur(60.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .scale(blobScale2)
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        listOf(SecondaryContainer.copy(0.4f), Color.Transparent)
                    ),
                    CircleShape
                )
                .blur(60.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 100.dp)
                .size(200.dp)
                .background(
                    Brush.radialGradient(
                        listOf(TertiaryContainer.copy(0.3f), Color.Transparent)
                    ),
                    CircleShape
                )
                .blur(60.dp)
        )

        // Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mascot in glass circle
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer {
                        translationY = mascotY
                        alpha = mascotAlpha
                    }
                    .clip(CircleShape)
                    .background(Color.White.copy(0.5f))
                    .border(1.5.dp, Color.White.copy(0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                SaathiMascot(
                    emotion = MascotEmotion.HAPPY,
                    size = 130.dp,
                    onTap = {}
                )
            }

            // Typewriter app name
            Text(
                text = displayedText,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp
                ),
                color = Primary
            )

            // Tagline
            if (displayedText.length == fullText.length) {
                Text(
                    text = "Your empathetic cycle companion",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    PeriodSaathiTheme {
        SplashScreen()
    }
}

