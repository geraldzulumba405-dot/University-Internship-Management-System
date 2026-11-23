
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun StudentHomeScreen(
    modifier: Modifier = Modifier,
    studentName: String = "Gerald Zulumba",
    onSearchClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onViewAllRecommended: () -> Unit = {},
    onApplyClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onApplicationsClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedBottomNav by remember { mutableIntStateOf(0) }

    var studentProgramme by remember {
        mutableStateOf("Computer Science & Engineering")
    }

    var firestoreInternships by remember {
        mutableStateOf<List<InternshipModel>>(emptyList())
    }

    var availableCount by remember { mutableIntStateOf(0) }
    var submittedCount by remember { mutableIntStateOf(0) }
    var underReviewCount by remember { mutableIntStateOf(0) }
    var unreadCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var appliedInternshipIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var applicationsLoadError by remember { mutableStateOf<String?>(null) }

    val currentStudentUid = FirebaseAuth.getInstance()
        .currentUser?.uid

    // Load student information, internships and application statistics.
    LaunchedEffect(currentStudentUid) {
        if (currentStudentUid == null) {
            firestoreInternships = emptyList()
            availableCount = 0
            submittedCount = 0
            underReviewCount = 0
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        val db = FirebaseFirestore.getInstance()
        val loadedAppliedIds = mutableSetOf<String>()

        // 1. Cache first load
        try {
            val userDocCache = db.collection("users")
                .document(currentStudentUid)
                .get(com.google.firebase.firestore.Source.CACHE)
                .await()
            if (userDocCache.exists()) {
                val prog = userDocCache.getString("programme") ?: userDocCache.getString("fieldOfStudy")
                if (!prog.isNullOrBlank()) studentProgramme = prog
            }

            val cachedJobs = InternshipRepository.getPublishedInternships(com.google.firebase.firestore.Source.CACHE)
            if (cachedJobs.isNotEmpty()) {
                firestoreInternships = cachedJobs.ifEmpty {
                    InternshipRepository.getInternships(com.google.firebase.firestore.Source.CACHE)
                        .filter { it.status == "published" || it.status.isBlank() }
                }
                availableCount = firestoreInternships.size
                isLoading = false // Show cached data immediately!
            }

            val appSnapshotCache = db.collection("applications")
                .whereEqualTo("studentUid", currentStudentUid)
                .get(com.google.firebase.firestore.Source.CACHE)
                .await()
            submittedCount = appSnapshotCache.size()
            underReviewCount = appSnapshotCache.documents.count { doc ->
                val status = doc.getString("status") ?: ""
                status.equals("pending", ignoreCase = true) || status.equals("under review", ignoreCase = true)
            }
            appSnapshotCache.documents.forEach { doc ->
                doc.getString("internshipId")?.let { loadedAppliedIds.add(it) }
            }
            appliedInternshipIds = loadedAppliedIds
        } catch (_: Exception) {}

        // 2. Server refresh in background
        try {
            val userDoc = db.collection("users")
                .document(currentStudentUid)
                .get(com.google.firebase.firestore.Source.SERVER)
                .await()

            val programme = userDoc.getString("programme")
                ?: userDoc.getString("fieldOfStudy")

            if (!programme.isNullOrBlank()) {
                studentProgramme = programme
            }

            val jobs = InternshipRepository.getPublishedInternships(com.google.firebase.firestore.Source.SERVER)

            firestoreInternships = jobs.ifEmpty {
                InternshipRepository.getInternships(com.google.firebase.firestore.Source.SERVER)
                    .filter {
                        it.status == "published" || it.status.isBlank()
                    }
            }.ifEmpty { firestoreInternships }

            availableCount = firestoreInternships.size

            val appSnapshot = db.collection("applications")
                .whereEqualTo("studentUid", currentStudentUid)
                .get(com.google.firebase.firestore.Source.SERVER)
                .await()

            submittedCount = appSnapshot.size()

            underReviewCount = appSnapshot.documents.count { doc ->
                val status = doc.getString("status") ?: ""

                status.equals("pending", ignoreCase = true) ||
                        status.equals("under review", ignoreCase = true)
            }

            val serverAppliedIds = mutableSetOf<String>()
            appSnapshot.documents.forEach { doc ->
                doc.getString("internshipId")?.let { serverAppliedIds.add(it) }
            }
            appliedInternshipIds = serverAppliedIds
            applicationsLoadError = null

        } catch (e: Exception) {
            android.util.Log.e(
                "StudentHomeScreen",
                "Failed to refresh dashboard information from server",
                e
            )
            if (loadedAppliedIds.isEmpty() && firestoreInternships.isEmpty()) {
                applicationsLoadError = "Unable to load your applications and internship status. Please check your connection."
            }
        } finally {
            isLoading = false
        }
    }

    // Listen for notification changes in real time.
    DisposableEffect(currentStudentUid) {

        if (currentStudentUid == null) {
            unreadCount = 0
            return@DisposableEffect onDispose {}
        }

        val notificationListener = FirebaseFirestore.getInstance()
            .collection("notifications")
            .whereEqualTo("recipientUid", currentStudentUid)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    android.util.Log.e(
                        "StudentNotifications",
                        "Failed to listen for notifications",
                        error
                    )
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    unreadCount = snapshot.documents.count { document ->
                        val isRead = document.getBoolean("isRead") ?: false
                        val legacyRead = document.getBoolean("read") ?: false

                        !isRead && !legacyRead
                    }
                }
            }

        onDispose {
            notificationListener.remove()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
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
                        .background(
                            Color(0xFF1B3B80),
                            RoundedCornerShape(10.dp)
                        ),
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
                            cubicTo(
                                w * 0.25f, h * 0.82f,
                                w * 0.75f, h * 0.82f,
                                w * 0.75f, h * 0.68f
                            )
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
                        .clickable {
                            onNotificationClick()
                        },
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

            HorizontalDivider(
                color = Color(0xFFF1F5F9),
                thickness = 1.dp
            )

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

                // 3. Application Overview
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
                    OverviewCard(
                        modifier = Modifier.weight(1f),
                        countText = availableCount.toString(),
                        labelText = "Available Internships"
                    )

                    OverviewCard(
                        modifier = Modifier.weight(1f),
                        countText = submittedCount.toString(),
                        labelText = "Submitted"
                    )

                    OverviewCard(
                        modifier = Modifier.weight(1f),
                        countText = underReviewCount.toString(),
                        labelText = "Under Review"
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 4. Recommended for You
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Recommended for You",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )

                    Row(
                        modifier = Modifier.clickable {
                            onViewAllRecommended()
                        },
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading published internships...",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
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
                    firestoreInternships.take(2).forEach { item ->
                        InternshipCard(
                            title = item.title,
                            companyLocation =
                                "${item.companyName} • ${item.location}",
                            duration = item.duration,
                            deadline = item.deadline,
                            field = item.fieldOfStudy,
                            onApplyClick = {
                                onApplyClick(
                                    item.id,
                                    item.title,
                                    item.companyName,
                                    item.location
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 5. Latest Postings
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

                    Row(
                        modifier = Modifier.clickable {
                            onViewAllRecommended()
                        },
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

                val latestPostings = remember(firestoreInternships, appliedInternshipIds) {
                    firestoreInternships
                        .filter { item ->
                            (item.status == "published" || item.status.isBlank()) &&
                            item.id !in appliedInternshipIds
                        }
                        .take(2)
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading latest postings...",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else if (applicationsLoadError != null && latestPostings.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF2F2),
                        border = BorderStroke(1.dp, Color(0xFFFECACA))
                    ) {
                        Box(
                            modifier = Modifier.padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = applicationsLoadError ?: "Failed to load application history.",
                                fontSize = 13.sp,
                                color = Color(0xFFB91C1C),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else if (latestPostings.isNotEmpty()) {
                    latestPostings.forEach { item ->
                        PostingCard(
                            title = item.title,
                            isNew = true,
                            companyLocation =
                                "${item.companyName} • ${item.location}",
                            duration = item.duration,
                            deadline = item.deadline,
                            onDetailsClick = {
                                onApplyClick(
                                    item.id,
                                    item.title,
                                    item.companyName,
                                    item.location
                                )
                            }
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
                        Box(
                            modifier = Modifier.padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "All published internships have been applied for.",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 6. Bottom Navigation Bar
            HorizontalDivider(
                color = Color(0xFFE2E8F0),
                thickness = 1.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {

                BottomNavItem(
                    label = "Home",
                    isSelected = selectedBottomNav == 0,
                    icon = {
                        HomeNavIcon(
                            modifier = Modifier.size(20.dp),
                            tint = if (selectedBottomNav == 0) {
                                Color(0xFF1E3A8A)
                            } else {
                                Color(0xFF64748B)
                            }
                        )
                    },
                    onClick = { selectedBottomNav = 0 }
                )

                BottomNavItem(
                    label = "Internships",
                    isSelected = selectedBottomNav == 1,
                    icon = {
                        BriefcaseIcon(
                            modifier = Modifier.size(20.dp),
                            tint = if (selectedBottomNav == 1) {
                                Color(0xFF1E3A8A)
                            } else {
                                Color(0xFF64748B)
                            }
                        )
                    },
                    onClick = {
                        selectedBottomNav = 1
                        onSearchClick()
                    }
                )

                BottomNavItem(
                    label = "Applications",
                    isSelected = selectedBottomNav == 2,
                    icon = {
                        ApplicationsNavIcon(
                            modifier = Modifier.size(20.dp),
                            tint = if (selectedBottomNav == 2) {
                                Color(0xFF1E3A8A)
                            } else {
                                Color(0xFF64748B)
                            }
                        )
                    },
                    onClick = {
                        selectedBottomNav = 2
                        onApplicationsClick()
                    }
                )

                BottomNavItem(
                    label = "Profile",
                    isSelected = selectedBottomNav == 3,
                    icon = {
                        ProfileNavIcon(
                            modifier = Modifier.size(20.dp),
                            tint = if (selectedBottomNav == 3) {
                                Color(0xFF1E3A8A)
                            } else {
                                Color(0xFF64748B)
                            }
                        )
                    },
                    onClick = {
                        selectedBottomNav = 3
                        onProfileClick()
                    }
                )
            }
        }
    }
}

// ----------------------------------------------------
// OVERVIEW CARD
// ----------------------------------------------------

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
        modifier = modifier.height(92.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = labelText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B),
                lineHeight = 14.sp,
                maxLines = 2,
                minLines = 2
            )

            Text(
                text = countText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2C59)
            )
        }
    }
}

// ----------------------------------------------------
// INTERNSHIP CARD
// ----------------------------------------------------

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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Duration: ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF334155),
                                maxLines = 1
                            )

                            Text(
                                text = duration,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Deadline: ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF334155),
                                maxLines = 1
                            )

                            Text(
                                text = deadline,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Field: ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155)
                        )

                        Text(
                            text = field,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onApplyClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F2C59)
                ),
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

// ----------------------------------------------------
// LATEST POSTING CARD
// ----------------------------------------------------

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
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 2.dp
                            )
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Duration: ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155),
                            maxLines = 1
                        )

                        Text(
                            text = duration,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Deadline: ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155),
                            maxLines = 1
                        )

                        Text(
                            text = deadline,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onDetailsClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEFF6FF)
                ),
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

// ----------------------------------------------------
// BOTTOM NAVIGATION ITEM
// ----------------------------------------------------

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
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
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

// ----------------------------------------------------
// CUSTOM ICONS
// ----------------------------------------------------

@Composable
fun FilterIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F2C59)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.09f

        drawLine(
            color = tint,
            start = Offset(w * 0.15f, h * 0.30f),
            end = Offset(w * 0.85f, h * 0.30f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = Color.White,
            radius = w * 0.10f,
            center = Offset(w * 0.35f, h * 0.30f)
        )

        drawCircle(
            color = tint,
            radius = w * 0.10f,
            center = Offset(w * 0.35f, h * 0.30f),
            style = Stroke(stroke)
        )

        drawLine(
            color = tint,
            start = Offset(w * 0.15f, h * 0.70f),
            end = Offset(w * 0.85f, h * 0.70f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = Color.White,
            radius = w * 0.10f,
            center = Offset(w * 0.65f, h * 0.70f)
        )

        drawCircle(
            color = tint,
            radius = w * 0.10f,
            center = Offset(w * 0.65f, h * 0.70f),
            style = Stroke(stroke)
        )
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

        drawCircle(
            color = tint,
            radius = w * 0.42f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = w * 0.09f)
        )

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.25f)
            lineTo(w * 0.5f, h * 0.5f)
            lineTo(w * 0.68f, h * 0.5f)
        }

        drawPath(
            path,
            color = tint,
            style = Stroke(
                width = w * 0.09f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
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

        drawPath(
            path,
            color = tint,
            style = Stroke(
                width = w * 0.10f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
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

        drawPath(
            plane,
            color = tint,
            style = Stroke(
                width = w * 0.08f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
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

        drawPath(
            body,
            color = tint,
            style = Stroke(
                width = w * 0.08f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        drawLine(
            color = tint,
            start = Offset(w * 0.35f, h * 0.15f),
            end = Offset(w * 0.35f, h * 0.28f),
            strokeWidth = w * 0.08f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = tint,
            start = Offset(w * 0.65f, h * 0.15f),
            end = Offset(w * 0.65f, h * 0.28f),
            strokeWidth = w * 0.08f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = tint,
            start = Offset(w * 0.18f, h * 0.45f),
            end = Offset(w * 0.82f, h * 0.45f),
            strokeWidth = w * 0.07f
        )
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

        drawPath(
            path,
            color = tint,
            style = Stroke(
                width = w * 0.08f,
                join = StrokeJoin.Round
            )
        )

        val path2 = Path().apply {
            moveTo(w * 0.18f, h * 0.55f)
            lineTo(w * 0.5f, h * 0.72f)
            lineTo(w * 0.82f, h * 0.55f)
        }

        drawPath(
            path2,
            color = tint,
            style = Stroke(
                width = w * 0.08f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
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

        drawPath(
            path,
            color = tint,
            style = Stroke(
                width = w * 0.15f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
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

        drawPath(
            roof,
            color = tint,
            style = Stroke(
                width = w * 0.10f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        val base = Path().apply {
            moveTo(w * 0.25f, h * 0.45f)
            lineTo(w * 0.25f, h * 0.82f)
            lineTo(w * 0.75f, h * 0.82f)
            lineTo(w * 0.75f, h * 0.45f)
        }

        drawPath(
            base,
            color = tint,
            style = Stroke(
                width = w * 0.10f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
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

        drawCircle(
            color = tint,
            radius = w * 0.30f,
            center = Offset(w * 0.42f, h * 0.42f),
            style = Stroke(width = w * 0.09f)
        )

        drawLine(
            color = tint,
            start = Offset(w * 0.62f, h * 0.62f),
            end = Offset(w * 0.85f, h * 0.85f),
            strokeWidth = w * 0.09f,
            cap = StrokeCap.Round
        )
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

        drawPath(
            doc,
            color = tint,
            style = Stroke(
                width = w * 0.08f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        drawLine(
            color = tint,
            start = Offset(w * 0.35f, h * 0.35f),
            end = Offset(w * 0.65f, h * 0.35f),
            strokeWidth = w * 0.07f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = tint,
            start = Offset(w * 0.35f, h * 0.50f),
            end = Offset(w * 0.65f, h * 0.50f),
            strokeWidth = w * 0.07f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = tint,
            start = Offset(w * 0.35f, h * 0.65f),
            end = Offset(w * 0.52f, h * 0.65f),
            strokeWidth = w * 0.07f,
            cap = StrokeCap.Round
        )
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

        drawCircle(
            color = tint,
            radius = w * 0.20f,
            center = Offset(w * 0.5f, h * 0.32f),
            style = Stroke(width = w * 0.08f)
        )

        val body = Path().apply {
            moveTo(w * 0.22f, h * 0.82f)
            cubicTo(
                w * 0.22f, h * 0.62f,
                w * 0.78f, h * 0.62f,
                w * 0.78f, h * 0.82f
            )
        }

        drawPath(
            body,
            color = tint,
            style = Stroke(
                width = w * 0.08f,
                cap = StrokeCap.Round
            )
        )
    }
}

// ----------------------------------------------------
// PREVIEW
// ----------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentHomeScreenPreview() {
    UniversityIntershipAppTheme {
        StudentHomeScreen()
    }
}
