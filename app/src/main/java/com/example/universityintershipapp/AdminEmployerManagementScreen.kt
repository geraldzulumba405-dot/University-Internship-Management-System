package com.example.universityintershipapp

import android.widget.Toast
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val AdminDarkNavy = Color(0xFF0F2C59)
private val AdminAccentBlue = Color(0xFF1E3A8A)

@Composable
fun AdminEmployerManagementScreen(
    modifier: Modifier = Modifier,
    onNavigateDashboard: () -> Unit = {},
    onNavigateInternships: () -> Unit = {},
    onNavigateReports: () -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedBottomNav by remember { mutableIntStateOf(1) }

    var employersList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val db = FirebaseFirestore.getInstance()
            val snap = db.collection("users").whereEqualTo("role", "employer").get().await()
            val fetched = snap.documents.mapNotNull { doc ->
                val data = doc.data?.toMutableMap() ?: mutableMapOf()
                data["id"] = doc.id
                data
            }
            employersList = fetched
        } catch (_: Exception) {
            employersList = emptyList()
        } finally {
            isLoading = false
        }
    }

    val updateStatus: (String, String) -> Unit = { docId, newStatus ->
        scope.launch {
            try {
                FirebaseFirestore.getInstance().collection("users")
                    .document(docId)
                    .update(mapOf("status" to newStatus, "isVerified" to (newStatus == "Approved")))
                    .await()

                val db = FirebaseFirestore.getInstance()
                val snap = db.collection("users").whereEqualTo("role", "employer").get().await()
                employersList = snap.documents.mapNotNull { doc ->
                    val data = doc.data?.toMutableMap() ?: mutableMapOf()
                    data["id"] = doc.id
                    data
                }
                Toast.makeText(context, "Employer status updated to $newStatus", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to update: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val filteredEmployers = if (searchQuery.isBlank()) {
        employersList
    } else {
        employersList.filter { emp ->
            val compName = (emp["companyName"] as? String) ?: ""
            compName.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

    val pendingEmployers = filteredEmployers.filter { emp ->
        val status = (emp["status"] as? String) ?: "Pending"
        status.equals("Pending", ignoreCase = true) || status.isBlank()
    }
    val otherEmployers = filteredEmployers.filter { emp ->
        val status = (emp["status"] as? String) ?: "Pending"
        !status.equals("Pending", ignoreCase = true) && status.isNotBlank()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Employer Management",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )
                    Text(
                        text = "Employer Registration Preview",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
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
                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy),
                    placeholder = { Text("Search by company name", color = Color(0xFF64748B), fontSize = 14.sp) },
                    leadingIcon = { SearchNavIcon(modifier = Modifier.size(20.dp), tint = AdminDarkNavy) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AdminDarkNavy,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Loading employers...", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                } else if (filteredEmployers.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.5.dp, Color(0xFFCBD5E1))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No employer registrations found.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else {
                    // Pending Vetting Section
                    if (pendingEmployers.isNotEmpty()) {
                        Text(
                            text = "Pending Review (${pendingEmployers.size})",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdminDarkNavy
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        pendingEmployers.forEach { emp ->
                            EmployerCard(
                                employer = emp,
                                onApprove = { updateStatus(emp["id"] as String, "Approved") },
                                onReject = { updateStatus(emp["id"] as String, "Rejected") }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Other Pending Employers (or Processed Employers) Section
                    if (otherEmployers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Other Pending Employers",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdminDarkNavy
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        otherEmployers.forEach { emp ->
                            EmployerCard(
                                employer = emp,
                                onApprove = { updateStatus(emp["id"] as String, "Approved") },
                                onReject = { updateStatus(emp["id"] as String, "Rejected") }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
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
                AdminBottomNavItem(
                    label = "Dashboard",
                    isSelected = selectedBottomNav == 0,
                    icon = { DashboardGridIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 0) AdminDarkNavy else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 0
                        onNavigateDashboard()
                    }
                )

                AdminBottomNavItem(
                    label = "Employers",
                    isSelected = selectedBottomNav == 1,
                    icon = { BuildingIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 1) AdminDarkNavy else Color(0xFF64748B)) },
                    onClick = { selectedBottomNav = 1 }
                )

                AdminBottomNavItem(
                    label = "Internships",
                    isSelected = selectedBottomNav == 2,
                    icon = { BriefcaseIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 2) AdminDarkNavy else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 2
                        onNavigateInternships()
                    }
                )

                AdminBottomNavItem(
                    label = "Accounts",
                    isSelected = selectedBottomNav == 3,
                    icon = { BarChartIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 3) AdminDarkNavy else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 3
                        onNavigateReports()
                    }
                )
            }
        }
    }
}

@Composable
private fun EmployerCard(
    employer: Map<String, Any>,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val companyName = (employer["companyName"] as? String) ?: "Not provided"
    val industry = (employer["sector"] as? String) ?: (employer["industry"] as? String) ?: "Not provided"
    val location = (employer["location"] as? String) ?: "Not provided"
    val contactName = (employer["fullName"] as? String) ?: (employer["contactName"] as? String) ?: "Not provided"
    val email = (employer["email"] as? String) ?: "Not provided"
    val phone = (employer["phoneNumber"] as? String) ?: "Not provided"
    val status = (employer["status"] as? String) ?: "Pending"
    val createdAt = employer["createdAt"]

    val dateFormatted = remember(createdAt) {
        val millis = when (createdAt) {
            is Number -> createdAt.toLong()
            is String -> createdAt.toLongOrNull() ?: System.currentTimeMillis()
            else -> System.currentTimeMillis()
        }
        try {
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            sdf.format(Date(millis))
        } catch (_: Exception) {
            "Not provided"
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Company Information Hierarchy
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "COMPANY INFORMATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = companyName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = industry,
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = location,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }

                // Status Indicator Badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = when {
                        status.equals("Approved", ignoreCase = true) -> Color(0xFFDBEAFE)
                        status.equals("Active", ignoreCase = true) -> Color(0xFFE0F2FE)
                        status.equals("Rejected", ignoreCase = true) -> Color(0xFFFEE2E2)
                        else -> Color(0xFFEFF6FF)
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            status.equals("Approved", ignoreCase = true) -> Color(0xFF93C5FD)
                            status.equals("Active", ignoreCase = true) -> Color(0xFF7DD3FC)
                            status.equals("Rejected", ignoreCase = true) -> Color(0xFFFCA5A5)
                            else -> Color(0xFFBFDBFE)
                        }
                    )
                ) {
                    Text(
                        text = status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            status.equals("Approved", ignoreCase = true) -> Color(0xFF1E40AF)
                            status.equals("Active", ignoreCase = true) -> Color(0xFF0369A1)
                            status.equals("Rejected", ignoreCase = true) -> Color(0xFF991B1B)
                            else -> Color(0xFF1E3A8A)
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            // Registration Information Hierarchy
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "REGISTRATION INFORMATION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Submitted $dateFormatted",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF475569)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Contact Person Hierarchy
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.5.dp, Color(0xFFCBD5E1))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CONTACT PERSON",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = contactName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EmailIcon(modifier = Modifier.size(14.dp), tint = Color(0xFF475569))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = email,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AdminAccentBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PhoneIcon(modifier = Modifier.size(14.dp), tint = Color(0xFF475569))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = phone,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155)
                        )
                    }
                }
            }

            val isApproved = status.equals("Approved", ignoreCase = true)
            if (!isApproved) {
                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AdminDarkNavy)
                    ) {
                        Text(text = "Approve", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Text(text = "Reject", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminEmployerManagementScreenPreview() {
    UniversityIntershipAppTheme {
        AdminEmployerManagementScreen()
    }
}
