package com.deepak.periodsaathi.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Animated sliding dark-mode toggle.
 *
 * Shows 🌙/☀️ label with a coloured thumb that slides left/right.
 * Applies a 500ms cross-fade to the entire theme on toggle (via PeriodSaathiTheme Crossfade).
 */
@Composable
fun DarkModeToggle(
    isDark: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trackColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF3A1060) else Color(0xFFFFD6E7),
        animationSpec = spring(),
        label = "trackColor"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (isDark) 28.dp else 4.dp,
        animationSpec = spring(dampingRatio = 0.55f),
        label = "thumbOffset"
    )
    val thumbColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFFCCBCFF) else Color(0xFFFF6B9D),
        animationSpec = spring(),
        label = "thumbColor"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        Text(
            text = if (isDark) "🌙 Dark" else "☀️ Light",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        // Track
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(trackColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToggle
                )
        ) {
            // Thumb
            Box(
                modifier = Modifier
                    .padding(start = thumbOffset, top = 4.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(thumbColor)
            )
        }
    }
}
