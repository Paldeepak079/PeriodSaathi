package com.deepak.periodsaathi.ui.screens.wellness.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.deepak.periodsaathi.ui.theme.Lavender
import com.deepak.periodsaathi.ui.theme.OnSurface
import com.deepak.periodsaathi.ui.theme.Primary
import com.deepak.periodsaathi.ui.screens.wellness.DietRecommendation

@Composable
fun DietTracker(
    diet: DietRecommendation,
    customMeals: List<String>,
    onLogMeal: (String) -> Unit
) {
    var foodInput by remember { mutableStateOf("") }

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
                Text("🥗", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Sync-Diet Suggestions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = OnSurface
                )
            }

            // Diet list suggestions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(0.4f))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Recommended Foods For Recovery:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Primary
                )
                
                diet.meals.forEach { meal ->
                    Text(
                        text = "• $meal",
                        fontSize = 12.sp,
                        color = OnSurface,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Why this works: ${diet.benefits}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )
            }

            HorizontalDivider()

            // Custom meal logging input
            Text(
                text = "Log What You Ate Today",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = OnSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = foodInput,
                    onValueChange = { foodInput = it },
                    placeholder = { Text("e.g. Oatmeal with flaxseeds", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Color.White.copy(0.8f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                IconButton(
                    onClick = {
                        if (foodInput.isNotBlank()) {
                            onLogMeal(foodInput)
                            foodInput = ""
                        }
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Primary)
                        .size(48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Meal", tint = Color.White)
                }
            }

            // List of logged meals
            if (customMeals.isNotEmpty()) {
                Text(
                    text = "Logged Meals:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Primary
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(customMeals) { meal ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Lavender.copy(alpha = 0.25f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Lavender.copy(0.4f))
                        ) {
                            Text(
                                text = meal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
