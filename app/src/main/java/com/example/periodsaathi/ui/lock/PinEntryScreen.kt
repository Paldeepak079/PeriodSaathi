package com.example.periodsaathi.ui.lock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodsaathi.security.StealthModeManager
import kotlinx.coroutines.delay

private const val PIN_LENGTH = 4

@Composable
fun PinEntryScreen(
    stealthModeManager: StealthModeManager,
    pinVerified: () -> Unit,
    onBack: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var wrongAttempt by remember { mutableStateOf(false) }

    val shakeOffset = remember { Animatable(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A0E2E))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Enter PIN",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter your security PIN",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(PIN_LENGTH) { index ->
                val isFilled = index < pin.length
                val dotAlpha by animateFloatAsState(
                    targetValue = when {
                        isFilled -> 1f
                        else -> 0.3f
                    },
                    animationSpec = spring(),
                    label = "dotAlpha_$index"
                )
                val dotColor = if (wrongAttempt && index < pin.length) Color(0xFFE53935)
                    else Color.White

                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(dotColor.copy(alpha = dotAlpha))
                )
            }
        }

        if (wrongAttempt) {
            Text(
                text = "Wrong PIN",
                color = Color(0xFFE53935),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        NumPad(
            onDigit = { digit ->
                if (pin.length < PIN_LENGTH) {
                    val newPin = pin + digit
                    if (newPin.length == PIN_LENGTH) {
                        if (stealthModeManager.verifyPin(newPin)) {
                            pinVerified()
                        } else {
                            wrongAttempt = true
                            LaunchedEffect(Unit) {
                                shakeOffset.animateTo(10f, tween(50))
                                shakeOffset.animateTo(-10f, tween(50))
                                shakeOffset.animateTo(8f, tween(50))
                                shakeOffset.animateTo(-8f, tween(50))
                                shakeOffset.animateTo(0f, tween(50))
                                delay(300)
                                pin = ""
                                wrongAttempt = false
                            }
                        }
                    } else {
                        pin = newPin
                    }
                }
            },
            onDelete = {
                if (pin.isNotEmpty()) {
                    pin = pin.dropLast(1)
                }
            }
        )
    }
}

@Composable
private fun NumPad(
    onDigit: (String) -> Unit,
    onDelete: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "⌫")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { label ->
                    when (label) {
                        "" -> Box(modifier = Modifier.size(64.dp))
                        "⌫" -> NumPadButton(
                            label = label,
                            onClick = onDelete,
                            isDelete = true
                        )
                        else -> NumPadButton(
                            label = label,
                            onClick = { onDigit(label) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NumPadButton(
    label: String,
    onClick: () -> Unit,
    isDelete: Boolean = false
) {
    val scale = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .scale(scale.value),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    LaunchedEffect(Unit) {
                        scale.animateTo(0.9f, tween(80))
                        scale.animateTo(1f, spring())
                    }
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            if (isDelete) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Delete",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Text(
                    text = label,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}