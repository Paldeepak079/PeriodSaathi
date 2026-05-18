package com.deepak.periodsaathi.ui.screens.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun JournalScreen(
    viewModel: JournalViewModel = hiltViewModel()
) {
    val entries by viewModel.entries.collectAsState()
    val draft by viewModel.draft.collectAsState()
    val selectedMoods by viewModel.selectedMoods.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Text(text = "Journal", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurface)

        Spacer(modifier = Modifier.height(16.dp))

        // Write section
        GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { viewModel.updateDraft(it) },
                    placeholder = { Text("How are you feeling today?", color = SoftLavender) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlushPink,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mood selector
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        listOf("😊", "😐", "😢", "😤", "😴", "🤢", "🤩", "😌").forEach { mood ->
                            FilterChip(
                                selected = mood in selectedMoods,
                                onClick = { viewModel.toggleMood(mood) },
                                label = { Text(mood, fontSize = 20.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BlushPink.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { viewModel.saveEntry(true) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftLavender),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Seal for later 💌")
                    }

                    Button(
                        onClick = { viewModel.saveEntry(false) },
                        colors = ButtonDefaults.buttonColors(containerColor = BlushPink),
                        shape = RoundedCornerShape(20.dp),
                        enabled = draft.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Past Entries", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(entries) { entry ->
                JournalEntryCard(
                    entry = entry,
                    onDelete = { viewModel.deleteEntry(entry.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun JournalEntryCard(entry: JournalEntry, onDelete: () -> Unit) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = dateFormat.format(Date(entry.date)), color = OnSurfaceVariant, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        entry.moods.forEach { Text(text = it, fontSize = 16.sp) }
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = OnSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = entry.content, color = OnSurface)

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = BlushPink.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = entry.phase,
                    color = BlushPink,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
