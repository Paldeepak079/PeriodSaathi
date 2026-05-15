package com.example.periodsaathi.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.periodsaathi.ui.screens.calendar.CalendarScreen
import com.example.periodsaathi.ui.screens.home.HomeScreen
import com.example.periodsaathi.ui.screens.login.LoginScreen
import com.example.periodsaathi.ui.screens.more.MoreScreen
import com.example.periodsaathi.ui.screens.splash.SplashScreen
import com.example.periodsaathi.ui.screens.wellness.WellnessScreen

@Composable
fun PeriodSaathiNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: Screen = Screen.Splash,
    modifier: Modifier = Modifier
) {
    var currentRoute by remember { mutableStateOf(startDestination) }

    val mainScreens = listOf(Screen.Home, Screen.Calendar, Screen.Wellness, Screen.More)

    val showBottomBar = currentRoute in mainScreens

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        currentRoute = screen
                        navController.navigate(screen) {
                            popUpTo(Screen.Home) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable<Screen.Splash> {
                    SplashScreen(
                        onNavigate = { screen ->
                            currentRoute = screen
                            navController.navigate(screen) {
                                popUpTo(Screen.Splash) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Screen.Login> {
                    LoginScreen(
                        onNavigate = { screen ->
                            currentRoute = screen
                            navController.navigate(screen) {
                                popUpTo(Screen.Login) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Screen.Home> {
                    HomeScreen(
                        onNavigate = { screen ->
                            currentRoute = screen
                            navController.navigate(screen)
                        }
                    )
                }

                composable<Screen.Calendar> {
                    CalendarScreen(
                        onNavigate = { screen ->
                            currentRoute = screen
                            navController.navigate(screen)
                        }
                    )
                }

                composable<Screen.Wellness> {
                    WellnessScreen(
                        onNavigate = { screen ->
                            currentRoute = screen
                            navController.navigate(screen)
                        }
                    )
                }

                composable<Screen.More> {
                    MoreScreen(
                        onNavigate = { screen ->
                            currentRoute = screen
                            navController.navigate(screen)
                        }
                    )
                }
            }
        }
    }
}