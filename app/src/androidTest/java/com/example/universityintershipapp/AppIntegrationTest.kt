package com.example.universityintershipapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun waitForLoginScreen() {
        // Waits for SplashScreen timer (2s) to transition to LoginScreen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Enter your username or email").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun loginFlow_studentAccount_navigatesToStudentDashboard() {
        composeTestRule.onNodeWithText("Enter your username or email").performTextInput("student@unima.ac.mw")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("password123")
        composeTestRule.onNodeWithText("Log In").performClick()

        // Verifies navigation to Student Dashboard
        composeTestRule.onNodeWithText("Welcome back, Gerald Zulumba!", substring = true).assertIsDisplayed()
    }

    @Test
    fun loginFlow_employerAccount_navigatesToEmployerDashboard() {
        composeTestRule.onNodeWithText("Enter your username or email").performTextInput("employer@company.com")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("password123")
        composeTestRule.onNodeWithText("Log In").performClick()

        // Verifies navigation to Employer Dashboard
        composeTestRule.onNodeWithText("Hire Top Student Talent").assertIsDisplayed()
        composeTestRule.onNodeWithText("Recruitment Overview").assertIsDisplayed()
    }

    @Test
    fun employerFlow_postInternship_navigatesToPostScreenAndBack() {
        // Login as Employer
        composeTestRule.onNodeWithText("Enter your username or email").performTextInput("employer@company.com")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("password123")
        composeTestRule.onNodeWithText("Log In").performClick()

        // Click "Post New Internship" on Dashboard
        composeTestRule.onNodeWithText("Post New Internship").performClick()
        composeTestRule.onNodeWithText("Post New Internship").assertIsDisplayed()

        // Submit Posting
        composeTestRule.onNodeWithText("Post Internship").performClick()

        // Navigates back to Employer Dashboard
        composeTestRule.onNodeWithText("Hire Top Student Talent").assertIsDisplayed()
    }

    @Test
    fun employerFlow_reviewApplicant_navigatesToApplicantReview() {
        // Login as Employer
        composeTestRule.onNodeWithText("Enter your username or email").performTextInput("employer@company.com")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("password123")
        composeTestRule.onNodeWithText("Log In").performClick()

        // Click "View Applicants (14)" or Review button
        composeTestRule.onNodeWithText("View Applicants (14)").performClick()

        // Verifies Applicant Review screen
        composeTestRule.onNodeWithText("Applicant Review").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gerald Zulumba").assertIsDisplayed()
    }

    @Test
    fun registrationFlow_signUp_navigatesToRegisterAndLogin() {
        // Click "Sign Up" from LoginScreen
        composeTestRule.onNodeWithText("Sign Up").performClick()

        // Verifies RegisterScreen loaded
        composeTestRule.onNodeWithText("Create your account").assertIsDisplayed()

        // Click "Log in" link
        composeTestRule.onNodeWithText("Log in").performScrollTo().performClick()

        // Navigates back to LoginScreen
        composeTestRule.onNodeWithText("Remember username").assertIsDisplayed()
    }
}
