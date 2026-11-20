
package com.example.universityintershipapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
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
    STUDENT_NOTIFICATION_PREFERENCES,
    EMPLOYER_HOME,
    POST_INTERNSHIP,
    EMPLOYER_APPLICATION_REVIEW,
    EMPLOYER_RECEIVED_APPLICATIONS,
    EMPLOYER_PROFILE,
    EMPLOYER_NOTIFICATION_PREFERENCES,
    ADMIN_DASHBOARD,
    ADMIN_EMPLOYERS,
    ADMIN_INTERNSHIPS,
    ADMIN_REPORTS,
    NOTIFICATIONS,
    FORGOT_PASSWORD
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            UniversityIntershipAppTheme {

                val scope = rememberCoroutineScope()

                // Notification permission launcher
                val notificationPermissionLauncher =
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { granted ->

                        if (granted) {
                            Log.d(
                                "UniInternFCM",
                                "Notification permission granted"
                            )
                        } else {
                            Log.d(
                                "UniInternFCM",
                                "Notification permission denied"
                            )
                        }
                    }

                var currentScreen by remember {
                    mutableStateOf(AppScreen.SPLASH)
                }

                var notificationReturnScreen by remember {
                    mutableStateOf(AppScreen.STUDENT_HOME)
                }

                var loggedInStudentName by remember {
                    mutableStateOf("Gerald Zulumba")
                }

                var loggedInStudentEmail by remember {
                    mutableStateOf("gzulumba@unima.ac.mw")
                }

                var loggedInStudentId by remember {
                    mutableStateOf("BSC-INF-18-23")
                }

                var loggedInProgramme by remember {
                    mutableStateOf(
                        "BSc Computer Science & Software Engineering"
                    )
                }

                var loggedInYearOfStudy by remember {
                    mutableStateOf("Year 4")
                }

                var loggedInEmployerCompanyName by remember {
                    mutableStateOf("Telecom Networks Malawi")
                }

                var loggedInEmployerContactPerson by remember {
                    mutableStateOf("Blessings Phiri")
                }

                var loggedInEmployerEmail by remember {
                    mutableStateOf("careers@tnm.mw")
                }

                var loggedInEmployerIndustry by remember {
                    mutableStateOf(
                        "Telecommunications & Digital Services"
                    )
                }

                var loggedInEmployerLocation by remember {
                    mutableStateOf("Blantyre, Malawi")
                }

                var selectedTitle by remember {
                    mutableStateOf("Software Engineering Intern")
                }

                var selectedCompany by remember {
                    mutableStateOf("Google")
                }

                var selectedLocation by remember {
                    mutableStateOf("New York, USA")
                }

                BackHandler(
                    enabled = currentScreen != AppScreen.SPLASH &&
                            currentScreen != AppScreen.LOGIN
                ) {
                    when (currentScreen) {

                        AppScreen.REGISTER ->
                            currentScreen = AppScreen.LOGIN

                        AppScreen.STUDENT_SEARCH,
                        AppScreen.STUDENT_APPLICATION,
                        AppScreen.MY_APPLICATIONS,
                        AppScreen.STUDENT_PROFILE,
                        AppScreen.STUDENT_NOTIFICATION_PREFERENCES ->
                            currentScreen = AppScreen.STUDENT_HOME

                        AppScreen.EMPLOYER_HOME ->
                            currentScreen = AppScreen.LOGIN

                        AppScreen.POST_INTERNSHIP,
                        AppScreen.EMPLOYER_RECEIVED_APPLICATIONS,
                        AppScreen.EMPLOYER_APPLICATION_REVIEW,
                        AppScreen.EMPLOYER_PROFILE,
                        AppScreen.EMPLOYER_NOTIFICATION_PREFERENCES ->
                            currentScreen = AppScreen.EMPLOYER_HOME

                        AppScreen.ADMIN_DASHBOARD ->
                            currentScreen = AppScreen.LOGIN

                        AppScreen.ADMIN_EMPLOYERS,
                        AppScreen.ADMIN_INTERNSHIPS,
                        AppScreen.ADMIN_REPORTS ->
                            currentScreen = AppScreen.ADMIN_DASHBOARD

                        AppScreen.SUBMIT_APPLICATION ->
                            currentScreen = AppScreen.STUDENT_APPLICATION

                        AppScreen.APPLICATION_CONFIRMATION ->
                            currentScreen = AppScreen.MY_APPLICATIONS

                        AppScreen.NOTIFICATIONS ->
                            currentScreen = notificationReturnScreen

                        AppScreen.FORGOT_PASSWORD ->
                            currentScreen = AppScreen.LOGIN

                        else ->
                            currentScreen = AppScreen.LOGIN
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

                                // Register the FCM token after successful login
                                FcmTokenManager.registerTokenForCurrentUser {
                                        success, error ->

                                    if (success) {
                                        Log.d(
                                            "UniInternFCM",
                                            "FCM token registered successfully"
                                        )
                                    } else {
                                        Log.e(
                                            "UniInternFCM",
                                            "FCM token registration failed: $error"
                                        )
                                    }
                                }

                                // Request notification permission on Android 13+
                                if (
                                    Build.VERSION.SDK_INT >=
                                    Build.VERSION_CODES.TIRAMISU
                                ) {

                                    val permissionStatus =
                                        ContextCompat.checkSelfPermission(
                                            this@MainActivity,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        )

                                    if (
                                        permissionStatus !=
                                        PackageManager.PERMISSION_GRANTED
                                    ) {
                                        notificationPermissionLauncher.launch(
                                            Manifest.permission.POST_NOTIFICATIONS
                                        )
                                    }
                                }

                                // Continue with existing role-based navigation
                                val cleanEmail = email.trim().lowercase()

                                if (
                                    role == "admin" ||
                                    cleanEmail.contains("admin")
                                ) {
                                    notificationReturnScreen =
                                        AppScreen.ADMIN_DASHBOARD

                                    currentScreen = AppScreen.ADMIN_DASHBOARD

                                } else if (
                                    role == "employer" ||
                                    cleanEmail.contains("employer") ||
                                    cleanEmail.contains("company")
                                ) {

                                    loggedInEmployerEmail = cleanEmail

                                    loggedInEmployerContactPerson =
                                        if (fullName.isNotBlank()) {
                                            fullName
                                        } else {
                                            AuthRepository.formatNameFromEmail(
                                                cleanEmail
                                            )
                                        }

                                    loggedInEmployerCompanyName =
                                        AuthRepository.formatCompanyNameFromEmail(
                                            cleanEmail
                                        )

                                    notificationReturnScreen =
                                        AppScreen.EMPLOYER_HOME

                                    currentScreen = AppScreen.EMPLOYER_HOME

                                } else {

                                    loggedInStudentEmail = cleanEmail

                                    loggedInStudentName =
                                        if (fullName.isNotBlank()) {
                                            fullName
                                        } else {
                                            AuthRepository.formatNameFromEmail(
                                                cleanEmail
                                            )
                                        }

                                    notificationReturnScreen =
                                        AppScreen.STUDENT_HOME

                                    currentScreen = AppScreen.STUDENT_HOME
                                }
                            },

                            onRegisterClick = {
                                currentScreen = AppScreen.REGISTER
                            },

                            onForgotPasswordClick = {
                                currentScreen = AppScreen.FORGOT_PASSWORD
                            }
                        )
                    }

                    AppScreen.REGISTER -> {
                        RegisterScreen(
                            onBackClick = {
                                currentScreen = AppScreen.LOGIN
                            },

                            onLoginClick = {
                                currentScreen = AppScreen.LOGIN
                            },

                            onCreateAccountSuccessWithDetails = {
                                    role, name, email, studentId, programme, year ->

                                if (role == "employer") {

                                    if (name.isNotBlank()) {
                                        loggedInEmployerContactPerson = name
                                    }

                                    if (email.isNotBlank()) {
                                        loggedInEmployerEmail = email
                                    }

                                } else {

                                    if (name.isNotBlank()) {
                                        loggedInStudentName = name
                                    }

                                    if (email.isNotBlank()) {
                                        loggedInStudentEmail = email
                                    }

                                    if (studentId.isNotBlank()) {
                                        loggedInStudentId = studentId
                                    }

                                    if (programme.isNotBlank()) {
                                        loggedInProgramme = programme
                                    }

                                    if (year.isNotBlank()) {
                                        loggedInYearOfStudy = year
                                    }
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

                            onSearchClick = {
                                currentScreen = AppScreen.STUDENT_SEARCH
                            },

                            onViewAllRecommended = {
                                currentScreen = AppScreen.STUDENT_SEARCH
                            },

                            onApplicationsClick = {
                                currentScreen = AppScreen.MY_APPLICATIONS
                            },

                            onProfileClick = {
                                currentScreen = AppScreen.STUDENT_PROFILE
                            },

                            onNotificationClick = {
                                notificationReturnScreen =
                                    AppScreen.STUDENT_HOME

                                currentScreen = AppScreen.NOTIFICATIONS
                            },

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
                            onBackClick = {
                                currentScreen = AppScreen.STUDENT_HOME
                            },

                            onHomeNavClick = {
                                currentScreen = AppScreen.STUDENT_HOME
                            },

                            onApplicationsNavClick = {
                                currentScreen = AppScreen.MY_APPLICATIONS
                            },

                            onProfileNavClick = {
                                currentScreen = AppScreen.STUDENT_PROFILE
                            },

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

                            onBackClick = {
                                currentScreen = AppScreen.STUDENT_HOME
                            },

                            onOneTapApplyClick = {
                                currentScreen = AppScreen.SUBMIT_APPLICATION
                            }
                        )
                    }

                    AppScreen.SUBMIT_APPLICATION -> {
                        SubmitApplicationScreen(
                            onCloseClick = {
                                currentScreen = AppScreen.STUDENT_APPLICATION
                            },

                            onSaveDraftClick = {
                                currentScreen = AppScreen.MY_APPLICATIONS
                            },

                            onSubmitApplicationClick = {

                                val uid =
                                    FirebaseAuth.getInstance()
                                        .currentUser?.uid
                                        ?: "test-student-uid"

                                val email =
                                    FirebaseAuth.getInstance()
                                        .currentUser?.email
                                        ?: loggedInStudentEmail

                                if (uid.isNotBlank()) {

                                    val appData = hashMapOf(
                                        "studentUid" to uid,
                                        "studentEmail" to email,
                                        "internshipTitle" to selectedTitle,
                                        "company" to selectedCompany,
                                        "location" to selectedLocation,
                                        "status" to "Under Review",
                                        "createdAt" to System.currentTimeMillis()
                                    )

                                    FirebaseFirestore.getInstance()
                                        .collection("applications")
                                        .add(appData)

                                    scope.launch {
                                        try {

                                            // Notify the student
                                            NotificationRepository.sendNotification(
                                                recipientUid = uid,
                                                title = "Application Submitted Successfully",
                                                message = "Your application for $selectedTitle at $selectedCompany has been submitted successfully.",
                                                type = "application_submitted"
                                            )

                                            val employersSnap =
                                                FirebaseFirestore.getInstance()
                                                    .collection("users")
                                                    .whereEqualTo("role", "employer")
                                                    .get()
                                                    .await()

                                            val employerDoc =
                                                employersSnap.documents.find {
                                                    (it.getString("companyName") ?: "")
                                                        .equals(
                                                            selectedCompany,
                                                            ignoreCase = true
                                                        )
                                                } ?: employersSnap.documents.firstOrNull()

                                            if (employerDoc != null) {

                                                val employerUid = employerDoc.id

                                                NotificationRepository.sendNotification(
                                                    recipientUid = employerUid,
                                                    title = "New Internship Application",
                                                    message = "A student ($loggedInStudentName) has submitted an application for your $selectedTitle opportunity.",
                                                    type = "new_application"
                                                )
                                            }

                                        } catch (_: Exception) {
                                        }
                                    }
                                }

                                currentScreen = AppScreen.MY_APPLICATIONS
                            }
                        )
                    }

                    AppScreen.APPLICATION_CONFIRMATION -> {
                        ApplicationConfirmationScreen(
                            onDismissPreview = {
                                currentScreen = AppScreen.STUDENT_HOME
                            },

                            onViewApplications = {
                                currentScreen = AppScreen.MY_APPLICATIONS
                            }
                        )
                    }

                    AppScreen.MY_APPLICATIONS -> {
                        MyApplicationsScreen(
                            onNavigateHome = {
                                currentScreen = AppScreen.STUDENT_HOME
                            },

                            onNavigateSearch = {
                                currentScreen = AppScreen.STUDENT_SEARCH
                            },

                            onNavigateProfile = {
                                currentScreen = AppScreen.STUDENT_PROFILE
                            }
                        )
                    }

                    AppScreen.STUDENT_PROFILE -> {
                        StudentProfileScreen(
                            studentName = loggedInStudentName,
                            studentEmail = loggedInStudentEmail,
                            studentId = loggedInStudentId,
                            programme = loggedInProgramme,
                            yearOfStudy = loggedInYearOfStudy,

                            onNavigateHome = {
                                currentScreen = AppScreen.STUDENT_HOME
                            },

                            onNavigateSearch = {
                                currentScreen = AppScreen.STUDENT_SEARCH
                            },

                            onNavigateApplications = {
                                currentScreen = AppScreen.MY_APPLICATIONS
                            },

                            onNotificationPreferencesClick = {
                                currentScreen =
                                    AppScreen.STUDENT_NOTIFICATION_PREFERENCES
                            },

                            onLogOut = {
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    AppScreen.STUDENT_NOTIFICATION_PREFERENCES -> {
                        StudentNotificationPreferencesScreen(
                            onBackClick = {
                                currentScreen = AppScreen.STUDENT_PROFILE
                            }
                        )
                    }

                    AppScreen.EMPLOYER_HOME -> {
                        EmployerHomeScreen(
                            companyName = loggedInEmployerCompanyName,

                            onPostInternshipClick = {
                                currentScreen = AppScreen.POST_INTERNSHIP
                            },

                            onViewApplicantsClick = {
                                currentScreen =
                                    AppScreen.EMPLOYER_RECEIVED_APPLICATIONS
                            },

                            onReviewApplicantClick = {
                                currentScreen =
                                    AppScreen.EMPLOYER_APPLICATION_REVIEW
                            },

                            onViewAllListingsClick = {
                                currentScreen =
                                    AppScreen.EMPLOYER_RECEIVED_APPLICATIONS
                            },

                            onNavigateApplicants = {
                                currentScreen =
                                    AppScreen.EMPLOYER_RECEIVED_APPLICATIONS
                            },

                            onNavigateProfile = {
                                currentScreen = AppScreen.EMPLOYER_PROFILE
                            },

                            onNotificationClick = {
                                notificationReturnScreen =
                                    AppScreen.EMPLOYER_HOME

                                currentScreen = AppScreen.NOTIFICATIONS
                            },

                            onLogOutClick = {
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    AppScreen.POST_INTERNSHIP -> {
                        PostInternshipScreen(
                            onCloseClick = {
                                currentScreen = AppScreen.EMPLOYER_HOME
                            },

                            onPostSuccess = {
                                currentScreen = AppScreen.EMPLOYER_HOME
                            }
                        )
                    }

                    AppScreen.EMPLOYER_RECEIVED_APPLICATIONS -> {
                        EmployerReceivedApplications(
                            onApplicantClick = {
                                currentScreen =
                                    AppScreen.EMPLOYER_APPLICATION_REVIEW
                            },

                            onNavigateHome = {
                                currentScreen = AppScreen.EMPLOYER_HOME
                            },

                            onNavigatePostJob = {
                                currentScreen = AppScreen.POST_INTERNSHIP
                            },

                            onNavigateApplications = {
                                currentScreen =
                                    AppScreen.EMPLOYER_RECEIVED_APPLICATIONS
                            },

                            onNavigateProfile = {
                                currentScreen = AppScreen.EMPLOYER_PROFILE
                            }
                        )
                    }

                    AppScreen.EMPLOYER_APPLICATION_REVIEW -> {
                        EmployerApplicationReview(
                            onBackClick = {
                                currentScreen = AppScreen.EMPLOYER_HOME
                            },

                            onAcceptSuccess = {
                                currentScreen =
                                    AppScreen.EMPLOYER_RECEIVED_APPLICATIONS
                            },

                            onRejectSuccess = {
                                currentScreen =
                                    AppScreen.EMPLOYER_RECEIVED_APPLICATIONS
                            },

                            onNavigateHome = {
                                currentScreen = AppScreen.EMPLOYER_HOME
                            },

                            onNavigatePostJob = {
                                currentScreen = AppScreen.POST_INTERNSHIP
                            },

                            onNavigateApplications = {
                                currentScreen =
                                    AppScreen.EMPLOYER_RECEIVED_APPLICATIONS
                            },

                            onNavigateProfile = {
                                currentScreen = AppScreen.EMPLOYER_PROFILE
                            }
                        )
                    }

                    AppScreen.EMPLOYER_PROFILE -> {
                        EmployerProfileScreen(
                            companyName = loggedInEmployerCompanyName,
                            contactPerson = loggedInEmployerContactPerson,
                            officialEmail = loggedInEmployerEmail,
                            industry = loggedInEmployerIndustry,
                            location = loggedInEmployerLocation,

                            onNavigateHome = {
                                currentScreen = AppScreen.EMPLOYER_HOME
                            },

                            onNavigateInternships = {
                                currentScreen = AppScreen.POST_INTERNSHIP
                            },

                            onNavigateApplications = {
                                currentScreen =
                                    AppScreen.EMPLOYER_RECEIVED_APPLICATIONS
                            },

                            onNavigateProfile = {
                                currentScreen = AppScreen.EMPLOYER_PROFILE
                            },

                            onNotificationPreferencesClick = {
                                currentScreen =
                                    AppScreen.EMPLOYER_NOTIFICATION_PREFERENCES
                            },

                            onLogOutClick = {
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    AppScreen.EMPLOYER_NOTIFICATION_PREFERENCES -> {
                        EmployerNotificationPreferencesScreen(
                            onBackClick = {
                                currentScreen = AppScreen.EMPLOYER_PROFILE
                            }
                        )
                    }

                    AppScreen.ADMIN_DASHBOARD -> {
                        AdminDashboardScreen(
                            onNavigateEmployers = {
                                currentScreen = AppScreen.ADMIN_EMPLOYERS
                            },

                            onNavigateInternships = {
                                currentScreen = AppScreen.ADMIN_INTERNSHIPS
                            },

                            onNavigateReports = {
                                currentScreen = AppScreen.ADMIN_REPORTS
                            },

                            onNotificationClick = {
                                notificationReturnScreen =
                                    AppScreen.ADMIN_DASHBOARD

                                currentScreen = AppScreen.NOTIFICATIONS
                            },

                            onLogOut = {
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    AppScreen.ADMIN_EMPLOYERS -> {
                        AdminEmployerManagementScreen(
                            onNavigateDashboard = {
                                currentScreen = AppScreen.ADMIN_DASHBOARD
                            },

                            onNavigateInternships = {
                                currentScreen = AppScreen.ADMIN_INTERNSHIPS
                            },

                            onNavigateReports = {
                                currentScreen = AppScreen.ADMIN_REPORTS
                            }
                        )
                    }

                    AppScreen.ADMIN_INTERNSHIPS -> {
                        AdminInternshipManagementScreen(
                            onNavigateDashboard = {
                                currentScreen = AppScreen.ADMIN_DASHBOARD
                            },

                            onNavigateEmployers = {
                                currentScreen = AppScreen.ADMIN_EMPLOYERS
                            },

                            onNavigateReports = {
                                currentScreen = AppScreen.ADMIN_REPORTS
                            }
                        )
                    }

                    AppScreen.ADMIN_REPORTS -> {
                        AdminReportsScreen(
                            onNavigateDashboard = {
                                currentScreen = AppScreen.ADMIN_DASHBOARD
                            },

                            onNavigateEmployers = {
                                currentScreen = AppScreen.ADMIN_EMPLOYERS
                            },

                            onNavigateInternships = {
                                currentScreen = AppScreen.ADMIN_INTERNSHIPS
                            },

                            onLogOut = {
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    AppScreen.NOTIFICATIONS -> {
                        NotificationScreen(
                            onBackClick = {
                                currentScreen = notificationReturnScreen
                            }
                        )
                    }

                    AppScreen.FORGOT_PASSWORD -> {
                        ForgotPasswordScreen(
                            onBackClick = {
                                currentScreen = AppScreen.LOGIN
                            },

                            onBackToLoginClick = {
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }
                }
            }
        }
    }
}
