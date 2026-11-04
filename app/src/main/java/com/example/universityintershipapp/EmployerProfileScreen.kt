package com.example.universityintershipapp

import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun EmployerProfileScreen(
    modifier: Modifier = Modifier,
    companyName: String = "",
    contactPerson: String = "",
    officialEmail: String = "",
    industry: String = "",
    location: String = "",
    onNavigateHome: () -> Unit = {},
    onNavigateInternships: () -> Unit = {},
    onNavigateApplications: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onLogOutClick: () -> Unit = {}
) {
    var selectedBottomNav by remember { mutableIntStateOf(3) } // Profile selected
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Company Information States (Retrieved from registration fields)
    var companyNameState by remember { mutableStateOf(companyName) }
    var industryState by remember { mutableStateOf(industry) }
    var locationState by remember { mutableStateOf(location) }
    var officialEmailState by remember { mutableStateOf(officialEmail) }
    var contactPersonState by remember { mutableStateOf(contactPerson) }
    var phoneState by remember { mutableStateOf("") }
    var isVerified by remember { mutableStateOf(false) }
    var accountStatus by remember { mutableStateOf("Active") }

    // Recruitment Contact States (Independent fields)
    var recruitmentContactNameState by remember { mutableStateOf("") }
    var recruitmentContactTitleState by remember { mutableStateOf("") }
    var recruitmentContactEmailState by remember { mutableStateOf("") }
    var recruitmentContactPhoneState by remember { mutableStateOf("") }

    var showEditDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val userDoc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
                if (userDoc.exists()) {
                    // Company Info from Registration
                    val comp = userDoc.getString("companyName")
                    if (!comp.isNullOrBlank()) companyNameState = comp

                    val sec = userDoc.getString("sector") ?: userDoc.getString("industry")
                    if (!sec.isNullOrBlank()) industryState = sec

                    val loc = userDoc.getString("location")
                    if (!loc.isNullOrBlank()) locationState = loc

                    val email = userDoc.getString("email")
                    if (!email.isNullOrBlank()) officialEmailState = email

                    val cPerson = userDoc.getString("fullName") ?: userDoc.getString("contactName")
                    if (!cPerson.isNullOrBlank()) contactPersonState = cPerson

                    val phone = userDoc.getString("phoneNumber")
                    if (!phone.isNullOrBlank()) phoneState = phone

                    val verified = userDoc.getBoolean("isVerified")
                    if (verified != null) isVerified = verified

                    val status = userDoc.getString("accountStatus")
                    if (!status.isNullOrBlank()) accountStatus = status

                    // Independent Recruitment Contact Fields
                    val rName = userDoc.getString("recruitmentContactName")
                    if (!rName.isNullOrBlank()) recruitmentContactNameState = rName

                    val rTitle = userDoc.getString("recruitmentContactTitle")
                    if (!rTitle.isNullOrBlank()) recruitmentContactTitleState = rTitle

                    val rEmail = userDoc.getString("recruitmentContactEmail")
                    if (!rEmail.isNullOrBlank()) recruitmentContactEmailState = rEmail

                    val rPhone = userDoc.getString("recruitmentContactPhone")
                    if (!rPhone.isNullOrBlank()) recruitmentContactPhoneState = rPhone
                }
            }
        } catch (_: Exception) {}
    }

    val darkNavy = Color(0xFF0F2C59)
    val primaryBlue = Color(0xFF1B3B80)
    val surfaceBg = Color(0xFFF8FAFC)
    val textBlack = Color(0xFF0F172A)
    val textSecondary = Color(0xFF64748B)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceBg)
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Company Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkNavy
                )
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // 2. Scrollable Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 1: Company Information (Retrieved from Registration)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Company Information",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkNavy
                        )
                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = companyNameState.ifBlank { "Not provided" },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textBlack
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = industryState.ifBlank { "Not provided" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = textSecondary
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        ProfileInfoRow(label = "Account Status", value = accountStatus.ifBlank { "Not provided" })
                        ProfileInfoRow(label = "Location", value = locationState.ifBlank { "Not provided" })
                        ProfileInfoRow(label = "Official Email", value = officialEmailState.ifBlank { "Not provided" })
                        ProfileInfoRow(label = "Contact Person", value = contactPersonState.ifBlank { "Not provided" })
                        ProfileInfoRow(label = "Phone Number", value = phoneState.ifBlank { "Not provided" })
                    }
                }

                // SECTION 2: Recruitment Contact (Independent Information)
                Text(
                    text = "Recruitment Contact",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textBlack
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = recruitmentContactNameState.ifBlank { "Not provided" },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = textBlack
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = recruitmentContactTitleState.ifBlank { "Not provided" },
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        ProfileInfoRow(label = "Email Address", value = recruitmentContactEmailState.ifBlank { "Not provided" })
                        ProfileInfoRow(label = "Phone Number", value = recruitmentContactPhoneState.ifBlank { "Not provided" })

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                errorMessage = null
                                showEditDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                        ) {
                            Text(
                                text = "Edit Details",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                // SECTION 3: Account Settings
                Text(
                    text = "Account Settings",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textBlack
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SettingsRow(
                            title = "Notification Preferences",
                            subtitle = "Manage application alerts & candidate digests"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Logout Button
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                    border = BorderStroke(1.dp, Color(0xFF3B82F6))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        LogoutIcon(modifier = Modifier.size(18.dp), tint = darkNavy)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Out",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = darkNavy
                        )
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
                    icon = { HomeNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 0) primaryBlue else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 0
                        onNavigateHome()
                    }
                )

                BottomNavItem(
                    label = "Internships",
                    isSelected = selectedBottomNav == 1,
                    icon = { BriefcaseIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 1) primaryBlue else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 1
                        onNavigateInternships()
                    }
                )

                BottomNavItem(
                    label = "Applicants",
                    isSelected = selectedBottomNav == 2,
                    icon = { ApplicationsNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 2) primaryBlue else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 2
                        onNavigateApplications()
                    }
                )

                BottomNavItem(
                    label = "Profile",
                    isSelected = selectedBottomNav == 3,
                    icon = { ProfileNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 3) primaryBlue else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 3
                        onNavigateProfile()
                    }
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(text = "Log Out", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkNavy)
            },
            text = {
                Text(text = "Are you sure you want to log out?", fontSize = 13.sp, color = Color(0xFF334155))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        FirebaseAuth.getInstance().signOut()
                        onLogOutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = darkNavy)
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

    // Edit Recruitment Contact Dialog
    if (showEditDialog) {
        var editName by remember { mutableStateOf(recruitmentContactNameState) }
        var editTitle by remember { mutableStateOf(recruitmentContactTitleState) }
        var editEmail by remember { mutableStateOf(recruitmentContactEmailState) }
        var editPhone by remember { mutableStateOf(recruitmentContactPhoneState) }

        AlertDialog(
            onDismissRequest = { if (!isSaving) showEditDialog = false },
            title = {
                Text(text = "Edit Recruitment Contact", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkNavy)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEE2E2),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                        ) {
                            Text(
                                text = errorMessage!!,
                                fontSize = 12.sp,
                                color = Color(0xFF991B1B),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Recruitment Officer's Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Job Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmedName = editName.trim()
                        val trimmedTitle = editTitle.trim()
                        val trimmedEmail = editEmail.trim()
                        val trimmedPhone = editPhone.trim()

                        when {
                            trimmedName.isBlank() -> {
                                errorMessage = "Please enter the recruitment officer's full name."
                            }
                            trimmedTitle.isBlank() -> {
                                errorMessage = "Please enter the job title."
                            }
                            trimmedEmail.isBlank() || !trimmedEmail.contains("@") || !trimmedEmail.contains(".") -> {
                                errorMessage = "Please enter a valid email address."
                            }
                            trimmedPhone.isBlank() -> {
                                errorMessage = "Please enter a phone number."
                            }
                            else -> {
                                scope.launch {
                                    isSaving = true
                                    errorMessage = null
                                    try {
                                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                                        if (uid != null) {
                                            val updates = mapOf(
                                                "recruitmentContactName" to trimmedName,
                                                "recruitmentContactTitle" to trimmedTitle,
                                                "recruitmentContactEmail" to trimmedEmail,
                                                "recruitmentContactPhone" to trimmedPhone
                                            )
                                            FirebaseFirestore.getInstance().collection("users")
                                                .document(uid)
                                                .update(updates)
                                                .await()

                                            recruitmentContactNameState = trimmedName
                                            recruitmentContactTitleState = trimmedTitle
                                            recruitmentContactEmailState = trimmedEmail
                                            recruitmentContactPhoneState = trimmedPhone

                                            Toast.makeText(context, "Recruitment contact updated successfully!", Toast.LENGTH_SHORT).show()
                                            showEditDialog = false
                                        } else {
                                            errorMessage = "User not authenticated."
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to update: ${e.localizedMessage}"
                                    } finally {
                                        isSaving = false
                                    }
                                }
                            }
                        }
                    },
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                ) {
                    Text(if (isSaving) "Saving..." else "Save Changes")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEditDialog = false },
                    enabled = !isSaving
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0F172A),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
        RightChevronIcon(
            modifier = Modifier.size(16.dp),
            tint = Color(0xFF94A3B8)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmployerProfileScreenPreview() {
    UniversityIntershipAppTheme {
        EmployerProfileScreen()
    }
}
