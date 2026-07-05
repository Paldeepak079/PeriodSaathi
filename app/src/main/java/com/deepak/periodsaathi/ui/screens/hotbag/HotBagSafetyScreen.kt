package com.deepak.periodsaathi.ui.screens.hotbag

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun HotBagSafetyScreen(onBack: () -> Unit = {}) {
    var temperature by remember { mutableStateOf(40f) }
    val dangerThreshold = 45f
    val warningThreshold = 43f
    val isInDanger = temperature > dangerThreshold
    val isWarning = temperature in warningThreshold..dangerThreshold
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(isInDanger) {
        if (isInDanger) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(800)
            temperature = dangerThreshold - 0.5f
        }
    }

    val trackColor by animateColorAsState(
        targetValue = when {
            isInDanger -> Color(0xFFD32F2F)
            isWarning  -> Color(0xFFF57C00)
            else       -> Color(0xFF43A047)
        },
        animationSpec = tween(400),
        label = "trackColor"
    )

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Primary)
                }
                Text("Hot Bag Safety",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Primary)
            }

            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Fire", fontSize = 48.sp)
                    Text(
                        text = "${temperature.toInt()}C",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = trackColor
                    )
                    Text(
                        text = when {
                            isInDanger -> "Danger Zone - Burn Risk!"
                            isWarning  -> "Getting warm - be careful"
                            temperature < 38f -> "Too cool for effective relief"
                            else       -> "Safe and Effective Temperature"
                        },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = trackColor
                    )
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Adjust Temperature",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = OnSurface)
                    Box(
                        modifier = Modifier.fillMaxWidth().height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF43A047), Color(0xFFF57C00), Color(0xFFD32F2F))
                                )
                            )
                    )
                    Slider(
                        value = temperature,
                        onValueChange = { temperature = it },
                        valueRange = 35f..50f,
                        colors = SliderDefaults.colors(
                            thumbColor = trackColor,
                            activeTrackColor = trackColor.copy(alpha = 0.3f),
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("35C", style = MaterialTheme.typography.labelSmall, color = Color(0xFF43A047))
                        Text("42C Safe Zone", style = MaterialTheme.typography.labelSmall, color = Color(0xFF43A047))
                        Text("50C", style = MaterialTheme.typography.labelSmall, color = Color(0xFFD32F2F))
                    }
                }
            }

            if (isInDanger || isWarning) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isInDanger) Color(0xFFFFEBEE) else Color(0xFFFFF8E1),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Rounded.Warning, null,
                            tint = if (isInDanger) Color(0xFFD32F2F) else Color(0xFFF57C00),
                            modifier = Modifier.size(28.dp))
                        Column {
                            Text(
                                if (isInDanger) "Burn Danger! Temperature too high."
                                else "Temperature approaching unsafe level",
                                fontWeight = FontWeight.Bold,
                                color = if (isInDanger) Color(0xFFD32F2F) else Color(0xFFF57C00)
                            )
                            Text(
                                if (isInDanger)
                                    "Hot water bottles above 45C can cause contact burns, especially during menstruation when skin sensitivity increases. Lower temperature immediately."
                                else
                                    "Between 43 and 45C: always use a cloth barrier between the bag and skin.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Safe Use Tips",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Primary)
                    listOf(
                        "Ideal temperature range: 38 to 42C for pain relief",
                        "Always use a cloth cover or towel as a barrier",
                        "Apply for max 20 minutes, then rest 10 minutes",
                        "Never sleep with a hot water bottle directly on skin",
                        "Check for leaks and cracks before each use"
                    ).forEach { tip ->
                        Text(tip, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HotBagSafetyPreview() {
    PeriodSaathiTheme { HotBagSafetyScreen() }
}
