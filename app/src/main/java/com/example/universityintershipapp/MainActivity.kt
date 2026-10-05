package com.example.universityintershipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniversityIntershipAppTheme {
                var currentScreen by remember { mutableStateOf("home") }

                if (currentScreen != "home") {
                    BackHandler {
                        currentScreen = when (currentScreen) {
                            "my_applications" -> "student_home"
                            "application_submitted" -> "student_home"
                            "submit_application" -> "student_application"
                            "student_application" -> "student_home"
                            "student_search" -> "student_home"
                            "student_home" -> "home"
                            else -> "home"
                        }
                    }
                }

                when (currentScreen) {
                    "home" -> HomeScreen(
                        onGetStartedClick = { pathway ->
                            currentScreen = when (pathway) {
                                PortalPathway.STUDENT -> "student_login"
                                PortalPathway.EMPLOYER -> "employer_login"
                            }
                        },
                        onLogInClick = { pathway ->
                            currentScreen = when (pathway) {
                                PortalPathway.STUDENT -> "student_login"
                                PortalPathway.EMPLOYER -> "employer_login"
                            }
                        },
                    )
                    "student_login" -> LoginScreen(
                        onBackClick = {
                            currentScreen = "home"
                        },
                        onLoginSubmit = { _, _ ->
                            currentScreen = "student_home"
                        },
                    )
                    "employer_login" -> EmployerLoginScreen(
                        onBackClick = {
                            currentScreen = "home"
                        },
                    )
                    "student_home" -> StudentHomeScreen(
                        onSearchClick = {
                            currentScreen = "student_search"
                        },
                        onApplyClick = { _ ->
                            currentScreen = "student_application"
                        },
                        onApplicationsClick = {
                            currentScreen = "my_applications"
                        },
                        onProfileClick = {
                            currentScreen = "home"
                        },
                    )
                    "student_search" -> StudentSearchScreen(
                        onBackClick = {
                            currentScreen = "student_home"
                        },
                        onHomeNavClick = {
                            currentScreen = "student_home"
                        },
                        onApplicationsNavClick = {
                            currentScreen = "my_applications"
                        },
                        onApplyClick = { _ ->
                            currentScreen = "student_application"
                        },
                    )
                    "student_application" -> StudentApplicationScreen(
                        onBackClick = {
                            currentScreen = "student_home"
                        },
                        onOneTapApplyClick = {
                            currentScreen = "submit_application"
                        },
                    )
                    "submit_application" -> SubmitApplicationScreen(
                        onCloseClick = {
                            currentScreen = "student_application"
                        },
                        onSaveDraftClick = {
                            currentScreen = "student_application"
                        },
                        onSubmitApplicationClick = {
                            currentScreen = "application_submitted"
                        },
                    )
                    "application_submitted" -> ApplicationConfirmationScreen(
                        onDismissPreview = {
                            currentScreen = "my_applications"
                        },
                        onViewApplications = {
                            currentScreen = "my_applications"
                        },
                    )
                    "my_applications" -> MyApplicationsScreen(
                        onNavigateHome = {
                            currentScreen = "student_home"
                        },
                        onNavigateSearch = {
                            currentScreen = "student_search"
                        },
                        onNavigateProfile = {
                            currentScreen = "home"
                        },
                        onViewDetails = { _ ->
                            currentScreen = "student_application"
                        },
                        onPrepInterview = { _ ->
                            currentScreen = "student_application"
                        },
                    )
                }
            }
        }
    }
}
