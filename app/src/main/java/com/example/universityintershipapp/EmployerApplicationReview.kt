package com.example.universityintershipapp

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmployerApplicationReview(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onAcceptSuccess: () -> Unit = {},
    onRejectSuccess: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigatePostJob: () -> Unit = {},
    onNavigateApplications: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(1) } // 0: All, 1: Pending, 2: Accepted, 3: Rejected
    var selectedBottomNav by remember { mutableIntStateOf(2) } // Applications tab selected
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    var applicationStatus by remember { mutableStateOf("PENDING REVIEW") }
    var currentAppDocId by remember { mutableStateOf<String?>(null) }
    var internshipTitleState by remember { mutableStateOf("Full Stack Web Developer") }
    var workModeState by remember { mutableStateOf("Work Mode: On-site") }
    var candidateName by remember { mutableStateOf("Gerald Zulumba") }
    var candidateProgram by remember { mutableStateOf("B.Sc. Computer Engineering (Year 3)") }
    var studentSkills by remember { mutableStateOf(listOf("React", "Node.js", "PostgreSQL", "Docker", "AWS")) }
    var studentDocuments by remember {
        mutableStateOf(
            listOf(
                "Resume.pdf" to "240 KB",
                "Transcript" to "Official Seal",
                "Portfolio" to "GitHub/Live"
            )
        )
    }

    LaunchedEffect(Unit) {
        try {
            val appsSnapshot = FirebaseFirestore.getInstance().collection("applications").get().await()
            if (!appsSnapshot.isEmpty) {
                val firstDoc = appsSnapshot.documents.first()
                currentAppDocId = firstDoc.id
                val firstApp = firstDoc.data
                if (firstApp != null) {
                    val status = firstApp["status"] as? String
                    if (!status.isNullOrBlank()) {
                        applicationStatus = status.uppercase()
                    }
                    val title = firstApp["internshipTitle"] as? String
                    if (!title.isNullOrBlank()) internshipTitleState = title

                    val studentUid = firstApp["studentUid"] as? String
                    if (!studentUid.isNullOrBlank()) {
                        val userDoc = FirebaseFirestore.getInstance().collection("users").document(studentUid).get().await()
                        if (userDoc.exists()) {
                            val name = userDoc.getString("fullName") ?: userDoc.getString("studentName")
                            if (!name.isNullOrBlank()) candidateName = name
                            val prog = userDoc.getString("programme")
                            val year = userDoc.getString("yearOfStudy")
                            if (!prog.isNullOrBlank()) {
                                candidateProgram = if (!year.isNullOrBlank()) "$prog ($year)" else prog
                            }
                            val skillsStr = userDoc.getString("skills")
                            if (!skillsStr.isNullOrBlank()) {
                                studentSkills = skillsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            }
                            val docs = userDoc.get("documents") as? List<*>
                            if (!docs.isNullOrEmpty()) {
                                studentDocuments = docs.mapNotNull { item ->
                                    val map = item as? Map<*, *>
                                    val docTitle = map?.get("title") as? String ?: "Document"
                                    val subtitle = map?.get("subtitle") as? String ?: "Attached"
                                    docTitle to subtitle
                                }
                            }
                        }
                    }
                }
            }

            val jobs = InternshipRepository.getInternships()
            val match = jobs.find { it.title.equals(internshipTitleState, ignoreCase = true) }
            if (match != null) {
                if (match.workMode.isNotBlank()) workModeState = "Work Mode: ${match.workMode}"
            }
        } catch (_: Exception) {}
    }

    val darkNavy = Color(0xFF0F2C59)
    val textBlack = Color(0xFF000000)
    val textDarkGrey = Color(0xFF171717)
    val textHelperGrey = Color(0xFF404040)

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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    BackArrowIcon(
                        modifier = Modifier.size(20.dp),
                        tint = textBlack
                    )
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Applicant Review",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkNavy
                    )
                }

                Spacer(modifier = Modifier.size(36.dp))
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // 2. Scrollable Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Active Internship Header Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        Text(
                            text = internshipTitleState,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textBlack,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = workModeState,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF525252)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // MAIN FOCUS CANDIDATE CARD (Gerald Zulumba)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Candidate Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = candidateName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textBlack
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = candidateProgram,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334155),
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Status & Date Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Status:",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = when (applicationStatus) {
                                        "ACCEPTED" -> Color(0xFFDCFCE7)
                                        "REJECTED" -> Color(0xFFFEE2E2)
                                        else -> Color(0xFFEFF6FF)
                                    },
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(
                                                    when (applicationStatus) {
                                                        "ACCEPTED" -> Color(0xFF10B981)
                                                        "REJECTED" -> Color(0xFFEF4444)
                                                        else -> Color(0xFF3B82F6)
                                                    },
                                                    CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = applicationStatus,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (applicationStatus) {
                                                "ACCEPTED" -> Color(0xFF166534)
                                                "REJECTED" -> Color(0xFF991B1B)
                                                else -> Color(0xFF1E3A8A)
                                            }
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Applied May 3, 2025",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF475569)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Student Skills Section
                        Text(
                            text = "STUDENT SKILLS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            studentSkills.forEach { skill ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEEF2FF)
                                ) {
                                    Text(
                                        text = skill,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))



                        // Verification Documents Section
                        Text(
                            text = "VERIFICATION DOCUMENTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            studentDocuments.forEach { (docTitle, docSubtitle) ->
                                DocCard(
                                    modifier = Modifier.weight(1f),
                                    title = docTitle,
                                    subtitle = docSubtitle,
                                    onClick = {
                                        val url = when {
                                            docTitle.contains("Portfolio", ignoreCase = true) || docTitle.contains("GitHub", ignoreCase = true) -> "https://github.com"
                                            else -> "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
                                        }
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                            Toast.makeText(context, "Opening $docTitle...", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Action Buttons: Accept / Reject
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    applicationStatus = "ACCEPTED"
                                    Toast.makeText(context, "Application accepted successfully!", Toast.LENGTH_SHORT).show()
                                    scope.launch {
                                        try {
                                            if (currentAppDocId != null) {
                                                val appDoc = FirebaseFirestore.getInstance().collection("applications")
                                                    .document(currentAppDocId!!)
                                                    .get()
                                                    .await()
                                                val studentUid = appDoc.getString("studentUid")
                                                val title = appDoc.getString("internshipTitle") ?: internshipTitleState

                                                FirebaseFirestore.getInstance().collection("applications")
                                                    .document(currentAppDocId!!)
                                                    .update("status", "Accepted")
                                                    .await()

                                                if (!studentUid.isNullOrBlank()) {
                                                    NotificationRepository.sendNotification(
                                                        recipientUserId = studentUid,
                                                        title = "Application Accepted!",
                                                        message = "Congratulations! Your application for $title has been accepted.",
                                                        type = "application"
                                                    )
                                                }
                                            }
                                        } catch (_: Exception) {}
                                    }
                                    onAcceptSuccess()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CheckIcon(modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Accept",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    applicationStatus = "REJECTED"
                                    Toast.makeText(context, "Application rejected", Toast.LENGTH_SHORT).show()
                                    scope.launch {
                                        try {
                                            if (currentAppDocId != null) {
                                                val appDoc = FirebaseFirestore.getInstance().collection("applications")
                                                    .document(currentAppDocId!!)
                                                    .get()
                                                    .await()
                                                val studentUid = appDoc.getString("studentUid")
                                                val title = appDoc.getString("internshipTitle") ?: internshipTitleState

                                                FirebaseFirestore.getInstance().collection("applications")
                                                    .document(currentAppDocId!!)
                                                    .update("status", "Rejected")
                                                    .await()

                                                if (!studentUid.isNullOrBlank()) {
                                                    NotificationRepository.sendNotification(
                                                        recipientUserId = studentUid,
                                                        title = "Application Update",
                                                        message = "Your application for $title was not selected at this time.",
                                                        type = "application"
                                                    )
                                                }
                                            }
                                        } catch (_: Exception) {}
                                    }
                                    onRejectSuccess()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CloseIcon(modifier = Modifier.size(16.dp), tint = Color(0xFF0F172A))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Reject",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
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
private fun DocCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
private fun QueueApplicantCard(
    name: String,
    gpa: String,
    program: String,
    date: String,
    statusText: String,
    statusBg: Color,
    statusTextCol: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Blank Profile Avatar Box (Replaced photo with neutral blank profile icon)
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF1F5F9),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    PersonIcon(
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = gpa,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$program • $date",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(50),
                color = statusBg
            ) {
                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusTextCol,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            RightChevronIcon(
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF94A3B8)
            )
        }
    }
}

// Icon Components
@Composable
private fun SortSwapIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E3A8A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.12f

        // Up Arrow
        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.8f), end = Offset(w * 0.35f, h * 0.2f), strokeWidth = stroke, cap = StrokeCap.Round)
        val upHead = Path().apply {
            moveTo(w * 0.15f, h * 0.4f)
            lineTo(w * 0.35f, h * 0.18f)
            lineTo(w * 0.55f, h * 0.4f)
        }
        drawPath(upHead, color = tint, style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Down Arrow
        drawLine(color = tint, start = Offset(w * 0.65f, h * 0.2f), end = Offset(w * 0.65f, h * 0.8f), strokeWidth = stroke, cap = StrokeCap.Round)
        val downHead = Path().apply {
            moveTo(w * 0.45f, h * 0.6f)
            lineTo(w * 0.65f, h * 0.82f)
            lineTo(w * 0.85f, h * 0.6f)
        }
        drawPath(downHead, color = tint, style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun PdfDocIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f

        drawRect(
            color = Color(0xFFDC2626),
            topLeft = Offset(w * 0.15f, h * 0.15f),
            size = Size(w * 0.70f, h * 0.70f),
            style = Stroke(stroke)
        )
        drawLine(
            color = Color(0xFFDC2626),
            start = Offset(w * 0.30f, h * 0.45f),
            end = Offset(w * 0.70f, h * 0.45f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun TranscriptDocIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f

        val cap = Path().apply {
            moveTo(w * 0.5f, h * 0.2f)
            lineTo(w * 0.85f, h * 0.4f)
            lineTo(w * 0.5f, h * 0.6f)
            lineTo(w * 0.15f, h * 0.4f)
            close()
        }
        drawPath(cap, color = Color(0xFF1E3A8A))
    }
}

@Composable
private fun PortfolioDocIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f

        drawRect(
            color = Color(0xFF0F766E),
            topLeft = Offset(w * 0.2f, h * 0.2f),
            size = Size(w * 0.6f, h * 0.6f),
            style = Stroke(stroke)
        )
        drawLine(color = Color(0xFF0F766E), start = Offset(w * 0.5f, h * 0.2f), end = Offset(w * 0.8f, h * 0.2f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = Color(0xFF0F766E), start = Offset(w * 0.8f, h * 0.2f), end = Offset(w * 0.8f, h * 0.5f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun ChatMessageIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F2C59)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f

        val msg = Path().apply {
            moveTo(w * 0.15f, h * 0.25f)
            lineTo(w * 0.85f, h * 0.25f)
            lineTo(w * 0.85f, h * 0.65f)
            lineTo(w * 0.45f, h * 0.65f)
            lineTo(w * 0.25f, h * 0.82f)
            lineTo(w * 0.25f, h * 0.65f)
            lineTo(w * 0.15f, h * 0.65f)
            close()
        }
        drawPath(msg, color = tint, style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmployerApplicationReviewPreview() {
    UniversityIntershipAppTheme {
        EmployerApplicationReview()
    }
}
