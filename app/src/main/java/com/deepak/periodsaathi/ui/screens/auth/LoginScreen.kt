package com.deepak.periodsaathi.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.MascotEmotion
import com.deepak.periodsaathi.ui.components.SaathiMascot
import com.deepak.periodsaathi.ui.theme.*

// ─────────────────────────────────────────────
//  Login Screen  (Stitch reference: login_screen)
//  Background: Warm cream mesh gradient (#FFF8F2)
//  Style: Glassmorphism with spring animations
// ─────────────────────────────────────────────
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onContinueAsGuest: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) onLoginSuccess()
    }

    // Mesh gradient background — warm cream with soft pink/lavender blobs
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Decorative blobs behind everything
        MeshGradientBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Header ──────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Period Saathi",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Primary
                )
            }

            // ── Mascot with pulsing glow ──────────────
            Box(
                modifier = Modifier.size(192.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow pulse
                val pulseAnim = rememberInfiniteTransition(label = "glow")
                val glowAlpha by pulseAnim.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 0.6f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "glowAlpha"
                )
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    PrimaryContainer.copy(alpha = glowAlpha),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .blur(30.dp)
                )
                // Glass circle frame
                Box(
                    modifier = Modifier
                        .size(192.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                        .border(1.dp, GlassBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    SaathiMascot(
                        size = 140.dp,
                        emotion = MascotEmotion.HAPPY,
                        onTap = {}
                    )
                }
            }

            // ── Welcome text ──────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Welcome to Period Saathi",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp
                    ),
                    color = Primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Your empathetic cycle companion.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // ── Google Sign In button ─────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Google button (glass style)
                val isLoading = loginState is LoginState.Loading
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { if (!isLoading) viewModel.signInWithGoogle() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            // Google coloured G icon using text
                            Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Google",
                                style = MaterialTheme.typography.labelLarge,
                                color = OnSurface
                            )
                        }
                    }
                }

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariant)
                    Text(
                        text = "or explore as guest",
                        style = MaterialTheme.typography.labelSmall,
                        color = Outline
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariant)
                }

                // Guest button
                TextButton(
                    onClick = onContinueAsGuest,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Continue without account",
                        color = Primary.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Error message
                if (loginState is LoginState.Error) {
                    Text(
                        text = (loginState as LoginState.Error).message,
                        color = Error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── Footer ────────────────────────────────
            Text(
                text = "By continuing, you agree to our Terms & Privacy Policy",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth()
            )
        }
    }
}

// Warm-cream mesh gradient background blobs
@Composable
private fun MeshGradientBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top-left pink blob
        GradientBlob(
            color = PrimaryContainer.copy(alpha = 0.4f),
            offsetX = (-40).dp,
            offsetY = (-40).dp,
            size = 280.dp
        )
        // Top-right lavender blob
        GradientBlob(
            color = SecondaryContainer.copy(alpha = 0.3f),
            offsetX = 200.dp,
            offsetY = (-60).dp,
            size = 240.dp
        )
        // Bottom center pink blob
        GradientBlob(
            color = PrimaryFixed.copy(alpha = 0.4f),
            offsetX = 40.dp,
            offsetY = 600.dp,
            size = 300.dp
        )
    }
}

@Composable
private fun GradientBlob(
    color: Color,
    offsetX: Dp,
    offsetY: Dp,
    size: Dp
) {
    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(size)
            .background(
                brush = Brush.radialGradient(listOf(color, Color.Transparent)),
                shape = CircleShape
            )
            .blur(40.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PeriodSaathiTheme {
        LoginScreen()
    }
}
