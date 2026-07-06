package com.deepak.periodsaathi.ui.screens.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepak.periodsaathi.domain.PredictionResult
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.ShimmerHomeScreen
import com.deepak.periodsaathi.ui.theme.*
import java.time.format.DateTimeFormatter

private fun deriveTag(title: String): String = when {
    title.contains("Cycle", ignoreCase = true) -> "Cycle Pattern"
    title.contains("Period", ignoreCase = true) || title.contains("Duration", ignoreCase = true) -> "Duration"
    title.contains("Symptom", ignoreCase = true) -> "Symptoms"
    title.contains("Mood", ignoreCase = true) -> "Mood Trend"
    else -> "Pattern"
}

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel(),
    onNavigateToDayLog: () -> Unit = {}
) {
    val cyclesLogged by viewModel.cyclesLogged.collectAsStateWithLifecycle()
    val insights by viewModel.insights.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val prediction by viewModel.prediction.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        if (isLoading) {
            ShimmerHomeScreen()
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Insights",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (cyclesLogged < 3) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            // Show prediction card even with < 3 cycles if we have any data
                            prediction?.let { pred -> PredictionCard(prediction = pred) }
                        }
                        item {
                            DetectiveEmptyState(
                                cyclesLogged = cyclesLogged,
                                onNavigateToDayLog = onNavigateToDayLog
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { InsightsHeader() }

                        prediction?.let { pred ->
                            item { PredictionCard(prediction = pred) }
                        }

                        items(insights) { insight ->
                            InsightCard(insight = insight)
                        }

                        item { TriggerHeatmap() }

                        item { RecommendedActions(onNavigateToDayLog = onNavigateToDayLog) }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

// ── Sub-composables ─────────────────────────────────────────────────────

@Composable
private fun InsightsHeader() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search insights",
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Your Patterns",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Discovered from your last 3 cycles",
            fontSize = 12.sp,
            color = OnSurfaceVariant
        )
    }
}

@Composable
private fun InsightCard(insight: PatternInsight) {
    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                    .background(Color(insight.color))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f).padding(top = 16.dp, bottom = 16.dp, end = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = deriveTag(insight.title),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(insight.color),
                        letterSpacing = 0.5.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(9999.dp),
                        color = Color(insight.color).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${(insight.confidence * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(insight.color),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = insight.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = insight.description, fontSize = 14.sp, color = OnSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PredictionCard(prediction: PredictionResult) {
    val fmt = DateTimeFormatter.ofPattern("MMM d")
    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.CalendarMonth, null, tint = Primary, modifier = Modifier.size(20.dp))
                Text(
                    "Next Period Prediction",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Primary
                )
                Spacer(Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(99.dp), color = Primary.copy(alpha = 0.15f)) {
                    Text(
                        "${prediction.confidencePercent}% confidence",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                PredictionStat(
                    label = "Next Period",
                    value = prediction.nextPeriodDate.format(fmt),
                    color = BlushPink
                )
                PredictionStat(
                    label = "Ovulation",
                    value = prediction.ovulationDate.format(fmt),
                    color = SoftLavender
                )
                PredictionStat(
                    label = "Fertile Window",
                    value = "${prediction.fertileWindowStart.format(fmt)}–${prediction.fertileWindowEnd.format(fmt)}",
                    color = BabyBlue
                )
            }

            if (prediction.patterns.isNotEmpty()) {
                HorizontalDivider(color = OutlineVariant.copy(0.5f))
                Text(
                    "Detected Patterns",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = OnSurfaceVariant
                )
                prediction.patterns.forEach { pattern ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(pattern.emoji, fontSize = 14.sp)
                        Column {
                            Text(
                                pattern.title,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = OnSurface
                            )
                            Text(pattern.description, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PredictionStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            value,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = color
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
    }
}

@Composable
private fun TriggerHeatmap() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Trigger Heatmap (Last 7 Days)",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            dayLabels.forEach { label ->
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            dayLabels.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(OnSurfaceVariant.copy(alpha = (index + 1) * 0.1f))
                )
            }
        }
    }
}

@Composable
private fun DetectiveEmptyState(
    cyclesLogged: Int,
    onNavigateToDayLog: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(GlassWhite)
                    .border(1.dp, GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔍", fontSize = 48.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Still gathering clues...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (cyclesLogged == 0) "Start tracking your cycle to discover patterns"
                       else "Your detective companion is analyzing $cyclesLogged of 3 cycles",
                fontSize = 14.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToDayLog,
                shape = RoundedCornerShape(9999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(text = "Log Today's Habits")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = BlushPink,
                trackColor = SoftLavender.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun RecommendedActions(
    onNavigateToDayLog: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Recommended Actions",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface
        )
        data class Action(val icon: ImageVector, val title: String, val desc: String)
        val actions = listOf(
            Action(Icons.Rounded.Bedtime, "Prioritize 7+ hours of sleep", "To mitigate predicted cramp intensity tomorrow."),
            Action(Icons.Rounded.WaterDrop, "Stay hydrated", "Drink at least 8 glasses of water daily."),
            Action(Icons.AutoMirrored.Rounded.DirectionsWalk, "Gentle exercise", "Light walking helps reduce PMS symptoms.")
        )
        actions.forEach { action ->
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToDayLog() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(SecondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = null,
                            tint = OnSecondaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = action.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                        Text(text = action.desc, fontSize = 12.sp, color = OnSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
