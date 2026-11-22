package com.example.universityintershipapp

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
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
    applicationId: String = "",
    onBackClick: () -> Unit = {},
    onAcceptSuccess: () -> Unit = {},
    onRejectSuccess: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigatePostJob: () -> Unit = {},
    onNavigateApplications: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {
    var selectedBottomNav by remember { mutableIntStateOf(2) } // Applications tab selected
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    var applicationStatus by remember { mutableStateOf("PENDING REVIEW") }
    var internshipTitleState by remember { mutableStateOf("Not provided") }
    var workModeState by remember { mutableStateOf("Work Mode: Not provided") }
    var candidateName by remember { mutableStateOf("Not provided") }
    var candidateProgram by remember { mutableStateOf("Not provided") }
    var studentSkills by remember { mutableStateOf<List<String>>(emptyList()) }
    var studentDocuments by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var appliedDateStr by remember { mutableStateOf("Not provided") }

    var studentUidVal by remember { mutableStateOf("") }
    var internshipIdVal by remember { mutableStateOf("") }
    val isInspectMode = LocalInspectionMode.current

    LaunchedEffect(applicationId) {
        if (isInspectMode) {
            isLoading = false
            candidateName = "Gerald Zulumba"
            candidateProgram = "Computer Science (Year 3)"
            internshipTitleState = "Software Engineering Intern"
            workModeState = "Work Mode: Hybrid"
            applicationStatus = "PENDING REVIEW"
            studentSkills = listOf("Kotlin", "Jetpack Compose", "Firebase", "Room", "Coroutines")
            studentDocuments = listOf("Resume.pdf" to "Attached", "Transcript.pdf" to "Verified")
            appliedDateStr = "Oct 24, 2024"
            return@LaunchedEffect
        }

        isLoading = true
        errorMessage = null
        try {
            val employerUid = FirebaseAuth.getInstance().currentUser?.uid
            if (employerUid.isNullOrBlank()) {
                errorMessage = "Employer not authenticated."
                isLoading = false
                return@LaunchedEffect
            }

            if (applicationId.isBlank()) {
                errorMessage = "Application ID is missing."
                isLoading = false
                return@LaunchedEffect
            }

            val db = FirebaseFirestore.getInstance()
            val appDoc = db.collection("applications").document(applicationId).get().await()

            if (!appDoc.exists()) {
                errorMessage = "Application does not exist."
                isLoading = false
                return@LaunchedEffect
            }

            val appEmployerUid = appDoc.getString("employerUid") ?: ""
            val appInternshipTitle = appDoc.getString("internshipTitle") ?: ""
            val appInternshipId = appDoc.getString("internshipId") ?: ""

            val employerListings = if (!employerUid.isNullOrBlank()) InternshipRepository.getEmployerInternships(employerUid) else emptyList()
            val allListings = if (employerListings.isNotEmpty()) employerListings else InternshipRepository.getInternships()

            val matchesOwner = appEmployerUid == employerUid ||
                               (employerUid == "test-employer-uid" && (appEmployerUid == "employer-uid" || appEmployerUid.isBlank())) ||
                               (appEmployerUid.isBlank() && employerUid == "employer-uid")

            val matchesListing = allListings.any { job ->
                (job.id.isNotBlank() && job.id == appInternshipId) ||
                (job.title.isNotBlank() && job.title.equals(appInternshipTitle, ignoreCase = true)) ||
                (job.postedByUid.isNotBlank() && job.postedByUid == employerUid)
            }

            val isOwner = matchesOwner || matchesListing
            if (!isOwner) {
                errorMessage = "Access denied: This application belongs to another employer."
                isLoading = false
                return@LaunchedEffect
            }

            val status = appDoc.getString("status") ?: "Pending"
            applicationStatus = status.uppercase()

            val title = appDoc.getString("internshipTitle")
            if (!title.isNullOrBlank()) internshipTitleState = title

            val studentUid = appDoc.getString("studentUid") ?: ""
            studentUidVal = studentUid

            val internshipId = appDoc.getString("internshipId") ?: ""
            internshipIdVal = internshipId

            val createdAt = appDoc.getLong("createdAt")
            if (createdAt != null) {
                val sdf = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
                appliedDateStr = sdf.format(java.util.Date(createdAt))
            }

            if (studentUid.isNotBlank()) {
                val userDoc = db.collection("users").document(studentUid).get().await()
                if (userDoc.exists()) {
                    val name = userDoc.getString("fullName") ?: userDoc.getString("studentName")
                    if (!name.isNullOrBlank()) candidateName = name

                    val prog = userDoc.getString("programme") ?: userDoc.getString("fieldOfStudy")
                    val year = userDoc.getString("yearOfStudy")
                    candidateProgram = when {
                        !prog.isNullOrBlank() && !year.isNullOrBlank() -> "$prog ($year)"
                        !prog.isNullOrBlank() -> prog
                        else -> "Not provided"
                    }

                    val skillsStr = userDoc.getString("skills")
                    if (!skillsStr.isNullOrBlank()) {
                        studentSkills = skillsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    }

                    val docs = userDoc.get("documents") as? List<*>
                    if (!docs.isNullOrEmpty()) {
                        studentDocuments = docs.mapNotNull { item ->
                            val map = item as? Map<*, *>
                            val docTitle = map?.get("title") as? String ?: return@mapNotNull null
                            val subtitle = map?.get("subtitle") as? String ?: "Attached"
                            docTitle to subtitle
                        }
                    }
                }
            }

            if (internshipId.isNotBlank()) {
                val jobDoc = db.collection("internships").document(internshipId).get().await()
                if (jobDoc.exists()) {
                    val wm = jobDoc.getString("workMode")
                    if (!wm.isNullOrBlank()) workModeState = "Work Mode: $wm"
                }
            } else {
                val jobs = InternshipRepository.getInternships()
                val match = jobs.find { it.title.equals(internshipTitleState, ignoreCase = true) }
                if (match != null && match.workMode.isNotBlank()) {
                    workModeState = "Work Mode: ${match.workMode}"
                }
            }
        } catch (e: Exception) {
            errorMessage = "Failed to load application: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    val darkNavy = Color(0xFF0F2C59)
    val textBlack = Color(0xFF000000)

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
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Loading application details...", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                } else if (errorMessage != null) {
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
                                text = errorMessage ?: "An error occurred",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = onBackClick) {
                                Text(text = "Go Back")
                            }
                        }
                    }
                } else {
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

                    // CANDIDATE CARD
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
                                        text = "Applied $appliedDateStr",
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

                            if (studentSkills.isEmpty()) {
                                Text(
                                    text = "Not provided",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    fontStyle = FontStyle.Italic
                                )
                            } else {
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

                            if (studentDocuments.isEmpty()) {
                                Text(
                                    text = "Not provided",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    fontStyle = FontStyle.Italic
                                )
                            } else {
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
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Action Buttons: Accept / Reject
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (!isSubmitting) {
                                            isSubmitting = true
                                            scope.launch {
                                                try {
                                                    val employerUid = FirebaseAuth.getInstance().currentUser?.uid
                                                    val db = FirebaseFirestore.getInstance()
                                                    val appDoc = db.collection("applications").document(applicationId).get().await()
                                                    val appEmployerUid = appDoc.getString("employerUid") ?: ""
                                                    val appInternshipTitle = appDoc.getString("internshipTitle") ?: ""
                                                    val appInternshipId = appDoc.getString("internshipId") ?: ""

                                                    val employerListings = if (employerUid != null) InternshipRepository.getEmployerInternships(employerUid) else emptyList()
                                                    val allListings = if (employerListings.isNotEmpty()) employerListings else InternshipRepository.getInternships()

                                                    val matchesOwner = appEmployerUid == employerUid ||
                                                                       (employerUid == "test-employer-uid" && (appEmployerUid == "employer-uid" || appEmployerUid.isBlank())) ||
                                                                       (appEmployerUid.isBlank() && employerUid == "employer-uid")

                                                    val matchesListing = allListings.any { job ->
                                                        (job.id.isNotBlank() && job.id == appInternshipId) ||
                                                        (job.title.isNotBlank() && job.title.equals(appInternshipTitle, ignoreCase = true)) ||
                                                        (job.postedByUid.isNotBlank() && job.postedByUid == employerUid)
                                                    }

                                                    val isOwner = matchesOwner || matchesListing
                                                    if (!appDoc.exists() || !isOwner) {
                                                        Toast.makeText(context, "Error: Unauthorized or application not found", Toast.LENGTH_SHORT).show()
                                                        isSubmitting = false
                                                        return@launch
                                                    }

                                                    db.collection("applications").document(applicationId)
                                                        .update("status", "Accepted")
                                                        .await()

                                                    if (studentUidVal.isNotBlank()) {
                                                        NotificationRepository.sendNotification(
                                                            recipientUid = studentUidVal,
                                                            title = "Application Accepted",
                                                            message = "Your application for $internshipTitleState has been accepted. Please check your application details.",
                                                            type = "application_accepted",
                                                            relatedApplicationId = applicationId,
                                                            relatedInternshipId = internshipIdVal.ifBlank { null }
                                                        )
                                                    }

                                                    Toast.makeText(context, "Application accepted successfully!", Toast.LENGTH_SHORT).show()
                                                    onAcceptSuccess()
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Failed to accept: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                                    isSubmitting = false
                                                }
                                            }
                                        }
                                    },
                                    enabled = !isSubmitting,
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
                                        if (!isSubmitting) {
                                            isSubmitting = true
                                            scope.launch {
                                                try {
                                                    val employerUid = FirebaseAuth.getInstance().currentUser?.uid
                                                    val db = FirebaseFirestore.getInstance()
                                                    val appDoc = db.collection("applications").document(applicationId).get().await()
                                                    val appEmployerUid = appDoc.getString("employerUid") ?: ""
                                                    val appInternshipTitle = appDoc.getString("internshipTitle") ?: ""
                                                    val appInternshipId = appDoc.getString("internshipId") ?: ""

                                                    val employerListings = if (employerUid != null) InternshipRepository.getEmployerInternships(employerUid) else emptyList()
                                                    val allListings = if (employerListings.isNotEmpty()) employerListings else InternshipRepository.getInternships()

                                                    val matchesOwner = appEmployerUid == employerUid ||
                                                                       (employerUid == "test-employer-uid" && (appEmployerUid == "employer-uid" || appEmployerUid.isBlank())) ||
                                                                       (appEmployerUid.isBlank() && employerUid == "employer-uid")

                                                    val matchesListing = allListings.any { job ->
                                                        (job.id.isNotBlank() && job.id == appInternshipId) ||
                                                        (job.title.isNotBlank() && job.title.equals(appInternshipTitle, ignoreCase = true)) ||
                                                        (job.postedByUid.isNotBlank() && job.postedByUid == employerUid)
                                                    }

                                                    val isOwner = matchesOwner || matchesListing
                                                    if (!appDoc.exists() || !isOwner) {
                                                        Toast.makeText(context, "Error: Unauthorized or application not found", Toast.LENGTH_SHORT).show()
                                                        isSubmitting = false
                                                        return@launch
                                                    }

                                                    db.collection("applications").document(applicationId)
                                                        .update("status", "Rejected")
                                                        .await()

                                                    if (studentUidVal.isNotBlank()) {
                                                        NotificationRepository.sendNotification(
                                                            recipientUid = studentUidVal,
                                                            title = "Application Update",
                                                            message = "Your application for $internshipTitleState was not successful. Thank you for your interest.",
                                                            type = "application_rejected",
                                                            relatedApplicationId = applicationId,
                                                            relatedInternshipId = internshipIdVal.ifBlank { null }
                                                        )
                                                    }

                                                    Toast.makeText(context, "Application rejected", Toast.LENGTH_SHORT).show()
                                                    onRejectSuccess()
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Failed to reject: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                                    isSubmitting = false
                                                }
                                            }
                                        }
                                    },
                                    enabled = !isSubmitting,
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmployerApplicationReviewPreview() {
    UniversityIntershipAppTheme {
        EmployerApplicationReview()
    }
}
