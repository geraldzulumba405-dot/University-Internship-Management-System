package com.example.universityintershipapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysMainFieldsAndText() {
        composeTestRule.setContent {
            LoginScreen()
        }

        composeTestRule.onNodeWithText("UniIntern").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log in to your account to continue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Remember username").assertIsDisplayed()
        composeTestRule.onNodeWithText("Forgot Password?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account? ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign Up").assertIsDisplayed()
    }

    @Test
    fun loginScreen_emptyEmail_displaysValidationError() {
        composeTestRule.setContent {
            LoginScreen()
        }

        composeTestRule.onNodeWithText("Log In").performClick()
        composeTestRule.onNodeWithText("Please enter your email address.").assertIsDisplayed()
    }

    @Test
    fun loginScreen_emptyPassword_displaysValidationError() {
        composeTestRule.setContent {
            LoginScreen()
        }

        composeTestRule.onNodeWithText("Enter your username or email").performTextInput("student@unima.ac.mw")
        composeTestRule.onNodeWithText("Log In").performClick()
        composeTestRule.onNodeWithText("Please enter your password.").assertIsDisplayed()
    }

    @Test
    fun loginScreen_recognizedStudentEmail_invokesCallback() {
        var submittedEmail = ""
        var submittedPassword = ""

        composeTestRule.setContent {
            LoginScreen(
                onLoginSubmit = { email, pass, _, _ ->
                    submittedEmail = email
                    submittedPassword = pass
                }
            )
        }

        composeTestRule.onNodeWithText("Enter your username or email").performTextInput("student@unima.ac.mw")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("password123")
        composeTestRule.onNodeWithText("Log In").performClick()

        assertEquals("student@unima.ac.mw", submittedEmail)
        assertEquals("password123", submittedPassword)
    }

    @Test
    fun loginScreen_recognizedEmployerEmail_invokesCallback() {
        var submittedEmail = ""

        composeTestRule.setContent {
            LoginScreen(
                onLoginSubmit = { email, _, _, _ ->
                    submittedEmail = email
                }
            )
        }

        composeTestRule.onNodeWithText("Enter your username or email").performTextInput("employer@company.com")
        composeTestRule.onNodeWithText("Enter your password").performTextInput("password123")
        composeTestRule.onNodeWithText("Log In").performClick()

        assertEquals("employer@company.com", submittedEmail)
    }

    @Test
    fun loginScreen_rememberUsername_canBeToggled() {
        composeTestRule.setContent {
            LoginScreen()
        }

        composeTestRule.onNodeWithText("Remember username").performClick()
        composeTestRule.onNodeWithText("Remember username").performClick()
    }

    @Test
    fun loginScreen_signUpAndForgotPassword_invokeCallbacks() {
        var signUpClicked = false
        var forgotPasswordClicked = false

        composeTestRule.setContent {
            LoginScreen(
                onRegisterClick = { signUpClicked = true },
                onForgotPasswordClick = { forgotPasswordClicked = true }
            )
        }

        composeTestRule.onNodeWithText("Sign Up").performClick()
        assertTrue(signUpClicked)

        composeTestRule.onNodeWithText("Forgot Password?").performClick()
        assertTrue(forgotPasswordClicked)
    }
}
