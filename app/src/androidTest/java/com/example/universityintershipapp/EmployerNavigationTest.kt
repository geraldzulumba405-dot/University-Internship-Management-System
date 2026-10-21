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
class EmployerNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun waitForLoginScreen() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Enter your username or email").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun employerFlow_loginNavigatesToEmployerDashboard() {
        composeTestRule.onNodeWithText("Enter your username or email").performTextInput("employer@company.com")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("password123")
        composeTestRule.onNodeWithText("Log In").performClick()

        composeTestRule.onNodeWithText("Hire Top Student Talent").assertIsDisplayed()
        composeTestRule.onNodeWithText("Recruitment Overview").assertIsDisplayed()
    }

    @Test
    fun employerFlow_postInternship_navigatesToPostScreenAndBack() {
        composeTestRule.onNodeWithText("Enter your username or email").performTextInput("employer@company.com")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("password123")
        composeTestRule.onNodeWithText("Log In").performClick()

        composeTestRule.onNodeWithText("Post New Internship").performClick()
        composeTestRule.onNodeWithText("Post New Internship").assertIsDisplayed()

        composeTestRule.onNodeWithText("Post Internship").performClick()

        composeTestRule.onNodeWithText("Hire Top Student Talent").assertIsDisplayed()
    }

    @Test
    fun employerFlow_reviewApplicant_navigatesToApplicantReview() {
        composeTestRule.onNodeWithText("Enter your username or email").performTextInput("employer@company.com")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("password123")
        composeTestRule.onNodeWithText("Log In").performClick()

        composeTestRule.onNodeWithText("View Applicants (14)").performClick()

        composeTestRule.onNodeWithText("Applicant Review").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gerald Zulumba").assertIsDisplayed()
    }
}
