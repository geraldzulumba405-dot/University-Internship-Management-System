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

enum class AppScreen {
    SPLASH,
    LOGIN,
    REGISTER,
    STUDENT_HOME,
    STUDENT_SEARCH,
    STUDENT_APPLICATION,
    SUBMIT_APPLICATION,
    APPLICATION_CONFIRMATION,
    MY_APPLICATIONS,
    STUDENT_PROFILE,
    EMPLOYER_HOME,
    POST_INTERNSHIP,
    EMPLOYER_APPLICATION_REVIEW,
    EMPLOYER_RECEIVED_APPLICATIONS,
    EMPLOYER_PROFILE,
    ADMIN_DASHBOARD,
    ADMIN_EMPLOYERS,
    ADMIN_INTERNSHIPS,
    ADMIN_REPORTS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniversityIntershipAppTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

                BackHandler(enabled = currentScreen != AppScreen.SPLASH && currentScreen != AppScreen.LOGIN) {
                    when (currentScreen) {
                        AppScreen.REGISTER -> currentScreen = AppScreen.LOGIN
                        AppScreen.STUDENT_SEARCH, AppScreen.STUDENT_APPLICATION, AppScreen.MY_APPLICATIONS, AppScreen.STUDENT_PROFILE -> currentScreen = AppScreen.STUDENT_HOME
                        AppScreen.EMPLOYER_HOME -> currentScreen = AppScreen.LOGIN
                        AppScreen.POST_INTERNSHIP, AppScreen.EMPLOYER_RECEIVED_APPLICATIONS, AppScreen.EMPLOYER_APPLICATION_REVIEW, AppScreen.EMPLOYER_PROFILE -> currentScreen = AppScreen.EMPLOYER_HOME
                        AppScreen.ADMIN_DASHBOARD -> currentScreen = AppScreen.LOGIN
                        AppScreen.ADMIN_EMPLOYERS, AppScreen.ADMIN_INTERNSHIPS, AppScreen.ADMIN_REPORTS -> currentScreen = AppScreen.ADMIN_DASHBOARD
                        AppScreen.SUBMIT_APPLICATION -> currentScreen = AppScreen.STUDENT_APPLICATION
                        AppScreen.APPLICATION_CONFIRMATION -> currentScreen = AppScreen.MY_APPLICATIONS
                        else -> currentScreen = AppScreen.LOGIN
                    }
                }

                when (currentScreen) {
                    AppScreen.SPLASH -> {
                        SplashScreen(
                            onSplashFinished = {
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }
                    AppScreen.LOGIN -> {
                        LoginScreen(
                            onBackClick = {},
                            onLoginSubmit = { email, _, role ->
                                val cleanEmail = email.trim().lowercase()
                                if (role == "admin" || cleanEmail == "admin@gmail.com" || cleanEmail.contains("admin")) {
                                    currentScreen = AppScreen.ADMIN_DASHBOARD
                                } else if (role == "employer" || cleanEmail == "employer@company.com" || cleanEmail.contains("employer") || cleanEmail.contains("company")) {
                                    currentScreen = AppScreen.EMPLOYER_HOME
                                } else {
                                    currentScreen = AppScreen.STUDENT_HOME
                                }
                            },
                            onRegisterClick = {
                                currentScreen = AppScreen.REGISTER
                            }
                        )
                    }
                    AppScreen.REGISTER -> {
                        RegisterScreen(
                            onBackClick = { currentScreen = AppScreen.LOGIN },
                            onLoginClick = { currentScreen = AppScreen.LOGIN },
                            onCreateAccountSuccessWithRole = { role ->
                                if (role == "admin") {
                                    currentScreen = AppScreen.ADMIN_DASHBOARD
                                } else if (role == "employer") {
                                    currentScreen = AppScreen.EMPLOYER_HOME
                                } else {
                                    currentScreen = AppScreen.STUDENT_HOME
                                }
                            },
                            onCreateAccountSuccess = {
                                if (currentScreen == AppScreen.REGISTER) {
                                    currentScreen = AppScreen.STUDENT_HOME
                                }
                            }
                        )
                    }
                    AppScreen.STUDENT_HOME -> {
                        StudentHomeScreen(
                            onSearchClick = { currentScreen = AppScreen.STUDENT_SEARCH },
                            onApplicationsClick = { currentScreen = AppScreen.MY_APPLICATIONS },
                            onProfileClick = { currentScreen = AppScreen.STUDENT_PROFILE },
                            onApplyClick = { currentScreen = AppScreen.STUDENT_APPLICATION }
                        )
                    }
                    AppScreen.STUDENT_SEARCH -> {
                        StudentSearchScreen(
                            onBackClick = { currentScreen = AppScreen.STUDENT_HOME },
                            onHomeNavClick = { currentScreen = AppScreen.STUDENT_HOME },
                            onApplicationsNavClick = { currentScreen = AppScreen.MY_APPLICATIONS },
                            onProfileNavClick = { currentScreen = AppScreen.STUDENT_PROFILE },
                            onApplyClick = { currentScreen = AppScreen.STUDENT_APPLICATION }
                        )
                    }
                    AppScreen.STUDENT_APPLICATION -> {
                        StudentApplicationScreen(
                            onBackClick = { currentScreen = AppScreen.STUDENT_HOME },
                            onOneTapApplyClick = { currentScreen = AppScreen.SUBMIT_APPLICATION }
                        )
                    }
                    AppScreen.SUBMIT_APPLICATION -> {
                        SubmitApplicationScreen(
                            onCloseClick = { currentScreen = AppScreen.STUDENT_APPLICATION },
                            onSaveDraftClick = { currentScreen = AppScreen.MY_APPLICATIONS },
                            onSubmitApplicationClick = { currentScreen = AppScreen.APPLICATION_CONFIRMATION }
                        )
                    }
                    AppScreen.APPLICATION_CONFIRMATION -> {
                        ApplicationConfirmationScreen(
                            onDismissPreview = { currentScreen = AppScreen.STUDENT_HOME },
                            onViewApplications = { currentScreen = AppScreen.MY_APPLICATIONS }
                        )
                    }
                    AppScreen.MY_APPLICATIONS -> {
                        MyApplicationsScreen(
                            onNavigateHome = { currentScreen = AppScreen.STUDENT_HOME },
                            onNavigateSearch = { currentScreen = AppScreen.STUDENT_SEARCH },
                            onNavigateProfile = { currentScreen = AppScreen.STUDENT_PROFILE }
                        )
                    }
                    AppScreen.STUDENT_PROFILE -> {
                        StudentProfileScreen(
                            onNavigateHome = { currentScreen = AppScreen.STUDENT_HOME },
                            onNavigateSearch = { currentScreen = AppScreen.STUDENT_SEARCH },
                            onNavigateApplications = { currentScreen = AppScreen.MY_APPLICATIONS },
                            onLogOut = { currentScreen = AppScreen.LOGIN }
                        )
                    }
                    AppScreen.EMPLOYER_HOME -> {
                        EmployerHomeScreen(
                            onPostInternshipClick = { currentScreen = AppScreen.POST_INTERNSHIP },
                            onViewApplicantsClick = { currentScreen = AppScreen.EMPLOYER_APPLICATION_REVIEW },
                            onNavigateApplicants = { currentScreen = AppScreen.EMPLOYER_RECEIVED_APPLICATIONS },
                            onNavigateProfile = { currentScreen = AppScreen.EMPLOYER_PROFILE },
                            onLogOutClick = { currentScreen = AppScreen.LOGIN }
                        )
                    }
                    AppScreen.POST_INTERNSHIP -> {
                        PostInternshipScreen(
                            onCloseClick = { currentScreen = AppScreen.EMPLOYER_HOME },
                            onPostSuccess = { currentScreen = AppScreen.EMPLOYER_HOME }
                        )
                    }
                    AppScreen.EMPLOYER_RECEIVED_APPLICATIONS -> {
                        EmployerReceivedApplications(
                            onApplicantClick = { currentScreen = AppScreen.EMPLOYER_APPLICATION_REVIEW },
                            onNavigateHome = { currentScreen = AppScreen.EMPLOYER_HOME },
                            onNavigatePostJob = { currentScreen = AppScreen.POST_INTERNSHIP },
                            onNavigateApplications = { currentScreen = AppScreen.EMPLOYER_RECEIVED_APPLICATIONS },
                            onNavigateProfile = { currentScreen = AppScreen.EMPLOYER_PROFILE }
                        )
                    }
                    AppScreen.EMPLOYER_APPLICATION_REVIEW -> {
                        EmployerApplicationReview(
                            onBackClick = { currentScreen = AppScreen.EMPLOYER_HOME },
                            onNavigateHome = { currentScreen = AppScreen.EMPLOYER_HOME },
                            onNavigatePostJob = { currentScreen = AppScreen.POST_INTERNSHIP },
                            onNavigateApplications = { currentScreen = AppScreen.EMPLOYER_RECEIVED_APPLICATIONS },
                            onNavigateProfile = { currentScreen = AppScreen.EMPLOYER_PROFILE }
                        )
                    }
                    AppScreen.EMPLOYER_PROFILE -> {
                        EmployerProfileScreen(
                            onNavigateHome = { currentScreen = AppScreen.EMPLOYER_HOME },
                            onNavigateInternships = { currentScreen = AppScreen.POST_INTERNSHIP },
                            onNavigateApplications = { currentScreen = AppScreen.EMPLOYER_RECEIVED_APPLICATIONS },
                            onNavigateProfile = { currentScreen = AppScreen.EMPLOYER_PROFILE },
                            onLogOutClick = { currentScreen = AppScreen.LOGIN }
                        )
                    }
                    AppScreen.ADMIN_DASHBOARD -> {
                        AdminDashboardScreen(
                            onNavigateEmployers = { currentScreen = AppScreen.ADMIN_EMPLOYERS },
                            onNavigateInternships = { currentScreen = AppScreen.ADMIN_INTERNSHIPS },
                            onNavigateReports = { currentScreen = AppScreen.ADMIN_REPORTS },
                            onLogOut = { currentScreen = AppScreen.LOGIN }
                        )
                    }
                    AppScreen.ADMIN_EMPLOYERS -> {
                        AdminEmployerManagementScreen(
                            onNavigateDashboard = { currentScreen = AppScreen.ADMIN_DASHBOARD },
                            onNavigateInternships = { currentScreen = AppScreen.ADMIN_INTERNSHIPS },
                            onNavigateReports = { currentScreen = AppScreen.ADMIN_REPORTS }
                        )
                    }
                    AppScreen.ADMIN_INTERNSHIPS -> {
                        AdminInternshipManagementScreen(
                            onNavigateDashboard = { currentScreen = AppScreen.ADMIN_DASHBOARD },
                            onNavigateEmployers = { currentScreen = AppScreen.ADMIN_EMPLOYERS },
                            onNavigateReports = { currentScreen = AppScreen.ADMIN_REPORTS }
                        )
                    }
                    AppScreen.ADMIN_REPORTS -> {
                        AdminReportsScreen(
                            onNavigateDashboard = { currentScreen = AppScreen.ADMIN_DASHBOARD },
                            onNavigateEmployers = { currentScreen = AppScreen.ADMIN_EMPLOYERS },
                            onNavigateInternships = { currentScreen = AppScreen.ADMIN_INTERNSHIPS },
                            onLogOut = { currentScreen = AppScreen.LOGIN }
                        )
                    }
                }
            }
        }
    }
}
