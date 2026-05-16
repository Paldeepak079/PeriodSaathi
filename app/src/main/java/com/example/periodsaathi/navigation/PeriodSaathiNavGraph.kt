package com.example.periodsaathi.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.periodsaathi.ui.components.GlassCard
import com.example.periodsaathi.ui.screens.auth.LoginScreen
import com.example.periodsaathi.ui.screens.calendar.CalendarScreen
import com.example.periodsaathi.ui.screens.challenges.ChallengesScreen
import com.example.periodsaathi.ui.screens.home.HomeScreen
import com.example.periodsaathi.ui.screens.insights.InsightsScreen
import com.example.periodsaathi.ui.screens.journal.JournalScreen
import com.example.periodsaathi.ui.screens.moodmap.MoodMapScreen
import com.example.periodsaathi.ui.screens.onboarding.OnboardingScreen
import com.example.periodsaathi.ui.screens.partner.PartnerModeScreen
import com.example.periodsaathi.ui.screens.payment.PaymentScreen
import com.example.periodsaathi.ui.screens.remedies.RemediesScreen
import com.example.periodsaathi.ui.screens.report.ReportExportScreen
import com.example.periodsaathi.ui.screens.settings.SettingsScreen
import com.example.periodsaathi.ui.screens.splash.SplashScreen
import com.example.periodsaathi.ui.screens.wellness.WellnessScreen
import com.example.periodsaathi.ui.screens.yoga.YogaFlowScreen
import com.example.periodsaathi.ui.screens.wardrobe.WardrobeScreen
import com.example.periodsaathi.ui.theme.Primary
import com.example.periodsaathi.ui.theme.PrimaryContainer
import com.example.periodsaathi.ui.theme.WarmCream
import com.example.periodsaathi.ui.theme.OnSurface
import com.example.periodsaathi.ui.theme.OnSurfaceVariant

@Composable
fun PeriodSaathiNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItemData(Screen.Home, "Home", Icons.Default.Home),
        BottomNavItemData(Screen.Calendar, "Calendar", Icons.Default.CalendarMonth),
        BottomNavItemData(Screen.Wellness, "Wellness", Icons.Default.SelfImprovement),
        BottomNavItemData(Screen.Insights, "Insights", Icons.Default.Insights),
        BottomNavItemData(Screen.More, "More", Icons.Default.MoreHoriz)
    )

    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.Calendar.route,
        Screen.Wellness.route,
        Screen.Insights.route,
        Screen.More.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                indicatorColor = PrimaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            // 1. Splash Screen
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigate = { destination ->
                        when (destination) {
                            com.example.periodsaathi.ui.screens.splash.SplashDestination.ONBOARDING -> {
                                navController.navigate(Screen.Onboarding.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                            com.example.periodsaathi.ui.screens.splash.SplashDestination.LOGIN -> {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                            com.example.periodsaathi.ui.screens.splash.SplashDestination.HOME -> {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                            else -> {
                                navController.navigate(Screen.Onboarding.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }

            // 2. Onboarding Screen
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // 3. Login Screen
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // 4. Home Screen
            composable(Screen.Home.route) {
                HomeScreen()
            }

            // 5. Calendar Screen
            composable(Screen.Calendar.route) {
                CalendarScreen()
            }

            // 6. Wellness Screen
            composable(Screen.Wellness.route) {
                WellnessScreen()
            }

            // 7. Partner Mode Screen
            composable(Screen.PartnerMode.route) {
                PartnerModeScreen()
            }

            // 8. Remedies Screen
            composable(Screen.Remedies.route) {
                RemediesScreen()
            }

            // 9. Yoga Flow Screen
            composable(Screen.YogaFlow.route) {
                YogaFlowScreen(
                    onExit = { navController.popBackStack() }
                )
            }

            // 10. Journal Screen
            composable(Screen.Journal.route) {
                JournalScreen()
            }

            // 11. Mood Map Screen
            composable(Screen.MoodMap.route) {
                MoodMapScreen()
            }

            // 12. Insights Screen
            composable(Screen.Insights.route) {
                InsightsScreen()
            }

            // 13. Settings Screen
            composable(Screen.Settings.route) {
                SettingsScreen()
            }

            // 14. Report Export Screen
            composable(Screen.ReportExport.route) {
                ReportExportScreen()
            }

            // 15. Payment Screen
            composable(Screen.Payment.route) {
                PaymentScreen()
            }

            // 16. Wardrobe Screen
            composable(Screen.Wardrobe.route) {
                WardrobeScreen()
            }

            // 17. Challenges Screen
            composable(Screen.Challenges.route) {
                ChallengesScreen()
            }

            // 18. Time Capsule (using Journal)
            composable(Screen.TimeCapsule.route) {
                JournalScreen()
            }

            // 19. Breathing Mode (using Yoga)
            composable(Screen.BreathingMode.route) {
                YogaFlowScreen(
                    onExit = { navController.popBackStack() }
                )
            }

            // 20. Profile (using Settings)
            composable(Screen.Profile.route) {
                SettingsScreen()
            }

            // 21. More Screen
            composable(Screen.More.route) {
                MoreScreenContent(
                    onNavigateToPartner = { navController.navigate(Screen.PartnerMode.route) },
                    onNavigateToRemedies = { navController.navigate(Screen.Remedies.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToPayment = { navController.navigate(Screen.Payment.route) },
                    onNavigateToChallenges = { navController.navigate(Screen.Challenges.route) },
                    onNavigateToWardrobe = { navController.navigate(Screen.Wardrobe.route) },
                    onNavigateToReport = { navController.navigate(Screen.ReportExport.route) }
                )
            }
        }
    }
}

data class BottomNavItemData(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

@Composable
private fun MoreScreenContent(
    onNavigateToPartner: () -> Unit,
    onNavigateToRemedies: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPayment: () -> Unit,
    onNavigateToChallenges: () -> Unit,
    onNavigateToWardrobe: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "More",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Primary
        )

        val menuItems = listOf(
            "Partner Mode" to onNavigateToPartner,
            "Remedies" to onNavigateToRemedies,
            "Challenges" to onNavigateToChallenges,
            "Wardrobe" to onNavigateToWardrobe,
            "Reports" to onNavigateToReport,
            "Payment" to onNavigateToPayment,
            "Settings" to onNavigateToSettings
        )

        menuItems.forEach { (title, action) ->
            MoreMenuItemContent(title = title, onClick = action)
        }
    }
}

@Composable
private fun MoreMenuItemContent(title: String, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                color = OnSurface
            )
        }
    }
}