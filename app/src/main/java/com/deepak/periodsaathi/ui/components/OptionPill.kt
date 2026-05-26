package com.deepak.periodsaathi.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.theme.DeepRose
import com.deepak.periodsaathi.ui.theme.OutlineVariant
import com.deepak.periodsaathi.ui.theme.Primary
import com.deepak.periodsaathi.ui.theme.PrimaryContainer

@Composable
fun OptionPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Primary else Color.White.copy(alpha = 0.7f),
        animationSpec = tween(200), label = "pillBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else DeepRose,
        animationSpec = tween(200), label = "pillText"
    )

    ScaleButton(onClick = onClick, modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = bgColor,
            border = BorderStroke(
                width = if (isSelected) 0.dp else 1.dp,
                color = if (isSelected) Color.Transparent else OutlineVariant
            ),
            tonalElevation = if (isSelected) 4.dp else 0.dp,
            shadowElevation = if (isSelected) 6.dp else 0.dp
        ) {
            Text(
                text = text,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}
