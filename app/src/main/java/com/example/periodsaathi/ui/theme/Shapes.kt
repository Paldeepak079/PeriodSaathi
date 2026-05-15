package com.example.periodsaathi.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CardShape = RoundedCornerShape(28.dp)
val ButtonShape = RoundedCornerShape(50.dp)
val ChipShape = RoundedCornerShape(20.dp)
val InputShape = RoundedCornerShape(16.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)

val PeriodSaathiShapes = Shapes(
    small = InputShape,
    medium = CardShape,
    large = BottomSheetShape
)
