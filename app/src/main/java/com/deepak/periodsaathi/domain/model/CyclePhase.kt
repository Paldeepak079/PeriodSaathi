package com.deepak.periodsaathi.domain.model

enum class CyclePhase(val displayName: String, val emoji: String) {
    MENSTRUAL("Period", "🩸"),
    FOLLICULAR("Follicular", "🌱"),
    OVULATORY("Ovulatory", "🥚"),
    OVULATION("Ovulation", "🥚"),
    LUTEAL("Luteal", "🌙"),
    PMS("PMS", "😔"),
    UNKNOWN("Tracking", "📊");

    companion object {
        fun fromDay(day: Int, totalDays: Int): CyclePhase {
            if (totalDays <= 0) return UNKNOWN
            val progress = day.toFloat() / totalDays
            return when {
                progress <= 0.20f -> MENSTRUAL
                progress <= 0.55f -> FOLLICULAR
                progress <= 0.65f -> OVULATORY
                progress <= 0.90f -> LUTEAL
                else -> PMS
            }
        }
    }
}

