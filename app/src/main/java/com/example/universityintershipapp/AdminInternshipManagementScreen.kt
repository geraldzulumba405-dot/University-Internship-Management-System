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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

private val AdminDarkNavy = Color(0xFF0F2C59)
private val AdminAccentBlue = Color(0xFF1E3A8A)
private val GreenPublish = Color(0xFF047857)

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
    val filterTabs = listOf("Pending Review 9", "Published 34", "Rejected 3", "All 46")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onNavigateDashboard() },
                    contentAlignment = Alignment.Center
                ) {
                    BackArrowIcon(modifier = Modifier.size(20.dp), tint = AdminDarkNavy)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(AdminDarkNavy, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AdminShieldIcon(modifier = Modifier.size(20.dp), tint = Color.White)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Internship Management",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )
                    Text(
                        text = "UNIMA Moderation Queue • UniIntern Admin",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }

                Box(modifier = Modifier.padding(end = 4.dp)) {
                    IconButton(onClick = {}) {
                        BellIcon(modifier = Modifier.size(24.dp), tint = AdminDarkNavy)
                    }
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDC2626))
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
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
                    trailingIcon = { SlidersFilterIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF64748B)) },
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

                // Primary Moderation Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                BuildingIcon(modifier = Modifier.size(28.dp), tint = AdminAccentBlue)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.size(6.dp).background(Color(0xFFD97706), CircleShape))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "PENDING REVIEW",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Junior Network & Systems Administrator Intern",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AdminDarkNavy,
                                    lineHeight = 21.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Telekom Networks Malawi (TNM Plc)",
                                    fontSize = 13.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Grid Specs
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "📍 Blantyre / Lilongwe", fontSize = 12.sp, color = Color(0xFF334155))
                                Text(text = "⏱ Full-time (6 Months)", fontSize = 12.sp, color = Color(0xFF334155))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "💵 MWK 250,000 / mo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy)
                                Text(text = "📅 Closes Nov 30, 2024", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                            Text(
                                text = "🕒 Submitted Oct 25, 2024 by TNM Human Capital Directorate",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Academic Vetting Section Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    GraduationCapIcon(modifier = Modifier.size(16.dp), tint = AdminAccentBlue)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "UNIMA ACADEMIC CRITERIA VETTING",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AdminAccentBlue
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Target Academic Cohort",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                        Text(
                                            text = "UNIMA Dept of Computer Science & ICT",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AdminDarkNavy
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFD1FAE5)
                                    ) {
                                        Text(
                                            text = "Eligible (Yr 3/4)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GreenPublish,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Description Text
                        Text(
                            text = "SCOPE & DESCRIPTION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "The candidate will assist TNM senior network engineers in monitoring telecommunications switches, managing campus fiber endpoints, resolving network fault escalations, and maintaining enterprise Linux servers.",
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Competency Chips
                        Text(
                            text = "Verified Required Competencies",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Linux SysAdmin", "Cisco Routing", "TCP/IP Subnetting", "Network Diagnostics", "Bash Scripting").forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEFF6FF),
                                    border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AdminDarkNavy,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Action Buttons: Approve & Publish | Request Changes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {},
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPublish)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CheckCircleIcon(modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Approve & Publish", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            OutlinedButton(
                                onClick = {},
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, AdminDarkNavy)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    EditNoteIcon(modifier = Modifier.size(16.dp), tint = AdminDarkNavy)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Request Changes", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Upcoming in Queue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upcoming in Queue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "8 remaining",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Queue Item 1
                QueuePostingCard(
                    title = "Airtel Malawi - Mobile App Dev Intern",
                    subtitle = "Lilongwe Headquarters • B.Sc. Computing",
                    icon = { SmartphoneIcon(modifier = Modifier.size(20.dp), tint = AdminAccentBlue) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Queue Item 2
                QueuePostingCard(
                    title = "Reserve Bank of Malawi - Financial Systems Analyst",
                    subtitle = "Blantyre Branch • BIS / CS Year 4",
                    icon = { BankBuildingIcon(modifier = Modifier.size(20.dp), tint = AdminAccentBlue) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Queue Item 3
                QueuePostingCard(
                    title = "Illovo Sugar - IT Infrastructure Intern",
                    subtitle = "Nchalo Sugar Estate • Systems Engineering",
                    icon = { FactoryIcon(modifier = Modifier.size(20.dp), tint = AdminAccentBlue) }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Navigation
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
                    label = "Reports",
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
private fun QueuePostingCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF64748B))
            }

            Spacer(modifier = Modifier.width(6.dp))

            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFFEF3C7)
            ) {
                Text(
                    text = "Pending",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            RightArrowIcon(modifier = Modifier.size(16.dp), tint = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun SlidersFilterIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.30f), end = Offset(w * 0.85f, h * 0.30f), strokeWidth = w * 0.10f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.70f), end = Offset(w * 0.85f, h * 0.70f), strokeWidth = w * 0.10f, cap = StrokeCap.Round)
        drawCircle(color = tint, radius = w * 0.12f, center = Offset(w * 0.38f, h * 0.30f))
        drawCircle(color = tint, radius = w * 0.12f, center = Offset(w * 0.62f, h * 0.70f))
    }
}

@Composable
fun EditNoteIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminDarkNavy
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val line1 = Path().apply {
            moveTo(w * 0.20f, h * 0.30f)
            lineTo(w * 0.80f, h * 0.30f)
            moveTo(w * 0.20f, h * 0.50f)
            lineTo(w * 0.60f, h * 0.50f)
            moveTo(w * 0.20f, h * 0.70f)
            lineTo(w * 0.45f, h * 0.70f)
        }
        drawPath(line1, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round))
    }
}

@Composable
fun SmartphoneIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminAccentBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val phone = Path().apply {
            moveTo(w * 0.28f, h * 0.15f)
            lineTo(w * 0.72f, h * 0.15f)
            lineTo(w * 0.72f, h * 0.85f)
            lineTo(w * 0.28f, h * 0.85f)
            close()
        }
        drawPath(phone, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(color = tint, start = Offset(w * 0.42f, h * 0.25f), end = Offset(w * 0.58f, h * 0.25f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.50f, h * 0.75f))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminInternshipManagementScreenPreview() {
    UniversityIntershipAppTheme {
        AdminInternshipManagementScreen()
    }
}
