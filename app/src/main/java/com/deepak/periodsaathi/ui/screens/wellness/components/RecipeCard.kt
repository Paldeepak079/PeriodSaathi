package com.deepak.periodsaathi.ui.screens.wellness.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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

@Composable
fun RecipeCardOverlay(
    solution: SolutionEntity,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            onClick = {},
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 30.dp, y = (-20).dp)
                        .size(150.dp)
                        .background(
                            Brush.radialGradient(listOf(Color(0x30FFB5C8), Color.Transparent)),
                            CircleShape
                        )
                        .blur(50.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, "Close", tint = OnSurfaceVariant)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(BlushPink.copy(0.3f))
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(solution.emoji.ifEmpty { "🫚" }, fontSize = 40.sp)
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = solution.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = solution.subtitle,
                        fontSize = 13.sp,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    )

                    Spacer(Modifier.height(24.dp))

                    val ingredients: List<String> = try {
                        Gson().fromJson(solution.ingredients, object : TypeToken<List<String>>() {}.type)
                    } catch (e: Exception) { emptyList() }
                    val steps: List<String> = try {
                        Gson().fromJson(solution.steps, object : TypeToken<List<String>>() {}.type)
                    } catch (e: Exception) { emptyList() }
                    val benefits: List<String> = try {
                        Gson().fromJson(solution.benefits, object : TypeToken<List<String>>() {}.type)
                    } catch (e: Exception) { emptyList() }

                    if (ingredients.isNotEmpty()) {
                        Text("🧾 Ingredients", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Spacer(Modifier.height(8.dp))
                        ingredients.forEach { ing ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(BlushPink)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text("• $ing", fontSize = 13.sp, color = OnSurface)
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                    }

                    if (steps.isNotEmpty()) {
                        Text("📋 Preparation Steps", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Spacer(Modifier.height(8.dp))
                        steps.forEachIndexed { i, step ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${i + 1}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    step,
                                    fontSize = 13.sp,
                                    color = OnSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                    }

                    if (benefits.isNotEmpty()) {
                        Text("✨ Benefits", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Spacer(Modifier.height(8.dp))
                        benefits.forEach { benefit ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🌸", fontSize = 14.sp)
                                Spacer(Modifier.width(10.dp))
                                Text("• $benefit", fontSize = 13.sp, color = OnSurface, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
