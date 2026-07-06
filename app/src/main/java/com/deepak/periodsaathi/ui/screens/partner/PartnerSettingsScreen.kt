package com.deepak.periodsaathi.ui.screens.partner

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PartnerSettingsScreen(
    viewModel: PartnerViewModel,
    onBack: () -> Unit,
    onNavigateToInvite: () -> Unit
) {
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    
    var showDisconnectDialog by remember { mutableStateOf(false) }

    var notifyPeriod by remember { mutableStateOf(true) }
    var notifyFertile by remember { mutableStateOf(true) }
    var notifyTips by remember { mutableStateOf(true) }

    val partnerName = when (val state = connectionState) {
        is ConnectionUIState.Connected -> state.partnerName
        else -> "Deepak"
    }

    val isPrimary = when (val state = connectionState) {
        is ConnectionUIState.Connected -> state.isPrimary
        else -> true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Surface,
                        WarmCream,
                        BlushPink.copy(alpha = 0.2f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                        .border(1.dp, GlassBorder, CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("⬅️", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Partner Settings ⚙️",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepRose
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Connection Details Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Connection Status",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(BlushPink.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👩‍❤️‍👨", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = partnerName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                                Text(
                                    text = if (isPrimary) "Primary Tracker" else "Partner View",
                                    fontSize = 11.sp,
                                    color = Outline
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(MintGreen.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Connected", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Notification preferences section
            if (isPrimary) {
                Text(
                    text = "Shared Notifications Settings",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepRose,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(10.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Period Approaching Predictions", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                Text("Notify partner 1 day prior to predicted period.", fontSize = 11.sp, color = Outline)
                            }
                            Switch(
                                checked = notifyPeriod,
                                onCheckedChange = { notifyPeriod = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = DeepRose,
                                    uncheckedThumbColor = Outline,
                                    uncheckedTrackColor = GlassWhite
                                )
                            )
                        }

                        HorizontalDivider(color = GlassBorder, thickness = 0.5.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Fertility Window Starting", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                Text("Notify partner when fertile window opens.", fontSize = 11.sp, color = Outline)
                            }
                            Switch(
                                checked = notifyFertile,
                                onCheckedChange = { notifyFertile = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = DeepRose,
                                    uncheckedThumbColor = Outline,
                                    uncheckedTrackColor = GlassWhite
                                )
                            )
                        }

                        HorizontalDivider(color = GlassBorder, thickness = 0.5.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Daily Support & Tips", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                Text("Share daily emotional/rest suggestions with partner.", fontSize = 11.sp, color = Outline)
                            }
                            Switch(
                                checked = notifyTips,
                                onCheckedChange = { notifyTips = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = DeepRose,
                                    uncheckedThumbColor = Outline,
                                    uncheckedTrackColor = GlassWhite
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            // Remove/Disconnect button
            Button(
                onClick = { showDisconnectDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Error),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (isPrimary) "Remove Partner 💔" else "Disconnect Connection 💔",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Animated Glassmorphic Dialog
        if (showDisconnectDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { showDisconnectDialog = false },
                contentAlignment = Alignment.Center
            ) {
                // Spring Scale for dialog
                val scale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "scale"
                )

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .scale(scale)
                        .clickable(enabled = false) {}, // prevent click-through
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Are you sure?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isPrimary) {
                                "This will instantly revoke Arya's access to your cycle insights. They will be logged out of this view."
                            } else {
                                "This will disconnect you from Priya's cycle predictions and clear all quiz answers."
                            },
                            fontSize = 14.sp,
                            color = OnSurface,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showDisconnectDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = GlassWhite),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                                shape = RoundedCornerShape(50.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                            ) {
                                Text("Cancel", color = OnSurface, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    showDisconnectDialog = false
                                    viewModel.revokeConnection()
                                    onNavigateToInvite()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Error),
                                shape = RoundedCornerShape(50.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                            ) {
                                Text("Disconnect", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
