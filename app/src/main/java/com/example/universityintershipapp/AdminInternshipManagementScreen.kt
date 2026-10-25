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
    val filterTabs = listOf("Pending Review", "Published", "Rejected", "All")

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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    placeholder = { Text("Search title or company name.", color = Color(0xFF64748B), fontSize = 13.sp) },
                    leadingIcon = { SearchNavIcon(modifier = Modifier.size(20.dp), tint = AdminDarkNavy) },
                    trailingIcon = { FilterIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF64748B)) },
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

                // Primary Moderation Card (Junior Network & Systems Administrator Intern)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(Color(0xFFEFF6FF), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                BuildingIcon(modifier = Modifier.size(28.dp), tint = AdminAccentBlue)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Junior Network & Systems Administrator Intern",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AdminDarkNavy,
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Telekom Networks Malawi (TNM Plc)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AdminAccentBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Meta details
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    LocationPinIcon(modifier = Modifier.size(14.dp), tint = Color(0xFF334155))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Blantyre / Lilongwe",
                                        fontSize = 12.sp,
                                        color = Color(0xFF334155)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    DurationClockIcon(modifier = Modifier.size(14.dp), tint = Color(0xFF334155))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Full-time (6 Months)",
                                        fontSize = 12.sp,
                                        color = Color(0xFF334155)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CalendarIcon(modifier = Modifier.size(14.dp), tint = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Closes Nov 30, 2024",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Text(
                                    text = "MWK 250,000 / mo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AdminDarkNavy
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HistoryClockIcon(modifier = Modifier.size(15.dp), tint = Color(0xFF64748B))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Submitted Oct 25, 2024 by TNM Human Capital Directorate",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

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
                                    text = "The candidate will assist TNM senior network engineers in monitoring telecommunications switches, managing campus fiber endpoints, resolving network fault escalations, and maintaining enterprise Linux servers.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF334155),
                                    lineHeight = 18.sp
                                )

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
                                    listOf("Linux SysAdmin", "Cisco Routing", "TCP/IP Subnetting", "Network Diagnostics", "Bash Scripting").forEach { comp ->
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

                        Spacer(modifier = Modifier.height(18.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {},
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPublish)
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
                                onClick = {},
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, AdminDarkNavy),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                            ) {
                                Text(
                                    text = "Request Changes",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AdminDarkNavy,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Upcoming in Queue Section
                Text(
                    text = "Upcoming in Queue",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminDarkNavy
                )

                Spacer(modifier = Modifier.height(12.dp))

                QueueItemCard(
                    title = "Airtel Malawi - Mobile App Dev Intern",
                    subtitle = "Lilongwe Headquarters • B.Sc. Computing"
                )

                Spacer(modifier = Modifier.height(12.dp))

                QueueItemCard(
                    title = "Reserve Bank of Malawi - Financial Systems Analyst",
                    subtitle = "Blantyre Branch • BIS / CS Year 4"
                )

                Spacer(modifier = Modifier.height(12.dp))

                QueueItemCard(
                    title = "Illovo Sugar - IT Infrastructure Intern",
                    subtitle = "Nchalo Estate • Electrical / ICT Eng"
                )

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
private fun QueueItemCard(
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {},
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
        shadowElevation = 2.dp
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
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.dp, Color(0xFFFCD34D))
                ) {
                    Text(
                        text = "Pending",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
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
fun DurationClockIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFF334155)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(color = tint, radius = w * 0.38f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.08f))
        drawLine(color = tint, start = Offset(w * 0.5f, h * 0.5f), end = Offset(w * 0.5f, h * 0.3f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.5f, h * 0.5f), end = Offset(w * 0.65f, h * 0.5f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminInternshipManagementScreenPreview() {
    UniversityIntershipAppTheme {
        AdminInternshipManagementScreen()
    }
}
