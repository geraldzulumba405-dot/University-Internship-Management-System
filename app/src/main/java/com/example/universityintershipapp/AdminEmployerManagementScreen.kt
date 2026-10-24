package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
private val GreenApprove = Color(0xFF047857)
private val RedReject = Color(0xFFDC2626)

@Composable
fun AdminEmployerManagementScreen(
    modifier: Modifier = Modifier,
    onNavigateDashboard: () -> Unit = {},
    onNavigateInternships: () -> Unit = {},
    onNavigateReports: () -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    var selectedBottomNav by remember { mutableIntStateOf(1) }
    val filterTabs = listOf("Pending 6", "Approved 48", "Rejected 5", "All 59")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(AdminDarkNavy, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AdminShieldIcon(modifier = Modifier.size(20.dp), tint = Color.White)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "UniIntern Admin",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminDarkNavy
                )
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Employer Management",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminDarkNavy,
                    lineHeight = 30.sp
                )
                Text(
                    text = "Vetting & Compliance Pipeline",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                // Primary Vetting Card
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
                                FactoryIcon(modifier = Modifier.size(28.dp), tint = AdminAccentBlue)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Illovo Sugar Malawi Plc",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AdminDarkNavy
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Commercial Agribusiness & Mfg",
                                    fontSize = 13.sp,
                                    color = Color(0xFF475569)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    LocationPinIcon(modifier = Modifier.size(13.dp), tint = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Blantyre, Southern Region",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AdminDarkNavy
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(6.dp).background(Color(0xFFD97706), CircleShape))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Pending Verification",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB45309)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CalendarIcon(modifier = Modifier.size(13.dp), tint = Color(0xFF64748B))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Submitted Oct 24, 2026",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Principal Liaison Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "PRINCIPAL LIAISON",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF475569)
                                    )
                                    Text(
                                        text = "HR Operations",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Blessings Phiri",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AdminDarkNavy
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "✉ bphiri@illovo.mw\n📞 +265 888 421 900",
                                    fontSize = 12.sp,
                                    color = Color(0xFF334155),
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Approve / Reject Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {},
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenApprove)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CheckCircleIcon(modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Approve", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            OutlinedButton(
                                onClick = {},
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, RedReject)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CloseIcon(modifier = Modifier.size(16.dp), tint = RedReject)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Reject", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RedReject)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Remaining Pending Entities
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Remaining Pending Entities",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "5 in line",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Item 1: FDH Financial
                EntityRowCard(
                    title = "FDH Financial Holdings Ltd",
                    subtitle = "Banking & Financial Services • Blantyre",
                    pocText = "POC: Mirriam Banda",
                    dateBadge = "Oct 23, 2024",
                    icon = { BankBuildingIcon(modifier = Modifier.size(22.dp), tint = AdminAccentBlue) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Item 2: Nico Holdings
                EntityRowCard(
                    title = "Nico Holdings Plc",
                    subtitle = "Insurance & Asset Management • Lilongwe",
                    pocText = "POC: Chikondi Phiri",
                    dateBadge = "Oct 21, 2024",
                    icon = { AdminShieldIcon(modifier = Modifier.size(22.dp), tint = AdminAccentBlue) }
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
private fun EntityRowCard(
    title: String,
    subtitle: String,
    pocText: String,
    dateBadge: String,
    icon: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF64748B))
                }

                RightArrowIcon(modifier = Modifier.size(16.dp), tint = Color(0xFF94A3B8))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = pocText, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF475569))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = dateBadge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FactoryIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminAccentBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.85f)
            lineTo(w * 0.15f, h * 0.35f)
            lineTo(w * 0.40f, h * 0.52f)
            lineTo(w * 0.40f, h * 0.35f)
            lineTo(w * 0.65f, h * 0.52f)
            lineTo(w * 0.65f, h * 0.20f)
            lineTo(w * 0.85f, h * 0.20f)
            lineTo(w * 0.85f, h * 0.85f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun BankBuildingIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminAccentBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val roof = Path().apply {
            moveTo(w * 0.15f, h * 0.38f)
            lineTo(w * 0.50f, h * 0.18f)
            lineTo(w * 0.85f, h * 0.38f)
            close()
        }
        drawPath(roof, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(color = tint, start = Offset(w * 0.28f, h * 0.42f), end = Offset(w * 0.28f, h * 0.75f), strokeWidth = w * 0.08f)
        drawLine(color = tint, start = Offset(w * 0.50f, h * 0.42f), end = Offset(w * 0.50f, h * 0.75f), strokeWidth = w * 0.08f)
        drawLine(color = tint, start = Offset(w * 0.72f, h * 0.42f), end = Offset(w * 0.72f, h * 0.75f), strokeWidth = w * 0.08f)
        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.82f), end = Offset(w * 0.85f, h * 0.82f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminEmployerManagementScreenPreview() {
    UniversityIntershipAppTheme {
        AdminEmployerManagementScreen()
    }
}
