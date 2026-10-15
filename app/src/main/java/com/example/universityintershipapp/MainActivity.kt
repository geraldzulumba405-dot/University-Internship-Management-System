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
                var currentScreen by remember { mutableStateOf("splash") }
                var loginErrorMsg by remember { mutableStateOf<String?>(null) }

                if (currentScreen != "student_login" && currentScreen != "splash") {
                    BackHandler {
                        currentScreen = when (currentScreen) {
                            "register" -> "student_login"
                            "my_applications" -> "student_home"
                            "student_profile" -> "student_home"
                            "application_submitted" -> "student_home"
                            "submit_application" -> "student_application"
                            "student_application" -> "student_home"
                            "student_search" -> "student_home"
                            "employer_home" -> "student_login"
                            "student_home" -> "student_login"
                            else -> "student_login"
                        }
                    }
                }

                when (currentScreen) {
                    "splash" -> SplashScreen(
                        onSplashFinished = {
                            currentScreen = "student_login"
                        }
                    )
                    "student_login" -> LoginScreen(
                        errorMessage = loginErrorMsg,
                        onLoginSubmit = { email, _ ->
                            val trimmed = email.trim().lowercase()
                            when {
                                trimmed == "student@unima.ac.mw" || trimmed.endsWith("@unima.ac.mw") || trimmed.contains("student") -> {
                                    loginErrorMsg = null
                                    currentScreen = "student_home"
                                }
                                trimmed == "employer@company.com" || trimmed.endsWith("@company.com") || trimmed.contains("employer") || trimmed.contains("hiring") -> {
                                    loginErrorMsg = null
                                    currentScreen = "employer_home"
                                }
                                else -> {
                                    loginErrorMsg = "Unrecognized email or invalid test credentials. Use student@unima.ac.mw or employer@company.com"
                                }
                            }
                        },
                        onSignUpClick = {
                            loginErrorMsg = null
                            currentScreen = "register"
                        },
                    )
                    "register" -> RegisterScreen(
                        onBackClick = {
                            currentScreen = "student_login"
                        },
                        onLoginClick = {
                            currentScreen = "student_login"
                        },
                        onCreateAccountSuccess = {
                            currentScreen = "student_home"
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
                            currentScreen = "student_profile"
                        },
                    )
                    "employer_home" -> EmployerHomeScreen(
                        onPostInternshipClick = {
                            // Can route to post job or search
                        },
                        onViewApplicantsClick = { _ ->
                            // View applicants
                        },
                        onLogOutClick = {
                            loginErrorMsg = null
                            currentScreen = "student_login"
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
                        onProfileNavClick = {
                            currentScreen = "student_profile"
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
                            currentScreen = "student_profile"
                        },
                        onViewDetails = { _ ->
                            currentScreen = "student_application"
                        },
                        onPrepInterview = { _ ->
                            currentScreen = "student_application"
                        },
                    )
                    "student_profile" -> StudentProfileScreen(
                        onNavigateHome = {
                            currentScreen = "student_home"
                        },
                        onNavigateSearch = {
                            currentScreen = "student_search"
                        },
                        onNavigateApplications = {
                            currentScreen = "my_applications"
                        },
                        onLogOut = {
                            loginErrorMsg = null
                            currentScreen = "student_login"
                        },
                    )
                }
            }
        }
    }
}
