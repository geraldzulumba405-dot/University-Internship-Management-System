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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

data class StudentApplicationModel(
    val id: String = "",
    val studentUid: String = "",
    val internshipTitle: String = "",
    val company: String = "",
    val location: String = "",
    val status: String = "Under Review",
    val createdAt: Long = System.currentTimeMillis()
)

private val NavyDark = Color(0xFF0F1E4A)
private val LightNavyContainerVal = Color(0xFFEFF4FF)
private val TealPrimary = Color(0xFF0F766E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplicationsScreen(
    modifier: Modifier = Modifier,
    onNavigateHome: () -> Unit = {},
    onNavigateSearch: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onViewDetails: (String) -> Unit = {}
) {
    var myApplications by remember { mutableStateOf<List<StudentApplicationModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val snapshot = FirebaseFirestore.getInstance().collection("applications")
                    .whereEqualTo("studentUid", uid)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
                myApplications = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(StudentApplicationModel::class.java)?.copy(id = doc.id)
                }
            }
        } catch (_: Exception) {
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    val snapshot = FirebaseFirestore.getInstance().collection("applications")
                        .whereEqualTo("studentUid", uid)
                        .get()
                        .await()
                    myApplications = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(StudentApplicationModel::class.java)?.copy(id = doc.id)
                    }
                }
            } catch (_: Exception) {
                myApplications = emptyList()
            }
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top App Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "My Applications",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            }

            HorizontalDivider(color = OutlineVariant, thickness = 1.dp)

            // Scrollable Content Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Loading your applications...", fontSize = 13.sp, color = TextSecondary)
                    }
                } else if (myApplications.isEmpty()) {
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
                                text = "No applications submitted yet.",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Browse and apply for verified internships to track your status here.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    myApplications.forEach { app ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, OutlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Row(modifier = Modifier.weight(1f)) {
                                        Column {
                                            Text(
                                                text = app.internshipTitle,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${app.company} • ${app.location}",
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                    Surface(
                                        color = LightNavyContainerVal,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = app.status.ifBlank { "Under Review" },
                                            color = NavyDark,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "🕒 Submitted: ${android.text.format.DateFormat.format("MMM dd, yyyy", java.util.Date(app.createdAt))}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
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
                    isSelected = true,
                    icon = { ApplicationsNavIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF1E3A8A)) },
                    onClick = { }
                )

                BottomNavItem(
                    label = "Profile",
                    isSelected = false,
                    icon = { ProfileNavIcon(modifier = Modifier.size(20.dp), tint = TextSecondary) },
                    onClick = onNavigateProfile
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    label: String,
    count: String,
    subtext: String,
    icon: @Composable () -> Unit,
    iconColor: Color,
    borderColor: Color,
    bgColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, fontSize = 11.sp, color = TextSecondary)
                icon()
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = count,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (iconColor == Color.Red) Color.Red else NavyDark
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = subtext,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(text: String, isDone: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(if (isDone) TealPrimary else OutlineVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Text(text = "✓", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 11.sp, color = if (isDone) TealPrimary else TextSecondary, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MenuIcon(modifier: Modifier = Modifier, tint: Color = NavyDark) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawLine(color = tint, start = Offset(w * 0.1f, h * 0.25f), end = Offset(w * 0.9f, h * 0.25f), strokeWidth = w * 0.12f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.1f, h * 0.50f), end = Offset(w * 0.9f, h * 0.50f), strokeWidth = w * 0.12f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.1f, h * 0.75f), end = Offset(w * 0.9f, h * 0.75f), strokeWidth = w * 0.12f, cap = StrokeCap.Round)
    }
}

@Composable
fun SendIcon(modifier: Modifier = Modifier, tint: Color = NavyDark) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.1f, h * 0.15f)
            lineTo(w * 0.9f, h * 0.5f)
            lineTo(w * 0.1f, h * 0.85f)
            lineTo(w * 0.25f, h * 0.5f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun WarningIcon(modifier: Modifier = Modifier, tint: Color = Color.Red) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val tri = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            lineTo(w * 0.88f, h * 0.82f)
            lineTo(w * 0.12f, h * 0.82f)
            close()
        }
        drawPath(tri, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(color = tint, start = Offset(w * 0.5f, h * 0.38f), end = Offset(w * 0.5f, h * 0.58f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.70f))
    }
}

@Composable
fun CodeIcon(modifier: Modifier = Modifier, tint: Color = NavyDark) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val left = Path().apply {
            moveTo(w * 0.38f, h * 0.28f)
            lineTo(w * 0.18f, h * 0.50f)
            lineTo(w * 0.38f, h * 0.72f)
        }
        drawPath(left, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val right = Path().apply {
            moveTo(w * 0.62f, h * 0.28f)
            lineTo(w * 0.82f, h * 0.50f)
            lineTo(w * 0.62f, h * 0.72f)
        }
        drawPath(right, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun VisibilityIcon(modifier: Modifier = Modifier, tint: Color = NavyDark) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val eye = Path().apply {
            moveTo(w * 0.12f, h * 0.5f)
            cubicTo(w * 0.32f, h * 0.22f, w * 0.68f, h * 0.22f, w * 0.88f, h * 0.5f)
            cubicTo(w * 0.68f, h * 0.78f, w * 0.32f, h * 0.78f, w * 0.12f, h * 0.5f)
            close()
        }
        drawPath(eye, color = tint, style = Stroke(width = w * 0.08f))
        drawCircle(color = tint, radius = w * 0.14f, center = Offset(w * 0.5f, h * 0.5f))
    }
}

@Composable
fun VideocamIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val body = Path().apply {
            moveTo(w * 0.15f, h * 0.28f)
            lineTo(w * 0.62f, h * 0.28f)
            lineTo(w * 0.62f, h * 0.72f)
            lineTo(w * 0.15f, h * 0.72f)
            close()
        }
        drawPath(body, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun BarChartIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFF0F766E)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawRect(color = tint, topLeft = Offset(w * 0.15f, h * 0.5f), size = androidx.compose.ui.geometry.Size(w * 0.2f, h * 0.35f))
        drawRect(color = tint, topLeft = Offset(w * 0.40f, h * 0.2f), size = androidx.compose.ui.geometry.Size(w * 0.2f, h * 0.65f))
        drawRect(color = tint, topLeft = Offset(w * 0.65f, h * 0.35f), size = androidx.compose.ui.geometry.Size(w * 0.2f, h * 0.50f))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyApplicationsPreview() {
    UniversityIntershipAppTheme {
        MyApplicationsScreen()
    }
}
