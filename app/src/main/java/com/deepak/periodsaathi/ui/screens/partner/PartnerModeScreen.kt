package com.deepak.periodsaathi.ui.screens.partner

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A0E2E), Color(0xFF1A1228))
                )
            )
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Partner Mode",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            TextButton(onClick = { /* Show privacy info */ }) {
                Text("🔒 Privacy", color = SoftLavender)
            }
        }

        // Privacy note
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "✅", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Zero health data shared. Only your selected care requests.",
                    color = SoftLavender,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Partner name
        Text(
            text = "Send to $partnerName 💕",
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Care request cards
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(viewModel.careRequests) { request ->
                CareRequestCard(
                    request = request,
                    isSelected = request.id in selectedRequests,
                    onToggle = { viewModel.toggleRequest(request.id) }
                )
            }
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
                            append("\n💬 ${customMessage}")
                        }
                    }
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message)
                    }
                    context.startActivity(Intent.createChooser(intent, "Send via"))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedRequests.isEmpty()) SoftLavender else BlushPink
            ),
            enabled = selectedRequests.isNotEmpty() && sendState !is SendState.Sending,
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = when (sendState) {
                    is SendState.Idle -> "Send Care Request 💌"
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
    onToggle: () -> Unit
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
        modifier = Modifier
            .fillMaxWidth()
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
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
