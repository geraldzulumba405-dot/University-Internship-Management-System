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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

private val AdminDarkNavy = Color(0xFF0F2C59)
private val AdminAccentBlue = Color(0xFF1E3A8A)
private val AdminPillBadgeBg = Color(0xFFE0E7FF)
private val AdminPillBadgeText = Color(0xFF3730A3)
private val AdminRedAlert = Color(0xFFDC2626)
private val AdminRedAlertBg = Color(0xFFFEE2E2)

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
            // 1. Top Bar Header
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
                            .size(38.dp)
                            .background(AdminDarkNavy, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AdminShieldIcon(modifier = Modifier.size(22.dp), tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

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

                // 2. Metrics Grid Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Students",
                        value = "1,180",
                        subtext = "registered",
                        icon = { PeopleGroupIcon(modifier = Modifier.size(20.dp), tint = AdminAccentBlue) }
                    )

                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Employers",
                        value = "68",
                        subtext = "partners",
                        icon = { BuildingIcon(modifier = Modifier.size(20.dp), tint = AdminAccentBlue) }
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
                        badgeText = "Vetting",
                        badgeBg = AdminPillBadgeBg,
                        badgeTextCol = AdminPillBadgeText,
                        icon = { AdminShieldIcon(modifier = Modifier.size(20.dp), tint = AdminAccentBlue) },
                        onClick = onNavigateEmployers
                    )

                    AdminMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Pending Postings",
                        value = "9",
                        badgeText = "Review",
                        badgeBg = AdminPillBadgeBg,
                        badgeTextCol = AdminPillBadgeText,
                        icon = { BriefcaseIcon(modifier = Modifier.size(20.dp), tint = AdminAccentBlue) },
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
                        badgeText = "Flagged",
                        badgeBg = AdminRedAlertBg,
                        badgeTextCol = AdminRedAlert,
                        icon = { FlagIcon(modifier = Modifier.size(20.dp), tint = AdminRedAlert) },
                        accentLineColor = AdminRedAlert,
                        onClick = onNavigateReports
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Pending Actions Section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WarningBoxIcon(modifier = Modifier.size(20.dp), tint = AdminDarkNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pending Actions",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Card 1: Review 6 Pending Employers
                PendingActionCard(
                    title = "Review 6 Pending Employers",
                    subtitle = "Illovo Sugar, FDH Holdings, Nico Holdings",
                    statusText = "Awaiting accreditation check",
                    actionText = "Start Vetting",
                    icon = { CheckboxDocIcon(modifier = Modifier.size(22.dp), tint = AdminAccentBlue) },
                    onClick = onNavigateEmployers
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Card 2: Review 9 Pending Internships
                PendingActionCard(
                    title = "Review 9 Pending Internships",
                    subtitle = "TNM Systems Admin, Airtel Mobile Dev, RBM...",
                    statusText = "Department alignment check",
                    actionText = "Review Listings",
                    icon = { ListCheckIcon(modifier = Modifier.size(22.dp), tint = AdminAccentBlue) },
                    onClick = onNavigateInternships
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Recent Activity Section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HistoryClockIcon(modifier = Modifier.size(20.dp), tint = AdminDarkNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recent Activity",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Item 1
                        ActivityItem(
                            boldText = "Illovo Sugar Malawi Plc",
                            normalText = " uploaded PACRA registration documents.",
                            timestamp = "15m ago • Compliance Desk",
                            icon = { FileUpIcon(modifier = Modifier.size(18.dp), tint = AdminAccentBlue) }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFF1F5F9)
                        )

                        // Item 2
                        ActivityItem(
                            boldText = "National Bank of Malawi",
                            normalText = " approved by Dean of Science.",
                            timestamp = "Yesterday • Institutional Clearance",
                            icon = { CheckCircleIcon(modifier = Modifier.size(18.dp), tint = AdminAccentBlue) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 5. Admin Bottom Navigation Bar
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
    subtext: String = "",
    badgeText: String = "",
    badgeBg: Color = Color.Transparent,
    badgeTextCol: Color = Color.Unspecified,
    icon: @Composable () -> Unit,
    accentLineColor: Color? = null,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                    icon()
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = value,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminDarkNavy
                    )

                    if (subtext.isNotEmpty()) {
                        Text(
                            text = subtext,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }

                    if (badgeText.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = badgeBg
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextCol,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
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
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
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
    timestamp: String,
    icon: @Composable () -> Unit
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFEFF6FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(
                    text = boldText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminDarkNavy
                )
                Text(
                    text = normalText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155),
                    lineHeight = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = timestamp,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )
        }
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
fun PeopleGroupIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminAccentBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(color = tint, radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.3f))
        val body = Path().apply {
            moveTo(w * 0.2f, h * 0.8f)
            cubicTo(w * 0.2f, h * 0.55f, w * 0.8f, h * 0.55f, w * 0.8f, h * 0.8f)
        }
        drawPath(body, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round))
    }
}

@Composable
fun FlagIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminRedAlert
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawLine(color = tint, start = Offset(w * 0.2f, h * 0.15f), end = Offset(w * 0.2f, h * 0.88f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        val flag = Path().apply {
            moveTo(w * 0.2f, h * 0.18f)
            lineTo(w * 0.82f, h * 0.32f)
            lineTo(w * 0.2f, h * 0.52f)
            close()
        }
        drawPath(flag, color = tint)
    }
}

@Composable
fun WarningBoxIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminDarkNavy
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val box = Path().apply {
            moveTo(w * 0.15f, h * 0.22f)
            lineTo(w * 0.85f, h * 0.22f)
            lineTo(w * 0.85f, h * 0.78f)
            lineTo(w * 0.15f, h * 0.78f)
            close()
        }
        drawPath(box, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(color = tint, start = Offset(w * 0.5f, h * 0.38f), end = Offset(w * 0.5f, h * 0.54f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawCircle(color = tint, radius = w * 0.04f, center = Offset(w * 0.5f, h * 0.64f))
    }
}

@Composable
fun CheckboxDocIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminAccentBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val box = Path().apply {
            moveTo(w * 0.2f, h * 0.2f)
            lineTo(w * 0.8f, h * 0.2f)
            lineTo(w * 0.8f, h * 0.8f)
            lineTo(w * 0.2f, h * 0.8f)
            close()
        }
        drawPath(box, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val check = Path().apply {
            moveTo(w * 0.35f, h * 0.50f)
            lineTo(w * 0.48f, h * 0.62f)
            lineTo(w * 0.68f, h * 0.38f)
        }
        drawPath(check, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ListCheckIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminAccentBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val box = Path().apply {
            moveTo(w * 0.2f, h * 0.2f)
            lineTo(w * 0.8f, h * 0.2f)
            lineTo(w * 0.8f, h * 0.8f)
            lineTo(w * 0.2f, h * 0.8f)
            close()
        }
        drawPath(box, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.38f), end = Offset(w * 0.65f, h * 0.38f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.50f), end = Offset(w * 0.65f, h * 0.50f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.62f), end = Offset(w * 0.55f, h * 0.62f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
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
fun FileUpIcon(
    modifier: Modifier = Modifier,
    tint: Color = AdminAccentBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val doc = Path().apply {
            moveTo(w * 0.25f, h * 0.18f)
            lineTo(w * 0.75f, h * 0.18f)
            lineTo(w * 0.75f, h * 0.82f)
            lineTo(w * 0.25f, h * 0.82f)
            close()
        }
        drawPath(doc, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val arrow = Path().apply {
            moveTo(w * 0.50f, h * 0.62f)
            lineTo(w * 0.50f, h * 0.38f)
            moveTo(w * 0.38f, h * 0.48f)
            lineTo(w * 0.50f, h * 0.38f)
            lineTo(w * 0.62f, h * 0.48f)
        }
        drawPath(arrow, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
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
