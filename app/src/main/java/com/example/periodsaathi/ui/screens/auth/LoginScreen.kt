package com.example.periodsaathi.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import android.view.HapticFeedbackConstants
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.periodsaathi.ui.components.*
import com.example.periodsaathi.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    var showEmailForm by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedGradientMesh()
        FloatingParticles(particleCount = 30)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Mascot with spring animation
            var mascotVisible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                mascotVisible = true
            }

            AnimatedVisibility(
                visible = mascotVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -it }
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(GlassWhite.copy(alpha = 0.5f))
                        .border(1.dp, GlassBorder, RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🌸", fontSize = 80.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to",
                fontSize = 16.sp,
                color = OnSurfaceVariant
            )

            Text(
                text = "Period Saathi",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login container
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (loginState) {
                        is LoginState.Loading -> {
                            CircularProgressIndicator(color = Primary)
                        }
                        is LoginState.Error -> {
                            Text(
                                text = (loginState as LoginState.Error).message,
                                color = Error,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        else -> {}
                    }

                    // Google Sign In Button
                    HapticButton(
                        onClick = { viewModel.signInWithGoogle() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(OnSurface.copy(alpha = 0.05f))
                                .border(1.dp, OnSurface.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = "🔵", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Continue with Google",
                                    fontWeight = FontWeight.Medium,
                                    color = OnSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(OnSurfaceVariant.copy(alpha = 0.2f))
                        )
                        Text(
                            text = "or",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = OnSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(OnSurfaceVariant.copy(alpha = 0.2f))
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email/Password form (expandable)
                    AnimatedVisibility(
                        visible = showEmailForm,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            var passwordVisible by remember { mutableStateOf(false) }
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            SpringBounceButton(
                                text = "Sign In",
                                onClick = { viewModel.signInWithEmail(email, password) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // Toggle email form
                    Text(
                        text = if (showEmailForm) "Use Google Sign-In" else "Use Email & Password",
                        color = Primary,
                        modifier = Modifier
                            .clickable { showEmailForm = !showEmailForm }
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Guest button
                    Text(
                        text = "Continue without account",
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { viewModel.continueAsGuest() }
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Privacy text
            Text(
                text = "By continuing, you agree to our Terms & Privacy Policy",
                fontSize = 12.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(GlassWhite.copy(alpha = 0.45f))
            .border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
            .padding(1.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f))
        ) {
            content()
        }
    }
}