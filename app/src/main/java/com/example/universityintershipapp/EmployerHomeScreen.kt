
package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun EmployerHomeScreen(
    modifier: Modifier = Modifier,
    companyName: String = "",
    onPostInternshipClick: () -> Unit = {},
    onViewApplicantsClick: (String) -> Unit = {},
    onReviewApplicantClick: (String) -> Unit = {},
    onViewAllListingsClick: () -> Unit = {},
    onNavigateApplicants: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onLogOutClick: () -> Unit = {}
) {
    var selectedBottomNav by remember { mutableIntStateOf(0) }
    var companyNameState by remember { mutableStateOf(companyName.ifBlank { "Company account" }) }
    var sectorState by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }

    var activePostingsCount by remember { mutableStateOf("0") }
    var applicationsCount by remember { mutableStateOf("0") }
    var pendingCount by remember { mutableStateOf("0") }

    var employerListings by remember {
        mutableStateOf<List<InternshipModel>>(emptyList())
    }

    var recentApplicants by remember {
        mutableStateOf<List<Map<String, Any>>>(emptyList())
    }

    var unreadCount by remember { mutableIntStateOf(0) }

    val currentEmployerUid = remember {
        FirebaseAuth.getInstance().currentUser?.uid
    }

    val db = remember { FirebaseFirestore.getInstance() }

    val scope = rememberCoroutineScope()

    // Load only the signed-in employer's company information and internships.
    LaunchedEffect(currentEmployerUid) {
        val uid = currentEmployerUid ?: return@LaunchedEffect

        try {
            val userDoc = db.collection("users")
                .document(uid)
                .get()
                .await()

            if (userDoc.exists()) {
                companyNameState =
                    userDoc.getString("companyName")
                        ?.takeIf { it.isNotBlank() }
                        ?: "Company account"

                sectorState =
                    userDoc.getString("sector")
                        ?.takeIf { it.isNotBlank() }
                        ?: ""
            }

            // IMPORTANT:
            // Never fall back to getInternships().
            // That method retrieves internships belonging to all employers.
            val ownedInternships =
                InternshipRepository.getEmployerInternships(uid)

            employerListings = ownedInternships
            activePostingsCount = ownedInternships.size.toString()

        } catch (e: Exception) {
            android.util.Log.e(
                "EmployerHome",
                "Failed to load employer information",
                e
            )
        }
    }

    // Listen only to notifications addressed to this employer.
    DisposableEffect(currentEmployerUid) {
        val uid = currentEmployerUid

        if (uid == null) {
            unreadCount = 0
            onDispose { }
        } else {
            val notificationListener = db.collection("notifications")
                .whereEqualTo("recipientUid", uid)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        android.util.Log.e(
                            "EmployerNotifications",
                            "Failed to listen for notifications",
                            error
                        )
                        return@addSnapshotListener
                    }

                    unreadCount = snapshot?.documents?.count { document ->
                        val notification =
                            parseNotification(document)

                        notification != null &&
                                !notification.effectiveIsRead &&
                                notification.effectiveRecipientUid == uid
                    } ?: 0
                }

            onDispose {
                notificationListener.remove()
            }
        }
    }

    // Listen for applications belonging to this employer.
    DisposableEffect(currentEmployerUid, employerListings) {
        val uid = currentEmployerUid

        if (uid == null) {
            applicationsCount = "0"
            pendingCount = "0"
            recentApplicants = emptyList()
            onDispose { }
        } else {
            var active = true

            val ownedInternshipIds =
                employerListings
                    .map { it.id }
                    .filter { it.isNotBlank() }
                    .toSet()

            val applicationListener =
                db.collection("applications")
                    .addSnapshotListener { snapshot, error ->

                        if (error != null) {
                            android.util.Log.e(
                                "EmployerApplications",
                                "Failed to listen for applications",
                                error
                            )
                            return@addSnapshotListener
                        }

                        if (snapshot == null) return@addSnapshotListener

                        // An application must belong to this employer.
                        // Ownership is checked using the UID or an actual
                        // internship document ID owned by this employer.
                        val employerApplications =
                            snapshot.documents.filter { document ->

                                val appEmployerUid =
                                    document.getString("employerUid").orEmpty()

                                val internshipId =
                                    document.getString("internshipId").orEmpty()

                                val matchesEmployer =
                                    appEmployerUid == uid

                                val matchesOwnedInternship =
                                    internshipId.isNotBlank() &&
                                            internshipId in ownedInternshipIds

                                matchesEmployer || matchesOwnedInternship
                            }

                        val sortedApplications =
                            employerApplications.sortedByDescending {
                                it.getLong("createdAt") ?: 0L
                            }

                        applicationsCount =
                            sortedApplications.size.toString()

                        pendingCount =
                            sortedApplications.count { document ->
                                val status =
                                    document.getString("status").orEmpty()

                                status.equals(
                                    "Under Review",
                                    ignoreCase = true
                                ) || status.equals(
                                    "pending",
                                    ignoreCase = true
                                )
                            }.toString()

                        // Enrich the two most recent applications with
                        // the corresponding student's profile information.
                        val recentDocuments =
                            sortedApplications.take(2)

                        scope.launch(Dispatchers.IO) {
                            val enrichedApplicants =
                                mutableListOf<Map<String, Any>>()

                            for (document in recentDocuments) {
                                if (!active) break

                                val app = document.data ?: continue

                                val studentUid =
                                    app["studentUid"] as? String

                                var studentName =
                                    app["studentName"] as? String
                                        ?: "Student Applicant"

                                var programme = "Not provided"

                                if (!studentUid.isNullOrBlank()) {
                                    try {
                                        val studentDoc =
                                            db.collection("users")
                                                .document(studentUid)
                                                .get()
                                                .await()

                                        if (studentDoc.exists()) {
                                            studentName =
                                                studentDoc.getString("fullName")
                                                    ?.takeIf { it.isNotBlank() }
                                                    ?: studentDoc.getString("studentName")
                                                        ?.takeIf { it.isNotBlank() }
                                                            ?: studentName

                                            programme =
                                                studentDoc.getString("programme")
                                                    ?.takeIf { it.isNotBlank() }
                                                    ?: studentDoc.getString("fieldOfStudy")
                                                        ?.takeIf { it.isNotBlank() }
                                                            ?: "Not provided"
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e(
                                            "EmployerApplications",
                                            "Failed to load student profile",
                                            e
                                        )
                                    }
                                }

                                enrichedApplicants.add(
                                    mapOf(
                                        "applicationId" to document.id,
                                        "studentName" to studentName,
                                        "programme" to programme,
                                        "internshipTitle" to (
                                                app["internshipTitle"] as? String
                                                    ?: "Internship Application"
                                                )
                                    )
                                )
                            }

                            if (active) {
                                recentApplicants = enrichedApplicants
                            }
                        }
                    }

            onDispose {
                active = false
                applicationListener.remove()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = companyNameState,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )

                    Text(
                        text = sectorState.ifBlank {
                            "Connecting organizations with emerging talent."
                        },
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    BellIcon(
                        modifier = Modifier.size(22.dp),
                        badgeCount = unreadCount,
                        tint = Color(0xFF0F2C59)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFEFF6FF), CircleShape)
                ) {
                    LogoutIcon(
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF1E3A8A)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0))

            // Scrollable Dashboard
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Post Internship Opportunities",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Create and publish internship opportunities to connect with talented students.",
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onPostInternshipClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E3A8A)
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PlusIcon(
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Post New Internship",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Recruitment Overview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EmployerMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Active Postings",
                        count = activePostingsCount
                    )

                    EmployerMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Applications",
                        count = applicationsCount
                    )

                    EmployerMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Pending Applications",
                        count = pendingCount
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Internship Listings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Text(
                        text = "View All (${applicationsCount})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.clickable {
                            onViewAllListingsClick()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (employerListings.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No active internship listings found.",
                                fontSize = 13.sp,
                                color = Color(0xFF334155)
                            )
                        }
                    }
                } else {
                    employerListings.forEach { job ->
                        EmployerListingCard(
                            title = job.title,
                            locationType = "${job.location} • ${job.duration}",
                            deadline = job.deadline,
                            onViewApplicants = {
                                // Pass the Firestore internship document ID.
                                onViewApplicantsClick(job.id)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Recent Verified Applicants",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (recentApplicants.isEmpty()) {
                    Text(
                        text = "No recent applicants yet",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                } else {
                    recentApplicants.forEach { app ->
                        val studentName =
                            app["studentName"] as? String
                                ?: "Student Applicant"

                        val programme =
                            app["programme"] as? String
                                ?: "Not provided"

                        val title =
                            app["internshipTitle"] as? String
                                ?: "Internship Application"

                        val appId =
                            app["applicationId"] as? String ?: ""

                        ApplicantCard(
                            studentName = studentName,
                            program = programme,
                            appliedFor = title,
                            onReviewClick = {
                                if (appId.isNotBlank()) {
                                    onReviewApplicantClick(appId)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Bottom Navigation
            HorizontalDivider(color = Color(0xFFE2E8F0))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    label = "Dashboard",
                    isSelected = selectedBottomNav == 0,
                    icon = {
                        HomeNavIcon(
                            modifier = Modifier.size(20.dp),
                            tint = if (selectedBottomNav == 0)
                                Color(0xFF1E3A8A)
                            else Color(0xFF64748B)
                        )
                    },
                    onClick = { selectedBottomNav = 0 }
                )

                BottomNavItem(
                    label = "Post Job",
                    isSelected = selectedBottomNav == 1,
                    icon = {
                        PlusIcon(
                            modifier = Modifier.size(20.dp),
                            tint = if (selectedBottomNav == 1)
                                Color(0xFF1E3A8A)
                            else Color(0xFF64748B)
                        )
                    },
                    onClick = {
                        selectedBottomNav = 1
                        onPostInternshipClick()
                    }
                )

                BottomNavItem(
                    label = "Applicants",
                    isSelected = selectedBottomNav == 2,
                    icon = {
                        ApplicationsNavIcon(
                            modifier = Modifier.size(20.dp),
                            tint = if (selectedBottomNav == 2)
                                Color(0xFF1E3A8A)
                            else Color(0xFF64748B)
                        )
                    },
                    onClick = {
                        selectedBottomNav = 2
                        onNavigateApplicants()
                    }
                )

                BottomNavItem(
                    label = "Profile",
                    isSelected = selectedBottomNav == 3,
                    icon = {
                        ProfileNavIcon(
                            modifier = Modifier.size(20.dp),
                            tint = if (selectedBottomNav == 3)
                                Color(0xFF1E3A8A)
                            else Color(0xFF64748B)
                        )
                    },
                    onClick = {
                        selectedBottomNav = 3
                        onNavigateProfile()
                    }
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Log Out",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2C59)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out?",
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        FirebaseAuth.getInstance().signOut()
                        onLogOutClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F2C59)
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EmployerMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    count: String
) {
    Card(
        modifier = modifier.height(92.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155),
                maxLines = 2,
                minLines = 2
            )

            Text(
                text = count,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2C59)
            )
        }
    }
}

@Composable
private fun EmployerListingCard(
    title: String,
    locationType: String,
    deadline: String,
    onViewApplicants: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = locationType,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Deadline: $deadline",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155)
                )

                Button(
                    onClick = onViewApplicants,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F2C59)
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "View",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ApplicantCard(
    studentName: String,
    program: String,
    appliedFor: String,
    onReviewClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = studentName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = program.replace(
                    Regex("\\s*\\(Yr\\s*\\d+\\)"),
                    ""
                ).trim(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Applied: $appliedFor",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E3A8A),
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onReviewClick,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F2C59)
                    ),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = "Review",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmployerHomeScreenPreview() {
    UniversityIntershipAppTheme {
        EmployerHomeScreen()
    }
}
