package com.example.periodsaathi.ui.components

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.periodsaathi.ui.theme.BabyBlue
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.ButterYellow
import com.example.periodsaathi.ui.theme.MintGreen
import com.example.periodsaathi.ui.theme.SoftLavender
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val confettiColors = listOf(BlushPink, SoftLavender, BabyBlue, ButterYellow, MintGreen)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float,
    var rotation: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiOverlay(
    visible: Boolean,
    onComplete: () -> Unit = {}
) {
    if (!visible) return

    val particles = remember {
        List(60) {
            val color = confettiColors[Random.nextInt(confettiColors.size)]
            Particle(
                x = Random.nextFloat(),
                y = -Random.nextFloat() * 0.5f,
                vx = (Random.nextFloat() - 0.5f) * 600f,
                vy = Random.nextFloat() * 300f + 200f,
                color = color,
                size = Random.nextFloat() * 12f + 4f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f
            )
        }
    }

    LaunchedEffect(visible) {
        var lastFrameTime = 0L
        val gravity = 980f

        while (true) {
            withInfiniteAnimationFrameMillis { frameTimeMillis ->
                if (lastFrameTime == 0L) {
                    lastFrameTime = frameTimeMillis
                    return@withInfiniteAnimationFrameMillis
                }
                val dt = (frameTimeMillis - lastFrameTime) / 1000f
                lastFrameTime = frameTimeMillis

                var allBelow = true
                for (p in particles) {
                    p.x += p.vx * dt
                    p.y += p.vy * dt
                    p.vy += gravity * dt
                    p.rotation += p.rotationSpeed * dt
                    if (p.y < 1.2f) allBelow = false
                }
                if (allBelow) {
                    onComplete()
                    return@withInfiniteAnimationFrameMillis
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        for (p in particles) {
            val px = p.x * canvasWidth
            val py = p.y * canvasHeight
            if (py < -50f || py > canvasHeight + 50f || px < -50f || px > canvasWidth + 50f) continue
            rotate(p.rotation, Offset(px, py)) {
                drawRoundRect(
                    color = p.color,
                    topLeft = Offset(px - p.size / 2, py - p.size / 4),
                    size = Size(p.size, p.size / 2),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f)
                )
            }
        }
    }
}
