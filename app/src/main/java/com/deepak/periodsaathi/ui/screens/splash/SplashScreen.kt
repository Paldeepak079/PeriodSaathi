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

// Splash plays a 3-phase animation sequence and navigates ONLY after completion:
// Phase 1 (0–800ms)   : mascot springs down with bounce
// Phase 2 (800–2000ms): typewriter reveals "Period Saathi"
// Phase 3 (2000–3000ms): tagline fades in, glow ring expands, then nav fires
@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToLock: () -> Unit = {},
    viewModel: SplashViewModel = hiltViewModel()
) {
    val splashState by viewModel.splashState.collectAsState()
    var animationComplete by remember { mutableStateOf(false) }

    // Navigate only AFTER the animation is done AND the DB check has resolved
    LaunchedEffect(splashState, animationComplete) {
        if (animationComplete) {
            when (val s = splashState) {
                is SplashState.NavigateTo -> when (s.destination) {
                    "Onboarding" -> onNavigateToOnboarding()
                    "LockScreen" -> onNavigateToLock()
                    "Home"       -> onNavigateToHome()
                }
                is SplashState.Loading -> { /* Wait for DB check */ }
            }
        }
    }

    // ── Phase 1: mascot drop (spring) ──────────────────────────────────────
    var mascotDropped by remember { mutableStateOf(false) }
    val mascotY by animateFloatAsState(
        targetValue = if (mascotDropped) 0f else -260f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "mascotDrop"
    )
    val mascotAlpha by animateFloatAsState(
        targetValue = if (mascotDropped) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "mascotAlpha"
    )

    // ── Phase 2: typewriter ───────────────────────────────────────────────
    var displayedText by remember { mutableStateOf("") }
    val fullText = "Period Saathi"

    // ── Phase 3: tagline + glow ring ──────────────────────────────────────
    var taglineVisible by remember { mutableStateOf(false) }
    val taglineAlpha by animateFloatAsState(
        targetValue = if (taglineVisible) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "taglineAlpha"
    )
    var glowExpanded by remember { mutableStateOf(false) }
    val glowScale by animateFloatAsState(
        targetValue = if (glowExpanded) 1.45f else 0.8f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "glowScale"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (glowExpanded) 0f else 0.55f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "glowAlpha"
    )

    // ── Sequence controller ────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        delay(200)
        mascotDropped = true                      // Phase 1 start
        delay(800)                                // Wait for spring to settle
        // Phase 2: typewriter (80ms / char)
        fullText.forEach { char ->
            displayedText += char
            delay(80)
        }
        delay(350)                                // Pause on full title
        // Phase 3: tagline + glow ring
        taglineVisible = true
        glowExpanded = true
        delay(1000)                               // Let everything breathe
        animationComplete = true                  // ← triggers navigation
    }

    // Pulsing background blobs (run in parallel, infinite)
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val blobScale1 by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1"
    )
    val blobScale2 by infiniteTransition.animateFloat(
        initialValue = 1.1f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2"
    )
    val blobScale3 by infiniteTransition.animateFloat(
        initialValue = 1.0f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob3"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        // Background gradient blobs
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-80).dp, y = (-80).dp)
                .graphicsLayer { scaleX = blobScale1; scaleY = blobScale1 }
                .size(360.dp)
                .background(
                    Brush.radialGradient(listOf(PrimaryContainer.copy(0.5f), Color.Transparent)),
                    CircleShape
                )
                .blur(60.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .graphicsLayer { scaleX = blobScale2; scaleY = blobScale2 }
                .size(300.dp)
                .background(
                    Brush.radialGradient(listOf(SecondaryContainer.copy(0.4f), Color.Transparent)),
                    CircleShape
                )
                .blur(60.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 140.dp)
                .graphicsLayer { scaleX = blobScale3; scaleY = blobScale3 }
                .size(220.dp)
                .background(
                    Brush.radialGradient(listOf(TertiaryContainer.copy(0.3f), Color.Transparent)),
                    CircleShape
                )
                .blur(60.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Mascot in glass circle with expanding glow ring
            Box(contentAlignment = Alignment.Center) {
                // Expanding glow ring (Phase 3)
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .graphicsLayer { scaleX = glowScale; scaleY = glowScale }
                        .background(
                            Brush.radialGradient(
                                listOf(PrimaryContainer.copy(alpha = glowAlpha), Color.Transparent)
                            ),
                            CircleShape
                        )
                        .blur(24.dp)
                )
                // Glass circle with mascot
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .graphicsLayer { translationY = mascotY; alpha = mascotAlpha }
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    SaathiMascot(
                        emotion = MascotEmotion.HAPPY,
                        size = 130.dp,
                        onTap = {}
                    )
                }
            }

            // Typewriter title
            Text(
                text = displayedText,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp
                ),
                color = Primary
            )

            // Tagline — Phase 3
            Text(
                text = "Your empathetic cycle companion 🌸",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant,
                modifier = Modifier.graphicsLayer { alpha = taglineAlpha }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    PeriodSaathiTheme { SplashScreen() }
}
