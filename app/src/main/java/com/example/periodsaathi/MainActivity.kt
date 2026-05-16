package com.example.periodsaathi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.example.periodsaathi.navigation.PeriodSaathiNavGraph
import com.example.periodsaathi.security.StealthModeManager
import com.example.periodsaathi.ui.lock.LockScreen
import com.example.periodsaathi.ui.lock.PinEntryScreen
import com.example.periodsaathi.ui.theme.PeriodSaathiTheme

private enum class LockState { Checking, Locked, PinFallback, Unlocked }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PeriodSaathiTheme {
                LockGate()
            }
        }
    }
}

@Composable
private fun LockGate() {
    val context = LocalContext.current
    val stealthManager = remember { StealthModeManager(context) }
    var lockState by remember { mutableStateOf(LockState.Checking) }

    LaunchedEffect(Unit) {
        if (stealthManager.isStealthModeEnabled() && !stealthManager.isAuthenticated()) {
            lockState = LockState.Locked
        } else {
            lockState = LockState.Unlocked
        }
    }

    AnimatedContent(
        targetState = lockState,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        modifier = Modifier.fillMaxSize(),
        label = "lockGate"
    ) { state ->
        when (state) {
            LockState.Checking -> {}
            LockState.Locked -> {
                LockScreen(
                    onUnlocked = {
                        stealthManager.setAuthenticated(true)
                        lockState = LockState.Unlocked
                    },
                    onPinFallback = {
                        lockState = LockState.PinFallback
                    }
                )
            }
            LockState.PinFallback -> {
                PinEntryScreen(
                    stealthModeManager = stealthManager,
                    pinVerified = {
                        stealthManager.setAuthenticated(true)
                        lockState = LockState.Unlocked
                    },
                    onBack = {
                        lockState = LockState.Locked
                    }
                )
            }
            LockState.Unlocked -> {
                PeriodSaathiNavGraph()
            }
        }
    }
}