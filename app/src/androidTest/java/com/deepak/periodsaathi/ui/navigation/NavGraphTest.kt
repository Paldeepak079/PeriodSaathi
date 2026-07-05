package com.deepak.periodsaathi.ui.navigation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavGraphTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun navGraphStartsAtSplash() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            PeriodSaathiNavGraph(navController = navController)
        }
    }
}
