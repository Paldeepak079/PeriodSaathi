package com.example.periodsaathi.ui.screens.partner

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import android.view.HapticFeedbackConstants
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.periodsaathi.ui.components.*
import com.example.periodsaathi.ui.theme.*

@Composable
fun PartnerModeScreen(
    viewModel: PartnerViewModel = hiltViewModel()
) {
    val selectedRequests by viewModel.selectedRequests.collectAsStateWithLifecycle()
    val customMessage by viewModel.customMessage.collectAsStateWithLifecycle()
    val sendState by viewModel.sendState.collectAsStateWithLifecycle()
    val partnerName by viewModel.partnerName.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Partner Mode",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary
                )
            }

            // Privacy header
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔒", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Privacy First",
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Text(
                                text = "Zero health data shared. Only your message reaches them.",
                                fontSize = 12.sp,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Partner name
            item {
                Text(
                    text = "Send to $partnerName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
            }

            // Care request cards
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(400.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.careRequests) { request ->
                        CareRequestCard(
                            request = request,
                            isSelected = request.id in selectedRequests,
                            onClick = { viewModel.toggleRequest(request.id) }
                        )
                    }
                }
            }

            // Custom message
            item {
                OutlinedTextField(
                    value = customMessage,
                    onValueChange = { viewModel.updateCustomMessage(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Add a personal message (optional)") },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )
            }

            // Preview card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Preview",
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = buildPreviewMessage(selectedRequests, customMessage, viewModel.careRequests),
                            color = OnSurface
                        )
                    }
                }
            }

            // Send button
            item {
                when (sendState) {
                    SendState.Idle -> {
                        SpringBounceButton(
                            text = "Send to $partnerName 💌",
                            onClick = {
                                viewModel.sendViaShareIntent()
                                val shareText = buildPreviewMessage(selectedRequests, customMessage, viewModel.careRequests)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Send to $partnerName"))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = Primary
                        )
                    }
                    SendState.Sending -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(Primary.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = OnPrimary)
                        }
                    }
                    SendState.Sent -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(50.dp))
                                .background(MintGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sent 💌",
                                color = OnPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CareRequestCard(
    request: CareRequest,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val view = LocalView.current

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "cardScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "heart")
    val heartAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartAlpha"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) BlushPink.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.5f))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Primary else Color.Gray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                onClick()
            }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box {
                Text(text = request.emoji, fontSize = 32.sp)
                if (isSelected) {
                    Text(
                        text = "❤️",
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .graphicsLayer { alpha = heartAlpha }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = request.text,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = OnSurface
            )
        }
    }
}

private fun buildPreviewMessage(
    selectedIds: Set<String>,
    customMessage: String,
    requests: List<CareRequest>
): String {
    val selectedEmojis = requests.filter { it.id in selectedIds }.map { it.emoji }.joinToString(" ")
    return buildString {
        append("Hey! $selectedEmojis")
        if (customMessage.isNotBlank()) {
            append("\n\n$customMessage")
        }
        append("\n\n- Sent via Period Saathi")
    }
}