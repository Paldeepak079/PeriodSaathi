package com.example.periodsaathi.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.periodsaathi.ui.components.GlassCard
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.SoftLavender
import com.example.periodsaathi.ui.theme.BabyBlue
import com.example.periodsaathi.ui.theme.DeepRose
import kotlin.random.Random

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onContinueAsGuest: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()
    val showEmailForm by viewModel.showEmailForm.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "login")
    val particles = remember {
        List(25) {
            ParticleData(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() * 0.5f + 0.5f,
                size = Random.nextFloat() * 4f + 2f
            )
        }
    }
    val particleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleProgress"
    )

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A0E2E), Color(0xFF2D1B4E), Color(0xFF1A1228))
                )
            )
    ) {
        // Animated particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEachIndexed { index, particle ->
                val y = (particle.y + (particleProgress + index * 0.04f) % 1f) % 1f
                drawCircle(
                    color = SoftLavender.copy(alpha = 0.3f),
                    radius = particle.size.dp.toPx(),
                    center = Offset(
                        particle.x * size.width,
                        y * size.height
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App name
            Text(
                text = "Period Saathi",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome Back",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Google Sign In button
                    Button(
                        onClick = { viewModel.signInWithGoogle() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = loginState !is LoginState.Loading
                    ) {
                        if (loginState is LoginState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black
                            )
                        } else {
                            Text("Sign in with Google", fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Toggle email form
                    TextButton(onClick = { viewModel.toggleEmailForm() }) {
                        Text(
                            text = if (showEmailForm) "Use Google instead" else "Use email instead",
                            color = SoftLavender
                        )
                    }

                    // Email form
                    AnimatedVisibility(visible = showEmailForm) {
                        Column {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlushPink,
                                    unfocusedBorderColor = SoftLavender
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BlushPink,
                                    unfocusedBorderColor = SoftLavender
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.signInWithEmail(email, password) },
                                colors = ButtonDefaults.buttonColors(containerColor = BlushPink),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Sign In")
                            }
                        }
                    }

                    // Error message
                    if (loginState is LoginState.Error) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = (loginState as LoginState.Error).message,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Continue as guest
                    TextButton(onClick = onContinueAsGuest) {
                        Text(
                            text = "Continue without account",
                            color = SoftLavender.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your data stays on your device",
                        fontSize = 12.sp,
                        color = SoftLavender.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

private data class ParticleData(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float
)

