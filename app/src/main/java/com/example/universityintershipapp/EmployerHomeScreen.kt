package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

@Composable
fun EmployerHomeScreen(
    modifier: Modifier = Modifier,
    companyName: String = "Google LLC",
    onPostInternshipClick: () -> Unit = {},
    onViewApplicantsClick: (String) -> Unit = {},
    onReviewApplicantClick: () -> Unit = {},
    onViewAllListingsClick: () -> Unit = {},
    onNavigateApplicants: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onLogOutClick: () -> Unit = {}
) {
    var selectedBottomNav by remember { mutableIntStateOf(0) }
    var companyNameState by remember { mutableStateOf(companyName) }
    var sectorState by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }

    var activePostingsCount by remember { mutableStateOf("0") }
    var applicationsCount by remember { mutableStateOf("0") }
    var pendingCount by remember { mutableStateOf("0") }
    var employerListings by remember { mutableStateOf<List<InternshipModel>>(emptyList()) }
    var allApps by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var recentApplicants by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var unreadCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val userDoc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
                if (userDoc.exists()) {
                    val cName = userDoc.getString("companyName") ?: userDoc.getString("fullName")
                    if (!cName.isNullOrBlank()) companyNameState = cName
                    val sec = userDoc.getString("sector")
                    if (!sec.isNullOrBlank()) sectorState = sec
                }

                // Fetch internships
                val internships = InternshipRepository.getEmployerInternships(uid)
                val allInternships = if (internships.isNotEmpty()) internships else InternshipRepository.getInternships()
                employerListings = allInternships
                activePostingsCount = allInternships.size.toString()

                // Fetch applications
                val appsSnapshot = FirebaseFirestore.getInstance().collection("applications").get().await()
                val apps = appsSnapshot.documents.mapNotNull { it.data }
                allApps = apps
                applicationsCount = apps.size.toString()
                pendingCount = apps.count { (it["status"] as? String)?.equals("Under Review", ignoreCase = true) == true || (it["status"] as? String)?.equals("pending", ignoreCase = true) == true }.toString()

                // Recent applicants
                recentApplicants = apps.take(5)

                val notifs = NotificationRepository.getNotifications(uid)
                unreadCount = notifs.count { !it.effectiveIsRead }
            }
        } catch (_: Exception) {}
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar Header
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
                        text = if (sectorState.isNotBlank()) sectorState else "Connecting organizations with emerging talent.",
                        fontSize = 12.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Notification Bell
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

                // Logout Button
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

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // 2. Welcome Banner & Post Internship Action
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PlusIcon(modifier = Modifier.size(16.dp), tint = Color.White)
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

                // 3. Metrics Summary Grid
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

                // 4. Manage Active Listings Section
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
                        text = "View All (${employerListings.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.clickable { onViewAllListingsClick() }
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
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "No active internship listings found.", fontSize = 13.sp, color = Color(0xFF334155))
                        }
                    }
                } else {
                    employerListings.forEach { job ->
                        val countForJob = allApps.count { (it["internshipTitle"] as? String)?.equals(job.title, ignoreCase = true) == true }
                        EmployerListingCard(
                            title = job.title,
                            locationType = "${job.location} • ${job.duration}",
                            applicantsCount = countForJob,
                            deadline = job.deadline,
                            onViewApplicants = { onViewApplicantsClick(job.title) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Recent Student Applicants Section
                Text(
                    text = "Recent Verified Applicants",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (recentApplicants.isEmpty()) {
                    Text(text = "No recent applicants yet", fontSize = 13.sp, color = Color(0xFF64748B))
                } else {
                    recentApplicants.forEach { app ->
                        val studentName = app["studentName"] as? String ?: "Student Applicant"
                        val title = app["internshipTitle"] as? String ?: "Internship Application"
                        ApplicantCard(
                            studentName = studentName,
                            program = title,
                            appliedFor = title,
                            onReviewClick = onReviewApplicantClick
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Bottom Navigation Bar
            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

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
                    icon = { HomeNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 0) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
                    onClick = { selectedBottomNav = 0 }
                )

                BottomNavItem(
                    label = "Post Job",
                    isSelected = selectedBottomNav == 1,
                    icon = { PlusIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 1) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 1
                        onPostInternshipClick()
                    }
                )

                BottomNavItem(
                    label = "Applicants",
                    isSelected = selectedBottomNav == 2,
                    icon = { ApplicationsNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 2) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 2
                        onNavigateApplicants()
                    }
                )

                BottomNavItem(
                    label = "Profile",
                    isSelected = selectedBottomNav == 3,
                    icon = { ProfileNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 3) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
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
                Text(text = "Log Out", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2C59))
            },
            text = {
                Text(text = "Are you sure you want to log out?", fontSize = 13.sp, color = Color(0xFF334155))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        FirebaseAuth.getInstance().signOut()
                        onLogOutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2C59))
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) {
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
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
            Spacer(modifier = Modifier.height(6.dp))
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
    applicantsCount: Int,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2C59)),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "View Applicants ($applicantsCount)",
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
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = studentName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = program.replace(Regex("\\s*\\(Yr\\s*\\d+\\)"), "").trim(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155)
                )
                Text(
                    text = "Applied: $appliedFor",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onReviewClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2C59)),
                modifier = Modifier.height(34.dp)
            ) {
                Text(text = "Review", fontSize = 11.sp, color = Color.White)
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
