package com.deepak.periodsaathi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.deepak.periodsaathi.ui.components.FloatingSaathiButton
import com.deepak.periodsaathi.ui.navigation.BottomNavBar
import com.deepak.periodsaathi.ui.navigation.Calendar
import com.deepak.periodsaathi.ui.navigation.Chat
import com.deepak.periodsaathi.ui.navigation.Community
import com.deepak.periodsaathi.ui.navigation.Home
import com.deepak.periodsaathi.ui.navigation.PeriodSaathiNavGraph
import com.deepak.periodsaathi.ui.navigation.Settings
import com.deepak.periodsaathi.ui.navigation.Wellness
import com.deepak.periodsaathi.ui.theme.PeriodSaathiTheme
import dagger.hilt.android.AndroidEntryPoint
import io.sentry.Sentry
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var stripePaymentService: com.deepak.periodsaathi.data.repository.StripePaymentService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stripePaymentService.initialize(this)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        Sentry.captureMessage("Sentry integration test — PeriodSaathi v1.1.0")
        setContent {
            PeriodSaathiTheme {
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
                val isOnChatScreen = currentRoute?.destination?.route == Chat::class.qualifiedName

                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomNav,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it })
                        ) {
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        PeriodSaathiNavGraph(navController = navController)

                        if (!isOnChatScreen) {
                            FloatingSaathiButton(
                                onClick = { navController.navigate(Chat) },
                                modifier = Modifier.align(Alignment.BottomEnd)
                            )
                        }
                    }
                }
            }
        }
    }

}
