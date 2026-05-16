package com.example.periodsaathi.ui.screens.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
fun JournalScreen(viewModel: JournalViewModel = hiltViewModel()) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    val draft by viewModel.draft.collectAsStateWithLifecycle()
    val selectedMoods by viewModel.selectedMoods.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1228))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Journal 💌",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            // Write section
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "How are you feeling today?",
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Mood selector
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(viewModel.moodOptions) { mood ->
                                MoodChip(
                                    mood = mood,
                                    selected = mood in selectedMoods,
                                    onClick = { viewModel.toggleMood(mood) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = draft,
                            onValueChange = { viewModel.updateDraft(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = { Text("Write your thoughts...", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpringBounceButton(
                                text = "Seal for later 💌",
                                onClick = { viewModel.saveEntry() },
                                backgroundColor = SoftLavender
                            )

                            SpringBounceButton(
                                text = "Save",
                                onClick = { viewModel.saveEntry() },
                                backgroundColor = Primary
                            )
                        }
                    }
                }
            }

            // Entries list
            items(entries) { entry ->
                JournalEntryCard(entry = entry)
            }
        }
    }
}

@Composable
private fun MoodChip(mood: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (selected) PrimaryContainer else Color.Gray.copy(alpha = 0.2f))
            .then(
                if (selected) Modifier else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = mood, fontSize = 24.sp)
    }
}

@Composable
private fun JournalEntryCard(entry: JournalEntryUi) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = entry.date, fontSize = 12.sp, color = Color.Gray)
                Text(text = entry.phase, fontSize = 12.sp, color = Primary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = entry.content, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = entry.mood, fontSize = 20.sp)
        }
    }
}