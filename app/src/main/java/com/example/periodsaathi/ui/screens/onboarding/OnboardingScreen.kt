package com.example.periodsaathi.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.periodsaathi.ui.components.AnimatedDotsIndicator
import com.example.periodsaathi.ui.components.AnimatedGradientMesh
import com.example.periodsaathi.ui.components.ConfettiEffect
import com.example.periodsaathi.ui.components.HapticButton
import com.example.periodsaathi.ui.components.SpringBounceButton
import com.example.periodsaathi.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onComplete: () -> Unit = {}
) {
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
    val showConfetti by viewModel.showConfetti.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentPage) {
        pagerState.animateScrollToPage(currentPage)
    }

    LaunchedEffect(pagerState.currentPage) {
        _currentPage.value = pagerState.currentPage
    }

    LaunchedEffect(showConfetti) {
        if (!showConfetti) {
            onComplete()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedGradientMesh()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Skip button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                AnimatedVisibility(
                    visible = currentPage < 2,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Skip",
                        color = OnSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable { viewModel.skip() }
                            .padding(8.dp)
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingPage(
                    page = page,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Progress dots and buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedDotsIndicator(
                    totalDots = 3,
                    currentDot = currentPage,
                    activeColor = Primary,
                    inactiveColor = OnSurfaceVariant.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (currentPage == 2) {
                    SpringBounceButton(
                        text = "Get Started 🌟",
                        onClick = { viewModel.completeOnboarding() },
                        backgroundColor = Primary
                    )
                } else {
                    HapticButton(
                        onClick = { viewModel.nextPage() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(50.dp))
                                .background(Primary)
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Next",
                                color = OnPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Confetti overlay
        ConfettiEffect(
            visible = showConfetti,
            onComplete = { viewModel.onConfettiComplete() }
        )
    }
}

@Composable
private fun OnboardingPage(
    page: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (page) {
            0 -> OnboardingPage1()
            1 -> OnboardingPage2()
            2 -> OnboardingPage3()
        }
    }
}

@Composable
private fun OnboardingPage1() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Bouncing mascot
            val infiniteTransition = rememberInfiniteTransition(label = "bounce")
            val offset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -20f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "mascotBounce"
            )

            Text(
                text = "🌸",
                fontSize = 120.sp,
                modifier = Modifier.offset(y = offset.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Meet your Saathi",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your personal companion for every cycle day. She'll cheer you on, remind you to rest, and never judge you.",
                fontSize = 16.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun OnboardingPage2() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Calendar illustration with Canvas
            CalendarIllustration()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Track with Confidence",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Log symptoms, track patterns, and get personalized insights. Your data stays private - always.",
                fontSize = 16.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun CalendarIllustration() {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val dates = listOf("12", "13", "14", "15", "16", "17", "18")
    val todayIndex = 2

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEachIndexed { index, day ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = day,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when (index) {
                                todayIndex -> Primary
                                in 1..3 -> PrimaryContainer
                                else -> Color.Transparent
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dates[index],
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (index) {
                            todayIndex -> OnPrimary
                            else -> OnSurface
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPage3() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Stars and rewards illustration
            StarsIllustration()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Get Rewarded 🌟",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Earn points for logging, stay consistent, and unlock beautiful themes and accessories for your Saathi!",
                fontSize = 16.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun StarsIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "star$index"
            )

            Text(
                text = "⭐",
                fontSize = 32.sp,
                modifier = Modifier.graphicsLayer { this.alpha = alpha }
            )
        }
    }
}