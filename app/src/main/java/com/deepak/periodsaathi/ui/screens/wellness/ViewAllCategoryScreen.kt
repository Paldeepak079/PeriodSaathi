package com.deepak.periodsaathi.ui.screens.wellness

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.theme.*
import com.deepak.periodsaathi.wellness.data.local.entities.SolutionEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class CategoryInfo(
    val key: String,
    val title: String,
    val emoji: String
)

val wellnessCategories = listOf(
    CategoryInfo("yoga", "Yoga", "🧘"),
    CategoryInfo("exercise", "Exercises", "🤸"),
    CategoryInfo("remedy", "Remedies", "🌿"),
    CategoryInfo("breathing", "Breathing Therapy", "💨"),
    CategoryInfo("diet", "Diet Suggestions", "🥗"),
    CategoryInfo("hydration", "Hydration Tips", "💧"),
    CategoryInfo("mood", "Mood Support", "🌸"),
    CategoryInfo("sleep", "Sleep Recovery", "🌙")
)

@Composable
fun ViewAllCategoryScreen(
    category: String,
    allSolutions: List<SolutionEntity>,
    symptom: String,
    severity: String,
    onSolutionClick: (SolutionEntity) -> Unit,
    onFavoriteToggle: (String, Boolean) -> Unit,
    onBack: () -> Unit
) {
    val catInfo = wellnessCategories.find { it.key == category }

    val filtered = remember(allSolutions, category, symptom, severity) {
        val noSymptom = symptom == "none" || symptom.isEmpty()
        allSolutions.filter { sol ->
            val catMatch = sol.category == category
            val symptomMatch = noSymptom || sol.symptomType == symptom || sol.symptomType == "general"
            val severityMatch = noSymptom || sol.severity == severity || sol.severity == "all"
            catMatch && symptomMatch && severityMatch
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    val searched = remember(filtered, searchQuery) {
        if (searchQuery.isBlank()) filtered
        else filtered.filter { it.title.contains(searchQuery, ignoreCase = true) || it.subtitle.contains(searchQuery, ignoreCase = true) }
    }

    val difficultyFiltered = remember(searched, selectedDifficulty) {
        if (selectedDifficulty == null) searched
        else searched.filter { it.difficulty == selectedDifficulty }
    }

    val finalList = remember(difficultyFiltered, showFavoritesOnly) {
        if (showFavoritesOnly) difficultyFiltered.filter { it.isFavorite }
        else difficultyFiltered
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Primary)
                        }
                        Spacer(Modifier.weight(1f))
                        Text("${catInfo?.emoji ?: ""} ${catInfo?.title ?: "Wellness"}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Primary)
                        Spacer(Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(BlushPink.copy(0.2f))
                                .clickable { showFavoritesOnly = !showFavoritesOnly }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(if (showFavoritesOnly) "❤️ Favorites" else "☆ All", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search ${catInfo?.title?.lowercase() ?: ""}...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, "Clear", tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlushPink,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.8f),
                            focusedContainerColor = Color(0xFFF5F0FF),
                            unfocusedContainerColor = Color(0xFFF5F0FF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listOf("beginner", "intermediate", "advanced")) { diff ->
                            val isSelected = selectedDifficulty == diff
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedDifficulty = if (isSelected) null else diff },
                                label = { Text(diff.replaceFirstChar { it.uppercase() }, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BlushPink,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            if (finalList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌸", fontSize = 56.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No ${catInfo?.title?.lowercase() ?: "items"} found", fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("Try adjusting your filters", fontSize = 12.sp, color = OnSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(finalList, key = { it.id }) { solution ->
                        CategorySolutionCard(
                            solution = solution,
                            onClick = { onSolutionClick(solution) },
                            onFavorite = { fav -> onFavoriteToggle(solution.id, fav) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategorySolutionCard(
    solution: SolutionEntity,
    onClick: () -> Unit,
    onFavorite: (Boolean) -> Unit
) {
    val ingredients: List<String> = try {
        Gson().fromJson(solution.ingredients, object : TypeToken<List<String>>() {}.type)
    } catch (e: Exception) { emptyList() }

    val benefits: List<String> = try {
        Gson().fromJson(solution.benefits, object : TypeToken<List<String>>() {}.type)
    } catch (e: Exception) { emptyList() }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(BlushPink.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(solution.emoji.ifEmpty { "🌸" }, fontSize = 32.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(solution.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnSurface, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(4.dp))
                    if (solution.durationMinutes > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Lavender.copy(0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("${solution.durationMinutes}m", fontSize = 10.sp, color = Lavender)
                        }
                    }
                    Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (solution.isFavorite) BlushPink.copy(0.3f) else Color.Transparent)
                            .clickable { onFavorite(!solution.isFavorite) }
                            .padding(6.dp)
                    ) {
                        Text(if (solution.isFavorite) "❤️" else "🤍", fontSize = 14.sp)
                    }
                }
                if (solution.subtitle.isNotEmpty()) {
                    Text(solution.subtitle, fontSize = 12.sp, color = OnSurfaceVariant, maxLines = 2)
                }
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SoftCoral.copy(0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(solution.symptomType, fontSize = 9.sp, color = SoftCoral)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MintGreen.copy(0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(solution.difficulty, fontSize = 9.sp, color = MintGreen)
                    }
                }
                if (benefits.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(benefits.take(2).joinToString(" • "), fontSize = 11.sp, color = OnSurfaceVariant)
                }
            }
        }
    }
}
