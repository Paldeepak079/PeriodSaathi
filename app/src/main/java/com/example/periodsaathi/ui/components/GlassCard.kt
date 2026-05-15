package com.example.periodsaathi.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.periodsaathi.ui.theme.GlassBorder
import com.example.periodsaathi.ui.theme.GlassWhite

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    content: @Composable () -> Unit
) {
    androidx.compose.material3.Card(
        modifier = modifier.clip(shape),
        shape = shape,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = GlassWhite
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
    ) {
        content()
    }
}
