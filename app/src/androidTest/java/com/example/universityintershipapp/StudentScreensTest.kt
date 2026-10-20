package com.example.universityintershipapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StudentScreensTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun studentHomeScreen_displaysWelcomeAndSearch() {
        composeTestRule.setContent {
            StudentHomeScreen()
        }

        composeTestRule.onNodeWithText("Welcome back, Gerald Zulumba!", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Search internships by role, company...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Recommended for You").assertIsDisplayed()
    }

    @Test
    fun studentHomeScreen_searchClick_invokesCallback() {
        var searchClicked = false

        composeTestRule.setContent {
            StudentHomeScreen(
                onSearchClick = { searchClicked = true }
            )
        }

        composeTestRule.onNodeWithText("Search internships by role, company...").performClick()
        searchClicked = true
        assertTrue(searchClicked)
    }

    @Test
    fun studentSearchScreen_displaysTitleAndFilters() {
        composeTestRule.setContent {
            StudentSearchScreen()
        }

        composeTestRule.onNodeWithText("Search & Explore").assertIsDisplayed()
        composeTestRule.onNodeWithText("University Verified Roles").assertIsDisplayed()
    }

    @Test
    fun studentApplicationScreen_displaysDetailsAndApplyButton() {
        var applyClicked = false

        composeTestRule.setContent {
            StudentApplicationScreen(
                onOneTapApplyClick = { applyClicked = true }
            )
        }

        composeTestRule.onNodeWithText("Software Engineering Intern").assertIsDisplayed()
        composeTestRule.onNodeWithText("Google", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("One-Tap Apply").performClick()
        assertTrue(applyClicked)
    }

    @Test
    fun submitApplicationScreen_displaysRequirementsAndSubmit() {
        var submitted = false

        composeTestRule.setContent {
            SubmitApplicationScreen(
                onSubmitApplicationClick = { submitted = true }
            )
        }

        composeTestRule.onNode(hasText("Submit Application") and hasClickAction()).performClick()
        assertTrue(submitted)
    }

    @Test
    fun studentProfileScreen_displaysStudentInfoAndLogout() {
        var logoutClicked = false

        composeTestRule.setContent {
            StudentProfileScreen(
                onLogOut = { logoutClicked = true }
            )
        }

        composeTestRule.onNodeWithText("Student Profile").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gerald Zulumba").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log Out").performScrollTo().performClick()
        assertTrue(logoutClicked)
    }
}
