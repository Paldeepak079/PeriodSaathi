package com.deepak.periodsaathi.ui.screens.insights

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val cyclesLogged by viewModel.cyclesLogged.collectAsState()
    val minimumCyclesReached by viewModel.minimumCyclesReached.collectAsState()
    val insights by viewModel.insights.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Text(text = "Insights", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurface)

        Spacer(modifier = Modifier.height(16.dp))

        if (cyclesLogged < 3) {
            // Empty state
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🔍", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Still gathering data...", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Track $cyclesLogged of 3 cycles to see insights", color = OnSurfaceVariant)

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { cyclesLogged / 3f },
                        modifier = Modifier.fillMaxWidth(),
                        color = BlushPink,
                        trackColor = SoftLavender.copy(alpha = 0.3f)
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Text(text = "Your Patterns", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(insights) { insight ->
                    InsightCard(insight = insight)
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun InsightCard(insight: PatternInsight) {
    val animatedProgress by animateFloatAsState(
        targetValue = insight.confidence,
        animationSpec = tween(1000),
        label = "confidence"
    )

    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(insight.color))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = insight.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = insight.description, color = OnSurfaceVariant, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Confidence: ", color = OnSurfaceVariant, fontSize = 12.sp)
                    Text(text = "${(animatedProgress * 100).toInt()}%", color = Color(insight.color), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(insight.color),
                    trackColor = SoftLavender.copy(alpha = 0.2f)
                )
            }
        }
    }
}
