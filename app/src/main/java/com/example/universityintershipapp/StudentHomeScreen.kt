package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

@Composable
fun StudentHomeScreen(
    modifier: Modifier = Modifier,
    studentName: String = "Gerald Zulumba",
    onSearchClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onViewAllRecommended: () -> Unit = {},
    onApplyClick: (String, String, String) -> Unit = { _, _, _ -> },
    onApplicationsClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedBottomNav by remember { mutableIntStateOf(0) }

    var studentProgramme by remember { mutableStateOf("Computer Science & Engineering") }
    var firestoreInternships by remember { mutableStateOf<List<InternshipModel>>(emptyList()) }
    var availableCount by remember { mutableIntStateOf(0) }
    var submittedCount by remember { mutableIntStateOf(0) }
    var underReviewCount by remember { mutableIntStateOf(0) }
    var unreadCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "test-student-uid"
            val notifs = NotificationRepository.getNotifications(uid)
            unreadCount = notifs.count { !it.effectiveIsRead }

            val userDoc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
            val prog = userDoc.getString("programme") ?: userDoc.getString("fieldOfStudy")
            if (!prog.isNullOrBlank()) {
                studentProgramme = prog
            }
            val jobs = InternshipRepository.getPublishedInternships()
            firestoreInternships = jobs.ifEmpty {
                InternshipRepository.getInternships().filter { it.status == "published" || it.status.isBlank() }
            }
            availableCount = firestoreInternships.size

            try {
                val appSnapshot = FirebaseFirestore.getInstance().collection("applications")
                    .whereEqualTo("studentUid", uid)
                    .get()
                    .await()
                submittedCount = appSnapshot.size()
                underReviewCount = appSnapshot.documents.count { doc ->
                    val status = doc.getString("status") ?: ""
                    status.contains("Review", ignoreCase = true) || status.contains("Pending", ignoreCase = true)
                }
            } catch (_: Exception) {
                submittedCount = firestoreInternships.size.coerceAtMost(4)
                underReviewCount = firestoreInternships.size.coerceAtMost(2)
            }
        } catch (_: Exception) {
            firestoreInternships = emptyList()
            availableCount = 0
            submittedCount = 0
            underReviewCount = 0
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Top Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Logo
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF1B3B80), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val w = size.width
                        val h = size.height

                        val capTop = Path().apply {
                            moveTo(w * 0.5f, h * 0.15f)
                            lineTo(w * 0.9f, h * 0.38f)
                            lineTo(w * 0.5f, h * 0.60f)
                            lineTo(w * 0.1f, h * 0.38f)
                            close()
                        }
                        drawPath(capTop, color = Color.White)

                        val capBottom = Path().apply {
                            moveTo(w * 0.25f, h * 0.48f)
                            lineTo(w * 0.25f, h * 0.68f)
                            cubicTo(w * 0.25f, h * 0.82f, w * 0.75f, h * 0.82f, w * 0.75f, h * 0.68f)
                            lineTo(w * 0.75f, h * 0.48f)
                            close()
                        }
                        drawPath(capBottom, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name Greeting
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UniIntern",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )
                    Text(
                        text = "Welcome back, $studentName!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }

                // Notification Bell
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    BellIcon(
                        modifier = Modifier.size(24.dp),
                        badgeCount = unreadCount,
                        tint = Color(0xFF0F2C59)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Profile Avatar Circle
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFFDBEAFE), CircleShape)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    PersonIcon(
                        modifier = Modifier.size(22.dp),
                        tint = Color(0xFF1E3A8A)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // Scrollable Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // 2. Search Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0F172A)
                        ),
                        placeholder = {
                            Text(
                                text = "Search internships by role, company...",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            SearchNavIcon(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { onSearchClick() },
                                tint = Color(0xFF334155)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedBorderColor = Color(0xFF0F2C59),
                            unfocusedBorderColor = Color(0xFF94A3B8),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))



                // 4. Application Overview Section
                Text(
                    text = "Application Overview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1: Available Internships
                    OverviewCard(
                        modifier = Modifier.weight(1f),
                        countText = availableCount.toString(),
                        labelText = "Available Internships"
                    )

                    // Card 2: Submitted
                    OverviewCard(
                        modifier = Modifier.weight(1f),
                        countText = submittedCount.toString(),
                        labelText = "Submitted"
                    )

                    // Card 3: Under Review
                    OverviewCard(
                        modifier = Modifier.weight(1f),
                        countText = underReviewCount.toString(),
                        labelText = "Under Review"
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 5. Recommended for You Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Recommended for You",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F2C59)
                        )
                    }

                    Row(
                        modifier = Modifier.clickable { onViewAllRecommended() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View All",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        RightChevronIcon(
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF1E3A8A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Loading published internships...", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                } else if (firestoreInternships.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No published internships available at the moment.",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    firestoreInternships.forEach { item ->
                        InternshipCard(
                            title = item.title,
                            companyLocation = "${item.companyName} • ${item.location}",
                            duration = item.duration,
                            deadline = item.deadline,
                            field = item.fieldOfStudy,
                            onApplyClick = { onApplyClick(item.title, item.companyName, item.location) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                val hasInternshipToday = remember(firestoreInternships) {
                    val today = java.util.Calendar.getInstance()
                    val todayYear = today.get(java.util.Calendar.YEAR)
                    val todayDay = today.get(java.util.Calendar.DAY_OF_YEAR)
                    firestoreInternships.any { item ->
                        val itemCal = java.util.Calendar.getInstance().apply { timeInMillis = item.createdAt }
                        itemCal.get(java.util.Calendar.YEAR) == todayYear && itemCal.get(java.util.Calendar.DAY_OF_YEAR) == todayDay
                    }
                }

                // 6. Latest Postings Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Latest Postings",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )
                    if (hasInternshipToday) {
                        Text(
                            text = "Updated today",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (firestoreInternships.isNotEmpty()) {
                    firestoreInternships.takeLast(2).forEach { item ->
                        PostingCard(
                            title = item.title,
                            isNew = true,
                            companyLocation = "${item.companyName} • ${item.location}",
                            duration = item.duration,
                            deadline = item.deadline,
                            onDetailsClick = { onApplyClick(item.title, item.companyName, item.location) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No latest internships available.",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 7. Bottom Navigation Bar
            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nav Item 0: Home
                BottomNavItem(
                    label = "Home",
                    isSelected = selectedBottomNav == 0,
                    icon = { HomeNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 0) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
                    onClick = { selectedBottomNav = 0 }
                )

                // Nav Item 1: Internships
                BottomNavItem(
                    label = "Internships",
                    isSelected = selectedBottomNav == 1,
                    icon = { BriefcaseIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 1) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 1
                        onSearchClick()
                    }
                )

                // Nav Item 2: Applications
                BottomNavItem(
                    label = "Applications",
                    isSelected = selectedBottomNav == 2,
                    icon = { ApplicationsNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 2) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 2
                        onApplicationsClick()
                    }
                )

                // Nav Item 3: Profile
                BottomNavItem(
                    label = "Profile",
                    isSelected = selectedBottomNav == 3,
                    icon = { ProfileNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 3) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 3
                        onProfileClick()
                    }
                )
            }
        }
    }
}



@Composable
fun OverviewCard(
    modifier: Modifier = Modifier,
    topIcon: (@Composable () -> Unit)? = null,
    trendIcon: (@Composable () -> Unit)? = null,
    topTagText: String? = null,
    statusDotColor: Color? = null,
    countText: String,
    labelText: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = countText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2C59)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = labelText,
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun InternshipCard(
    title: String,
    companyLocation: String,
    duration: String,
    deadline: String,
    field: String,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = companyLocation,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F5F9)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(text = "Duration: ", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155), maxLines = 1)
                            Text(text = duration, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                            Text(text = "Deadline: ", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155), maxLines = 1)
                            Text(text = deadline, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Field: ", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
                        Text(text = field, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onApplyClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2C59)),
                modifier = Modifier.height(38.dp)
            ) {
                Text(
                    text = "Apply Now",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PostingCard(
    title: String,
    isNew: Boolean,
    companyLocation: String,
    duration: String,
    deadline: String,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.weight(1f)
                )

                if (isNew) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFE0E7FF)
                    ) {
                        Text(
                            text = "New",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4338CA),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = companyLocation,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F5F9)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(text = "Duration: ", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155), maxLines = 1)
                        Text(text = duration, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                        Text(text = "Deadline: ", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155), maxLines = 1)
                        Text(text = deadline, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onDetailsClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                modifier = Modifier.height(38.dp)
            ) {
                Text(
                    text = "Details",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    isSelected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    if (isSelected) {
        Surface(
            shape = RoundedCornerShape(50),
            color = Color(0xFFE0E7FF),
            modifier = Modifier.clickable { onClick() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                icon()
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}



@Composable
fun FilterIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F2C59)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.09f

        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.30f), end = Offset(w * 0.85f, h * 0.30f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawCircle(color = Color.White, radius = w * 0.10f, center = Offset(w * 0.35f, h * 0.30f))
        drawCircle(color = tint, radius = w * 0.10f, center = Offset(w * 0.35f, h * 0.30f), style = Stroke(stroke))

        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.70f), end = Offset(w * 0.85f, h * 0.70f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawCircle(color = Color.White, radius = w * 0.10f, center = Offset(w * 0.65f, h * 0.70f))
        drawCircle(color = tint, radius = w * 0.10f, center = Offset(w * 0.65f, h * 0.70f), style = Stroke(stroke))
    }
}

@Composable
fun ClockIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E3A8A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(color = tint, radius = w * 0.42f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.09f))

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.25f)
            lineTo(w * 0.5f, h * 0.5f)
            lineTo(w * 0.68f, h * 0.5f)
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}



@Composable
fun TrendUpIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF059669)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.75f)
            lineTo(w * 0.45f, h * 0.45f)
            lineTo(w * 0.62f, h * 0.60f)
            lineTo(w * 0.85f, h * 0.25f)
            moveTo(w * 0.62f, h * 0.25f)
            lineTo(w * 0.85f, h * 0.25f)
            lineTo(w * 0.85f, h * 0.48f)
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun PaperPlaneIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val plane = Path().apply {
            moveTo(w * 0.15f, h * 0.50f)
            lineTo(w * 0.85f, h * 0.18f)
            lineTo(w * 0.52f, h * 0.85f)
            lineTo(w * 0.40f, h * 0.58f)
            close()
        }
        drawPath(plane, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun CalendarIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val body = Path().apply {
            moveTo(w * 0.18f, h * 0.28f)
            lineTo(w * 0.82f, h * 0.28f)
            lineTo(w * 0.82f, h * 0.85f)
            lineTo(w * 0.18f, h * 0.85f)
            close()
        }
        drawPath(body, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.15f), end = Offset(w * 0.35f, h * 0.28f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.65f, h * 0.15f), end = Offset(w * 0.65f, h * 0.28f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.18f, h * 0.45f), end = Offset(w * 0.82f, h * 0.45f), strokeWidth = w * 0.07f)
    }
}

@Composable
fun CategoryIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.18f)
            lineTo(w * 0.82f, h * 0.38f)
            lineTo(w * 0.5f, h * 0.58f)
            lineTo(w * 0.18f, h * 0.38f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.08f, join = StrokeJoin.Round))

        val path2 = Path().apply {
            moveTo(w * 0.18f, h * 0.55f)
            lineTo(w * 0.5f, h * 0.72f)
            lineTo(w * 0.82f, h * 0.55f)
        }
        drawPath(path2, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun RightChevronIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F766E)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.35f, h * 0.2f)
            lineTo(w * 0.70f, h * 0.5f)
            lineTo(w * 0.35f, h * 0.8f)
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.15f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun HomeNavIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F766E)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val roof = Path().apply {
            moveTo(w * 0.15f, h * 0.48f)
            lineTo(w * 0.50f, h * 0.18f)
            lineTo(w * 0.85f, h * 0.48f)
        }
        drawPath(roof, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val base = Path().apply {
            moveTo(w * 0.25f, h * 0.45f)
            lineTo(w * 0.25f, h * 0.82f)
            lineTo(w * 0.75f, h * 0.82f)
            lineTo(w * 0.75f, h * 0.45f)
        }
        drawPath(base, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun SearchNavIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(color = tint, radius = w * 0.30f, center = Offset(w * 0.42f, h * 0.42f), style = Stroke(width = w * 0.09f))
        drawLine(color = tint, start = Offset(w * 0.62f, h * 0.62f), end = Offset(w * 0.85f, h * 0.85f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    }
}

@Composable
fun ApplicationsNavIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val doc = Path().apply {
            moveTo(w * 0.22f, h * 0.18f)
            lineTo(w * 0.78f, h * 0.18f)
            lineTo(w * 0.78f, h * 0.82f)
            lineTo(w * 0.22f, h * 0.82f)
            close()
        }
        drawPath(doc, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.35f), end = Offset(w * 0.65f, h * 0.35f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.50f), end = Offset(w * 0.65f, h * 0.50f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.65f), end = Offset(w * 0.52f, h * 0.65f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
    }
}

@Composable
fun ProfileNavIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(color = tint, radius = w * 0.20f, center = Offset(w * 0.5f, h * 0.32f), style = Stroke(width = w * 0.08f))

        val body = Path().apply {
            moveTo(w * 0.22f, h * 0.82f)
            cubicTo(w * 0.22f, h * 0.62f, w * 0.78f, h * 0.62f, w * 0.78f, h * 0.82f)
        }
        drawPath(body, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentHomeScreenPreview() {
    UniversityIntershipAppTheme {
        StudentHomeScreen()
    }
}
