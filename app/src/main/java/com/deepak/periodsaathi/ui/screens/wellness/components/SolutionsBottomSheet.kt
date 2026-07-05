package com.deepak.periodsaathi.ui.screens.wellness.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.theme.*
import com.deepak.periodsaathi.wellness.data.local.entities.SolutionEntity

val symptomData = mapOf(
    "cramps" to listOf("Ginger Tea", "Hot Water Bag", "Magnesium Foods", "Child's Pose"),
    "bloating" to listOf("Fennel Tea", "Mint Water", "Reduce Salt", "Light Walking"),
    "headache" to listOf("Hydration", "Dark Room", "Peppermint Oil"),
    "fatigue" to listOf("Iron-Rich Foods", "Short Nap", "Gentle Yoga")
)

val symptomEmojis = mapOf(
    "cramps" to "🩸",
    "bloating" to "🫧",
    "headache" to "🤕",
    "fatigue" to "😴"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolutionsBottomSheet(
    symptom: String,
    solutions: List<SolutionEntity>,
    onDismiss: () -> Unit,
    onSolutionClick: (SolutionEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF5F7), Color(0xFFF9F3FF))
                    ),
                    RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-20).dp)
                    .size(200.dp)
                    .background(Brush.radialGradient(listOf(Color(0x30FFB5C8), Color.Transparent)), CircleShape)
                    .blur(60.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 40.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = symptomEmojis[symptom] ?: "🌸", fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = symptom.replaceFirstChar { it.uppercase() } + " Relief",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Primary
                            )
                            Text(
                                text = "Try these remedies for ${symptom} relief",
                                fontSize = 12.sp,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = OnSurfaceVariant)
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = BlushPink.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(Modifier.height(16.dp))

                if (solutions.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✨", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = symptomData[symptom]?.joinToString("\n• ", "• ") ?: "",
                            fontSize = 15.sp,
                            color = OnSurface,
                            lineHeight = 24.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Log your symptoms to get personalized solutions",
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(solutions) { sol ->
                            SolutionCard(
                                solution = sol,
                                onClick = { onSolutionClick(sol) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SolutionCard(
    solution: SolutionEntity,
    onClick: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Card(
        onClick = {
            scope.launch {
                onClick()
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.75f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(BlushPink.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = solution.emoji.ifEmpty { "🌸" }, fontSize = 28.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = solution.title, fontWeight = FontWeight.Bold, color = OnSurface, fontSize = 16.sp)
                if (solution.subtitle.isNotEmpty()) {
                    Text(text = solution.subtitle, fontSize = 12.sp, color = OnSurfaceVariant, maxLines = 2)
                }
                if (solution.durationMinutes > 0) {
                    Spacer(Modifier.height(4.dp))
                    Text(text = "${solution.durationMinutes} min", fontSize = 11.sp, color = Lavender)
                }
            }
            if (solution.isRecipe) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SoftCoral.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Recipe", fontSize = 10.sp, color = SoftCoral, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
