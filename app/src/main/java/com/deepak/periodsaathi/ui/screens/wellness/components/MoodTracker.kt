package com.deepak.periodsaathi.ui.screens.wellness.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.screens.wellness.MoodHistoryEntry
import com.deepak.periodsaathi.ui.theme.Lavender
import com.deepak.periodsaathi.ui.theme.OnSurface
import com.deepak.periodsaathi.ui.theme.OnSurfaceVariant
import com.deepak.periodsaathi.ui.theme.Primary

data class MoodItem(val name: String, val emoji: String)

@Composable
fun MoodTracker(
    currentMood: String,
    moodHistory: List<MoodHistoryEntry>,
    onMoodSelect: (String) -> Unit
) {
    val moods = listOf(
        MoodItem("Sad", "\uD83D\uDE22"),
        MoodItem("Anxious", "\uD83D\uDE30"),
        MoodItem("Irritated", "\uD83D\uDE20"),
        MoodItem("Emotional", "\uD83D\uDE2A"),
        MoodItem("Calm", "\uD83E\uDDD8"),
        MoodItem("Happy", "\uD83D\uDE0A"),
        MoodItem("Energetic", "\u26A1")
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("\uD83E\uDDE0", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Cycle Sync Mood Log",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = OnSurface
                )
            }

            Text(
                text = "Observe emotional cycles. Fluctuations are natural biological shifts.",
                fontSize = 11.sp,
                color = OnSurfaceVariant,
                lineHeight = 14.sp
            )

            // Emoji grid selector
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(moods) { item ->
                    val isSelected = currentMood.lowercase() == item.name.lowercase()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onMoodSelect(item.name) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Primary.copy(0.2f) else Color.White.copy(0.4f)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Primary else Color.White.copy(0.7f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.emoji, fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.name,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Primary else OnSurface
                        )
                    }
                }
            }

            HorizontalDivider()

            // Emotional Vitality Trend — real data
            Text(
                text = "Emotional Vitality Trend:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = OnSurface
            )

            if (moodHistory.size < 2) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.White.copy(0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\uD83D\uDCC8", fontSize = 24.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Log your mood daily to see your emotional trend",
                            fontSize = 11.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }
            } else {
                // Real mood trend chart
                val last7 = moodHistory.takeLast(7)
                val moodValueMap = mapOf(
                    "sad" to 1f, "anxious" to 2f, "irritated" to 3f,
                    "emotional" to 4f, "calm" to 5f, "happy" to 6f, "energetic" to 7f
                )

                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.White.copy(0.3f), RoundedCornerShape(12.dp))
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val padding = 16.dp.toPx()
                    val usableWidth = canvasWidth - padding * 2
                    val usableHeight = canvasHeight - padding * 2

                    val points = last7.mapIndexed { index, entry ->
                        val x = padding + (index.toFloat() / (last7.size - 1).coerceAtLeast(1)) * usableWidth
                        val normalizedMood = (moodValueMap[entry.mood.lowercase()] ?: 4f) / 7f
                        val y = padding + usableHeight - (normalizedMood * usableHeight)
                        Offset(x, y)
                    }

                    // Gradient fill under curve
                    val fillPath = Path().apply {
                        moveTo(points.first().x, canvasHeight)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, canvasHeight)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        color = Primary.copy(alpha = 0.1f)
                    )

                    // Connecting bezier path
                    val linePath = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            val prev = points[i - 1]
                            val curr = points[i]
                            val cX = (prev.x + curr.x) / 2f
                            cubicTo(cX, prev.y, cX, curr.y, curr.x, curr.y)
                        }
                    }
                    drawPath(
                        path = linePath,
                        color = Primary,
                        style = Stroke(width = 2.5.dp.toPx())
                    )

                    // Data points
                    points.forEach { pt ->
                        drawCircle(color = Lavender, radius = 4.dp.toPx(), center = pt)
                        drawCircle(color = Color.White, radius = 1.5.dp.toPx(), center = pt)
                    }
                }

                // Date labels
                if (last7.size >= 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val firstDate = java.time.Instant.ofEpochMilli(last7.first().date)
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        val lastDate = java.time.Instant.ofEpochMilli(last7.last().date)
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        Text("${firstDate.monthValue}/${firstDate.dayOfMonth}", fontSize = 9.sp, color = OnSurfaceVariant)
                        Text("${lastDate.monthValue}/${lastDate.dayOfMonth}", fontSize = 9.sp, color = OnSurfaceVariant)
                    }
                }
            }
        }
    }
}
