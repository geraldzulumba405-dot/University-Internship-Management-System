package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

private val MintSuccessContainer = Color(0xFFCCFBF1)
private val RedAccent = Color(0xFFE11D48)
private val RedAccentContainer = Color(0xFFFFE4E6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitApplicationScreen(
    internshipId: String = "",
    title: String = "Software Engineering Intern",
    company: String = "Google LLC",
    location: String = "Mountain View, CA (Hybrid)",
    onCloseClick: () -> Unit = {},
    onSaveDraftClick: () -> Unit = {},
    onSubmitApplicationClick: (() -> Unit)? = null
) {
    var studentNameState by remember { mutableStateOf("Alex Chen") }
    var studentIdState by remember { mutableStateOf("U2021-0842") }
    var programmeState by remember { mutableStateOf("B.Sc. Computer Science (Yr 3)") }

    var internshipTitleState by remember { mutableStateOf(title) }
    var companyState by remember { mutableStateOf(company) }
    var locationState by remember { mutableStateOf(location) }
    var deadlineState by remember { mutableStateOf("May 15, 2025") }
    var workModeState by remember { mutableStateOf("On-site") }

    var uploadedDocuments by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var isLoadingDocs by remember { mutableStateOf(true) }
    var isAvailable by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = getFileName(context, uri) ?: "Document_${System.currentTimeMillis()}"
            scope.launch {
                try {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null) {
                        val db = FirebaseFirestore.getInstance()
                        val userRef = db.collection("users").document(uid)
                        val snapshot = userRef.get().await()
                        val existingDocs = (snapshot.get("documents") as? List<*>)?.mapNotNull { item ->
                            val map = item as? Map<*, *>
                            val t = map?.get("title") as? String ?: return@mapNotNull null
                            val s = map?.get("subtitle") as? String ?: return@mapNotNull null
                            mapOf("title" to t, "subtitle" to s)
                        } ?: emptyList()

                        val updatedDocs = existingDocs + listOf(mapOf("title" to fileName, "subtitle" to "PDF • Uploaded just now"))
                        userRef.update("documents", updatedDocs).await()
                        uploadedDocuments = updatedDocs.map { (it["title"] as String) to (it["subtitle"] as String) }
                        Toast.makeText(context, "Document added successfully!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Upload failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(title) {
        isLoadingDocs = true
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val userDoc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
                if (userDoc.exists()) {
                    val name = userDoc.getString("fullName") ?: userDoc.getString("studentName")
                    if (!name.isNullOrBlank()) studentNameState = name
                    val sid = userDoc.getString("studentId")
                    if (!sid.isNullOrBlank()) studentIdState = sid
                    val prog = userDoc.getString("programme")
                    val year = userDoc.getString("yearOfStudy")
                    if (!prog.isNullOrBlank()) {
                        programmeState = if (!year.isNullOrBlank()) "$prog ($year)" else prog
                    }

                    val docs = userDoc.get("documents") as? List<*>
                    if (docs != null) {
                        uploadedDocuments = docs.mapNotNull { item ->
                            val map = item as? Map<*, *>
                            val docTitle = map?.get("title") as? String ?: "Document"
                            val subtitle = map?.get("subtitle") as? String ?: "Uploaded"
                            docTitle to subtitle
                        }
                    }
                }
            }

            val jobs = InternshipRepository.getInternships()
            if (jobs.isEmpty()) {
                isAvailable = false
            } else {
                val match = jobs.find { it.title.trim().equals(title.trim(), ignoreCase = true) }
                    ?: jobs.find { it.title.contains(title, ignoreCase = true) || title.contains(it.title, ignoreCase = true) }
                    ?: jobs.firstOrNull()

                if (match != null) {
                    if (match.title.isNotBlank()) internshipTitleState = match.title
                    if (match.companyName.isNotBlank()) companyState = match.companyName
                    if (match.location.isNotBlank()) locationState = match.location
                    if (match.deadline.isNotBlank()) deadlineState = match.deadline
                    if (match.workMode.isNotBlank()) workModeState = match.workMode
                    isAvailable = true
                } else {
                    isAvailable = true
                }
            }
        } catch (_: Exception) {
            isAvailable = true
        } finally {
            isLoadingDocs = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Submit Application",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        CloseIcon(modifier = Modifier.size(20.dp), tint = TextPrimary)
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceBackground)
            )
        },
        containerColor = SurfaceBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoadingDocs) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Loading application details...", fontSize = 13.sp, color = TextSecondary)
                }
            } else if (!isAvailable) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = CardBackground,
                    border = BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No application to submit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            } else {
                // 1. Internship Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFEFF6FF)
                            ) {
                                Text(
                                    text = workModeState,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E3A8A),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = "Deadline: $deadlineState",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = internshipTitleState,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$companyState • $locationState",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }

                // 2. Verified Academic Profile Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Academic Profile",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                        }
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Student Name", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = studentNameState,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(text = "Student ID", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = studentIdState,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Program of Study", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = programmeState,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                // 3. Required Credentials & Attached Documents
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Required Credentials (${uploadedDocuments.size} files attached)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    if (uploadedDocuments.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, OutlineVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No documents uploaded in profile yet.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }
                        }
                    } else {
                        uploadedDocuments.forEach { (docTitle, docSubtitle) ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackground),
                                border = BorderStroke(1.dp, OutlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = docTitle,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = docSubtitle,
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                                                    if (uid != null) {
                                                        val db = FirebaseFirestore.getInstance()
                                                        val userRef = db.collection("users").document(uid)
                                                        val updatedDocs = uploadedDocuments.filterNot { it.first == docTitle }
                                                            .map { mapOf("title" to it.first, "subtitle" to it.second) }
                                                        userRef.update("documents", updatedDocs).await()
                                                        uploadedDocuments = updatedDocs.map { (it["title"] as String) to (it["subtitle"] as String) }
                                                        Toast.makeText(context, "Document removed successfully!", Toast.LENGTH_SHORT).show()
                                                    }
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Removal failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    ) {
                                        ClearIcon(
                                            modifier = Modifier.size(16.dp),
                                            tint = RedAccent
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { launcher.launch("*/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF1B3B80))
                ) {
                    Text(
                        text = "Add Document",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B3B80)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (onSubmitApplicationClick != null) {
                            onSubmitApplicationClick()
                        } else {
                            scope.launch {
                                try {
                                    val currentUser = FirebaseAuth.getInstance().currentUser
                                    val studentUid = currentUser?.uid ?: "test-student-uid"
                                    val studentEmail = currentUser?.email ?: "student@example.com"

                                    val db = FirebaseFirestore.getInstance()
                                    var targetInternshipId = internshipId
                                    var employerUid = ""

                                    if (targetInternshipId.isBlank()) {
                                        try {
                                            val snapshot = db.collection("internships")
                                                .whereEqualTo("title", internshipTitleState)
                                                .get()
                                                .await()
                                            if (!snapshot.isEmpty) {
                                                val doc = snapshot.documents.first()
                                                targetInternshipId = doc.id
                                                employerUid = doc.getString("postedByUid") ?: ""
                                            }
                                        } catch (_: Exception) {}
                                    }

                                    if (targetInternshipId.isNotBlank() && employerUid.isBlank()) {
                                        try {
                                            val internshipSnapshot = db.collection("internships")
                                                .document(targetInternshipId)
                                                .get()
                                                .await()
                                            if (internshipSnapshot.exists()) {
                                                employerUid = internshipSnapshot.getString("postedByUid") ?: ""
                                            }
                                        } catch (_: Exception) {}
                                    }

                                    if (employerUid.isBlank()) {
                                        employerUid = "employer-uid"
                                    }
                                    if (targetInternshipId.isBlank()) {
                                        targetInternshipId = "internship-${System.currentTimeMillis()}"
                                    }

                                    val appData = hashMapOf(
                                        "studentUid" to studentUid,
                                        "studentEmail" to studentEmail,
                                        "internshipId" to targetInternshipId,
                                        "employerUid" to employerUid,
                                        "internshipTitle" to internshipTitleState,
                                        "company" to companyState,
                                        "location" to locationState,
                                        "status" to "Under Review",
                                        "createdAt" to System.currentTimeMillis()
                                    )

                                    val applicationRef = db.collection("applications")
                                        .add(appData)
                                        .await()

                                    NotificationRepository.sendNotification(
                                        recipientUid = studentUid,
                                        title = "Application Sent & Under Review",
                                        message = "Your application for $internshipTitleState at $companyState has been sent and is currently under review.",
                                        type = "application_submitted",
                                        relatedInternshipId = targetInternshipId,
                                        relatedApplicationId = applicationRef.id
                                    )

                                    val notifyUids = listOfNotNull(
                                        employerUid,
                                        "employer-uid",
                                        "test-employer-uid"
                                    ).distinct()

                                    for (eUid in notifyUids) {
                                        if (eUid.isNotBlank()) {
                                            try {
                                                NotificationRepository.sendNotification(
                                                    recipientUid = eUid,
                                                    title = "New Internship Application",
                                                    message = "A student ($studentNameState) has submitted an application for your $internshipTitleState opportunity.",
                                                    type = "new_application",
                                                    relatedInternshipId = targetInternshipId,
                                                    relatedApplicationId = applicationRef.id
                                                )
                                            } catch (_: Exception) {}
                                        }
                                    }

                                    Toast.makeText(context, "Application submitted successfully!", Toast.LENGTH_SHORT).show()
                                    onSaveDraftClick()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Failed to submit application: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text(
                            text = "Submit Application",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        RightArrowIcon(
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private fun getFileName(context: android.content.Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    result = cursor.getString(nameIndex)
                }
            }
        } catch (_: Exception) {
            cursor?.close()
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SubmitApplicationPreview() {
    UniversityIntershipAppTheme {
        SubmitApplicationScreen()
    }
}
