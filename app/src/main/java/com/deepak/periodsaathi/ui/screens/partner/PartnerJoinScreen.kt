package com.deepak.periodsaathi.ui.screens.partner

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PartnerJoinScreen(
    viewModel: PartnerViewModel,
    onBack: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    val joinState by viewModel.joinState.collectAsState()
    var inviteCode by remember { mutableStateOf("") }
    var partnerNameInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Shake offset for error
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(joinState) {
        if (joinState is JoinUIState.Error) {
            // Trigger bouncy shake animation
            scope.launch {
                repeat(4) {
                    shakeOffset.animateTo(15f, animationSpec = tween(50, easing = LinearEasing))
                    shakeOffset.animateTo(-15f, animationSpec = tween(50, easing = LinearEasing))
                }
                shakeOffset.animateTo(0f, animationSpec = spring())
            }
        } else if (joinState is JoinUIState.Success) {
            onNavigateToDashboard()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Surface,
                        SoftLavender.copy(alpha = 0.4f),
                        WarmCream
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = shakeOffset.value.dp)
        ) {
            Text(
                text = "Connect with Saathi 🤝",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DeepRose,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter the 6-digit sync code shared by your partner to start viewing cycle insights.",
                fontSize = 14.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Name field
                    OutlinedTextField(
                        value = partnerNameInput,
                        onValueChange = { partnerNameInput = it },
                        label = { Text("Your Name (visible to her)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepRose,
                            unfocusedBorderColor = SoftLavender,
                            focusedLabelColor = DeepRose
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = joinState !is JoinUIState.Submitting
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Enter 6-Digit Code",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated block-based OTP input using a hidden transparent TextField
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedTextField(
                            value = inviteCode,
                            onValueChange = {
                                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                    inviteCode = it
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent,
                                errorBorderColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = joinState !is JoinUIState.Submitting
                        )

                        // Visual OTP Blocks overlay
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.clickable { /* focus input implicitly */ }
                        ) {
                            for (i in 0 until 6) {
                                val char = inviteCode.getOrNull(i)?.toString() ?: ""
                                val isFocused = inviteCode.length == i

                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            if (isFocused) BlushPink.copy(alpha = 0.4f) else GlassWhite,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .border(
                                            width = if (isFocused) 2.dp else 1.dp,
                                            color = if (isFocused) DeepRose else GlassBorder,
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepRose
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (joinState is JoinUIState.Error) {
                        Text(
                            text = (joinState as JoinUIState.Error).message,
                            color = Error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (inviteCode.length == 6 && partnerNameInput.isNotBlank()) {
                                viewModel.joinWithCode(inviteCode, partnerNameInput)
                            }
                        },
                        enabled = inviteCode.length == 6 && partnerNameInput.isNotBlank() && joinState !is JoinUIState.Submitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepRose,
                            disabledContainerColor = SoftLavender
                        ),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (joinState is JoinUIState.Submitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Connect Now 🤝", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = {
                            viewModel.resetJoinState()
                            onBack()
                        },
                        enabled = joinState !is JoinUIState.Submitting
                    ) {
                        Text("Go Back", color = Outline, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
