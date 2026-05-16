package com.example.periodsaathi.ui.journal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.BabyBlue
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DarkBackground = Color(0xFF1A1228)
private val CreamWhite = Color(0xFFF5F0E6)
private val GlassWhite = Color(0x0AFFFFFF)

private val moodColors = mapOf(
    "😭" to Color(0xFF7B9AC4),
    "😡" to Color(0xFFE57373),
    "🌟" to Color(0xFFFFD54F),
    "😊" to Color(0xFFAED581),
    "😰" to Color(0xFFBA68C8),
    "📝" to Color(0xFF64B5F6)
)

private val monthFormatter = DateTimeFormatter.ofPattern("MMM")

@Composable
fun MoodMapScreen(
    viewModel: JournalViewModel = hiltViewModel()
) {
    val moodMapData by viewModel.moodMapData.collectAsState()
    var showCycleOverlay by remember { mutableStateOf(true) }
    var selectedCell by remember { mutableStateOf<Pair<LocalDate, MoodData>?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Mood Story 🗺️",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = CreamWhite
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GlassWhite),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cycle Overlay",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CreamWhite
                    )

                    Switch(
                        checked = showCycleOverlay,
                        onCheckedChange = { showCycleOverlay = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BlushPink,
                            checkedTrackColor = BlushPink.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        item {
            MoodHeatmap(
                data = moodMapData,
                showCycleOverlay = showCycleOverlay,
                onCellClick = { date, mood -> selectedCell = date to mood }
            )
        }

        item {
            MoodLegend()
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    AnimatedVisibility(
        visible = selectedCell != null,
        enter = scaleIn(spring(stiffness = Spring.StiffnessMedium)) + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        selectedCell?.let { (date, mood) ->
            CellTooltip(
                date = date,
                mood = mood,
                onDismiss = { selectedCell = null }
            )
        }
    }
}

@Composable
private fun MoodHeatmap(
    data: Map<LocalDate, MoodData>,
    showCycleOverlay: Boolean,
    onCellClick: (LocalDate, MoodData) -> Unit
) {
    val cellSize = 28.dp
    val gap = 4.dp
    val columns = 7

    val sortedDates = data.keys.sorted()
    val months = sortedDates.groupBy { it.month }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            months.forEach { (month, dates) ->
                Text(
                    text = month.format(monthFormatter),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = CreamWhite.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp, top = if (month != months.keys.first()) 16.dp else 0.dp)
                )

                val rows = (dates.size + columns - 1) / columns

                repeat(rows) { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(gap),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(columns) { col ->
                            val index = row * columns + col
                            val date = dates.getOrNull(index)

                            if (date != null) {
                                val moodData = data[date]
                                val cellColor = if (moodData?.emoji?.isNotEmpty() == true) {
                                    moodColors[moodData.emoji] ?: Color.Gray.copy(alpha = 0.2f)
                                } else {
                                    Color.Gray.copy(alpha = 0.2f)
                                }

                                val dayOfCycle = ((date.toEpochDay() % 28) + 28) % 28
                                val isPeriod = dayOfCycle <= 5
                                val isFertile = dayOfCycle in 12..16

                                MoodCell(
                                    date = date,
                                    moodData = moodData,
                                    cellColor = cellColor,
                                    showCycleOverlay = showCycleOverlay,
                                    isPeriod = isPeriod,
                                    isFertile = isFertile,
                                    cellSize = cellSize,
                                    onClick = { onCellClick(date, moodData!!) }
                                )
                            } else {
                                Spacer(modifier = Modifier.size(cellSize))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(gap))
                }
            }
        }
    }
}

@Composable
private fun MoodCell(
    date: LocalDate,
    moodData: MoodData?,
    cellColor: Color,
    showCycleOverlay: Boolean,
    isPeriod: Boolean,
    isFertile: Boolean,
    cellSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(),
        label = "cellScale"
    )

    Box(
        modifier = Modifier
            .size(cellSize)
            .clip(RoundedCornerShape(4.dp))
            .background(cellColor)
            .clickable { onClick() }
            .then(
                if (showCycleOverlay && isPeriod) {
                    Modifier.background(
                        color = BlushPink.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    )
                } else if (showCycleOverlay && isFertile) {
                    Modifier.background(
                        color = BabyBlue.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (moodData?.emoji?.isNotEmpty() == true) {
            Text(
                text = moodData.emoji,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun CellTooltip(
    date: LocalDate,
    mood: MoodData,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = mood.emoji,
                    fontSize = 48.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = mood.phase,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
private fun MoodLegend() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Mood Legend",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = CreamWhite
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                LegendItem(emoji = "😭", label = "Teary", color = moodColors["😭"]!!)
                LegendItem(emoji = "😡", label = "Angry", color = moodColors["😡"]!!)
                LegendItem(emoji = "🌟", label = "Amazing", color = moodColors["🌟"]!!)
                LegendItem(emoji = "😊", label = "Happy", color = moodColors["😊"]!!)
                LegendItem(emoji = "😰", label = "Anxious", color = moodColors["😰"]!!)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    color = BlushPink.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Period",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = BabyBlue.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Fertile",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(emoji: String, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = CreamWhite.copy(alpha = 0.7f)
        )
    }
}