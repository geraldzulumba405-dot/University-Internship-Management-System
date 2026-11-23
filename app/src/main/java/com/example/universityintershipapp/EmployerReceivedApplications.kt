package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

@Composable
fun EmployerReceivedApplications(
    modifier: Modifier = Modifier,
    internshipTitle: String = "",
    onApplicantClick: (String) -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigatePostJob: () -> Unit = {},
    onNavigateApplications: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {
    var selectedFilterTab by remember { mutableIntStateOf(0) } // 0: All, 1: Pending, 2: Accepted
    var selectedBottomNav by remember { mutableIntStateOf(2) } // Applicants selected

    val textBlack = Color(0xFF000000)
    val textDarkNavy = Color(0xFF0F2C59)

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
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Received Applications",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDarkNavy
                )
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // 2. Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Applicant Cards List
                var applicantsList by remember { mutableStateOf<List<ApplicantData>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    isLoading = true
                    val parseApps: suspend (com.google.firebase.firestore.Source) -> List<ApplicantData> = { source ->
                        val employerUid = FirebaseAuth.getInstance().currentUser?.uid
                        if (employerUid.isNullOrBlank()) {
                            emptyList()
                        } else {
                            val employerListings = InternshipRepository.getEmployerInternships(employerUid, source)
                            val ownedInternshipIds = employerListings.map { it.id }.filter { it.isNotBlank() }.toSet()

                            val appsSnapshot = FirebaseFirestore.getInstance().collection("applications")
                                .get(source)
                                .await()
                            val fetched = mutableListOf<ApplicantData>()
                            for (doc in appsSnapshot.documents) {
                                val data = doc.data ?: continue
                                val appEmployerUid = data["employerUid"] as? String ?: ""
                                val internshipId = data["internshipId"] as? String ?: ""

                                val matchesEmployer = appEmployerUid == employerUid ||
                                                      (employerUid == "test-employer-uid" && (appEmployerUid == "employer-uid" || appEmployerUid.isBlank())) ||
                                                      (appEmployerUid.isBlank() && employerUid == "employer-uid")

                                val matchesOwnedInternship = internshipId.isNotBlank() && internshipId in ownedInternshipIds

                                if (!matchesEmployer && !matchesOwnedInternship) continue

                                val title = data["internshipTitle"] as? String ?: "Software Engineering Intern"
                                val statusRaw = data["status"] as? String ?: "Pending"
                                val statusText = when {
                                    statusRaw.equals("Accepted", ignoreCase = true) || statusRaw.equals("Accept", ignoreCase = true) -> "Accepted"
                                    statusRaw.equals("Rejected", ignoreCase = true) || statusRaw.equals("Reject", ignoreCase = true) -> "Rejected"
                                    else -> "Pending"
                                }
                                val studentUid = data["studentUid"] as? String
                                var name = "Not provided"
                                var prog = "Not provided"
                                if (!studentUid.isNullOrBlank()) {
                                    val userDoc = FirebaseFirestore.getInstance().collection("users").document(studentUid).get(source).await()
                                    if (userDoc.exists()) {
                                        val n = userDoc.getString("fullName") ?: userDoc.getString("studentName")
                                        if (!n.isNullOrBlank()) name = n
                                        val p = userDoc.getString("programme") ?: userDoc.getString("fieldOfStudy")
                                        if (!p.isNullOrBlank()) prog = p
                                    }
                                }
                                fetched.add(
                                    ApplicantData(
                                        id = doc.id,
                                        name = name,
                                        gpa = "Not provided",
                                        programAndDate = prog,
                                        statusText = statusText,
                                        statusBg = when (statusText) {
                                            "Accepted" -> Color(0xFFDCFCE7)
                                            "Rejected" -> Color(0xFFFEE2E2)
                                            else -> Color(0xFFEFF6FF)
                                        },
                                        statusTextCol = when (statusText) {
                                            "Accepted" -> Color(0xFF166534)
                                            "Rejected" -> Color(0xFF991B1B)
                                            else -> Color(0xFF1E3A8A)
                                        },
                                        appliedInternship = title
                                    )
                                )
                            }
                            fetched
                        }
                    }

                    // 1. Cache first load
                    try {
                        val cachedApplicants = parseApps(com.google.firebase.firestore.Source.CACHE)
                        if (cachedApplicants.isNotEmpty()) {
                            applicantsList = cachedApplicants
                            isLoading = false
                        }
                    } catch (_: Exception) {}

                    // 2. Server background refresh
                    try {
                        val serverApplicants = parseApps(com.google.firebase.firestore.Source.SERVER)
                        applicantsList = serverApplicants.ifEmpty { applicantsList }
                    } catch (_: Exception) {
                        // Keep cached data if server fails
                    } finally {
                        isLoading = false
                    }
                }

                val filteredByInternship = if (internshipTitle.isNotBlank()) {
                    applicantsList.filter { it.appliedInternship.equals(internshipTitle, ignoreCase = true) }
                } else {
                    applicantsList
                }

                // Filter Tabs Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val allCount = filteredByInternship.size.toString()
                    val pendingCount = filteredByInternship.count { it.statusText == "Pending" }.toString()
                    val acceptedCount = filteredByInternship.count { it.statusText == "Accepted" }.toString()
                    val rejectedCount = filteredByInternship.count { it.statusText == "Rejected" }.toString()

                    FilterTabPill(
                        label = "All",
                        count = allCount,
                        isSelected = selectedFilterTab == 0,
                        onClick = { selectedFilterTab = 0 }
                    )
                    FilterTabPill(
                        label = "Pending Review",
                        count = pendingCount,
                        isSelected = selectedFilterTab == 1,
                        onClick = { selectedFilterTab = 1 }
                    )
                    FilterTabPill(
                        label = "Accepted",
                        count = acceptedCount,
                        isSelected = selectedFilterTab == 2,
                        onClick = { selectedFilterTab = 2 }
                    )
                    FilterTabPill(
                        label = "Rejected",
                        count = rejectedCount,
                        isSelected = selectedFilterTab == 3,
                        onClick = { selectedFilterTab = 3 }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (internshipTitle.isNotBlank()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Text(
                            text = "Applicants for: $internshipTitle",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                val filteredList = when (selectedFilterTab) {
                    1 -> filteredByInternship.filter { it.statusText == "Pending" }
                    2 -> filteredByInternship.filter { it.statusText == "Accepted" }
                    3 -> filteredByInternship.filter { it.statusText == "Rejected" }
                    else -> filteredByInternship
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Loading applications...", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                } else if (applicantsList.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No applications received",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Students' submitted applications will appear here.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else if (filteredList.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No applications found for this filter.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else {
                    filteredList.forEach { applicant ->
                        ReceivedApplicantCard(
                            applicant = applicant,
                            onClick = { onApplicantClick(applicant.id) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Bottom Navigation Bar
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
                    onClick = {
                        selectedBottomNav = 0
                        onNavigateHome()
                    }
                )

                BottomNavItem(
                    label = "Post Job",
                    isSelected = selectedBottomNav == 1,
                    icon = { PlusIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 1) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 1
                        onNavigatePostJob()
                    }
                )

                BottomNavItem(
                    label = "Applicants",
                    isSelected = selectedBottomNav == 2,
                    icon = { ApplicationsNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 2) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 2
                        onNavigateApplications()
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
}

private data class ApplicantData(
    val id: String,
    val name: String,
    val gpa: String,
    val programAndDate: String,
    val statusText: String,
    val statusBg: Color,
    val statusTextCol: Color,
    val appliedInternship: String = "Software Engineering Intern"
)

@Composable
private fun FilterTabPill(
    label: String,
    count: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0xFF0F2C59) else Color.White,
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF0F2C59) else Color(0xFFCBD5E1))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else Color(0xFF334155)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                shape = CircleShape,
                color = if (isSelected) Color(0xFF1E3A8A) else Color(0xFFF1F5F9)
            ) {
                Text(
                    text = count,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else Color(0xFF475569),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun ReceivedApplicantCard(
    applicant: ApplicantData,
    onClick: () -> Unit
) {
    val isPending = applicant.statusText.equals("Pending", ignoreCase = true)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isPending) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = applicant.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = applicant.programAndDate,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(50),
                color = when (applicant.statusText) {
                    "Accepted" -> Color.Black
                    "Rejected" -> Color.White
                    else -> Color(0xFF1E3A8A) // Pending: Blue
                },
                border = if (applicant.statusText == "Rejected") BorderStroke(1.dp, Color(0xFFE2E8F0)) else null
            ) {
                Text(
                    text = applicant.statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (applicant.statusText) {
                        "Rejected" -> Color.Black
                        else -> Color.White
                    },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            if (isPending) {
                Spacer(modifier = Modifier.width(6.dp))

                RightChevronIcon(
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmployerReceivedApplicationsPreview() {
    UniversityIntershipAppTheme {
        EmployerReceivedApplications()
    }
}
