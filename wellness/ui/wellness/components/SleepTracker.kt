package com.deepak.periodsaathi.ui.screens.wellness.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.OnSurface
import com.deepak.periodsaathi.ui.theme.Primary
import com.deepak.periodsaathi.ui.theme.WarmGold

@Composable
fun SleepTracker(
    sleepHours: Float,
    rating: Int,
    onLogSleep: (Float, Int) -> Unit
) {
    var hours by remember { mutableStateOf(if (sleepHours == 0f) 7f else sleepHours) }
    var selectedRating by remember { mutableStateOf(if (rating == 0) 3 else rating) }

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
                Text("🌙", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Sleep Log & Quality",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = OnSurface
                )
            }

            Text(
                text = "Rest is critical for regulating cortisol levels and supporting recovery.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp
            )

            // Hour Slider
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Duration Rested:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${hours.toInt()} Hours", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                }

                Slider(
                    value = hours,
                    onValueChange = {
                        hours = it
                        onLogSleep(hours, selectedRating)
                    },
                    valueRange = 4f..12f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = Primary,
                        activeTrackColor = Primary,
                        inactiveTrackColor = Color.White.copy(0.5f)
                    )
                )
            }

            // Quality Star Ratings
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Sleep Restfulness Quality:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (star <= selectedRating) WarmGold else Color.White.copy(0.6f),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    selectedRating = star
                                    onLogSleep(hours, selectedRating)
                                }
                        )
                    }
                }
            }
        }
    }
}
