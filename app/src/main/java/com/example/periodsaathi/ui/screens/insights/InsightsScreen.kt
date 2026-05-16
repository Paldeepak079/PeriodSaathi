package com.example.periodsaathi.ui.screens.insights

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.periodsaathi.ui.components.*
import com.example.periodsaathi.ui.theme.*

@Composable
fun InsightsScreen(viewModel: InsightsViewModel = hiltViewModel()) {
    val insights by viewModel.insights.collectAsStateWithLifecycle()
    val cyclesLogged by viewModel.cyclesLogged.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(WarmCream)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Pattern Insights 🧠",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary
                )
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📊", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "$cyclesLogged of 3 cycles", fontWeight = FontWeight.Bold, color = OnSurface)
                            Text(text = "Keep logging to unlock more insights!", fontSize = 12.sp, color = OnSurfaceVariant)
                        }
                    }
                }
            }

            items(insights) { insight ->
                InsightCard(insight = insight)
            }
        }
    }
}

@Composable
private fun InsightCard(insight: InsightItem) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = insight.title, fontWeight = FontWeight.Bold, color = OnSurface)
                Text(text = "${(insight.confidence * 100).toInt()}%", fontWeight = FontWeight.Bold, color = Primary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = insight.description, color = OnSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { insight.confidence },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = Primary,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )
        }
    }
}