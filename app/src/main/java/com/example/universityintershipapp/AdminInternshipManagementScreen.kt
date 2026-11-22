package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme
import kotlinx.coroutines.launch

private val AdminDarkNavy = Color(0xFF0F2C59)
private val AdminAccentBlue = Color(0xFF1E3A8A)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminInternshipManagementScreen(
    modifier: Modifier = Modifier,
    onNavigateDashboard: () -> Unit = {},
    onNavigateEmployers: () -> Unit = {},
    onNavigateReports: () -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    var selectedBottomNav by remember { mutableIntStateOf(2) }
    val filterTabs = listOf("All", "Pending Review", "Published", "Rejected")

    var allInternships by remember { mutableStateOf<List<InternshipModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    fun loadInternships() {
        scope.launch {
            isLoading = true
            try {
                allInternships = InternshipRepository.getInternships()
            } catch (_: Exception) {
                allInternships = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadInternships()
    }

    val filteredListings = allInternships.filter { item ->
        val matchesSearch = item.title.contains(searchQuery, ignoreCase = true) ||
                item.companyName.contains(searchQuery, ignoreCase = true) ||
                item.location.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (selectedFilterIndex) {
            0 -> true
            1 -> item.status == "pending" || item.status.isBlank()
            2 -> item.status == "published"
            3 -> item.status == "rejected"
            else -> true
        }
        matchesSearch && matchesTab
    }

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
                        text = "Internship Management",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )
                    Text(
                        text = "Internship Review & Approval",
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
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy),
                    placeholder = { Text("Search title or company name", color = Color(0xFF64748B), fontSize = 13.sp) },
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
                        Text(text = "Loading internships from Firestore...", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                } else if (filteredListings.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No internship listings found in this filter.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else {
                    val primaryItem = filteredListings.first()
                    val queueItems = filteredListings.drop(1)

                    // Primary Moderation Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = primaryItem.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AdminDarkNavy,
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = primaryItem.companyName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AdminAccentBlue
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Meta details
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = primaryItem.location.ifBlank { "Malawi" },
                                        fontSize = 12.sp,
                                        color = Color(0xFF334155)
                                    )
                                    Text(
                                        text = primaryItem.duration.ifBlank { "Full-time" },
                                        fontSize = 12.sp,
                                        color = Color(0xFF334155)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Closes ${primaryItem.deadline.ifBlank { "Soon" }}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = primaryItem.fieldOfStudy.ifBlank { "General" },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AdminDarkNavy
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Status: ${(primaryItem.status.ifBlank { "pending" }).replaceFirstChar { it.uppercase() }}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when(primaryItem.status.ifBlank { "pending" }) {
                                    "published" -> AdminDarkNavy
                                    "rejected" -> Color(0xFFDC2626)
                                    else -> Color(0xFFB45309)
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Scope & Description Box
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.5.dp, Color(0xFFCBD5E1))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "SCOPE & DESCRIPTION",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AdminDarkNavy,
                                        letterSpacing = 0.5.sp
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = primaryItem.description.ifBlank { "No description provided." },
                                        fontSize = 13.sp,
                                        color = Color(0xFF334155),
                                        lineHeight = 18.sp
                                    )

                                    if (primaryItem.skills.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Verified Required Competencies",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AdminAccentBlue
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            primaryItem.skills.forEach { comp ->
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFFEFF6FF),
                                                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                                                ) {
                                                    Text(
                                                        text = comp,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = AdminAccentBlue,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            val isPending = primaryItem.status.equals("pending", ignoreCase = true) || primaryItem.status.isBlank()
                            if (isPending) {
                                Spacer(modifier = Modifier.height(18.dp))

                                // Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                InternshipRepository.updateInternshipStatus(primaryItem.id, "published")
                                                loadInternships()
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AdminDarkNavy)
                                    ) {
                                        Text(
                                            text = "Approve & Publish",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                InternshipRepository.updateInternshipStatus(primaryItem.id, "rejected")
                                                loadInternships()
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, AdminDarkNavy),
                                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                                    ) {
                                        Text(
                                            text = "Reject",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AdminDarkNavy,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (queueItems.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))

                        // Upcoming in Queue Section
                        Text(
                            text = "Upcoming in Queue",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdminDarkNavy
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        queueItems.forEach { qItem ->
                            QueueItemCard(
                                title = qItem.title,
                                subtitle = "${qItem.companyName} • ${qItem.location}",
                                status = qItem.status
                            )
                            Spacer(modifier = Modifier.height(12.dp))
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
                    onClick = { selectedBottomNav = 2 }
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
private fun QueueItemCard(
    title: String,
    subtitle: String,
    status: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {},
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminDarkNavy
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = when(status) {
                        "published" -> Color(0xFFD1FAE5)
                        "rejected" -> Color(0xFFFEE2E2)
                        else -> Color(0xFFFEF3C7)
                    },
                    border = BorderStroke(1.dp, when(status) {
                        "published" -> Color(0xFF6EE7B7)
                        "rejected" -> Color(0xFFFCA5A5)
                        else -> Color(0xFFFCD34D)
                    })
                ) {
                    Text(
                        text = status.replaceFirstChar { it.uppercase() }.ifBlank { "Pending" },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when(status) {
                            "published" -> Color(0xFF065F46)
                            "rejected" -> Color(0xFF991B1B)
                            else -> Color(0xFFB45309)
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                RightArrowIcon(modifier = Modifier.size(16.dp), tint = Color(0xFF64748B))
            }
        }
    }
}

@Composable
fun EditNoteIcon(modifier: Modifier = Modifier, tint: Color = AdminDarkNavy) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val note = Path().apply {
            moveTo(w * 0.2f, h * 0.15f)
            lineTo(w * 0.7f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.30f)
            lineTo(w * 0.85f, h * 0.85f)
            lineTo(w * 0.2f, h * 0.85f)
            close()
        }
        drawPath(note, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val pen = Path().apply {
            moveTo(w * 0.65f, h * 0.40f)
            lineTo(w * 0.75f, h * 0.50f)
            lineTo(w * 0.45f, h * 0.80f)
            lineTo(w * 0.35f, h * 0.80f)
            lineTo(w * 0.35f, h * 0.70f)
            close()
        }
        drawPath(pen, color = tint)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminInternshipManagementScreenPreview() {
    UniversityIntershipAppTheme {
        AdminInternshipManagementScreen()
    }
}
