package com.example.periodsaathi.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Splash : Screen

    @Serializable
    data class Onboarding(val page: Int = 0) : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object Calendar : Screen

    @Serializable
    data object Wellness : Screen

    @Serializable
    data object PartnerMode : Screen

    @Serializable
    data object Remedies : Screen

    @Serializable
    data object YogaFlow : Screen

    @Serializable
    data object Journal : Screen

    @Serializable
    data object MoodMap : Screen

    @Serializable
    data object Insights : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object ReportExport : Screen

    @Serializable
    data object TimeCapsule : Screen

    @Serializable
    data object Wardrobe : Screen

    @Serializable
    data object Challenges : Screen

    @Serializable
    data object BreathingMode : Screen

    @Serializable
    data object VoiceLog : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object More : Screen
}

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", "home_health"),
    BottomNavItem(Screen.Calendar, "Calendar", "calendar_month"),
    BottomNavItem(Screen.Wellness, "Wellness", "self_care"),
    BottomNavItem(Screen.More, "More", "menu")
)

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val iconName: String
)