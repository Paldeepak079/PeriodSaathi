package com.deepak.periodsaathi.ui.util

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.minimumTouchTarget(size: Dp = 48.dp): Modifier =
    this.defaultMinSize(minWidth = size, minHeight = size)
