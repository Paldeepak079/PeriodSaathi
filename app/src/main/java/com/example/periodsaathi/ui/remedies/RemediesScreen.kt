package com.example.periodsaathi.ui.remedies

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
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.periodsaathi.data.Remedy
import com.example.periodsaathi.data.RemediesData
import com.example.periodsaathi.ui.theme.BabyBlue
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.Lavender
import com.example.periodsaathi.ui.theme.SoftCoral
import com.example.periodsaathi.ui.theme.SoftLavender

@Composable
fun RemediesScreen(
    viewModel: RemediesViewModel = hiltViewModel(),
    onStartYogaFlow: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "Natural Remedies 🌿",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(RemediesData.remedies) { remedy ->
                    FlipCard(
                        remedy = remedy,
                        modifier = Modifier.size(160.dp, 200.dp)
                    )
                }
            }
        }

        item {
            YogaFlowSection(onStartFlow = onStartYogaFlow)
        }

        item {
            HotBagSafetySection()

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun YogaFlowSection(onStartFlow: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Cramp Relief Flow",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)
                    )
                    Text(
                        text = "15 Mins • 5 Poses",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Surface(
                    color = BlushPink.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🧘",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Gentle yoga sequence to ease menstrual cramps and relax the body.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF5D4037).copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStartFlow,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BlushPink),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "START FLOW",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlipCard(
    remedy: Remedy,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardRotation"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { isFlipped = !isFlipped }
                )
            }
    ) {
        if (rotation < 90f) {
            CardFront(remedy = remedy)
        } else {
            CardBack(
                remedy = remedy,
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            )
        }
    }
}

@Composable
private fun CardFront(remedy: Remedy) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = remedy.frontColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = remedy.emoji,
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = remedy.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tap to reveal 👆",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CardBack(
    remedy: Remedy,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = remedy.backColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = remedy.emoji,
                    fontSize = 28.sp
                )

                IconButton(
                    onClick = { },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Flip back",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = remedy.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
            )

            Text(
                text = "⏱ ${remedy.prepTime}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ingredients:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                remedy.ingredients.forEach { ingredient ->
                    Surface(
                        color = SoftLavender.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = ingredient,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Steps:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            remedy.steps.forEachIndexed { index, step ->
                Text(
                    text = "${index + 1}. $step",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5D4037).copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Helpful for:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                remedy.helpfulFor.forEach { tag ->
                    Surface(
                        color = BlushPink.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = BlushPink,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HotBagSafetySection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🌡️",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hot Bag Safety",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D4037)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Safety Badge",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HotBagSlider()
        }
    }
}

@Composable
private fun HotBagSlider() {
    var sliderPosition by remember { mutableFloatStateOf(0.4f) }
    var isInDangerZone by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "shake")

    val shakeOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )

    val thumbColor by animateFloatAsState(
        targetValue = sliderPosition,
        animationSpec = spring(),
        label = "thumbColor"
    )

    val thumbColorValue = when {
        sliderPosition < 0.6f -> Color(0xFF4CAF50)
        sliderPosition < 0.8f -> Color(0xFFFFC107)
        else -> Color(0xFFFF4444)
    }

    isInDangerZone = sliderPosition >= 0.8f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newPosition = (sliderPosition + dragAmount / 300f).coerceIn(0f, 1f)
                        sliderPosition = newPosition
                    }
                }
        ) {
            val trackWidth = size.width * 0.9f
            val trackHeight = 12.dp.toPx()
            val startX = size.width * 0.05f
            val centerY = size.height / 2

            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF4CAF50),
                        Color(0xFFFFEB3B),
                        Color(0xFFFF9800),
                        Color(0xFFFF4444)
                    )
                ),
                topLeft = Offset(startX, centerY - trackHeight / 2),
                size = androidx.compose.ui.geometry.Size(trackWidth, trackHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(trackHeight / 2, trackHeight / 2)
            )

            val thumbX = startX + trackWidth * sliderPosition
            val thumbRadius = 20.dp.toPx()

            drawCircle(
                color = thumbColorValue,
                radius = thumbRadius,
                center = Offset(thumbX, centerY + (if (isInDangerZone) shakeOffset.dp.toPx() else 0f))
            )

            drawCircle(
                color = Color.White,
                radius = thumbRadius * 0.4f,
                center = Offset(thumbX, centerY + (if (isInDangerZone) shakeOffset.dp.toPx() else 0f))
            )

            if (sliderPosition < 0.6f) {
                drawCircle(
                    color = Color(0xFF4CAF50),
                    radius = 12.dp.toPx(),
                    center = Offset(thumbX + 30.dp.toPx(), centerY)
                )
            } else if (sliderPosition < 0.8f) {
                drawCircle(
                    color = Color(0xFFFFC107),
                    radius = 12.dp.toPx(),
                    center = Offset(thumbX + 30.dp.toPx(), centerY)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val temperature = (30 + sliderPosition * 15).toInt()
            Text(
                text = "Current: ${temperature}°C",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
            )

            Text(
                text = when {
                    sliderPosition < 0.6f -> "✅ Safe"
                    sliderPosition < 0.8f -> "⚠️ Getting warm"
                    else -> "❌ Too hot!"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    sliderPosition < 0.6f -> Color(0xFF4CAF50)
                    sliderPosition < 0.8f -> Color(0xFFFFC107)
                    else -> Color(0xFFFF4444)
                }
            )
        }

        AnimatedVisibility(
            visible = isInDangerZone,
            enter = expandVertically(spring(stiffness = Spring.StiffnessMedium)) + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Too hot! Risk of burns 🚫",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try: Rice bag, warm towel, or microwaveable plushie",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5D4037)
                    )
                }
            }
        }
    }
}

class RemediesViewModel : androidx.lifecycle.ViewModel()