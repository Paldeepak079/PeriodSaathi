package com.deepak.periodsaathi.ui.screens.wellness.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.BlushPink
import com.deepak.periodsaathi.ui.theme.DeepRose
import com.deepak.periodsaathi.ui.theme.Primary
import kotlinx.coroutines.delay

@Composable
fun CoinRewardAnimation(
    amount: Int,
    onAnimationComplete: () -> Unit
) {
    val scaleAnim = remember { Animatable(0f) }
    val alphaAnim = remember { Animatable(1f) }
    val floatOffsetAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Expand
        scaleAnim.animateTo(
            targetValue = 1.15f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        scaleAnim.animateTo(1.0f, tween(150))
        
        // Wait before float
        delay(1200)

        // Float up and disappear
        kotlinx.coroutines.coroutineScope {
            launch {
                floatOffsetAnim.animateTo(-150f, tween(600, easing = FastOutLinearInEasing))
            }
            launch {
                alphaAnim.animateTo(0f, tween(600))
            }
        }
        
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .offset(y = floatOffsetAnim.value.dp)
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Glowing Petal Coin shape
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(scaleAnim.value)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD1DC),
                                Color(0xFFC9B8FF)
                            )
                        ),
                        CircleShape
                    )
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌸",
                    fontSize = 44.sp
                )
            }

            // Coin earned text card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BlushPink)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "+$amount Petal Coins!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFE91E63)
                    )
                    Text(
                        text = "Saathi Rewards 🌸",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }
        }
    }
}
