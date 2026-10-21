package com.example.universityintershipapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StudentNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun waitForLoginScreen() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Enter your username or email").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun studentFlow_loginNavigatesToStudentDashboard() {
        composeTestRule.onNodeWithText("Enter your username or email").performTextInput("student@unima.ac.mw")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("password123")
        composeTestRule.onNodeWithText("Log In").performClick()

        composeTestRule.onNodeWithText("Welcome back, Gerald Zulumba!", substring = true).assertIsDisplayed()
    }
}
