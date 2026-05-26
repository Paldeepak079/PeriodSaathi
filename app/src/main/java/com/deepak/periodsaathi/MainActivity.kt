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
import com.deepak.periodsaathi.ui.theme.PeriodSaathiTheme
import com.razorpay.Checkout
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            // Read dark mode preference — drives 500ms cross-fade in PeriodSaathiTheme
            val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = false)

            PeriodSaathiTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val currentRoute by navController.currentBackStackEntryAsState()

                // Routes that show the bottom navigation bar
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

    @Deprecated("Razorpay legacy callback")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Checkout.RZP_REQUEST_CODE && data != null && data.hasExtra("response")) {
            try {
                val response = JSONObject(data.getStringExtra("response"))
                val vm = PaymentViewModel.currentInstance
                val productId = vm?.pendingProductId ?: return
                if (resultCode == Activity.RESULT_OK && response.has("razorpay_payment_id")) {
                    vm.onPaymentSuccess(productId, response.getString("razorpay_payment_id"))
                } else {
                    val code = response.optInt("code", -1)
                    val desc = response.optString("description", "Payment failed")
                    vm.onPaymentError(code, desc)
                }
            } catch (_: Exception) { }
        }
    }
}
