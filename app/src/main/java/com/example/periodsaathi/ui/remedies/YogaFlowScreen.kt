package com.example.periodsaathi.ui.remedies

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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodsaathi.data.YogaPose
import com.example.periodsaathi.data.RemediesData
import com.example.periodsaathi.ui.theme.BabyBlue
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.Lavender
import com.example.periodsaathi.ui.theme.SoftLavender
import kotlinx.coroutines.delay

enum class BreathingPhase {
    INHALE, HOLD, EXHALE
}

@Composable
fun YogaFlowScreen(
    onExit: () -> Unit = {}
) {
    var currentPoseIndex by remember { mutableIntStateOf(0) }
    var poseProgress by remember { mutableFloatStateOf(0f) }
    var swipeOffset by remember { mutableFloatStateOf(0f) }

    val poses = RemediesData.yogaPoses
    val currentPose = poses.getOrNull(currentPoseIndex)

    LaunchedEffect(currentPoseIndex) {
        if (currentPose != null) {
            poseProgress = 0f
            val duration = currentPose.durationSeconds * 1000L
            val steps = 100
            val stepDuration = duration / steps

            repeat(steps) {
                delay(stepDuration)
                poseProgress = (it + 1) / steps.toFloat()
            }

            if (currentPoseIndex < poses.size - 1) {
                delay(500)
                currentPoseIndex++
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E),
                        Color(0xFF0D1B2A)
                    )
                )
            )
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (swipeOffset > 100 && currentPoseIndex < poses.size - 1) {
                            currentPoseIndex++
                        } else if (swipeOffset < -100 && currentPoseIndex > 0) {
                            currentPoseIndex--
                        }
                        swipeOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        swipeOffset += dragAmount
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onExit,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .padding(horizontal = 48.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(poseProgress)
                            .height(4.dp)
                            .background(Color.White.copy(alpha = 0.8f))
                    )
                }
            }

            if (currentPose != null) {
                AnimatedContent(
                    targetState = currentPose,
                    transitionSpec = {
                        (slideInHorizontally { it } + fadeIn()).togetherWith(
                            slideOutHorizontally { -it } + fadeOut()
                        )
                    },
                    label = "poseTransition"
                ) { pose ->
                    PoseDisplay(pose = pose)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            BreathingCircle(
                currentPose = currentPose,
                onComplete = {
                    if (currentPoseIndex < poses.size - 1) {
                        currentPoseIndex++
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            PoseProgressIndicator(
                totalPoses = poses.size,
                currentPose = currentPoseIndex
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (currentPoseIndex < poses.size - 1) {
                        currentPoseIndex++
                    } else {
                        onExit()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlushPink),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(56.dp)
            ) {
                Text(
                    text = if (currentPoseIndex < poses.size - 1) "Next Pose →" else "Finish 🎉",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PoseDisplay(pose: YogaPose) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Canvas(
            modifier = Modifier
                .size(180.dp)
                .graphicsLayer {
                    alpha = 0.9f
                }
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            when (pose.id) {
                "child_pose" -> drawChildPose(centerX, centerY)
                "cat_cow" -> drawCatCowPose(centerX, centerY)
                "supine_twist" -> drawSupineTwist(centerX, centerY)
                "legs_up_wall" -> drawLegsUpWall(centerX, centerY)
                "savasana" -> drawSavasana(centerX, centerY)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = pose.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = pose.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            pose.benefits.forEach { benefit ->
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = benefit,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = BlushPink.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "⏱ ${pose.durationSeconds}s",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                color = SoftLavender.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "In: ${pose.inhaleSeconds}s / Out: ${pose.exhaleSeconds}s",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

private fun DrawScope.drawChildPose(centerX: Float, centerY: Float) {
    val bodyColor = Color(0xFFFFCDD2)

    drawCircle(bodyColor, radius = 25f, center = Offset(centerX, centerY + 40f))

    drawOval(
        color = bodyColor,
        topLeft = Offset(centerX - 60f, centerY - 20f),
        size = androidx.compose.ui.geometry.Size(120f, 50f)
    )

    drawCircle(Color(0xFF37474F), radius = 8f, center = Offset(centerX - 30f, centerY - 30f))
    drawCircle(Color(0xFF37474F), radius = 8f, center = Offset(centerX + 30f, centerY - 30f))

    drawLine(
        color = bodyColor,
        start = Offset(centerX - 50f, centerY + 50f),
        end = Offset(centerX - 80f, centerY + 30f),
        strokeWidth = 20f,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
    drawLine(
        color = bodyColor,
        start = Offset(centerX + 50f, centerY + 50f),
        end = Offset(centerX + 80f, centerY + 30f),
        strokeWidth = 20f,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
}

private fun DrawScope.drawCatCowPose(centerX: Float, centerY: Float) {
    val bodyColor = Color(0xFFBBDEFB)

    for (i in 0..4) {
        val x = centerX - 60f + i * 30f
        drawCircle(bodyColor, radius = 12f, center = Offset(x, centerY))
    }

    drawCircle(Color(0xFF37474F), radius = 6f, center = Offset(centerX - 40f, centerY - 25f))
    drawCircle(Color(0xFF37474F), radius = 6f, center = Offset(centerX - 20f, centerY - 25f))

    drawOval(
        color = bodyColor,
        topLeft = Offset(centerX - 50f, centerY + 10f),
        size = androidx.compose.ui.geometry.Size(40f, 25f)
    )

    drawLine(
        color = bodyColor,
        start = Offset(centerX - 55f, centerY - 10f),
        end = Offset(centerX - 70f, centerY - 40f),
        strokeWidth = 12f,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
    drawLine(
        color = bodyColor,
        start = Offset(centerX + 55f, centerY - 10f),
        end = Offset(centerX + 70f, centerY - 40f),
        strokeWidth = 12f,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
}

private fun DrawScope.drawSupineTwist(centerX: Float, centerY: Float) {
    val bodyColor = Color(0xFFC8E6C9)

    drawOval(
        color = bodyColor,
        topLeft = Offset(centerX - 40f, centerY - 20f),
        size = androidx.compose.ui.geometry.Size(80f, 30f)
    )

    drawCircle(Color(0xFF37474F), radius = 6f, center = Offset(centerX - 30f, centerY - 10f))

    drawLine(
        color = bodyColor,
        start = Offset(centerX - 40f, centerY),
        end = Offset(centerX - 70f, centerY + 40f),
        strokeWidth = 15f,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
    drawLine(
        color = bodyColor,
        start = Offset(centerX + 40f, centerY),
        end = Offset(centerX + 70f, centerY - 30f),
        strokeWidth = 15f,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
}

private fun DrawScope.drawLegsUpWall(centerX: Float, centerY: Float) {
    val bodyColor = Color(0xFFFFE0B2)

    drawOval(
        color = bodyColor,
        topLeft = Offset(centerX - 30f, centerY - 30f),
        size = androidx.compose.ui.geometry.Size(60f, 30f)
    )

    drawCircle(Color(0xFF37474F), radius = 5f, center = Offset(centerX - 15f, centerY - 20f))

    drawLine(
        color = bodyColor,
        start = Offset(centerX - 25f, centerY - 15f),
        end = Offset(centerX - 25f, centerY + 50f),
        strokeWidth = 12f,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )

    drawLine(
        color = bodyColor,
        start = Offset(centerX + 25f, centerY - 15f),
        end = Offset(centerX + 25f, centerY + 50f),
        strokeWidth = 12f,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
}

private fun DrawScope.drawSavasana(centerX: Float, centerY: Float) {
    val bodyColor = Color(0xFFF8BBD9)

    drawOval(
        color = bodyColor,
        topLeft = Offset(centerX - 50f, centerY - 15f),
        size = androidx.compose.ui.geometry.Size(100f, 30f)
    )

    drawCircle(Color(0xFF37474F), radius = 6f, center = Offset(centerX - 20f, centerY - 5f))

    val armY = centerY + 10f
    drawLine(
        color = bodyColor,
        start = Offset(centerX - 50f, armY),
        end = Offset(centerX - 80f, armY),
        strokeWidth = 14f,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
    drawLine(
        color = bodyColor,
        start = Offset(centerX + 50f, armY),
        end = Offset(centerX + 80f, armY),
        strokeWidth = 14f,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )

    drawLine(
        color = bodyColor,
        start = Offset(centerX - 40f, centerY + 15f),
        end = Offset(centerX + 40f, centerY + 15f),
        strokeWidth = 12f,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
}

@Composable
private fun BreathingCircle(
    currentPose: YogaPose?,
    onComplete: () -> Unit
) {
    var phase by remember { mutableStateOf(BreathingPhase.INHALE) }
    var breathText by remember { mutableStateOf("Breathe In...") }
    var targetRadius by remember { mutableFloatStateOf(240f) }

    val inhaleTime = (currentPose?.inhaleSeconds ?: 4) * 1000
    val holdTime = 2000
    val exhaleTime = (currentPose?.exhaleSeconds ?: 6) * 1000

    LaunchedEffect(currentPose) {
        if (currentPose != null) {
            while (true) {
                phase = BreathingPhase.INHALE
                breathText = "Breathe In..."
                targetRadius = 340f
                delay(inhaleTime.toLong())

                phase = BreathingPhase.HOLD
                breathText = "Hold..."
                delay(holdTime.toLong())

                phase = BreathingPhase.EXHALE
                breathText = "Breathe Out..."
                targetRadius = 240f
                delay(exhaleTime.toLong())
            }
        }
    }

    val animatedRadius by animateFloatAsState(
        targetValue = targetRadius,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "breathRadius"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (phase == BreathingPhase.INHALE) 1f else if (phase == BreathingPhase.HOLD) 0.7f else 0.4f,
        label = "glowAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = animatedRadius * pulseScale

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        BlushPink.copy(alpha = glowAlpha * 0.5f),
                        Color.Transparent
                    ),
                    radius = radius * 1.3f
                ),
                radius = radius * 1.3f,
                center = Offset(centerX, centerY)
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 4.dp.toPx())
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = if (phase == BreathingPhase.INHALE) {
                        listOf(BabyBlue, Lavender)
                    } else if (phase == BreathingPhase.HOLD) {
                        listOf(BlushPink.copy(alpha = 0.6f), Lavender.copy(alpha = 0.4f))
                    } else {
                        listOf(Lavender, BabyBlue.copy(alpha = 0.5f))
                    }
                ),
                radius = radius - 4.dp.toPx(),
                center = Offset(centerX, centerY)
            )
        }

        AnimatedContent(
            targetState = breathText,
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            },
            label = "breathText"
        ) { text ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = when (phase) {
                        BreathingPhase.INHALE -> "${currentPose?.inhaleSeconds ?: 4}s"
                        BreathingPhase.HOLD -> "2s"
                        BreathingPhase.EXHALE -> "${currentPose?.exhaleSeconds ?: 6}s"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun PoseProgressIndicator(
    totalPoses: Int,
    currentPose: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        repeat(totalPoses) { index ->
            val isActive = index == currentPose
            val size = if (isActive) 12.dp else 6.dp

            val infiniteTransition = rememberInfiniteTransition(label = "dot$index")
            val glowAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "glow$index"
            )

            Box(
                modifier = Modifier
                    .size(size)
                    .background(
                        color = if (isActive) Color.White else Color.White.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
                    .then(
                        if (isActive) {
                            Modifier.background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = glowAlpha),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                        } else Modifier
                    )
            )
        }
    }
}