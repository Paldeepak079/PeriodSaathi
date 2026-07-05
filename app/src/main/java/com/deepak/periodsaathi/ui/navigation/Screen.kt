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
@Serializable object Friend       // Friends list & invites screen
@Serializable object PartnerInvite
@Serializable object PartnerJoin
@Serializable object PartnerDashboard
@Serializable object PartnerInsights
@Serializable object PartnerQuiz
@Serializable data class PartnerQuizDetail(val quizId: String)
@Serializable object PartnerSettings
@Serializable object HotBagSafety
@Serializable object Chat         // AI chatbot screen
@Serializable object Notifications // Notifications screen
@Serializable object Rewards      // Rewards shop screen
@Serializable object Profile      // Profile screen
@Serializable object Privacy      // Privacy policy screen
@Serializable object Support      // Support screen


