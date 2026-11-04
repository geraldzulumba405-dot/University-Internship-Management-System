package com.example.universityintershipapp

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

private val MintContainer = Color(0xFFCCFBF1)
private val LightNavyBg = Color(0xFFEFF4FF)
private val RedAlertColor = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(
    modifier: Modifier = Modifier,
    studentName: String = "Gerald Zulumba",
    studentEmail: String = "bsc-inf-18-23@unima.ac.mw",
    studentId: String = "BSC-INF-18-23",
    programme: String = "BSc Computer Science",
    yearOfStudy: String = "Year 4",
    onNavigateHome: () -> Unit = {},
    onNavigateSearch: () -> Unit = {},
    onNavigateApplications: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onLogOut: () -> Unit = {},
    onAddDocument: () -> Unit = {}
) {
    var studentNameState by remember { mutableStateOf(studentName) }
    var studentEmailState by remember { mutableStateOf(studentEmail) }
    var programmeState by remember { mutableStateOf(programme) }
    var yearOfStudyState by remember { mutableStateOf(yearOfStudy) }

    var documentsList by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = getFileName(context, uri) ?: "Document_${System.currentTimeMillis()}"
            documentsList = documentsList + (fileName to "Uploaded just now")
        }
    }

    LaunchedEffect(Unit) {
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val userDoc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
                if (userDoc.exists()) {
                    val name = userDoc.getString("fullName") ?: userDoc.getString("studentName")
                    if (!name.isNullOrBlank()) studentNameState = name
                    val email = userDoc.getString("email")
                    if (!email.isNullOrBlank()) studentEmailState = email
                    val prog = userDoc.getString("programme")
                    if (!prog.isNullOrBlank()) programmeState = prog
                    val year = userDoc.getString("yearOfStudy")
                    if (!year.isNullOrBlank()) yearOfStudyState = year
                }
            }
        } catch (_: Exception) {}
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Student Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            }

            HorizontalDivider(color = OutlineVariant, thickness = 1.dp)

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Identity & Academic Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(studentNameState, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$programmeState • $yearOfStudyState",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            studentEmailState,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                }

                // 2. Credentials & Documents
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Credentials & Documents",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                onAddDocument()
                                launcher.launch("*/*")
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            PlusIcon(modifier = Modifier.size(14.dp), tint = NavyPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Add Document", fontSize = 11.sp, color = NavyPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (documentsList.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = CardBackground,
                        border = BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No attached documents available",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    documentsList.forEach { (docTitle, docSubtitle) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, OutlineVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Text(docTitle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text(docSubtitle, fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }

                // 3. Account & Security
                Text(
                    "Account & Security",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column {
                        SettingsRow(
                            title = "Notification Preferences",
                            subtitle = "Push notifications & email alerts"
                        )
                    }
                }

                // 4. Log Out CTA
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF3B82F6)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    LogoutIcon(modifier = Modifier.size(18.dp), tint = NavyPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", color = NavyPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bottom Navigation Bar
            HorizontalDivider(color = OutlineVariant, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground)
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    label = "Home",
                    isSelected = false,
                    icon = { HomeNavIcon(modifier = Modifier.size(20.dp), tint = TextSecondary) },
                    onClick = onNavigateHome
                )

                BottomNavItem(
                    label = "Internships",
                    isSelected = false,
                    icon = { BriefcaseIcon(modifier = Modifier.size(20.dp), tint = TextSecondary) },
                    onClick = onNavigateSearch
                )

                BottomNavItem(
                    label = "Applications",
                    isSelected = false,
                    icon = { ApplicationsNavIcon(modifier = Modifier.size(20.dp), tint = TextSecondary) },
                    onClick = onNavigateApplications
                )

                BottomNavItem(
                    label = "Profile",
                    isSelected = true,
                    icon = { ProfileNavIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF1E3A8A)) },
                    onClick = { }
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(text = "Log Out", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            },
            text = {
                Text(text = "Are you sure you want to log out?", fontSize = 13.sp, color = Color(0xFF334155))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        FirebaseAuth.getInstance().signOut()
                        onLogOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
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

private fun getFileName(context: android.content.Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } catch (_: Exception) {
            cursor?.close()
        }
        cursor?.close()
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1 && cut != null) {
            result = result.substring(cut + 1)
        }
    }
    return result
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
        }
        RightChevronIcon(modifier = Modifier.size(14.dp), tint = TextSecondary)
    }
}

@Composable
fun EditIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(w * 0.65f, h * 0.20f)
            lineTo(w * 0.80f, h * 0.35f)
            lineTo(w * 0.35f, h * 0.80f)
            lineTo(w * 0.20f, h * 0.80f)
            lineTo(w * 0.20f, h * 0.65f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentProfileScreenPreview() {
    UniversityIntershipAppTheme {
        StudentProfileScreen()
    }
}
