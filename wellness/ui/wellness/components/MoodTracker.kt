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
import com.deepak.periodsaathi.ui.theme.Lavender
import com.deepak.periodsaathi.ui.theme.OnSurface
import com.deepak.periodsaathi.ui.theme.Primary

data class MoodItem(val name: String, val emoji: String)

@Composable
fun MoodTracker(
    currentMood: String,
    onMoodSelect: (String) -> Unit
) {
    val moods = listOf(
        MoodItem("Sad", "😢"),
        MoodItem("Anxious", "😰"),
        MoodItem("Irritated", "😠"),
        MoodItem("Emotional", "🥺"),
        MoodItem("Calm", "🧘"),
        MoodItem("Happy", "😊"),
        MoodItem("Energetic", "⚡")
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
                Text("🧠", fontSize = 24.sp)
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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

            // Dynamic Canvas line trend chart showing fluctuations
            Text(
                text = "Emotional Vitality Trend:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = OnSurface
            )

            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color.White.copy(0.3f), RoundedCornerShape(12.dp))
            ) {
                val width = size.width
                val height = size.height

                // Mock 5 trend points representing recent daily fluctuations
                val points = listOf(
                    Offset(width * 0.1f, height * 0.7f),
                    Offset(width * 0.3f, height * 0.8f),
                    Offset(width * 0.5f, height * 0.3f),
                    Offset(width * 0.7f, height * 0.4f),
                    Offset(width * 0.9f, height * 0.2f)
                )

                // Draw connecting bezier path
                val path = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        val pPrev = points[i - 1]
                        val pCurr = points[i]
                        val cX = (pPrev.x + pCurr.x) / 2f
                        cubicTo(cX, pPrev.y, cX, pCurr.y, pCurr.x, pCurr.y)
                    }
                }

                // Draw path lines
                drawPath(
                    path = path,
                    color = Primary,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Draw points dots
                points.forEach { pt ->
                    drawCircle(
                        color = Lavender,
                        radius = 5.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = pt
                    )
                }
            }
        }
    }
}
