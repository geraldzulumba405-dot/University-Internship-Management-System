
package com.example.universityintershipapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

                val db = remember {
                    FirebaseFirestore.getInstance()
                }

                val auth = remember {
                    FirebaseAuth.getInstance()
                }

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

                // Student information
                var loggedInStudentName by remember {
                    mutableStateOf("")
                }

                var loggedInStudentEmail by remember {
                    mutableStateOf("")
                }

                var loggedInStudentId by remember {
                    mutableStateOf("")
                }

                var loggedInProgramme by remember {
                    mutableStateOf("")
                }

                var loggedInYearOfStudy by remember {
                    mutableStateOf("")
                }

                // Employer information
                // These values are loaded from the authenticated employer's
                // Firestore users/{uid} document after login.
                var loggedInEmployerCompanyName by remember {
                    mutableStateOf("")
                }

                var loggedInEmployerContactPerson by remember {
                    mutableStateOf("")
                }

                var loggedInEmployerEmail by remember {
                    mutableStateOf("")
                }

                var loggedInEmployerIndustry by remember {
                    mutableStateOf("")
                }

                var loggedInEmployerLocation by remember {
                    mutableStateOf("")
                }

                // Selected internship information
                var selectedInternshipId by remember {
                    mutableStateOf("")
                }

                var selectedTitle by remember {
                    mutableStateOf("")
                }

                var selectedCompany by remember {
                    mutableStateOf("")
                }

                var selectedLocation by remember {
                    mutableStateOf("")
                }

                // Selected application for review
                var selectedApplicationId by remember {
                    mutableStateOf("")
                }

                // Load the authenticated employer's actual registration details.
                suspend fun loadEmployerProfile(uid: String) {
                    try {
                        val userDocument = db.collection("users")
                            .document(uid)
                            .get()
                            .await()

                        if (userDocument.exists()) {

                            loggedInEmployerCompanyName =
                                userDocument.getString("companyName")
                                    ?.takeIf { it.isNotBlank() }
                                    ?: ""

                            loggedInEmployerContactPerson =
                                userDocument.getString("fullName")
                                    ?.takeIf { it.isNotBlank() }
                                    ?: userDocument.getString("contactName")
                                        ?.takeIf { it.isNotBlank() }
                                            ?: ""

                            loggedInEmployerEmail =
                                userDocument.getString("email")
                                    ?.takeIf { it.isNotBlank() }
                                    ?: auth.currentUser?.email
                                            ?: ""

                            loggedInEmployerIndustry =
                                userDocument.getString("sector")
                                    ?.takeIf { it.isNotBlank() }
                                    ?: userDocument.getString("industry")
                                        ?.takeIf { it.isNotBlank() }
                                            ?: ""

                            loggedInEmployerLocation =
                                userDocument.getString("location")
                                    ?.takeIf { it.isNotBlank() }
                                    ?: userDocument.getString("companyLocation")
                                        ?.takeIf { it.isNotBlank() }
                                            ?: ""
                        }

                    } catch (e: Exception) {
                        Log.e(
                            "EmployerProfile",
                            "Failed to load employer profile",
                            e
                        )
                    }
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

                    // -------------------------------------------------
                    // SPLASH
                    // -------------------------------------------------
                    AppScreen.SPLASH -> {
                        SplashScreen(
                            onSplashFinished = {
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    // -------------------------------------------------
                    // LOGIN
                    // -------------------------------------------------
                    AppScreen.LOGIN -> {
                        LoginScreen(
                            onBackClick = {},

                            onLoginSubmit = { email, _, role, fullName ->

                                val cleanEmail = email.trim().lowercase()
                                val cleanRole = role.trim().lowercase()

                                // Register FCM token after successful login.
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

                                // Request notification permission on Android 13+.
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

                                // Use the actual role returned by LoginScreen.
                                when (cleanRole) {

                                    "admin" -> {
                                        notificationReturnScreen =
                                            AppScreen.ADMIN_DASHBOARD

                                        currentScreen =
                                            AppScreen.ADMIN_DASHBOARD
                                    }

                                    "employer" -> {
                                        val employerUid =
                                            auth.currentUser?.uid

                                        if (employerUid == null) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Unable to identify your employer account.",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            return@LoginScreen
                                        }

                                        loggedInEmployerEmail = cleanEmail

                                        loggedInEmployerContactPerson =
                                            fullName.trim()

                                        scope.launch {
                                            loadEmployerProfile(employerUid)

                                            notificationReturnScreen =
                                                AppScreen.EMPLOYER_HOME

                                            currentScreen =
                                                AppScreen.EMPLOYER_HOME
                                        }
                                    }

                                    "student" -> {
                                        loggedInStudentEmail = cleanEmail

                                        loggedInStudentName =
                                            fullName.ifBlank {
                                                AuthRepository.formatNameFromEmail(
                                                    cleanEmail
                                                )
                                            }

                                        notificationReturnScreen =
                                            AppScreen.STUDENT_HOME

                                        currentScreen =
                                            AppScreen.STUDENT_HOME
                                    }

                                    else -> {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Your account role could not be identified. Please contact support.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
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

                    // -------------------------------------------------
                    // REGISTER
                    // -------------------------------------------------
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

                                    loggedInEmployerContactPerson = name
                                    loggedInEmployerEmail = email

                                } else {

                                    loggedInStudentName = name
                                    loggedInStudentEmail = email
                                    loggedInStudentId = studentId
                                    loggedInProgramme = programme
                                    loggedInYearOfStudy = year
                                }
                            },

                            onCreateAccountSuccessWithRole = { _ ->
                                currentScreen = AppScreen.LOGIN
                            },

                            onCreateAccountSuccess = {
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    // -------------------------------------------------
                    // STUDENT HOME
                    // -------------------------------------------------
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

                            onApplyClick = {
                                    internshipId, title, company, location ->

                                selectedInternshipId = internshipId
                                selectedTitle = title
                                selectedCompany = company
                                selectedLocation = location

                                currentScreen =
                                    AppScreen.STUDENT_APPLICATION
                            }
                        )
                    }

                    // -------------------------------------------------
                    // STUDENT SEARCH
                    // -------------------------------------------------
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

                            onApplyClick = {
                                    internshipId, title, company, location ->

                                selectedInternshipId = internshipId
                                selectedTitle = title
                                selectedCompany = company
                                selectedLocation = location

                                currentScreen =
                                    AppScreen.STUDENT_APPLICATION
                            }
                        )
                    }

                    // -------------------------------------------------
                    // STUDENT APPLICATION DETAILS
                    // -------------------------------------------------
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

                    // -------------------------------------------------
                    // SUBMIT APPLICATION
                    // -------------------------------------------------
                    AppScreen.SUBMIT_APPLICATION -> {
                        SubmitApplicationScreen(
                            internshipId = selectedInternshipId,
                            title = selectedTitle,
                            company = selectedCompany,
                            location = selectedLocation,

                            onCloseClick = {
                                currentScreen =
                                    AppScreen.STUDENT_APPLICATION
                            },

                            onSaveDraftClick = {
                                currentScreen = AppScreen.MY_APPLICATIONS
                            },

                            onSubmitApplicationClick = {
                                scope.launch {
                                    try {
                                        val currentUser =
                                            auth.currentUser

                                        if (currentUser == null) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Please log in to submit an application.",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            return@launch
                                        }

                                        // Never search by title or invent IDs.
                                        if (selectedInternshipId.isBlank()) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "The selected internship could not be identified. Please select it again.",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            return@launch
                                        }

                                        val studentUid = currentUser.uid

                                        val studentEmail =
                                            currentUser.email
                                                ?: loggedInStudentEmail

                                        // Retrieve the exact selected internship.
                                        val internshipSnapshot =
                                            db.collection("internships")
                                                .document(selectedInternshipId)
                                                .get()
                                                .await()

                                        if (!internshipSnapshot.exists()) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "This internship no longer exists.",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            return@launch
                                        }

                                        // The employer UID must come from
                                        // the actual internship document.
                                        val employerUid =
                                            internshipSnapshot.getString(
                                                "postedByUid"
                                            )

                                        if (employerUid.isNullOrBlank()) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "This internship has no valid employer account. Please contact support.",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            return@launch
                                        }

                                        val actualTitle =
                                            internshipSnapshot.getString("title")
                                                ?: selectedTitle

                                        val actualCompany =
                                            internshipSnapshot.getString("companyName")
                                                ?: selectedCompany

                                        val actualLocation =
                                            internshipSnapshot.getString("location")
                                                ?: selectedLocation

                                        // Save application with actual IDs.
                                        val appData = hashMapOf(
                                            "studentUid" to studentUid,
                                            "studentEmail" to studentEmail,
                                            "internshipId" to internshipSnapshot.id,
                                            "employerUid" to employerUid,
                                            "internshipTitle" to actualTitle,
                                            "company" to actualCompany,
                                            "location" to actualLocation,
                                            "status" to "Under Review",
                                            "createdAt" to System.currentTimeMillis()
                                        )

                                        val applicationRef =
                                            db.collection("applications")
                                                .add(appData)
                                                .await()

                                        // Notify the student.
                                        NotificationRepository.sendNotification(
                                            recipientUid = studentUid,
                                            title = "Application Sent & Under Review",
                                            message = "Your application for $actualTitle at $actualCompany has been sent and is currently under review.",
                                            type = "application_submitted",
                                            relatedInternshipId = internshipSnapshot.id,
                                            relatedApplicationId = applicationRef.id
                                        )

                                        // Notify only the actual employer.
                                        NotificationRepository.sendNotification(
                                            recipientUid = employerUid,
                                            title = "New Internship Application",
                                            message = "A student ($loggedInStudentName) has submitted an application for your $actualTitle opportunity.",
                                            type = "new_application",
                                            relatedInternshipId = internshipSnapshot.id,
                                            relatedApplicationId = applicationRef.id
                                        )

                                        Toast.makeText(
                                            this@MainActivity,
                                            "Application submitted successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        currentScreen =
                                            AppScreen.MY_APPLICATIONS

                                    } catch (e: Exception) {
                                        Log.e(
                                            "Application",
                                            "Failed to submit application",
                                            e
                                        )

                                        Toast.makeText(
                                            this@MainActivity,
                                            "Failed to submit application: ${e.localizedMessage}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        )
                    }

                    // -------------------------------------------------
                    // APPLICATION CONFIRMATION
                    // -------------------------------------------------
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

                    // -------------------------------------------------
                    // MY APPLICATIONS
                    // -------------------------------------------------
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

                    // -------------------------------------------------
                    // STUDENT PROFILE
                    // -------------------------------------------------
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
                                auth.signOut()
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    // -------------------------------------------------
                    // STUDENT NOTIFICATION PREFERENCES
                    // -------------------------------------------------
                    AppScreen.STUDENT_NOTIFICATION_PREFERENCES -> {
                        StudentNotificationPreferencesScreen(
                            onBackClick = {
                                currentScreen = AppScreen.STUDENT_PROFILE
                            }
                        )
                    }

                    // -------------------------------------------------
                    // EMPLOYER HOME
                    // -------------------------------------------------
                    AppScreen.EMPLOYER_HOME -> {
                        EmployerHomeScreen(
                            companyName = loggedInEmployerCompanyName,

                            onPostInternshipClick = {
                                currentScreen = AppScreen.POST_INTERNSHIP
                            },

                            onViewApplicantsClick = { internshipId ->
                                // The callback now receives the actual
                                // internship document ID.
                                currentScreen =
                                    AppScreen.EMPLOYER_RECEIVED_APPLICATIONS
                            },

                            onReviewApplicantClick = { appId ->
                                selectedApplicationId = appId

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
                                auth.signOut()
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    // -------------------------------------------------
                    // POST INTERNSHIP
                    // -------------------------------------------------
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

                    // -------------------------------------------------
                    // EMPLOYER RECEIVED APPLICATIONS
                    // -------------------------------------------------
                    AppScreen.EMPLOYER_RECEIVED_APPLICATIONS -> {
                        EmployerReceivedApplications(
                            onApplicantClick = { appId ->
                                selectedApplicationId = appId

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

                    // -------------------------------------------------
                    // EMPLOYER APPLICATION REVIEW
                    // -------------------------------------------------
                    AppScreen.EMPLOYER_APPLICATION_REVIEW -> {
                        EmployerApplicationReview(
                            applicationId = selectedApplicationId,

                            onBackClick = {
                                currentScreen =
                                    AppScreen.EMPLOYER_RECEIVED_APPLICATIONS
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

                    // -------------------------------------------------
                    // EMPLOYER PROFILE
                    // -------------------------------------------------
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
                                auth.signOut()
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    // -------------------------------------------------
                    // EMPLOYER NOTIFICATION PREFERENCES
                    // -------------------------------------------------
                    AppScreen.EMPLOYER_NOTIFICATION_PREFERENCES -> {
                        EmployerNotificationPreferencesScreen(
                            onBackClick = {
                                currentScreen = AppScreen.EMPLOYER_PROFILE
                            }
                        )
                    }

                    // -------------------------------------------------
                    // ADMIN DASHBOARD
                    // -------------------------------------------------
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
                                auth.signOut()
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    // -------------------------------------------------
                    // ADMIN EMPLOYERS
                    // -------------------------------------------------
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

                    // -------------------------------------------------
                    // ADMIN INTERNSHIPS
                    // -------------------------------------------------
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

                    // -------------------------------------------------
                    // ADMIN REPORTS
                    // -------------------------------------------------
                    AppScreen.ADMIN_REPORTS -> {
                        AdminAccountsScreen(
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
                                auth.signOut()
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    // -------------------------------------------------
                    // NOTIFICATIONS
                    // -------------------------------------------------
                    AppScreen.NOTIFICATIONS -> {
                        NotificationScreen(
                            onBackClick = {
                                currentScreen = notificationReturnScreen
                            }
                        )
                    }

                    // -------------------------------------------------
                    // FORGOT PASSWORD
                    // -------------------------------------------------
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
