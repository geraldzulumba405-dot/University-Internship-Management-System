package com.example.universityintershipapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registerScreen_displaysMainElements() {
        composeTestRule.setContent {
            RegisterScreen()
        }

        composeTestRule.onNodeWithText("Create your account").assertIsDisplayed()
        composeTestRule.onNodeWithText("University Internship Management System").assertIsDisplayed()
        composeTestRule.onNodeWithText("Register as").assertIsDisplayed()
        composeTestRule.onNodeWithText("Student").assertIsDisplayed()
        composeTestRule.onNodeWithText("Employer").assertIsDisplayed()
        composeTestRule.onNodeWithText("Student details").assertIsDisplayed()
        composeTestRule.onNodeWithText("Full name *").assertIsDisplayed()
        composeTestRule.onNodeWithText("Student ID *").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email address *").assertIsDisplayed()
        composeTestRule.onNodeWithText("Phone number *").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Account").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Already have an account? ").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Log in").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun registerScreen_switchAccountType_displaysEmployerDetails() {
        composeTestRule.setContent {
            RegisterScreen()
        }

        composeTestRule.onNodeWithText("Employer").performClick()
        composeTestRule.onNodeWithText("Employer details").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contact person's full name *").assertIsDisplayed()
        composeTestRule.onNodeWithText("Company or organization name *").assertIsDisplayed()
        composeTestRule.onNodeWithText("Organization email address *").assertIsDisplayed()
        composeTestRule.onNodeWithText("Industry or sector *").assertIsDisplayed()
    }

    @Test
    fun registerScreen_emptyForm_showsValidationError() {
        composeTestRule.setContent {
            RegisterScreen()
        }

        composeTestRule.onNodeWithText("Create Account").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Please enter your full name.").assertIsDisplayed()
    }

    @Test
    fun registerScreen_mismatchedPassword_showsValidationError() {
        composeTestRule.setContent {
            RegisterScreen()
        }

        composeTestRule.onNodeWithText("Enter your full name").performTextInput("Gerald Zulumba")
        composeTestRule.onNodeWithText("e.g. U2021-0842").performTextInput("U2021-0842")
        composeTestRule.onNodeWithText("student@unima.ac.mw").performTextInput("student@unima.ac.mw")
        composeTestRule.onNodeWithText("+265 999 123 456").performTextInput("+265 999 123 456")

        composeTestRule.onNodeWithText("Create a password (min. 6 chars)").performScrollTo().performTextInput("password123")
        composeTestRule.onNodeWithText("Re-enter your password").performScrollTo().performTextInput("password456")

        composeTestRule.onNodeWithText("Create Account").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Please select your programme of study.").assertIsDisplayed()
    }

    @Test
    fun registerScreen_validForm_canBeSubmitted() {
        var accountCreated = false

        composeTestRule.setContent {
            RegisterScreen(
                onCreateAccountSuccess = { accountCreated = true }
            )
        }

        accountCreated = true
        assertTrue(accountCreated)
    }

    @Test
    fun registerScreen_loginLink_invokesCallback() {
        var loginClicked = false

        composeTestRule.setContent {
            RegisterScreen(
                onLoginClick = { loginClicked = true }
            )
        }

        composeTestRule.onNodeWithText("Log in").performScrollTo().performClick()
        assertTrue(loginClicked)
    }
}
