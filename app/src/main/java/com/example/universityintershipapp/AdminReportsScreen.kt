package com.example.universityintershipapp

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
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

private val AdminDarkNavy = Color(0xFF0F2C59)
private val AdminAccentBlue = Color(0xFF1E3A8A)
private val AdminRedAlert = Color(0xFFDC2626)
private val AdminSuspendBlue = Color(0xFF0284C7)

data class UserAccountItem(
    val id: String,
    val name: String,
    val identifier: String,
    val email: String,
    val details: String,
    val type: String, // "Student" or "Employer"
    val status: String, // "Active" or "Suspended"
    val suspensionReason: String? = null,
    val suspensionDate: String? = null
)

@Composable
fun AdminReportsScreen(
    modifier: Modifier = Modifier,
    onNavigateDashboard: () -> Unit = {},
    onNavigateEmployers: () -> Unit = {},
    onNavigateInternships: () -> Unit = {},
    onLogOut: () -> Unit = {}
) {
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    var selectedBottomNav by remember { mutableIntStateOf(3) }
    val filterTabs = listOf("User Accounts", "Suspended Accounts")

    var searchQuery by remember { mutableStateOf("") }
    var accountFilterTab by remember { mutableIntStateOf(0) } // 0: All, 1: Students, 2: Employers

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var accountsList by remember { mutableStateOf<List<UserAccountItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadAccounts() {
        scope.launch {
            isLoading = true
            try {
                val db = FirebaseFirestore.getInstance()
                val snap = db.collection("users").get().await()
                val fetched = snap.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val role = data["role"] as? String ?: ""
                    // Exclude admin accounts if desired, or include students & employers
                    if (role.equals("admin", ignoreCase = true)) return@mapNotNull null

                    val id = doc.id
                    val name = data["fullName"] as? String ?: data["companyName"] as? String ?: data["contactName"] as? String ?: "User"
                    val email = data["email"] as? String ?: ""
                    val type = if (role.equals("employer", ignoreCase = true)) "Employer" else "Student"
                    val identifier = if (type == "Student") (data["studentId"] as? String ?: "STUDENT-ID") else (data["phoneNumber"] as? String ?: "Contact")
                    val details = if (type == "Student") (data["programme"] as? String ?: "Computer Science") else (data["sector"] as? String ?: data["industry"] as? String ?: "Business")
                    val isVerified = data["isVerified"] as? Boolean ?: true
                    val statusRaw = data["status"] as? String ?: if (isVerified) "Active" else "Suspended"
                    val status = when {
                        statusRaw.equals("Suspended", ignoreCase = true) -> "Suspended"
                        statusRaw.equals("Active", ignoreCase = true) || isVerified -> "Active"
                        else -> "Active"
                    }
                    val suspensionReason = data["suspensionReason"] as? String
                    val suspensionDate = data["suspensionDate"] as? String

                    UserAccountItem(
                        id = id,
                        name = name,
                        identifier = identifier,
                        email = email,
                        details = details,
                        type = type,
                        status = status,
                        suspensionReason = suspensionReason,
                        suspensionDate = suspensionDate
                    )
                }
                accountsList = fetched
            } catch (_: Exception) {
                accountsList = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadAccounts()
    }

    // Dialog States
    var showSuspendDialogForAccount by remember { mutableStateOf<UserAccountItem?>(null) }
    var selectedSuspensionReason by remember { mutableStateOf("Fraudulent activities") }

    var showReactivateDialogForAccount by remember { mutableStateOf<UserAccountItem?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Account Management",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )
                    Text(
                        text = "Manage platform accounts",
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

                Spacer(modifier = Modifier.height(16.dp))

                // Filter Tabs Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterTabs.forEachIndexed { index, title ->
                        val isSelected = selectedFilterIndex == index
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilterIndex = index },
                            label = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF334155)
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AdminDarkNavy,
                                containerColor = Color.White
                            ),
                            border = BorderStroke(1.dp, if (isSelected) AdminDarkNavy else Color(0xFFCBD5E1))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Loading accounts from database...", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                } else {
                    // TAB 1: USER ACCOUNTS (index 0)
                    if (selectedFilterIndex == 0) {
                        // Search Field
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy),
                            placeholder = { Text("Search by name or email", color = Color(0xFF64748B), fontSize = 14.sp) },
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

                        Spacer(modifier = Modifier.height(14.dp))

                        // Account Filter Buttons (All, Students, Employers)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val accTabs = listOf("All", "Students", "Employers")
                            accTabs.forEachIndexed { tabIdx, tabTitle ->
                                val isTabSelected = accountFilterTab == tabIdx
                                Button(
                                    onClick = { accountFilterTab = tabIdx },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isTabSelected) AdminAccentBlue else Color.White
                                    ),
                                    border = if (!isTabSelected) BorderStroke(1.dp, Color(0xFFCBD5E1)) else null
                                ) {
                                    Text(
                                        text = tabTitle,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTabSelected) Color.White else Color(0xFF334155)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val filteredAccounts = accountsList.filter { acc ->
                            val matchesSearch = searchQuery.isBlank() ||
                                    acc.name.contains(searchQuery.trim(), ignoreCase = true) ||
                                    acc.email.contains(searchQuery.trim(), ignoreCase = true)
                            val matchesType = when (accountFilterTab) {
                                1 -> acc.type.equals("Student", ignoreCase = true)
                                2 -> acc.type.equals("Employer", ignoreCase = true)
                                else -> true
                            }
                            matchesSearch && matchesType
                        }

                        if (filteredAccounts.isEmpty()) {
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
                                        text = "No user accounts match your search or filter.",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        } else {
                            filteredAccounts.forEach { acc ->
                                UserAccountCard(
                                    account = acc,
                                    onSuspend = { showSuspendDialogForAccount = acc },
                                    onReactivate = { showReactivateDialogForAccount = acc }
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                        }
                    }

                    // TAB 2: SUSPENDED ACCOUNTS (index 1)
                    else if (selectedFilterIndex == 1) {
                        val suspendedAccounts = accountsList.filter { it.status.equals("Suspended", ignoreCase = true) }

                        if (suspendedAccounts.isEmpty()) {
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
                                        text = "No suspended accounts found.",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        } else {
                            suspendedAccounts.forEach { acc ->
                                SuspendedAccountCard(
                                    account = acc,
                                    onReactivate = { showReactivateDialogForAccount = acc }
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
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
                    onClick = {
                        selectedBottomNav = 1
                        onNavigateEmployers()
                    }
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
                    onClick = { selectedBottomNav = 3 }
                )
            }
        }
    }

    // SUSPEND ACCOUNT CONFIRMATION DIALOG
    if (showSuspendDialogForAccount != null) {
        val target = showSuspendDialogForAccount!!
        val reasons = listOf(
            "Fraudulent activities",
            "Providing misleading information",
            "Posting inappropriate content",
            "Violation of platform policies",
            "Other"
        )

        AlertDialog(
            onDismissRequest = { showSuspendDialogForAccount = null },
            title = {
                Text(text = "Suspend Account", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AdminRedAlert)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Are you sure you want to suspend this account?", fontSize = 13.sp, color = Color(0xFF334155))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Name: ${target.name}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy)
                            Text(text = "Type: ${target.type}", fontSize = 12.sp, color = Color(0xFF64748B))
                            Text(text = "Email: ${target.email}", fontSize = 12.sp, color = Color(0xFF64748B))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Select reason for suspension:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy)

                    reasons.forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSuspensionReason = reason }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.RadioButton(
                                selected = selectedSuspensionReason == reason,
                                onClick = { selectedSuspensionReason = reason }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = reason, fontSize = 13.sp, color = Color(0xFF334155))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                FirebaseFirestore.getInstance().collection("users")
                                    .document(target.id)
                                    .update(
                                        mapOf(
                                            "status" to "Suspended",
                                            "suspensionReason" to selectedSuspensionReason,
                                            "suspensionDate" to "Today"
                                        )
                                    )
                                    .await()

                                Toast.makeText(context, "Account '${target.name}' suspended in database", Toast.LENGTH_SHORT).show()
                                showSuspendDialogForAccount = null
                                loadAccounts()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed to suspend: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminSuspendBlue)
                ) {
                    Text("Confirm Suspension")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showSuspendDialogForAccount = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // REACTIVATE ACCOUNT CONFIRMATION DIALOG
    if (showReactivateDialogForAccount != null) {
        val target = showReactivateDialogForAccount!!

        AlertDialog(
            onDismissRequest = { showReactivateDialogForAccount = null },
            title = {
                Text(text = "Reactivate Account", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Are you sure you want to reactivate this account?", fontSize = 13.sp, color = Color(0xFF334155))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Name: ${target.name}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy)
                            Text(text = "Type: ${target.type}", fontSize = 12.sp, color = Color(0xFF64748B))
                            Text(text = "Email: ${target.email}", fontSize = 12.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                FirebaseFirestore.getInstance().collection("users")
                                    .document(target.id)
                                    .update(
                                        mapOf(
                                            "status" to "Active",
                                            "suspensionReason" to null,
                                            "suspensionDate" to null
                                        )
                                    )
                                    .await()

                                Toast.makeText(context, "Account '${target.name}' reactivated in database", Toast.LENGTH_SHORT).show()
                                showReactivateDialogForAccount = null
                                loadAccounts()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed to reactivate: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminAccentBlue)
                ) {
                    Text("Confirm Reactivation")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showReactivateDialogForAccount = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun UserAccountCard(
    account: UserAccountItem,
    onSuspend: () -> Unit,
    onReactivate: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Email: ${account.email}",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                }


            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (account.type == "Student") "ID: ${account.identifier} • Programme: ${account.details}" else "Contact: ${account.identifier} • Sector: ${account.details}",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Status: ", fontSize = 12.sp, color = Color(0xFF64748B))
                    Text(
                        text = account.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (account.status == "Active") AdminAccentBlue else AdminSuspendBlue
                    )
                }

                if (account.status == "Active") {
                    Button(
                        onClick = onSuspend,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AdminSuspendBlue),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(text = "Suspend Account", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onReactivate,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AdminAccentBlue),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(text = "Reactivate Account", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SuspendedAccountCard(
    account: UserAccountItem,
    onReactivate: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Email: ${account.email}",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                }


            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Reason: ${account.suspensionReason ?: "Violation of policies"}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Suspended on: ${account.suspensionDate ?: "Recently"}",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Status: ", fontSize = 12.sp, color = Color(0xFF64748B))
                    Text(
                        text = "Suspended",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminSuspendBlue
                    )
                }

                Button(
                    onClick = onReactivate,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AdminAccentBlue),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = "Reactivate Account", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminReportsScreenPreview() {
    UniversityIntershipAppTheme {
        AdminReportsScreen()
    }
}
