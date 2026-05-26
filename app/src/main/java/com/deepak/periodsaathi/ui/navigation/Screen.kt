package com.deepak.periodsaathi.ui.navigation

import kotlinx.serialization.Serializable

@Serializable object Splash
@Serializable object Lock
@Serializable object Onboarding
@Serializable object Login
@Serializable data class NameSetup(val fromGoogle: Boolean = false)
@Serializable object Home
@Serializable object Calendar
@Serializable object Wellness
@Serializable object PartnerMode
@Serializable object Remedies
@Serializable object YogaFlow
@Serializable object Journal
@Serializable object MoodMap
@Serializable object Insights
@Serializable object Settings
@Serializable object ReportExport
@Serializable object Wardrobe
@Serializable object Challenges
@Serializable object BreathingMode
@Serializable object Payment
@Serializable object PhaseCoach
@Serializable data class DayLog(val dateEpoch: Long)
@Serializable object TimeCapsule
@Serializable object Community   // Secret Chats tab
@Serializable object PartnerDashboard
