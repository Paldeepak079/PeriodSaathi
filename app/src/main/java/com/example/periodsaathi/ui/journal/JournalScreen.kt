package com.example.periodsaathi.ui.journal

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.periodsaathi.ui.theme.BlushPink
import com.example.periodsaathi.ui.theme.BabyBlue
import com.example.periodsaathi.ui.theme.SoftLavender
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private val DarkBackground = Color(0xFF1A1228)
private val CreamWhite = Color(0xFFF5F0E6)
private val GlassWhite = Color(0x0AFFFFFF)

val moodEmojis = listOf("😭", "😔", "😐", "🙂", "😊", "🌟", "😰", "😡", "🥰")

@Composable
fun JournalScreen(
    viewModel: JournalViewModel = hiltViewModel(),
    onNavigateToMoodMap: () -> Unit = {}
) {
    val entries by viewModel.entries.collectAsState()
    val currentDraft by viewModel.currentDraft.collectAsState()
    val selectedMoods by viewModel.selectedMoods.collectAsState()
    val timeCapsuleToReveal by viewModel.timeCapsuleToReveal.collectAsState()
    val currentDay by viewModel.currentDay.collectAsState()
    val currentPhase by viewModel.currentPhase.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        if (timeCapsuleToReveal != null) {
            TimeCapsuleReveal(
                entry = timeCapsuleToReveal!!,
                onDismiss = { viewModel.dismissTimeCapsule() },
                onKeep = { viewModel.dismissTimeCapsule() },
                onLetGo = {
                    viewModel.deleteEntry(timeCapsuleToReveal!!.id)
                    viewModel.dismissTimeCapsule()
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Your Journal 📓",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = CreamWhite
                    )
                }

                item {
                    PhasePill(day = currentDay, phase = currentPhase)
                }

                item {
                    WriteEntrySection(
                        draft = currentDraft,
                        onDraftChange = { viewModel.updateDraft(it) },
                        selectedMoods = selectedMoods,
                        onMoodToggle = { viewModel.toggleMood(it) },
                        onSave = { viewModel.saveEntry(false) },
                        onSaveTimeCapsule = { viewModel.saveEntry(true) }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Past Entries",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CreamWhite.copy(alpha = 0.8f)
                        )

                        TextButton(onClick = onNavigateToMoodMap) {
                            Text(
                                text = "View Mood Map 🗺️",
                                color = BlushPink
                            )
                        }
                    }
                }

                items(entries.filter { !it.isTimeCapsule }) { entry ->
                    JournalEntryCard(
                        entry = entry,
                        onClick = { viewModel.selectEntry(entry) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun PhasePill(day: Int, phase: String) {
    val phaseColor = when (phase) {
        "Menstrual" -> BlushPink
        "Follicular" -> BabyBlue
        "Ovulation" -> SoftLavender
        else -> Color(0xFFBA68C8)
    }

    Surface(
        color = phaseColor.copy(alpha = 0.2f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = "Day $day — $phase 🌙",
            style = MaterialTheme.typography.labelLarge,
            color = CreamWhite,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WriteEntrySection(
    draft: String,
    onDraftChange: (String) -> Unit,
    selectedMoods: Set<String>,
    onMoodToggle: (String) -> Unit,
    onSave: () -> Unit,
    onSaveTimeCapsule: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = {
                    Text(
                        text = "How are you feeling today? This is just for you 💕",
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlushPink,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                    focusedTextColor = CreamWhite,
                    unfocusedTextColor = CreamWhite,
                    cursorColor = BlushPink
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 6
            )

            Text(
                text = "${draft.split(" ").filter { it.isNotBlank() }.size} words",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "How are you feeling?",
                style = MaterialTheme.typography.labelMedium,
                color = CreamWhite.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(moodEmojis) { emoji ->
                    MoodButton(
                        emoji = emoji,
                        isSelected = selectedMoods.contains(emoji),
                        onClick = { onMoodToggle(emoji) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CreamWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Entry")
                }

                Button(
                    onClick = onSaveTimeCapsule,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlushPink.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Seal for later 💌")
                }
            }
        }
    }
}

@Composable
private fun MoodButton(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var showFloatAnimation by remember { mutableStateOf(false) }
    var floatOffset by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(showFloatAnimation) {
        if (showFloatAnimation) {
            kotlinx.coroutines.delay(600)
            showFloatAnimation = false
            floatOffset = 0f
        }
    }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            showFloatAnimation = true
            floatOffset = -60f
        }
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) BlushPink.copy(alpha = 0.3f)
                else GlassWhite
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (showFloatAnimation && isSelected) {
            Text(
                text = emoji,
                fontSize = 28.sp,
                modifier = Modifier
                    .offset(y = floatOffset.dp)
                    .graphicsLayer {
                        alpha = if (floatOffset < -30f) 0f else 1f + (floatOffset / 60f)
                    }
            )
        } else {
            Text(
                text = emoji,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
private fun JournalEntryCard(
    entry: JournalEntry,
    onClick: () -> Unit
) {
    val dateText = ChronoUnit.daysBetween(entry.date, LocalDate.now()).let { days ->
        when {
            days == 0L -> "Today"
            days == 1L -> "Yesterday"
            else -> "${days} days ago"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )

                Row {
                    entry.moods.forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = entry.text,
                style = MaterialTheme.typography.bodyMedium,
                color = CreamWhite,
                maxLines = 3
            )
        }
    }
}

@Composable
private fun TimeCapsuleReveal(
    entry: JournalEntry,
    onDismiss: () -> Unit,
    onKeep: () -> Unit,
    onLetGo: () -> Unit
) {
    var isOpened by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    var wordIndex by remember { mutableStateOf(0) }

    LaunchedEffect(isOpened) {
        if (isOpened) {
            kotlinx.coroutines.delay(500)
            showContent = true

            val words = entry.text.split(" ")
            words.forEachIndexed { index, _ ->
                wordIndex = index + 1
                kotlinx.coroutines.delay(50)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        Color(0xFF2D1F3D)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            if (!isOpened) {
                Text(
                    text = "📩",
                    fontSize = 80.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "A message from yourself 💌",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CreamWhite
                )

                Spacer(modifier = Modifier.height(8.dp))

                val daysAgo = ChronoUnit.daysBetween(entry.date, LocalDate.now())
                Text(
                    text = "Written $daysAgo days ago",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { isOpened = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlushPink
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text(
                        text = "Open",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            } else if (showContent) {
                val words = entry.text.split(" ")

                Text(
                    text = entry.text.split(" ").take(wordIndex).joinToString(" "),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = FontStyle.Italic,
                        lineHeight = 32.sp
                    ),
                    color = CreamWhite,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(48.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onKeep,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CreamWhite
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Keep 📚")
                    }

                    Button(
                        onClick = onLetGo,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Let go 🍃")
                    }
                }
            }
        }
    }
}