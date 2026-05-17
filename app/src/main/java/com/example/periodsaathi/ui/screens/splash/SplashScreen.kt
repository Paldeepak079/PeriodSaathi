package com.example.periodsaathi.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodsaathi.ui.theme.BabyBlue
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.DeepRose
import com.example.periodsaathi.ui.theme.SoftLavender
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

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

    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    val mascotOffset by animateFloatAsState(
        targetValue = 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "mascotDrop"
    )

    var displayedText by remember { mutableStateOf("") }
    val fullText = "Period Saathi"

    LaunchedEffect(Unit) {
        delay(500)
        fullText.forEach { char ->
            displayedText += char
            delay(80)
        }
    }

    val orbPositions = listOf(
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "orb1"
        ),
        infiniteTransition.animateFloat(
            initialValue = 120f,
            targetValue = 480f,
            animationSpec = infiniteRepeatable(
                animation = tween(10000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "orb2"
        ),
        infiniteTransition.animateFloat(
            initialValue = 240f,
            targetValue = 600f,
            animationSpec = infiniteRepeatable(
                animation = tween(12000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "orb3"
        ),
        infiniteTransition.animateFloat(
            initialValue = 300f,
            targetValue = 660f,
            animationSpec = infiniteRepeatable(
                animation = tween(9000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "orb4"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A0E2E),
                        Color(0xFF2D1B4E),
                        Color(0xFF1A1228)
                    )
                )
            )
    ) {
        // Animated orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            listOf(
                BlushPink to 150.dp.toPx(),
                SoftLavender to 120.dp.toPx(),
                BabyBlue to 100.dp.toPx(),
                DeepRose to 80.dp.toPx()
            ).forEachIndexed { index, (color, radius) ->
                val angle = orbPositions[index].value * (Math.PI / 180f)
                val x = centerX + (200.dp.toPx() * kotlin.math.cos(angle))
                val y = centerY + (150.dp.toPx() * kotlin.math.sin(angle))

                drawCircle(
                    color = color.copy(alpha = 0.3f),
                    radius = radius,
                    center = Offset(x.toFloat(), y.toFloat())
                )
            }
        }

        // App name
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = mascotOffset.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = displayedText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}