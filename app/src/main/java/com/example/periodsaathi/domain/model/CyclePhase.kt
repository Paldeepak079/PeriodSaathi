package com.example.periodsaathi.domain.model

import androidx.compose.ui.graphics.Color

enum class CyclePhase {
    MENSTRUAL,
    FOLLICULAR,
    OVULATION,
    LUTEAL
}

fun CyclePhase.displayName(): String = when (this) {
    CyclePhase.MENSTRUAL -> "Menstrual"
    CyclePhase.FOLLICULAR -> "Follicular"
    CyclePhase.OVULATION -> "Ovulation"
    CyclePhase.LUTEAL -> "Luteal"
}

fun CyclePhase.emoji(): String = when (this) {
    CyclePhase.MENSTRUAL -> "\uD83D\uDEE1\uFE0F"
    CyclePhase.FOLLICULAR -> "\uD83C\uDF3F"
    CyclePhase.OVULATION -> "\uD83E\uDDEA"
    CyclePhase.LUTEAL -> "\uD83C\uDF1E"
}

fun CyclePhase.color(): Color = when (this) {
    CyclePhase.MENSTRUAL -> Color(0xFF874e58)
    CyclePhase.FOLLICULAR -> Color(0xFF42617d)
    CyclePhase.OVULATION -> Color(0xFF655781)
    CyclePhase.LUTEAL -> Color(0xFFffb6c1)
}

fun CyclePhase.description(): String = when (this) {
    CyclePhase.MENSTRUAL -> "Your period is here. Time to rest and recharge."
    CyclePhase.FOLLICULAR -> "Energy is rising. You're ready to take on the world."
    CyclePhase.OVULATION -> "Peak communication and confidence. Shine bright!"
    CyclePhase.LUTEAL -> "Focus inward. Your body is preparing for the next cycle."
}

fun CyclePhase.superpower(): String = when (this) {
    CyclePhase.MENSTRUAL -> "Rest & Reset"
    CyclePhase.FOLLICULAR -> "Bold Action"
    CyclePhase.OVULATION -> "Magnetic Charisma"
    CyclePhase.LUTEAL -> "Deep Focus"
}
