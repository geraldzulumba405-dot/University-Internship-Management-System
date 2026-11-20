package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration

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
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var retryTrigger by remember { mutableStateOf(0) }

    DisposableEffect(retryTrigger) {
        isLoading = true
        errorMessage = null

        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid ?: "test-student-uid"
        var listener: ListenerRegistration? = null

        try {
            listener = FirebaseFirestore.getInstance().collection("applications")
                .whereEqualTo("studentUid", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    isLoading = false
                    if (error != null) {
                        errorMessage = error.localizedMessage ?: "Failed to retrieve applications from Firestore."
                    } else if (snapshot != null) {
                        myApplications = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(StudentApplicationModel::class.java)?.copy(id = doc.id)
                        }
                        errorMessage = null
                    }
                }
        } catch (e: Exception) {
            isLoading = false
            errorMessage = e.localizedMessage ?: "An error occurred while loading applications."
        }

        onDispose {
            listener?.remove()
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = NavyDark)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Loading your applications...", fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                } else if (errorMessage != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFEF2F2),
                        border = BorderStroke(1.dp, Color(0xFFFECACA))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Unable to load applications",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB91C1C)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = errorMessage ?: "Please check your network connection.",
                                fontSize = 13.sp,
                                color = Color(0xFF7F1D1D),
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { retryTrigger++ },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Retry", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
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
                                text = "You haven't submitted any applications yet.",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Browse and apply for verified internships to track your application status here.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                } else {
                    myApplications.forEach { app ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, OutlineVariant),
                            onClick = { onViewDetails(app.id) }
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
                                            Spacer(modifier = Modifier.height(2.dp))
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
                                    text = "Submitted: ${android.text.format.DateFormat.format("MMM dd, yyyy", java.util.Date(app.createdAt))}",
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyApplicationsScreenPreview() {
    UniversityIntershipAppTheme {
        MyApplicationsScreen()
    }
}
