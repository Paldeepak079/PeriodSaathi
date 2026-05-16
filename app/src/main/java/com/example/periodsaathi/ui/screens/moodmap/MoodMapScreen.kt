package com.example.periodsaathi.ui.screens.moodmap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.periodsaathi.ui.components.*
import com.example.periodsaathi.ui.theme.*

@Composable
fun MoodMapScreen(viewModel: MoodMapViewModel = hiltViewModel()) {
    val moodData by viewModel.moodData.collectAsStateWithLifecycle()
    val showOverlay by viewModel.showCycleOverlay.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(WarmCream)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = "Mood Map 📊", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
            Spacer(modifier = Modifier.height(16.dp))
            GlassCard(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    val cellSize = 28.dp.toPx()
                    val gap = 4.dp.toPx()
                    val colors = listOf(Color.Gray, BabyBlue, SoftLavender, ButterYellow, BlushPink, SoftCoral)
                    for ((index, mood) in moodData.withIndex()) {
                        val row = index / 7
                        val col = index % 7
                        val x = col * (cellSize + gap)
                        val y = row * (cellSize + gap)
                        drawRect(color = colors[mood.mood].copy(alpha = 0.8f), topLeft = Offset(x, y), size = androidx.compose.ui.geometry.Size(cellSize, cellSize))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("None", "Low", "Calm", "Okay", "Good", "Great").forEachIndexed { index, label ->
                    if (index > 0) {
                        Box(modifier = Modifier.size(20.dp).background(if (index == 1) Color.Gray else if (index == 2) BabyBlue else if (index == 3) SoftLavender else if (index == 4) ButterYellow else SoftCoral))
                        Text(text = label, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}