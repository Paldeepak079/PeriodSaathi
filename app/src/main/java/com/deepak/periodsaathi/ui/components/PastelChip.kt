package com.deepak.periodsaathi.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.theme.BlushPink

@Composable
fun PastelChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color = BlushPink,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) color else Color.White,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "chipBg"
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.03f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "chipScale"
    )
    val textColor = if (selected) Color.White else Color(0xFF2D2D2D)

    ScaleButton(
        onClick = onClick,
        modifier = Modifier
            .padding(4.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = bgColor,
            border = BorderStroke(
                width = if (selected) 0.dp else 1.dp,
                color = color.copy(alpha = 0.4f)
            ),
            modifier = Modifier.height(36.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = textColor,
                    modifier = Modifier.padding(start = if (leadingIcon != null) 4.dp else 0.dp, end = if (trailingIcon != null) 4.dp else 0.dp)
                )
                if (trailingIcon != null) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

