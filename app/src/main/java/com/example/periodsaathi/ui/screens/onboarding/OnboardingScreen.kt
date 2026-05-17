package com.example.periodsaathi.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.SoftLavender
import com.example.periodsaathi.ui.theme.BabyBlue
import com.example.periodsaathi.ui.theme.DeepRose

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val currentPage by animateIntAsState(
        targetValue = pagerState.currentPage,
        animationSpec = tween(300),
        label = "page"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A0E2E), Color(0xFF2D1B4E), Color(0xFF1A1228))
                )
            )
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPage(page = page)
        }

        // Progress indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) { index ->
                val isActive = currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isActive) 20.dp else 8.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "indicatorWidth"
                )

                Box(
                    modifier = Modifier
                        .width(width)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isActive) BlushPink else SoftLavender.copy(alpha = 0.5f)
                        )
                )
            }
        }

        // Skip button
        AnimatedVisibility(
            visible = currentPage < 2,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            TextButton(onClick = onSkip) {
                Text("Skip", color = SoftLavender)
            }
        }

        // Get Started button on last page
        AnimatedVisibility(
            visible = currentPage == 2,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(containerColor = BlushPink),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun OnboardingPage(page: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val (emoji, title, description) = when (page) {
            0 -> Triple("🌸", "Meet your Saathi", "Your personal period companion, always here to help you track, understand, and embrace your cycle.")
            1 -> Triple("📅", "Smart Tracking", "Log your periods, symptoms, mood, and water intake. Get predictions and insights that actually make sense.")
            2 -> Triple("⭐", "Your Journey", "Earn points, complete challenges, and unlock rewards. Your health journey just got more fun!")
            else -> Triple("", "", "")
        }

        // Mascot/Illustration placeholder
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BlushPink.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(fontSize = 80.sp, text = emoji)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = description,
            fontSize = 16.sp,
            color = SoftLavender,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}