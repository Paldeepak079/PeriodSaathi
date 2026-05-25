package com.deepak.periodsaathi.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.ConfettiOverlay
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.MascotEmotion
import com.deepak.periodsaathi.ui.components.PrimaryButton
import com.deepak.periodsaathi.ui.components.SaathiMascot
import com.deepak.periodsaathi.ui.theme.*
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────
//  Onboarding Screen
//  Stitch references:
//    onboarding_meet_saathi, onboarding_privacy, onboarding_rewards
//  Background: Warm cream (#FFF8F2) with blobs
//  3-page horizontal pager
// ─────────────────────────────────────────────

private data class OnboardingPage(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val mascotEmotion: MascotEmotion,
    val blobColor: Color
)

private val pages = listOf(
    OnboardingPage(
        emoji = "🌸",
        title = "Your new best friend 💕",
        subtitle = "She'll cheer you on, remind you to rest, and never judge you.",
        mascotEmotion = MascotEmotion.HAPPY,
        blobColor = Color(0xFFFFB6C1)
    ),
    OnboardingPage(
        emoji = "🔒",
        title = "Your Privacy, Protected",
        subtitle = "Your data stays on your device. We use end-to-end encryption and never sell your health information. Ever.",
        mascotEmotion = MascotEmotion.LISTENING,
        blobColor = Color(0xFFD0BFEF)
    ),
    OnboardingPage(
        emoji = "⭐",
        title = "Earn Rewards",
        subtitle = "Track consistently, complete challenges, and unlock wardrobe items for Saathi. Your health journey, gamified!",
        mascotEmotion = MascotEmotion.EXCITED,
        blobColor = Color(0xFFADCDED)
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Animated background blob per page
        val blobColor by animateColorAsState(
            targetValue = pages[currentPage].blobColor.copy(alpha = 0.35f),
            animationSpec = tween(600),
            label = "blobColor"
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-60).dp, y = (-60).dp)
                .size(320.dp)
                .background(
                    Brush.radialGradient(listOf(blobColor, Color.Transparent)),
                    CircleShape
                )
                .blur(50.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .size(280.dp)
                .background(
                    Brush.radialGradient(listOf(blobColor, Color.Transparent)),
                    CircleShape
                )
                .blur(50.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // ── Skip button ─────────────────────────
            AnimatedVisibility(
                visible = currentPage < pages.size - 1,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(end = 16.dp, top = 8.dp)) {
                    TextButton(
                        onClick = onSkip,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "Skip",
                            color = Primary.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            // ── Pages ────────────────────────────────
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                OnboardingPageContent(page = pages[page], pageIndex = page)
            }

            // ── Dot Indicators ───────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    val isActive = currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isActive) 20.dp else 8.dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "dotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .width(width)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isActive) Primary else OutlineVariant
                            )
                    )
                }
            }

            // ── CTA Button ───────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                if (currentPage == pages.size - 1) {
                    PrimaryButton(
                        text = "Let's Start 🌸",
                        onClick = onComplete,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Button(
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(currentPage + 1) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            text = "Next →",
                            fontWeight = FontWeight.SemiBold,
                            color = OnPrimary,
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage, pageIndex: Int) {
    when (pageIndex) {
        2 -> RewardsPage(page)
        else -> StandardPageContent(page, pageIndex)
    }
}

@Composable
private fun StandardPageContent(page: OnboardingPage, pageIndex: Int) {
    val density = LocalDensity.current
    val bouncePx = with(density) { 8.dp.toPx() }

    val mascotModifier = if (pageIndex == 0) {
        val infiniteTransition = rememberInfiniteTransition(label = "bounce")
        val bounceOffset by infiniteTransition.animateFloat(
            initialValue = -bouncePx,
            targetValue = bouncePx,
            animationSpec = infiniteRepeatable(
                animation = tween(600),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bounceOffset"
        )
        Modifier.graphicsLayer { translationY = bounceOffset }
    } else Modifier

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.45f))
                .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                .then(mascotModifier),
            contentAlignment = Alignment.Center
        ) {
            SaathiMascot(
                emotion = page.mascotEmotion,
                size = 150.dp,
                onTap = {}
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            color = PrimaryContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = page.emoji,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp
            ),
            color = Primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        when (pageIndex) {
            0 -> {
                Text(
                    text = page.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )
            }
            1 -> {
                PrivacyBulletCards()
            }
        }
    }
}

@Composable
private fun PrivacyBulletCards() {
    val items = listOf(
        Triple(Icons.Rounded.Check, "No data sold. Ever.", 100),
        Triple(Icons.Rounded.WifiOff, "Works completely offline", 200),
        Triple(Icons.Rounded.Accessibility, "You control everything", 300)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { (icon, text, delay) ->
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    animationSpec = tween(delayMillis = delay)
                ) { it / 4 }
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardsPage(page: OnboardingPage) {
    Box(modifier = Modifier.fillMaxSize()) {
        ConfettiOverlay(visible = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Final Step",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary
                    )
                    Text(
                        text = "100% Complete",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = Primary,
                    trackColor = Color.White.copy(alpha = 0.3f),
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            GlassCard(
                modifier = Modifier
                    .width(260.dp)
                    .heightIn(min = 220.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = PrimaryContainer.copy(alpha = 0.3f),
                        shape = CircleShape,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "🌸",
                                fontSize = 36.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "500 Points",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Onboarding Bonus Unlocked",
                        style = MaterialTheme.typography.labelMedium,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                ),
                color = Primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    PeriodSaathiTheme {
        OnboardingScreen()
    }
}
