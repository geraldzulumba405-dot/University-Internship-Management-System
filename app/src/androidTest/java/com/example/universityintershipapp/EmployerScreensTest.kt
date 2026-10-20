package com.example.universityintershipapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmployerScreensTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun employerHomeScreen_displaysMetricsAndListings() {
        composeTestRule.setContent {
            EmployerHomeScreen()
        }

        composeTestRule.onNodeWithText("Hire Top Student Talent").assertIsDisplayed()
        composeTestRule.onNodeWithText("Post New Internship").assertIsDisplayed()
        composeTestRule.onNodeWithText("Recruitment Overview").assertIsDisplayed()
        composeTestRule.onNodeWithText("Active Postings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Applications").assertIsDisplayed()
        composeTestRule.onNodeWithText("Interviews").assertIsDisplayed()
        composeTestRule.onNodeWithText("Software Engineering Intern").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Recent Verified Applicants").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Gerald Zulumba").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Alex Chen").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun employerHomeScreen_postInternshipClick_invokesCallback() {
        var postClicked = false

        composeTestRule.setContent {
            EmployerHomeScreen(
                onPostInternshipClick = { postClicked = true }
            )
        }

        composeTestRule.onNodeWithText("Post New Internship").performClick()
        assertTrue(postClicked)
    }

    @Test
    fun employerHomeScreen_reviewApplicant_invokesCallbackWithId() {
        var reviewedApplicantId = ""

        composeTestRule.setContent {
            EmployerHomeScreen(
                onViewApplicantsClick = { id ->
                    reviewedApplicantId = id
                }
            )
        }

        composeTestRule.onNodeWithText("View Applicants (14)").performScrollTo().performClick()
        assertEquals("SE-01", reviewedApplicantId)
    }

    @Test
    fun postInternshipScreen_displaysFormAndSubmits() {
        var postSubmitted = false

        composeTestRule.setContent {
            PostInternshipScreen(
                onPostSuccess = { postSubmitted = true }
            )
        }

        composeTestRule.onNodeWithText("Post New Internship").assertIsDisplayed()
        composeTestRule.onNodeWithText("Role Basics").assertIsDisplayed()
        composeTestRule.onNodeWithText("Role Description").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Required Skills & Competencies").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Application Requirements").performScrollTo().assertIsDisplayed()

        composeTestRule.onNodeWithText("Post Internship").performClick()
        assertTrue(postSubmitted)
    }

    @Test
    fun employerApplicationReview_displaysCandidateDetailsAndActions() {
        composeTestRule.setContent {
            EmployerApplicationReview()
        }

        composeTestRule.onNodeWithText("Applicant Review").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gerald Zulumba").assertIsDisplayed()
        composeTestRule.onNodeWithText("VERIFIED COMPETENCIES").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Accept").assertExists()
        composeTestRule.onNodeWithText("Reject").assertExists()
        composeTestRule.onNodeWithText("Direct Message Gerald").assertExists()
    }

    @Test
    fun employerProfileScreen_displaysCompanyInfoAndMetrics() {
        composeTestRule.setContent {
            EmployerProfileScreen()
        }

        composeTestRule.onNodeWithText("Company Profile").assertIsDisplayed()
        composeTestRule.onNodeWithText("Apex Tech Solutions").assertIsDisplayed()
        composeTestRule.onNodeWithText("Recruitment Team & POC").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Chikondi Mapira").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Internship Preferences").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Log Out of Employer Portal").performScrollTo().assertIsDisplayed()
    }
}
