package com.deepak.periodsaathi.ui.screens.challenges

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChallengesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysTitle() {
        composeTestRule.setContent {
            ChallengesScreen()
        }
        composeTestRule.onNodeWithText("Challenges").assertExists()
    }

    @Test
    fun showsAvailableChallenges() {
        composeTestRule.setContent {
            ChallengesScreen()
        }
        composeTestRule.onNodeWithText("Available").assertExists()
    }
}
