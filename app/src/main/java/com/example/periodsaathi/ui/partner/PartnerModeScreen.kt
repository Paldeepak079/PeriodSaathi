package com.example.periodsaathi.ui.partner

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.periodsaathi.ui.components.MascotEmotion
import com.example.periodsaathi.ui.components.SaathiMascot
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.BabyBlue
import com.example.periodsaathi.ui.theme.SoftLavender
import com.example.periodsaathi.ui.theme.SoftCoral
import kotlin.math.abs

@Composable
fun PartnerModeScreen(
    viewModel: PartnerViewModel = hiltViewModel()
) {
    val selectedRequests by viewModel.selectedRequests.collectAsState()
    val customMessage by viewModel.customMessage.collectAsState()
    val sendState by viewModel.sendState.collectAsState()
    val partnerName by viewModel.partnerName.collectAsState()
    val isPartnerLinked by viewModel.isPartnerLinked.collectAsState()
    val partnerCode by viewModel.partnerCode.collectAsState()
    val showPrivacyExplanation by viewModel.showPrivacyExplanation.collectAsState()

    val view = LocalView.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (!isPartnerLinked) {
                NotLinkedState(
                    partnerCode = partnerCode,
                    onGenerateCode = { viewModel.generatePartnerCode() },
                    onLink = { name, code -> viewModel.setPartnerInfo(name, code) }
                )
            } else {
                LinkedState(
                    selectedRequests = selectedRequests,
                    customMessage = customMessage,
                    sendState = sendState,
                    partnerName = partnerName,
                    careRequests = viewModel.careRequests,
                    showPrivacyExplanation = showPrivacyExplanation,
                    onToggleRequest = { viewModel.toggleRequest(it) },
                    onUpdateMessage = { viewModel.updateCustomMessage(it) },
                    onSend = { viewModel.sendCareRequest(view.context) },
                    onTogglePrivacy = { viewModel.togglePrivacyExplanation() }
                )
            }
        }
    }
}

@Composable
private fun LinkedState(
    selectedRequests: Set<String>,
    customMessage: String,
    sendState: SendState,
    partnerName: String,
    careRequests: List<CareRequest>,
    showPrivacyExplanation: Boolean,
    onToggleRequest: (String) -> Unit,
    onUpdateMessage: (String) -> Unit,
    onSend: () -> Unit,
    onTogglePrivacy: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PrivacyHeader(onTogglePrivacy = onTogglePrivacy, showExplanation = showPrivacyExplanation)
        }

        item {
            Text(
                text = "What do you need right now?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            CareRequestGrid(
                requests = careRequests,
                selectedIds = selectedRequests,
                onToggle = onToggleRequest
            )
        }

        item {
            CustomMessageInput(
                message = customMessage,
                onMessageChange = onUpdateMessage
            )
        }

        item {
            SendButton(
                state = sendState,
                partnerName = partnerName,
                onClick = onSend
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PrivacyHeader(
    onTogglePrivacy: () -> Unit,
    showExplanation: Boolean
) {
    var hasAppeared by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hasAppeared = true
    }

    AnimatedVisibility(
        visible = hasAppeared,
        enter = fadeIn(tween(500))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTogglePrivacy() },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF3E0)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Partner Mode 💕",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛡️ Zero medical data shared",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Tap to learn more",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575)
                    )
                }

                AnimatedVisibility(
                    visible = showExplanation,
                    enter = expandVertically(spring(stiffness = Spring.StiffnessMedium)),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(
                            text = "✓ What IS shared:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "• Care requests (hug, rest, chocolate, etc.)\n• Your name\n• A gentle reminder they care about you",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5D4037)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "✗ What is NOT shared:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                        Text(
                            text = "• Cycle dates\n• Flow intensity\n• Symptoms\n• Any health data\n• Location",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CareRequestGrid(
    requests: List<CareRequest>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit
) {
    var lastTappedIndex by remember { mutableIntStateOf(-1) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.height(300.dp)
    ) {
        itemsIndexed(requests) { index, request ->
            val isSelected = selectedIds.contains(request.id)
            val delay = if (index > lastTappedIndex) 80L * (index - lastTappedIndex) else 0L
            lastTappedIndex = index

            CareRequestCard(
                request = request,
                isSelected = isSelected,
                onClick = { onToggle(request.id) }
            )
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
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "heart")
    val heartAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (isSelected) {
                    Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(BlushPink, BlushPink.copy(alpha = 0.7f))
                        )
                    )
                } else {
                    Modifier.background(
                        Color.White.copy(alpha = 0.7f)
                    )
                }
            )
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onClick()
            }
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = request.emoji,
                fontSize = 36.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = request.text,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = if (isSelected) Color.White else Color(0xFF5D4037),
                maxLines = 2
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .offset(x = 8.dp, y = (-8).dp)
                ) {
                    Text(
                        text = "💗",
                        fontSize = 16.sp,
                        modifier = Modifier.graphicsLayer { alpha = heartAlpha }
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomMessageInput(
    message: String,
    onMessageChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = message,
        onValueChange = onMessageChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "+ Add your own message...",
                color = Color.Gray
            )
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlushPink,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        maxLines = 2
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "${message.length}/50",
            style = MaterialTheme.typography.labelSmall,
            color = if (message.length > 45) Color.Red else Color.Gray
        )
    }
}

@Composable
private fun SendButton(
    state: SendState,
    partnerName: String,
    onClick: () -> Unit
) {
    val view = LocalView.current

    val buttonWidth by animateFloatAsState(
        targetValue = when (state) {
            is SendState.Sending -> 60f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonWidth"
    )

    val backgroundColor by animateFloatAsState(
        targetValue = if (state is SendState.Sent) 1f else 0f,
        label = "bgGreen"
    )

    Button(
        onClick = {
            if (state is SendState.Idle || state is SendState.Error) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onClick()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = state !is SendState.Sending,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            disabledContainerColor = BlushPink.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(28.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        AnimatedContent(
            targetState = state,
            transitionSpec = {
                fadeIn(tween(200)) togetherWith fadeOut(tween(200))
            },
            label = "buttonContent"
        ) { currentState ->
            when (currentState) {
                is SendState.Idle -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Send care request to ${partnerName.ifEmpty { "Partner" }} 💌",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF5D4037)
                        )
                    }
                }

                is SendState.Sending -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PulsingDots()
                    }
                }

                is SendState.Sent -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "💕",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sent! They'll know you need them 💕",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }

                is SendState.Error -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚠️ ${currentState.message}",
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PulsingDots() {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "dot$index")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dotScale$index"
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scale)
                    .background(BlushPink, CircleShape)
            )
        }
    }
}

@Composable
private fun NotLinkedState(
    partnerCode: String?,
    onGenerateCode: () -> Unit,
    onLink: (String, String) -> Unit
) {
    var showLinkDialog by remember { mutableStateOf(false) }
    var copiedState by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(
            modifier = Modifier.size(200.dp)
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            drawCircle(
                color = SoftLavender.copy(alpha = 0.3f),
                radius = 80.dp.toPx(),
                center = Offset(centerX, centerY - 30.dp.toPx())
            )

            val leftHandPath = Path().apply {
                moveTo(centerX - 50.dp.toPx(), centerY + 20.dp.toPx())
                quadraticBezierTo(
                    centerX - 60.dp.toPx(), centerY - 10.dp.toPx(),
                    centerX - 20.dp.toPx(), centerY - 30.dp.toPx()
                )
            }
            drawPath(
                path = leftHandPath,
                color = BlushPink.copy(alpha = 0.6f),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )

            val rightHandPath = Path().apply {
                moveTo(centerX + 50.dp.toPx(), centerY + 20.dp.toPx())
                quadraticBezierTo(
                    centerX + 60.dp.toPx(), centerY - 10.dp.toPx(),
                    centerX + 20.dp.toPx(), centerY - 30.dp.toPx()
                )
            }
            drawPath(
                path = rightHandPath,
                color = BlushPink.copy(alpha = 0.6f),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )

            drawCircle(
                color = SoftCoral,
                radius = 15.dp.toPx(),
                center = Offset(centerX - 35.dp.toPx(), centerY - 35.dp.toPx())
            )
            drawCircle(
                color = SoftCoral,
                radius = 15.dp.toPx(),
                center = Offset(centerX + 35.dp.toPx(), centerY - 35.dp.toPx())
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Link your partner or bestie",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF5D4037)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (partnerCode != null) {
            Card(
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your sharing code",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = partnerCode,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 8.sp,
                        color = BlushPink
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
                                clipboard?.setText(androidx.compose.ui.text.AnnotatedString(partnerCode))
                                copiedState = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftLavender.copy(alpha = 0.3f)
                            )
                        ) {
                            Icon(
                                imageVector = if (copiedState) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = Color(0xFF5D4037)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (copiedState) "Copied! ✓" else "Copy 📋",
                                color = Color(0xFF5D4037)
                            )
                        }

                        Button(
                            onClick = { /* Share intent */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BlushPink
                            )
                        ) {
                            Text(
                                text = "Share",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        } else {
            Button(
                onClick = onGenerateCode,
                colors = ButtonDefaults.buttonColors(containerColor = BlushPink),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Text(
                    text = "Generate Code",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}