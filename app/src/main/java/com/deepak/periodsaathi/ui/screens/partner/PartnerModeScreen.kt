package com.deepak.periodsaathi.ui.screens.partner

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*

@Composable
fun PartnerModeScreen(
    viewModel: PartnerViewModel = hiltViewModel()
) {
    val selectedRequests by viewModel.selectedRequests.collectAsState()
    val customMessage by viewModel.customMessage.collectAsState()
    val sendState by viewModel.sendState.collectAsState()
    val partnerName by viewModel.partnerName.collectAsState()
    val context = LocalContext.current

    val selectedRequest = viewModel.careRequests.firstOrNull { it.id in selectedRequests }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonPulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Partner Mode 💌",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Send a care request — no medical details shared",
                    color = OnSurfaceVariant,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Partner name
        Text(
            text = "Send to $partnerName 💕",
            color = OnSurface,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Care request cards
        val rows = viewModel.careRequests.chunked(2)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { request ->
                    CareRequestCard(
                        request = request,
                        isSelected = request.id in selectedRequests,
                        onToggle = { viewModel.toggleRequest(request.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Custom message
        OutlinedTextField(
            value = customMessage,
            onValueChange = { viewModel.updateCustomMessage(it) },
            label = { Text("Add a personal message (optional)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlushPink,
                unfocusedBorderColor = SoftLavender
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preview Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Preview for $partnerName",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (selectedRequest != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = selectedRequest.emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Priya needs you:",
                                fontWeight = FontWeight.Bold,
                                color = OnPrimaryContainer,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "\"${selectedRequest.text}\"",
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                fontSize = 18.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Select a care request above",
                        color = OnSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notification Mockup
        if (selectedRequest != null) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.widthIn(max = 280.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1A1A2E)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Primary,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "+",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Period Saathi • Now",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "New Care Request",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Priya needs you: \"${selectedRequest.text}\"",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Send button
        Button(
            onClick = {
                if (selectedRequests.isNotEmpty()) {
                    viewModel.sendViaShareIntent()
                    val selected = viewModel.careRequests.filter { it.id in selectedRequests }
                    val message = buildString {
                        append("💕 Care request for $partnerName:\n\n")
                        selected.forEach { append("${it.emoji} ${it.text}\n") }
                        if (customMessage.isNotBlank()) {
                            append("\n💬 $customMessage")
                        }
                    }
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message)
                    }
                    context.startActivity(Intent.createChooser(intent, "Send via"))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .scale(if (selectedRequests.isNotEmpty()) buttonScale else 1f)
                .background(
                    brush = if (selectedRequests.isNotEmpty())
                        Brush.horizontalGradient(listOf(SoftLavender, BlushPink))
                    else
                        Brush.horizontalGradient(listOf(SoftLavender, SoftLavender)),
                    shape = RoundedCornerShape(50.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            enabled = selectedRequests.isNotEmpty() && sendState !is SendState.Sending,
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = when (sendState) {
                    is SendState.Idle -> "Send to $partnerName 💌"
                    is SendState.Sending -> "Sending..."
                    is SendState.Sent -> "Sent! 💌"
                },
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun CareRequestCard(
    request: CareRequest,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) BlushPink.copy(alpha = 0.3f) else Color.Transparent,
        label = "cardBg"
    )

    GlassCard(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = request.emoji, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = request.text,
                color = OnSurface,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
