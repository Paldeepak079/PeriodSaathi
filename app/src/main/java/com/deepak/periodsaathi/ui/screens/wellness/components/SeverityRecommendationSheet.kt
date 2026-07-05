package com.deepak.periodsaathi.ui.screens.wellness.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeverityRecommendationSheet(
    currentPainLevel: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
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
                    .align(Alignment.TopStart)
                    .offset(x = (-40).dp, y = (-30).dp)
                    .size(200.dp)
                    .background(Brush.radialGradient(listOf(Color(0x30C9B8FF), Color.Transparent)), CircleShape)
                    .blur(60.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 40.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pain Level Guide", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = OnSurfaceVariant)
                    }
                }
                Text(
                    "Select your pain level for personalized recommendations",
                    fontSize = 12.sp, color = OnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(Modifier.height(20.dp))

                SeverityCard(
                    emoji = "🌿",
                    title = "Mild",
                    description = "Mild discomfort, notice but manageable",
                    remedies = "Ginger tea, gentle stretching, warm compress",
                    isSelected = currentPainLevel == "Mild",
                    colors = listOf(MintGreen.copy(0.3f), MintGreen.copy(0.1f)),
                    onClick = { onSelect("Mild") }
                )
                Spacer(Modifier.height(12.dp))
                SeverityCard(
                    emoji = "🔥",
                    title = "Moderate",
                    description = "Clear discomfort affecting daily activities",
                    remedies = "Heating pad, turmeric milk, rest, magnesium",
                    isSelected = currentPainLevel == "Moderate",
                    colors = listOf(ButterYellow.copy(0.3f), ButterYellow.copy(0.1f)),
                    onClick = { onSelect("Moderate") }
                )
                Spacer(Modifier.height(12.dp))
                SeverityCard(
                    emoji = "⚠️",
                    title = "Severe",
                    description = "Intense pain, difficulty functioning",
                    remedies = "Consult doctor, OTC pain relief, rest, hot compress",
                    isSelected = currentPainLevel == "Severe",
                    colors = listOf(SoftCoral.copy(0.3f), SoftCoral.copy(0.1f)),
                    onClick = { onSelect("Severe") }
                )
            }
        }
    }
}

@Composable
private fun SeverityCard(
    emoji: String,
    title: String,
    description: String,
    remedies: String,
    isSelected: Boolean,
    colors: List<Color>,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Primary else Color.White.copy(0.6f),
        label = "border"
    )

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        ),
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(colors[0]),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 26.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = OnSurface)
                Text(description, fontSize = 11.sp, color = OnSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text("→ $remedies", fontSize = 11.sp, color = Primary, fontWeight = FontWeight.Medium)
            }
        }
    }
}
