package com.deepak.periodsaathi.ui.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.MascotEmotion
import com.deepak.periodsaathi.ui.components.SaathiMascot
import com.deepak.periodsaathi.ui.theme.*
import com.google.android.gms.auth.api.signin.GoogleSignIn

// ─────────────────────────────────────────────
//  Login Screen  (Stitch reference: login_screen)
//  Background: Warm cream mesh gradient (#FFF8F2)
//  Style: Glassmorphism with spring animations
// ─────────────────────────────────────────────
@Composable
fun LoginScreen(
    navController: NavHostController? = null,
    onLoginSuccess: () -> Unit = {},
    onContinueAsGuest: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    // Legacy fallback launcher (used if Credential Manager unavailable)
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                viewModel.handleGoogleSignInResult(task, onSuccess = { onLoginSuccess() })
            } else if (result.data != null) {
                // Non-OK code but we have data (could be DEVELOPER_ERROR passed back)
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                viewModel.handleGoogleSignInResult(task, onSuccess = { onLoginSuccess() })
            } else {
                viewModel.resetState()
            }
        } catch (e: Exception) {
            viewModel.handleRawException(e)
        }
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
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "googleBtnScale"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = !isLoading
                        ) {
                            // Primary path: Credential Manager (no main thread freeze) with legacy fallback
                            viewModel.signInWithCredentialManager(
                                activityContext = context,
                                onFallback = {
                                    try {
                                        googleSignInLauncher.launch(viewModel.signInWithGoogle())
                                    } catch (e: Exception) {
                                        android.util.Log.e("GoogleSignIn", "Legacy sign-in fallback failed", e)
                                        viewModel.handleRawException(e)
                                    }
                                },
                                onSuccess = { onLoginSuccess() }
                            )
                        }
                ) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
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
                                GoogleLogoIcon()
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Continue with Google",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = OnSurface
                                )
                            }
                        }
                    }
                }

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.width(48.dp), color = OutlineVariant)
                    Text(
                        text = "or explore as guest",
                        style = MaterialTheme.typography.labelSmall,
                        color = Outline
                    )
                    HorizontalDivider(modifier = Modifier.width(48.dp), color = OutlineVariant)
                }

                // Guest button
                TextButton(
                    onClick = {
                        viewModel.continueAsGuest()
                        onContinueAsGuest()
                    },
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

@Composable
private fun GoogleLogoIcon() {
    Canvas(modifier = Modifier.size(20.dp)) {
        val s = size.width
        drawPath(
            path = Path().apply {
                moveTo(s * 0.2033f, s * 0.5201f)
                cubicTo(s * 0.2033f, s * 0.4653f, s * 0.2090f, s * 0.4250f, s * 0.2204f, s * 0.3854f)
                lineTo(s * 0.1354f, s * 0.3854f)
                cubicTo(s * 0.1146f, s * 0.4250f, s * 0.1042f, s * 0.4688f, s * 0.1042f, s * 0.5208f)
                cubicTo(s * 0.1042f, s * 0.6125f, s * 0.1319f, s * 0.6972f, s * 0.1792f, s * 0.7694f)
                lineTo(s * 0.2458f, s * 0.7160f)
                cubicTo(s * 0.2181f, s * 0.6701f, s * 0.2033f, s * 0.6090f, s * 0.2033f, s * 0.5201f)
                close()
            },
            color = Color(0xFFFBBC05)
        )
        drawPath(
            path = Path().apply {
                moveTo(s * 0.5000f, s * 0.3076f)
                cubicTo(s * 0.5590f, s * 0.3076f, s * 0.6069f, s * 0.3299f, s * 0.6444f, s * 0.3639f)
                lineTo(s * 0.7104f, s * 0.2979f)
                cubicTo(s * 0.6556f, s * 0.2479f, s * 0.5840f, s * 0.2153f, s * 0.5000f, s * 0.2153f)
                cubicTo(s * 0.3958f, s * 0.2153f, s * 0.3028f, s * 0.2563f, s * 0.2312f, s * 0.3208f)
                lineTo(s * 0.2972f, s * 0.3750f)
                cubicTo(s * 0.3299f, s * 0.3354f, s * 0.3799f, s * 0.3076f, s * 0.5000f, s * 0.3076f)
                close()
            },
            color = Color(0xFFEA4335)
        )
        drawPath(
            path = Path().apply {
                moveTo(s * 0.2033f, s * 0.5201f)
                cubicTo(s * 0.2033f, s * 0.6090f, s * 0.2181f, s * 0.6701f, s * 0.2458f, s * 0.7160f)
                lineTo(s * 0.1792f, s * 0.7694f)
                cubicTo(s * 0.1319f, s * 0.6972f, s * 0.1042f, s * 0.6125f, s * 0.1042f, s * 0.5208f)
                close()
            },
            color = Color(0xFF34A853)
        )
        drawPath(
            path = Path().apply {
                moveTo(s * 0.5000f, s * 0.7326f)
                cubicTo(s * 0.4403f, s * 0.7326f, s * 0.4021f, s * 0.7167f, s * 0.3701f, s * 0.6910f)
                lineTo(s * 0.3042f, s * 0.7465f)
                cubicTo(s * 0.3493f, s * 0.7861f, s * 0.4132f, s * 0.8153f, s * 0.5000f, s * 0.8153f)
                cubicTo(s * 0.5819f, s * 0.8153f, s * 0.6521f, s * 0.7840f, s * 0.7069f, s * 0.7361f)
                lineTo(s * 0.6403f, s * 0.6819f)
                cubicTo(s * 0.6049f, s * 0.7097f, s * 0.5590f, s * 0.7326f, s * 0.5000f, s * 0.7326f)
                close()
            },
            color = Color(0xFF4285F4)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PeriodSaathiTheme {
        LoginScreen()
    }
}
