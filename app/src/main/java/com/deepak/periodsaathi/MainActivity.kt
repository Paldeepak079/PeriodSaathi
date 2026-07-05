package com.deepak.periodsaathi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.deepak.periodsaathi.data.datastore.UserPreferences
import com.deepak.periodsaathi.ui.navigation.BottomNavBar
import com.deepak.periodsaathi.ui.navigation.Calendar
import com.deepak.periodsaathi.ui.navigation.Community
import com.deepak.periodsaathi.ui.navigation.Home
import com.deepak.periodsaathi.ui.navigation.PeriodSaathiNavGraph
import com.deepak.periodsaathi.ui.navigation.Settings
import com.deepak.periodsaathi.ui.navigation.Wellness
import com.deepak.periodsaathi.ui.screens.payment.PaymentViewModel
import com.deepak.periodsaathi.ui.theme.FontOption
import com.deepak.periodsaathi.ui.theme.PeriodSaathiTheme
import com.deepak.periodsaathi.ui.theme.ThemeCategory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var userPreferences: UserPreferences
    @Inject lateinit var stripePaymentService: com.deepak.periodsaathi.data.repository.StripePaymentService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stripePaymentService.initialize(this)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            com.deepak.periodsaathi.ui.theme.AppThemeProvider {
                val navController = rememberNavController()
                val currentRoute by navController.currentBackStackEntryAsState()

                val mainScreenRoutes = setOf(
                    Home::class.qualifiedName,
                    Calendar::class.qualifiedName,
                    Wellness::class.qualifiedName,
                    Community::class.qualifiedName,
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
