package com.example.universityintershipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
    ADMIN_REPORTS,
    NOTIFICATIONS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniversityIntershipAppTheme {
                val scope = rememberCoroutineScope()
                var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
                var loggedInStudentName by remember { mutableStateOf("Gerald Zulumba") }
                var loggedInStudentEmail by remember { mutableStateOf("gzulumba@unima.ac.mw") }
                var loggedInStudentId by remember { mutableStateOf("BSC-INF-18-23") }
                var loggedInProgramme by remember { mutableStateOf("BSc Computer Science & Software Engineering") }
                var loggedInYearOfStudy by remember { mutableStateOf("Year 4") }

                var loggedInEmployerCompanyName by remember { mutableStateOf("Telecom Networks Malawi") }
                var loggedInEmployerContactPerson by remember { mutableStateOf("Blessings Phiri") }
                var loggedInEmployerEmail by remember { mutableStateOf("careers@tnm.mw") }
                var loggedInEmployerIndustry by remember { mutableStateOf("Telecommunications & Digital Services") }
                var loggedInEmployerLocation by remember { mutableStateOf("Blantyre, Malawi") }

                var selectedTitle by remember { mutableStateOf("Software Engineering Intern") }
                var selectedCompany by remember { mutableStateOf("Google") }
                var selectedLocation by remember { mutableStateOf("New York, USA") }

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
                        AppScreen.NOTIFICATIONS -> currentScreen = AppScreen.STUDENT_HOME
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
                            onLoginSubmit = { email, _, role, fullName ->
                                val cleanEmail = email.trim().lowercase()
                                if (role == "admin" || cleanEmail.contains("admin")) {
                                    currentScreen = AppScreen.ADMIN_DASHBOARD
                                } else if (role == "employer" || cleanEmail.contains("employer") || cleanEmail.contains("company")) {
                                    loggedInEmployerEmail = cleanEmail
                                    loggedInEmployerContactPerson = if (fullName.isNotBlank()) fullName else AuthRepository.formatNameFromEmail(cleanEmail)
                                    loggedInEmployerCompanyName = AuthRepository.formatCompanyNameFromEmail(cleanEmail)
                                    currentScreen = AppScreen.EMPLOYER_HOME
                                } else {
                                    loggedInStudentEmail = cleanEmail
                                    loggedInStudentName = if (fullName.isNotBlank()) fullName else AuthRepository.formatNameFromEmail(cleanEmail)
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
                            onCreateAccountSuccessWithDetails = { role, name, email, studentId, programme, year ->
                                if (role == "employer") {
                                    if (name.isNotBlank()) loggedInEmployerContactPerson = name
                                    if (email.isNotBlank()) loggedInEmployerEmail = email
                                } else {
                                    if (name.isNotBlank()) loggedInStudentName = name
                                    if (email.isNotBlank()) loggedInStudentEmail = email
                                    if (studentId.isNotBlank()) loggedInStudentId = studentId
                                    if (programme.isNotBlank()) loggedInProgramme = programme
                                    if (year.isNotBlank()) loggedInYearOfStudy = year
                                }
                            },
                            onCreateAccountSuccessWithRole = { role ->
                                currentScreen = AppScreen.LOGIN
                            },
                            onCreateAccountSuccess = {
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }
                    AppScreen.STUDENT_HOME -> {
                        StudentHomeScreen(
                            studentName = loggedInStudentName,
                            onSearchClick = { currentScreen = AppScreen.STUDENT_SEARCH },
                            onViewAllRecommended = { currentScreen = AppScreen.STUDENT_SEARCH },
                            onApplicationsClick = { currentScreen = AppScreen.MY_APPLICATIONS },
                            onProfileClick = { currentScreen = AppScreen.STUDENT_PROFILE },
                            onNotificationClick = { currentScreen = AppScreen.NOTIFICATIONS },
                            onApplyClick = { title, company, location ->
                                selectedTitle = title
                                selectedCompany = company
                                selectedLocation = location
                                currentScreen = AppScreen.STUDENT_APPLICATION
                            }
                        )
                    }
                    AppScreen.STUDENT_SEARCH -> {
                        StudentInternshipScreen(
                            onBackClick = { currentScreen = AppScreen.STUDENT_HOME },
                            onHomeNavClick = { currentScreen = AppScreen.STUDENT_HOME },
                            onApplicationsNavClick = { currentScreen = AppScreen.MY_APPLICATIONS },
                            onProfileNavClick = { currentScreen = AppScreen.STUDENT_PROFILE },
                            onApplyClick = { title, company, location ->
                                selectedTitle = title
                                selectedCompany = company
                                selectedLocation = location
                                currentScreen = AppScreen.STUDENT_APPLICATION
                            }
                        )
                    }
                    AppScreen.STUDENT_APPLICATION -> {
                        StudentApplicationScreen(
                            title = selectedTitle,
                            company = selectedCompany,
                            location = selectedLocation,
                            onBackClick = { currentScreen = AppScreen.STUDENT_HOME },
                            onOneTapApplyClick = { currentScreen = AppScreen.SUBMIT_APPLICATION }
                        )
                    }
                    AppScreen.SUBMIT_APPLICATION -> {
                        SubmitApplicationScreen(
                            onCloseClick = { currentScreen = AppScreen.STUDENT_APPLICATION },
                            onSaveDraftClick = { currentScreen = AppScreen.MY_APPLICATIONS },
                            onSubmitApplicationClick = {
                                val uid = FirebaseAuth.getInstance().currentUser?.uid
                                if (uid != null) {
                                    val appData = hashMapOf(
                                        "studentUid" to uid,
                                        "internshipTitle" to selectedTitle,
                                        "company" to selectedCompany,
                                        "location" to selectedLocation,
                                        "status" to "Under Review",
                                        "createdAt" to System.currentTimeMillis()
                                    )
                                    FirebaseFirestore.getInstance().collection("applications").add(appData)

                                    scope.launch {
                                        try {
                                            val employersSnap = FirebaseFirestore.getInstance().collection("users")
                                                .whereEqualTo("role", "employer")
                                                .get()
                                                .await()
                                            val employerDoc = employersSnap.documents.find {
                                                (it.getString("companyName") ?: "").equals(selectedCompany, ignoreCase = true)
                                            } ?: employersSnap.documents.firstOrNull()

                                            if (employerDoc != null) {
                                                val employerUid = employerDoc.id
                                                NotificationRepository.sendNotification(
                                                    recipientUserId = employerUid,
                                                    title = "New Application Received",
                                                    message = "$loggedInStudentName applied for $selectedTitle.",
                                                    type = "application"
                                                )
                                            }
                                        } catch (_: Exception) {}
                                    }
                                }
                                currentScreen = AppScreen.APPLICATION_CONFIRMATION
                            }
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
                            studentName = loggedInStudentName,
                            studentEmail = loggedInStudentEmail,
                            studentId = loggedInStudentId,
                            programme = loggedInProgramme,
                            yearOfStudy = loggedInYearOfStudy,
                            onNavigateHome = { currentScreen = AppScreen.STUDENT_HOME },
                            onNavigateSearch = { currentScreen = AppScreen.STUDENT_SEARCH },
                            onNavigateApplications = { currentScreen = AppScreen.MY_APPLICATIONS },
                            onLogOut = { currentScreen = AppScreen.LOGIN }
                        )
                    }
                    AppScreen.EMPLOYER_HOME -> {
                        EmployerHomeScreen(
                            companyName = loggedInEmployerCompanyName,
                            onPostInternshipClick = { currentScreen = AppScreen.POST_INTERNSHIP },
                            onViewApplicantsClick = { currentScreen = AppScreen.EMPLOYER_RECEIVED_APPLICATIONS },
                            onReviewApplicantClick = { currentScreen = AppScreen.EMPLOYER_APPLICATION_REVIEW },
                            onViewAllListingsClick = { currentScreen = AppScreen.EMPLOYER_RECEIVED_APPLICATIONS },
                            onNavigateApplicants = { currentScreen = AppScreen.EMPLOYER_RECEIVED_APPLICATIONS },
                            onNavigateProfile = { currentScreen = AppScreen.EMPLOYER_PROFILE },
                            onNotificationClick = { currentScreen = AppScreen.NOTIFICATIONS },
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
                            onAcceptSuccess = { currentScreen = AppScreen.EMPLOYER_RECEIVED_APPLICATIONS },
                            onRejectSuccess = { currentScreen = AppScreen.EMPLOYER_RECEIVED_APPLICATIONS },
                            onNavigateHome = { currentScreen = AppScreen.EMPLOYER_HOME },
                            onNavigatePostJob = { currentScreen = AppScreen.POST_INTERNSHIP },
                            onNavigateApplications = { currentScreen = AppScreen.EMPLOYER_RECEIVED_APPLICATIONS },
                            onNavigateProfile = { currentScreen = AppScreen.EMPLOYER_PROFILE }
                        )
                    }
                    AppScreen.EMPLOYER_PROFILE -> {
                        EmployerProfileScreen(
                            companyName = loggedInEmployerCompanyName,
                            contactPerson = loggedInEmployerContactPerson,
                            officialEmail = loggedInEmployerEmail,
                            industry = loggedInEmployerIndustry,
                            location = loggedInEmployerLocation,
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
                            onNotificationClick = { currentScreen = AppScreen.NOTIFICATIONS },
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
                    AppScreen.NOTIFICATIONS -> {
                        NotificationScreen(
                            onBackClick = {
                                currentScreen = AppScreen.STUDENT_HOME
                            }
                        )
                    }
                }
            }
        }
    }
}
