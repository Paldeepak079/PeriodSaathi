package com.deepak.periodsaathi.ui.lock

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.deepak.periodsaathi.security.AppLockManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PIN_LENGTH = 4

/**
 * Smart PIN screen that handles two modes automatically:
 *
 * • **SETUP mode** (no PIN saved yet): Asks user to create and confirm a new 4-digit PIN.
 *   Once set, calls [onPinVerified].
 *
 * • **VERIFY mode** (PIN already exists): Asks user to enter their PIN.
 *   On match, calls [onPinVerified].
 *
 * [onBack] is called when the user taps the back arrow (only shown in VERIFY mode
 * so the user can try biometrics again).
 */
@Composable
fun PinEntryScreen(
    onPinVerified: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val appLockManager = remember { AppLockManager(context) }
    val scope = rememberCoroutineScope()

    // Determine mode on first composition
    val hasPin = remember { appLockManager.hasPin() }

    // Setup mode state
    var setupStep by remember { mutableStateOf(SetupStep.Create) }   // Create → Confirm
    var firstPin by remember { mutableStateOf("") }

    // Shared state
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var wrongAttempt by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }

    fun shakeAndReset(message: String) {
        scope.launch {
            wrongAttempt = true
            errorMessage = message
            shakeOffset.animateTo(10f, tween(50))
            shakeOffset.animateTo(-10f, tween(50))
            shakeOffset.animateTo(8f, tween(50))
            shakeOffset.animateTo(-8f, tween(50))
            shakeOffset.animateTo(0f, tween(50))
            delay(400)
            pin = ""
            wrongAttempt = false
        }
    }

    fun onDigitEntered(digit: String) {
        if (pin.length >= PIN_LENGTH) return
        val newPin = pin + digit

        if (newPin.length == PIN_LENGTH) {
            pin = newPin

            if (!hasPin) {
                // SETUP MODE
                when (setupStep) {
                    SetupStep.Create -> {
                        // Store first entry and move to confirm step
                        firstPin = newPin
                        setupStep = SetupStep.Confirm
                        pin = ""
                        errorMessage = null
                    }
                    SetupStep.Confirm -> {
                        if (newPin == firstPin) {
                            appLockManager.setPin(newPin)
                            scope.launch {
                                delay(200)
                                onPinVerified()
                            }
                        } else {
                            setupStep = SetupStep.Create
                            firstPin = ""
                            shakeAndReset("PINs didn't match — please try again")
                        }
                    }
                }
            } else {
                // VERIFY MODE
                if (appLockManager.verifyPin(newPin)) {
                    scope.launch {
                        delay(150)
                        onPinVerified()
                    }
                } else {
                    shakeAndReset("Incorrect PIN")
                }
            }
        } else {
            pin = newPin
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A0E2E))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Back arrow — only in verify mode (in setup mode there's nothing to go back to)
        if (hasPin) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to biometrics",
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        } else {
            Spacer(modifier = Modifier.height(48.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title — changes between setup steps
        AnimatedContent(
            targetState = if (!hasPin) setupStep else SetupStep.Confirm,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
            label = "pinTitle"
        ) { step ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = when {
                        !hasPin && step == SetupStep.Create -> "Create a PIN 🔐"
                        !hasPin && step == SetupStep.Confirm -> "Confirm your PIN"
                        else -> "Enter PIN"
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when {
                        !hasPin && step == SetupStep.Create ->
                            "Set a 4-digit PIN as a backup to biometrics"
                        !hasPin && step == SetupStep.Confirm ->
                            "Re-enter your PIN to confirm"
                        else ->
                            "Enter your security PIN to continue"
                    },
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // PIN dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(PIN_LENGTH) { index ->
                val isFilled = index < pin.length
                val dotAlpha by animateFloatAsState(
                    targetValue = if (isFilled) 1f else 0.3f,
                    animationSpec = spring(),
                    label = "dotAlpha_$index"
                )
                val dotColor = if (wrongAttempt && index < pin.length) Color(0xFFE53935)
                               else Color(0xFFFFB6C1)

                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(dotColor.copy(alpha = dotAlpha))
                )
            }
        }

        // Error or hint message
        Box(modifier = Modifier.height(40.dp), contentAlignment = Alignment.Center) {
            val msg = errorMessage
            if (msg != null) {
                Text(
                    text = msg,
                    color = Color(0xFFE53935),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (!hasPin && setupStep == SetupStep.Confirm) {
                Text(
                    text = "✓ First PIN entered",
                    color = Color(0xFF81C784),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        NumPad(
            onDigit = { onDigitEntered(it) },
            onDelete = {
                if (pin.isNotEmpty()) {
                    pin = pin.dropLast(1)
                    errorMessage = null
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Forgot PIN — only show in verify mode
        if (hasPin) {
            Text(
                text = "Forgot PIN? Sign out and sign back in.",
                color = Color.White.copy(alpha = 0.35f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}

private enum class SetupStep { Create, Confirm }

@Composable
private fun NumPad(
    onDigit: (String) -> Unit,
    onDelete: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "\u232B")
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
                        "\u232B" -> NumPadButton(label = label, onClick = onDelete, isDelete = true)
                        else -> NumPadButton(label = label, onClick = { onDigit(label) })
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
    val btnScope = rememberCoroutineScope()

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
                    btnScope.launch {
                        scale.animateTo(0.88f, tween(70))
                        scale.animateTo(1f, spring())
                    }
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            if (isDelete) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Delete digit",
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
