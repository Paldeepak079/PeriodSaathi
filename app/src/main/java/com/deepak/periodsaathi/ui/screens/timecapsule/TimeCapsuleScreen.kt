package com.deepak.periodsaathi.ui.screens.timecapsule

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepak.periodsaathi.ui.components.PrimaryButton
import com.deepak.periodsaathi.ui.theme.*
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private val DeepPurpleBg = Color(0xFF1A1228)
private val EnvelopeDark = Color(0xFF2D2142)
private val EnvelopeLight = Color(0xFF3A2D55)
private val GoldStart = Color(0xFFD4AF37)
private val GoldMid = Color(0xFFFFD700)
private val GoldEnd = Color(0xFFB8860B)

@Composable
fun TimeCapsuleScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: TimeCapsuleViewModel = hiltViewModel()
) {
    val text by viewModel.text.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.saveComplete.collect { onNavigateBack() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurpleBg)
    ) {
        StarField()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TimeCapsuleTopBar()

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cycle Time Capsule",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.02).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A gentle space for your future self.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .aspectRatio(0.78f)
            ) {
                EnvelopeSection(text = text, onTextChange = { viewModel.updateText(it) })
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Seal it \uD83D\uDC8C",
                onClick = { viewModel.saveCapsule() },
                isLoading = isSaving,
                enabled = text.isNotBlank(),
                icon = Icons.Default.Lock,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoCards()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StarField() {
    val stars = remember {
        List(30) {
            Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 2f + 0.5f)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val starPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
        ),
        label = "starPhase"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEachIndexed { index, (x, y, radius) ->
            val alpha = ((sin(starPhase * 2 * PI + index * 0.7) + 1) / 2).toFloat()
            val r = radius.dp.toPx() * 0.4f
            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.6f + 0.2f),
                radius = r,
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}

@Composable
private fun TimeCapsuleTopBar() {
    Surface(
        color = Color.White.copy(alpha = 0.08f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "\uD83C\uDF38", fontSize = 20.sp)
                }
                Text(
                    text = "Period Saathi",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun BoxScope.EnvelopeBack() {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(180.dp)
            .background(EnvelopeDark, RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
    )
}

@Composable
private fun EnvelopeSection(
    text: String,
    onTextChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .aspectRatio(0.8f),
        contentAlignment = Alignment.Center
    ) {
        EnvelopeBack()
        LetterCard(text = text, onTextChange = onTextChange)
        TimeBadge()
        EnvelopeFrontFlap()
        WaxSeal()
    }
}

@Composable
private fun BoxScope.LetterCard(
    text: String,
    onTextChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth(0.9f)
            .height(280.dp)
            .background(
                SurfaceContainerLowest.copy(alpha = 0.95f),
                RoundedCornerShape(12.dp)
            )
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Write a note to yourself for next month...",
                color = Primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = {
                    Text(
                        "Dear me, remember to...",
                        color = OnSurfaceVariant.copy(alpha = 0.3f),
                        fontFamily = FontFamily.Cursive,
                        fontSize = 22.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Cursive,
                    fontSize = 22.sp,
                    color = OnSurface,
                    lineHeight = 30.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Primary,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${text.length} / 200",
                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "With love, You",
                    color = Primary.copy(alpha = 0.6f),
                    fontFamily = FontFamily.Cursive,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
private fun BoxScope.TimeBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "badge")
    val translationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "badgeBounce"
    )

    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = 8.dp)
            .graphicsLayer { this.translationY = translationY }
            .background(
                Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(9999.dp)
            )
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(9999.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "You'll read this in ~28 days \uD83D\uDC8C",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BoxScope.EnvelopeFrontFlap() {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(180.dp)
            .background(
                Brush.verticalGradient(listOf(EnvelopeLight, EnvelopeDark)),
                RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
    )
}

@Composable
private fun BoxScope.WaxSeal() {
    val infiniteTransition = rememberInfiniteTransition(label = "seal")
    val sealScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sealPulse"
    )

    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = 100.dp)
            .graphicsLayer {
                scaleX = sealScale
                scaleY = sealScale
            }
            .size(80.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(GoldMid, GoldStart, GoldEnd),
                    center = Offset(0.3f, 0.3f)
                )
            )
            .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "\uD83C\uDF38", fontSize = 28.sp)
        }
    }
}

@Composable
private fun InfoCards() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoCard(
            icon = "\uD83D\uDD12",
            label = "Safe & Encrypted",
            modifier = Modifier.weight(1f)
        )
        InfoCard(
            icon = "\uD83D\uDCC5",
            label = "Release in 28 Days",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InfoCard(
    icon: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = icon, fontSize = 24.sp)
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
