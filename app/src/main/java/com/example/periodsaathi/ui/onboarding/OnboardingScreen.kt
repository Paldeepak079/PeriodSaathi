package com.example.periodsaathi.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodsaathi.navigation.Screen
import com.example.periodsaathi.ui.components.ConfettiOverlay
import com.example.periodsaathi.ui.components.SaathiMascot
import com.example.periodsaathi.ui.components.MascotEmotion
import com.example.periodsaathi.ui.theme.BabyBlue
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.ButterYellow
import com.example.periodsaathi.ui.theme.CreamWhite
import com.example.periodsaathi.ui.theme.DeepRose
import com.example.periodsaathi.ui.theme.MintGreen
import com.example.periodsaathi.ui.theme.SoftLavender
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun OnboardingScreen(
    onNavigate: (Screen) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    var showConfetti by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> OnboardingPageWelcome()
                1 -> OnboardingPageCalendar()
                2 -> OnboardingPageRewards(
                    onGetStarted = {
                        showConfetti = true
                    }
                )
            }
        }

        ProgressDots(
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
        )

        AnimatedVisibility(
            visible = pagerState.currentPage < 2,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 24.dp)
        ) {
            SkipButton(
                onClick = {
                    onNavigate(Screen.Login)
                }
            )
        }

        if (showConfetti) {
            ConfettiOverlay(
                visible = true,
                onComplete = {
                    showConfetti = false
                    onNavigate(Screen.Login)
                }
            )
        }
    }
}

@Composable
private fun OnboardingPageWelcome() {
    val infiniteTransition = rememberInfiniteTransition(label = "welcomeGradient")
    val colorPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colorPhase"
    )

    val mascotWaveAngle by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascotWave"
    )

    val sparkles = remember {
        List(8) {
            SparkleParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 6f + 4f,
                delay = Random.nextFloat() * 2000,
                duration = Random.nextInt(1500, 3000)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        interpolateColor(BlushPink, SoftLavender, colorPhase),
                        interpolateColor(SoftLavender, BabyBlue, colorPhase),
                        interpolateColor(BabyBlue, BlushPink, colorPhase)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1f, 1f)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            sparkles.forEach { sparkle ->
                val alpha = (sin(
                    (System.currentTimeMillis() + sparkle.delay) / sparkle.duration.toFloat() * Math.PI.toFloat()
                ) * 0.5f + 0.5f) * 0.8f
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = sparkle.size,
                    center = Offset(sparkle.x * size.width, sparkle.y * size.height)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .graphicsLayer {
                            rotationZ = mascotWaveAngle
                        }
                ) {
                    SaathiMascot(
                        emotion = MascotEmotion.HAPPY,
                        size = 140.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Hi! I'm Saathi \uD83C\uDF38",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your period best friend, not a medical chart",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun OnboardingPageCalendar() {
    val infiniteTransition = rememberInfiniteTransition(label = "calendarGradient")
    val colorPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colorPhase"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        interpolateColor(SoftLavender, BabyBlue, colorPhase),
                        interpolateColor(BabyBlue, MintGreen, colorPhase),
                        interpolateColor(MintGreen, SoftLavender, colorPhase)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1f, 1f)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CalendarIllustration(
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Track your cycle with love, not stress \uD83D\uDCC5",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Predict periods, fertile windows, and moods with gentle accuracy",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarIllustration(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "calendarPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Canvas(modifier = modifier) {
        val cellSize = size.width / 7f
        val padding = cellSize * 0.15f

        val periodCells = setOf(
            0 to 0, 0 to 1, 0 to 2, 1 to 0, 1 to 1,
            2 to 3, 2 to 4, 2 to 5, 3 to 3, 3 to 4
        )
        val fertileCells = setOf(
            2 to 6, 3 to 6, 4 to 0, 4 to 1, 4 to 2
        )

        for (row in 0 until 5) {
            for (col in 0 until 7) {
                val x = col * cellSize + padding
                val y = row * cellSize + padding
                val cellColor = when {
                    row to col in periodCells -> BlushPink.copy(alpha = pulseAlpha)
                    row to col in fertileCells -> MintGreen.copy(alpha = pulseAlpha)
                    else -> Color.White.copy(alpha = 0.15f)
                }
                drawRoundRect(
                    color = cellColor,
                    topLeft = Offset(x, y),
                    size = Size(cellSize - padding * 2, cellSize - padding * 2),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cellSize * 0.15f)
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageRewards(
    onGetStarted: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rewardsGradient")
    val colorPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colorPhase"
    )

    val stars = remember {
        List(12) {
            StarParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 8f + 4f,
                rotation = Random.nextFloat() * 360f,
                speed = Random.nextInt(2000, 4000)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        interpolateColor(BabyBlue, MintGreen, colorPhase),
                        interpolateColor(MintGreen, ButterYellow, colorPhase),
                        interpolateColor(ButterYellow, BabyBlue, colorPhase)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1f, 1f)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { star ->
                val alpha = (sin(
                    (System.currentTimeMillis()) / star.speed.toFloat() * Math.PI.toFloat()
                ) * 0.5f + 0.5f) * 0.6f
                drawStar(
                    center = Offset(star.x * size.width, star.y * size.height),
                    size = star.size,
                    color = Color.White.copy(alpha = alpha),
                    rotation = star.rotation
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SaathiMascot(
                emotion = MascotEmotion.EXCITED,
                size = 120.dp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Earn rewards for taking care of yourself \u2728",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Log your cycle, earn points, unlock cute accessories for Saathi",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            GetStartedButton(
                onClick = onGetStarted
            )
        }
    }
}

@Composable
private fun GetStartedButton(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    isPressed = true
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Get Started \uD83D\uDE80",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DeepRose
        )
    }
}

@Composable
private fun ProgressDots(
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 3) {
            val isActive = i == currentPage
            val animatedWidth by animateDpAsState(
                targetValue = if (isActive) 20.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "dotWidth"
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(animatedWidth)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isActive) Color.White
                        else Color.White.copy(alpha = 0.4f)
                    )
            )
        }
    }
}

@Composable
private fun SkipButton(
    onClick: () -> Unit
) {
    Text(
        text = "Skip",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White.copy(alpha = 0.7f),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

private fun interpolateColor(
    color1: Color,
    color2: Color,
    fraction: Float
): Color {
    return Color(
        red = color1.red + (color2.red - color1.red) * fraction,
        green = color1.green + (color2.green - color1.green) * fraction,
        blue = color1.blue + (color2.blue - color1.blue) * fraction,
        alpha = color1.alpha + (color2.alpha - color1.alpha) * fraction
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(
    center: Offset,
    size: Float,
    color: Color,
    rotation: Float
) {
    val path = androidx.compose.ui.graphics.Path().apply {
        val outerRadius = size
        val innerRadius = size * 0.4f
        val points = 5

        for (i in 0 until points * 2) {
            val radius = if (i % 2 == 0) outerRadius else innerRadius
            val angle = Math.toRadians((i * 360.0 / (points * 2)) - 90 + rotation)
            val x = center.x + (radius * cos(angle)).toFloat()
            val y = center.y + (radius * sin(angle)).toFloat()

            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    drawPath(path, color)
}

private data class SparkleParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val delay: Float,
    val duration: Int
)

private data class StarParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val rotation: Float,
    val speed: Int
)
