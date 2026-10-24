package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

private val AdminDarkNavy = Color(0xFF0F2C59)
private val AdminRedAlert = Color(0xFFDC2626)
private val AdminRedAlertBg = Color(0xFFFEE2E2)

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
    val filterTabs = listOf("Reported Content (3)", "Reported Accounts (2)", "Sanctions & Bans")

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
                        .background(AdminDarkNavy, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AdminShieldIcon(modifier = Modifier.size(20.dp), tint = Color.White)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UniIntern Admin",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )
                    Text(
                        text = "Compliance & Account Governance",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }

                IconButton(onClick = onLogOut) {
                    LogOutIcon(modifier = Modifier.size(22.dp), tint = AdminDarkNavy)
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
                // Title Banner
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFEFF6FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AdminShieldIcon(modifier = Modifier.size(20.dp), tint = AdminDarkNavy)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Reports & User Management",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdminDarkNavy,
                            lineHeight = 26.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Review scam reports, investigate student flags, and enact disciplinary enforcement.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 16.sp
                        )
                    }
                }

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

                // Active Case File Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(AdminRedAlert, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ACTIVE CASE FILE 1 OF 3",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Primary Active Case Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CASE #REP-2024-042",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = AdminRedAlertBg
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AlertTriangleIcon(modifier = Modifier.size(13.dp), tint = AdminRedAlert)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Scam Suspicion / Advance Fee Request",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AdminRedAlert
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Reported Listing Details Box
                        Text(
                            text = "REPORTED LISTING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Remote Data Entry Assistant",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AdminDarkNavy
                                )
                                Text(
                                    text = "MWK 400,000/mo",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F766E)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        BuildingIcon(modifier = Modifier.size(15.dp), tint = AdminDarkNavy)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "FastTrack Global Ventures",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AdminDarkNavy
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = AdminRedAlertBg
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AdminShieldIcon(modifier = Modifier.size(12.dp), tint = AdminRedAlert)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Unverified Employer",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AdminRedAlert
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Student Evidence & Deposition Box
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "STUDENT EVIDENCE & DEPOSITION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FlagIcon(modifier = Modifier.size(13.dp), tint = Color(0xFFB45309))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "4 Student Reports Filed",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = AdminRedAlertBg.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, Color(0xFFFECACA))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "\"Employer requested MWK 15,000 processing and interview fee via Airtel Money before issuing contract portal login credentials.\"",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF991B1B),
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                HorizontalDivider(color = Color(0xFFFECACA), thickness = 0.5.dp)

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Transaction Ref: AM-8829-019",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF991B1B)
                                    )
                                    Text(
                                        text = "Oct 25, 2024",
                                        fontSize = 11.sp,
                                        color = Color(0xFF991B1B)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Enforcement Action Buttons
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CloseIcon(modifier = Modifier.size(15.dp), tint = AdminDarkNavy)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Dismiss Report", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy)
                                }
                            }

                            OutlinedButton(
                                onClick = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = AdminRedAlertBg),
                                border = BorderStroke(1.dp, Color(0xFFFECACA))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TrashIcon(modifier = Modifier.size(15.dp), tint = AdminRedAlert)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Remove Content", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminRedAlert)
                                }
                            }

                            Button(
                                onClick = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AdminRedAlert)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BanIcon(modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Suspend Account", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Suspended Accounts & Sanctions Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AdminShieldIcon(modifier = Modifier.size(18.dp), tint = AdminDarkNavy)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Suspended Accounts & Sanctions",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdminDarkNavy
                        )
                    }

                    Text(
                        text = "View All (2)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A),
                        modifier = Modifier.clickable { }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sanctioned Card: QuickHire Malawi
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
                                    .size(44.dp)
                                    .background(Color(0xFFEFF6FF), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "QH", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "QuickHire Malawi",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AdminDarkNavy
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = Color(0xFFE0E7FF)
                                    ) {
                                        Text(
                                            text = "Employer",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF3730A3),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "Violation #892: Advance fee solicitation",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AdminRedAlert
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = "Appeal Pending",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sanction Enacted: Oct 20, 2024",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Review Dossier",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AdminDarkNavy
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                RightArrowIcon(modifier = Modifier.size(13.dp), tint = AdminDarkNavy)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Activity Log Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HistoryClockIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recent Administrative Activity Log",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(AdminRedAlertBg, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                BanIcon(modifier = Modifier.size(16.dp), tint = AdminRedAlert)
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row {
                                    Text(text = "Admin Director", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy)
                                    Text(text = " suspended QuickHire Malawi", fontSize = 13.sp, color = Color(0xFF334155), lineHeight = 18.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Yesterday • 16:42 CAT • Policy #892", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color(0xFFD1FAE5), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                CheckCircleIcon(modifier = Modifier.size(16.dp), tint = Color(0xFF047857))
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row {
                                    Text(text = "Dismissed false report ", fontSize = 13.sp, color = Color(0xFF334155), lineHeight = 18.sp)
                                    Text(text = "#REP-2024-039", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminDarkNavy)
                                    Text(text = " on Airtel Malawi", fontSize = 13.sp, color = Color(0xFF334155), lineHeight = 18.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "2 days ago • By Senior Registrar • Audited", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                    }
                }

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
                    onClick = {
                        selectedBottomNav = 2
                        onNavigateInternships()
                    }
                )

                AdminBottomNavItem(
                    label = "Reports",
                    isSelected = selectedBottomNav == 3,
                    icon = { BarChartIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 3) AdminDarkNavy else Color(0xFF64748B)) },
                    onClick = { selectedBottomNav = 3 }
                )
            }
        }
    }
}

@Composable
fun AlertTriangleIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminRedAlert
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val tri = Path().apply {
            moveTo(w * 0.50f, h * 0.15f)
            lineTo(w * 0.88f, h * 0.85f)
            lineTo(w * 0.12f, h * 0.85f)
            close()
        }
        drawPath(tri, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(color = tint, start = Offset(w * 0.5f, h * 0.38f), end = Offset(w * 0.5f, h * 0.60f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        drawCircle(color = tint, radius = w * 0.045f, center = Offset(w * 0.5f, h * 0.72f))
    }
}

@Composable
fun TrashIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminRedAlert
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.28f), end = Offset(w * 0.85f, h * 0.28f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        val bin = Path().apply {
            moveTo(w * 0.25f, h * 0.28f)
            lineTo(w * 0.30f, h * 0.82f)
            lineTo(w * 0.70f, h * 0.82f)
            lineTo(w * 0.75f, h * 0.28f)
        }
        drawPath(bin, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun BanIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminRedAlert
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(color = tint, radius = w * 0.38f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.09f))
        drawLine(color = tint, start = Offset(w * 0.24f, h * 0.76f), end = Offset(w * 0.76f, h * 0.24f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    }
}

@Composable
fun LogOutIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminDarkNavy
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val box = Path().apply {
            moveTo(w * 0.55f, h * 0.20f)
            lineTo(w * 0.20f, h * 0.20f)
            lineTo(w * 0.20f, h * 0.80f)
            lineTo(w * 0.55f, h * 0.80f)
        }
        drawPath(box, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val arrow = Path().apply {
            moveTo(w * 0.42f, h * 0.50f)
            lineTo(w * 0.82f, h * 0.50f)
            moveTo(w * 0.65f, h * 0.35f)
            lineTo(w * 0.85f, h * 0.50f)
            lineTo(w * 0.65f, h * 0.65f)
        }
        drawPath(arrow, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminReportsScreenPreview() {
    UniversityIntershipAppTheme {
        AdminReportsScreen()
    }
}
