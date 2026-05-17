package com.example.periodsaathi.ui.screens.moodmap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.periodsaathi.ui.components.GlassCard
import com.example.periodsaathi.ui.theme.*
import java.time.LocalDate

@Composable
fun MoodMapScreen(
    viewModel: MoodMapViewModel = hiltViewModel()
) {
    val moodData by viewModel.moodData.collectAsState()
    val showCycleOverlay by viewModel.showCycleOverlay.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()

    val moodColors = mapOf(
        "😊" to Color(0xFF4CAF50),
        "😐" to Color(0xFF2196F3),
        "😢" to Color(0xFF9C27B0),
        "😤" to Color(0xFFF44336),
        "😴" to Color(0xFF795548),
        "😌" to Color(0xFF00BCD4)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A1228), Color(0xFF0D0A14))))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Mood Map", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Cycle", color = SoftLavender, fontSize = 14.sp)
                Switch(
                    checked = showCycleOverlay,
                    onCheckedChange = { viewModel.toggleCycleOverlay() },
                    colors = SwitchDefaults.colors(checkedThumbColor = BlushPink, checkedTrackColor = BlushPink.copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Last 90 days", color = SoftLavender, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // 7x14 grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(moodData.entries.sortedBy { it.key }.takeLast(90).associate { it.key to it.value }.toList()) { (date, data) ->
                val color = moodColors[data.mood] ?: Color.Gray
                val isSelected = date == selectedDay

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) BlushPink else color.copy(alpha = 0.7f))
                        .clickable { viewModel.selectDay(date) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend
        GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                moodColors.forEach { (mood, color) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = mood, fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}