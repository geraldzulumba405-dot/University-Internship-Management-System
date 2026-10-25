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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

private val AdminDarkNavy = Color(0xFF0F2C59)
private val AdminRedAlert = Color(0xFFDC2626)

@Composable
fun AdminDashboardScreen(
    modifier: Modifier = Modifier,
    onNavigateEmployers: () -> Unit = {},
    onNavigateInternships: () -> Unit = {},
    onNavigateReports: () -> Unit = {},
    onLogOut: () -> Unit = {}
) {
    var selectedBottomNav by remember { mutableIntStateOf(0) }

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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = "UniIntern Admin",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdminDarkNavy
                        )
                        Text(
                            text = "University of Malawi Admin Console",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155)
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
                            .background(AdminRedAlert)
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
                // Greeting
                Text(
                    text = "Welcome, Administrator",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminDarkNavy,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Metrics Grid Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Students",
                        value = "1,180"
                    )

                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Employers",
                        value = "68"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Metrics Grid Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Pending Employers",
                        value = "6",
                        onClick = onNavigateEmployers
                    )

                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Pending Postings",
                        value = "9",
                        onClick = onNavigateInternships
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Metrics Row 3: Open Reports
                Row(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminMetricCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Open Reports",
                        value = "3",
                        accentLineColor = AdminRedAlert,
                        onClick = onNavigateReports
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pending Actions Section
                Text(
                    text = "Pending Actions",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminDarkNavy
                )

                Spacer(modifier = Modifier.height(12.dp))

                PendingActionCard(
                    title = "Review 6 Pending Employers",
                    subtitle = "Illovo Sugar, FDH Holdings, Nico Holdings",
                    statusText = "Awaiting accreditation check",
                    actionText = "Start Vetting",
                    onClick = onNavigateEmployers
                )

                Spacer(modifier = Modifier.height(12.dp))

                PendingActionCard(
                    title = "Review 9 Pending Internships",
                    subtitle = "TNM Systems Admin, Airtel Mobile Dev, RBM Analyst",
                    statusText = "Department alignment check",
                    actionText = "Review Listings",
                    onClick = onNavigateInternships
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Activity Section
                Text(
                    text = "Recent Activity",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminDarkNavy
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ActivityItem(
                            boldText = "Illovo Sugar Malawi Plc",
                            normalText = " uploaded PACRA registration documents.",
                            timestamp = "15m ago • Compliance Desk"
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFF1F5F9)
                        )

                        ActivityItem(
                            boldText = "National Bank of Malawi",
                            normalText = " approved by Dean of Science.",
                            timestamp = "Yesterday • Institutional Clearance"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Admin Bottom Navigation Bar
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
                    onClick = { selectedBottomNav = 0 }
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
private fun AdminMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    accentLineColor: Color? = null,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
        shadowElevation = 2.dp
    ) {
        Box {
            if (accentLineColor != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(4.dp)
                        .fillMaxWidth()
                        .background(accentLineColor)
                )
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminDarkNavy
                )
            }
        }
    }
}

@Composable
private fun PendingActionCard(
    title: String,
    subtitle: String,
    statusText: String,
    actionText: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = actionText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    RightArrowIcon(modifier = Modifier.size(14.dp), tint = AdminDarkNavy)
                }
            }
        }
    }
}

@Composable
private fun ActivityItem(
    boldText: String,
    normalText: String,
    timestamp: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )
                ) {
                    append(boldText)
                }
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                ) {
                    append(normalText)
                }
            },
            fontSize = 13.sp,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = timestamp,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
fun AdminBottomNavItem(
    label: String,
    isSelected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable { onClick() }
    ) {
        icon()
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) AdminDarkNavy else Color(0xFF64748B)
        )
    }
}

@Composable
fun AdminShieldIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminDarkNavy
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            lineTo(w * 0.88f, h * 0.25f)
            lineTo(w * 0.88f, h * 0.55f)
            cubicTo(w * 0.88f, h * 0.80f, w * 0.5f, h * 0.92f, w * 0.5f, h * 0.92f)
            cubicTo(w * 0.5f, h * 0.92f, w * 0.12f, h * 0.80f, w * 0.12f, h * 0.55f)
            lineTo(w * 0.12f, h * 0.25f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun HistoryClockIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminDarkNavy
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(color = tint, radius = w * 0.38f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.08f))
        drawLine(color = tint, start = Offset(w * 0.5f, h * 0.5f), end = Offset(w * 0.5f, h * 0.28f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.5f, h * 0.5f), end = Offset(w * 0.68f, h * 0.5f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
    }
}

@Composable
fun DashboardGridIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminDarkNavy
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sz = w * 0.30f
        drawRect(color = tint, topLeft = Offset(w * 0.15f, h * 0.15f), size = Size(sz, sz))
        drawRect(color = tint, topLeft = Offset(w * 0.55f, h * 0.15f), size = Size(sz, sz))
        drawRect(color = tint, topLeft = Offset(w * 0.15f, h * 0.55f), size = Size(sz, sz))
        drawRect(color = tint, topLeft = Offset(w * 0.55f, h * 0.55f), size = Size(sz, sz))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardScreenPreview() {
    UniversityIntershipAppTheme {
        AdminDashboardScreen()
    }
}
