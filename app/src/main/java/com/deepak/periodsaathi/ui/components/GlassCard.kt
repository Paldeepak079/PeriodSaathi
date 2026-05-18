package com.deepak.periodsaathi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.deepak.periodsaathi.ui.theme.CardShape

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    tint: Color = Color.Transparent,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardContent = @Composable {
        Surface(
            shape = shape,
            color = Color.White.copy(alpha = 0.45f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
            modifier = modifier
        ) {
            Column(content = content)
        }
    }

    if (onClick != null) {
        ScaleButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
        ) {
            Box {
                cardContent()
                if (tint != Color.Transparent) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .drawBehind {
                                drawRect(color = tint.copy(alpha = 0.15f))
                            }
                    )
                }
            }
        }
    } else {
        cardContent()
    }
}

