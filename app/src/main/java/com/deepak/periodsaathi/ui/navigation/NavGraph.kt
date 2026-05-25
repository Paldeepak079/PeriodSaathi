package com.deepak.periodsaathi.ui.navigation

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.deepak.periodsaathi.security.StealthModeManager
import com.deepak.periodsaathi.ui.lock.LockScreen
import com.deepak.periodsaathi.ui.lock.PinEntryScreen
import com.deepak.periodsaathi.ui.screens.auth.LoginScreen
import com.deepak.periodsaathi.ui.screens.breathing.BreathingModeScreen
import com.deepak.periodsaathi.ui.screens.calendar.CalendarScreen
import com.deepak.periodsaathi.ui.screens.challenges.ChallengesScreen
import com.deepak.periodsaathi.ui.screens.daylog.DayLogScreen
import com.deepak.periodsaathi.ui.screens.home.HomeScreen
import com.deepak.periodsaathi.ui.screens.phasecoach.PhaseCoachScreen
import com.deepak.periodsaathi.ui.screens.insights.InsightsScreen
import com.deepak.periodsaathi.ui.screens.journal.JournalScreen
import com.deepak.periodsaathi.ui.screens.moodmap.MoodMapScreen
import com.deepak.periodsaathi.ui.screens.namesetup.NameSetupScreen
import com.deepak.periodsaathi.ui.screens.onboarding.OnboardingScreen
import com.deepak.periodsaathi.ui.screens.partner.PartnerModeScreen
import com.deepak.periodsaathi.ui.screens.payment.PaymentScreen
import com.deepak.periodsaathi.ui.screens.remedies.RemediesScreen
import com.deepak.periodsaathi.ui.screens.report.ReportExportScreen
import com.deepak.periodsaathi.ui.screens.settings.SettingsScreen
import com.deepak.periodsaathi.ui.screens.splash.SplashScreen
import com.deepak.periodsaathi.ui.screens.timecapsule.TimeCapsuleScreen
import com.deepak.periodsaathi.ui.screens.wardrobe.WardrobeScreen
import com.deepak.periodsaathi.ui.screens.wellness.WellnessScreen
import com.deepak.periodsaathi.ui.screens.yoga.YogaFlowScreen

@Composable
fun PeriodSaathiNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Splash,
        enterTransition = {
            slideInHorizontally(animationSpec = spring(dampingRatio = 0.6f)) + fadeIn(tween(300))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = spring(dampingRatio = 0.6f)) + fadeOut(tween(200))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = spring(dampingRatio = 0.6f)) + fadeIn(tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(animationSpec = spring(dampingRatio = 0.6f)) + fadeOut(tween(200))
        }
    ) {
        composable<Splash> { SplashScreen(
            onNavigateToOnboarding = { navController.navigate(Onboarding) { popUpTo<Splash> { inclusive = true } } },
            onNavigateToHome = { navController.navigate(Home) { popUpTo<Splash> { inclusive = true } } },
            onNavigateToLock = { navController.navigate(Lock) { popUpTo<Splash> { inclusive = true } } }
        ) }
        composable<Lock> {
            val context = LocalContext.current
            var showPin by remember { mutableStateOf(false) }
            if (showPin) {
                val stealthModeManager = remember { StealthModeManager(context) }
                PinEntryScreen(
                    stealthModeManager = stealthModeManager,
                    pinVerified = {
                        navController.navigate(Home) { popUpTo<Lock> { inclusive = true } }
                    },
                    onBack = { showPin = false }
                )
            } else {
                LockScreen(
                    onUnlocked = {
                        navController.navigate(Home) { popUpTo<Lock> { inclusive = true } }
                    },
                    onPinFallback = { showPin = true }
                )
            }
        }
        composable<Onboarding> { OnboardingScreen(
            onComplete = { navController.navigate(Login) { popUpTo<Onboarding> { inclusive = true } } },
            onSkip = { navController.navigate(Login) { popUpTo<Onboarding> { inclusive = true } } }
        ) }
        composable<Login> { LoginScreen(
            navController = navController,
            onLoginSuccess = {
                navController.navigate(Home) {
                    popUpTo<Login> { inclusive = true }
                    launchSingleTop = true
                }
            },
            onContinueAsGuest = {
                navController.navigate(Home) {
                    popUpTo<Login> { inclusive = true }
                    launchSingleTop = true
                }
            }
        ) }
        composable<NameSetup> { backStackEntry ->
            val nameSetup = backStackEntry.toRoute<NameSetup>()
            NameSetupScreen(
                fromGoogle = nameSetup.fromGoogle,
                onComplete = { navController.navigate(Home) { popUpTo<NameSetup> { inclusive = true } } }
            )
        }
        composable<Home> { HomeScreen(
            onNavigateToCalendar = { navController.navigate(Calendar) },
            onNavigateToDayLog = { navController.navigate(DayLog(it)) },
            onNavigateToBreathing = { navController.navigate(BreathingMode) },
            onNavigateToPhaseCoach = { navController.navigate(PhaseCoach) }
        ) }
        composable<PhaseCoach> { PhaseCoachScreen() }
        composable<Calendar> { CalendarScreen(
            onNavigateToDayLog = { navController.navigate(DayLog(it)) }
        ) }
        composable<Wellness> { WellnessScreen() }
        composable<PartnerMode> { PartnerModeScreen() }
        composable<Remedies> { RemediesScreen(
            onNavigateToYoga = { navController.navigate(YogaFlow) }
        ) }
        composable<YogaFlow> { YogaFlowScreen(onExit = { navController.popBackStack() }) }
        composable<Journal> { JournalScreen() }
        composable<MoodMap> { MoodMapScreen() }
        composable<Insights> { InsightsScreen() }
        composable<Settings> { SettingsScreen(
            onNavigateToPayment = { navController.navigate(Payment) },
            onNavigateToReport = { navController.navigate(ReportExport) },
            onSignOut = { navController.navigate(Splash) { popUpTo<Settings> { inclusive = true } } }
        ) }
        composable<ReportExport> { ReportExportScreen() }
        composable<Wardrobe> { WardrobeScreen() }
        composable<Challenges> { ChallengesScreen() }
        composable<BreathingMode> { BreathingModeScreen(onExit = { navController.popBackStack() }) }
        composable<Payment> { PaymentScreen() }
        composable<TimeCapsule> { TimeCapsuleScreen(onNavigateBack = { navController.popBackStack() }) }
        composable<DayLog> { backStackEntry ->
            val dayLog = backStackEntry.toRoute<DayLog>()
            DayLogScreen(
                dateEpoch = dayLog.dateEpoch,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

