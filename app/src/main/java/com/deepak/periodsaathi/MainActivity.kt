package com.deepak.periodsaathi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.deepak.periodsaathi.ui.navigation.BottomNavBar
import com.deepak.periodsaathi.ui.navigation.Calendar
import com.deepak.periodsaathi.ui.navigation.Home
import com.deepak.periodsaathi.ui.navigation.PeriodSaathiNavGraph
import com.deepak.periodsaathi.ui.navigation.Settings
import com.deepak.periodsaathi.ui.navigation.Wellness
import com.deepak.periodsaathi.ui.theme.PeriodSaathiTheme

import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.screens.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PeriodSaathiTheme {
                val navController = rememberNavController()
                val currentRoute by navController.currentBackStackEntryAsState()

                val mainScreenRoutes = setOf(
                    Home::class.qualifiedName,
                    Calendar::class.qualifiedName,
                    Wellness::class.qualifiedName,
                    Settings::class.qualifiedName
                )

                val showBottomNav = currentRoute?.destination?.route in mainScreenRoutes

                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomNav,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it })
                        ) {
                            BottomNavBar(navController = navController)
                        }
                    },
                    contentWindowInsets = WindowInsets.navigationBars
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        PeriodSaathiNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}

