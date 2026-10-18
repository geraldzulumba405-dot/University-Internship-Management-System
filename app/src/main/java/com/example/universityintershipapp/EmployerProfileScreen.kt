package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmployerProfileScreen(
    modifier: Modifier = Modifier,
    companyName: String = "Apex Tech Solutions",
    onNavigateHome: () -> Unit = {},
    onNavigateInternships: () -> Unit = {},
    onNavigateApplications: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onLogOutClick: () -> Unit = {}
) {
    var selectedBottomNav by remember { mutableIntStateOf(3) } // Profile selected

    val darkNavy = Color(0xFF0F2C59)
    val textBlack = Color(0xFF000000)
    val textDarkGrey = Color(0xFF171717)
    val textHelperGrey = Color(0xFF404040)

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
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hamburger Menu Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    HamburgerMenuIcon(
                        modifier = Modifier.size(20.dp),
                        tint = textBlack
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Company Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkNavy,
                    modifier = Modifier.weight(1f)
                )

                // Edit Pencil Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    EditPencilIcon(
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF334155)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Settings Gear Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    SettingsGearIcon(
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF334155)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // 2. Scrollable Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Company Main Card (with dark blue accent top bar)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column {
                        // Dark Blue Top Line Accent
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(darkNavy)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Building Logo Box with Verified Badge
                            Box(modifier = Modifier.size(56.dp)) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFDBEAFE)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        BuildingIcon(
                                            modifier = Modifier.size(28.dp),
                                            tint = darkNavy
                                        )
                                    }
                                }

                                // Verified Green Check Badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(18.dp)
                                        .background(Color(0xFF059669), CircleShape)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CheckIcon(
                                        modifier = Modifier.size(10.dp),
                                        tint = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = companyName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textBlack
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Enterprise Software & Cloud Engineering • Official Campus Partner",
                                    fontSize = 12.sp,
                                    color = Color(0xFF475569),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Metrics Grid Cards (Postings, Applicants, Alumni)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1: Postings
                    ProfileMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Postings",
                        count = "4",
                        statusText = "Active",
                        statusColor = Color(0xFF059669),
                        icon = { BriefcaseIcon(modifier = Modifier.size(18.dp), tint = darkNavy) }
                    )

                    // Card 2: Applicants
                    ProfileMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Applicants",
                        count = "38",
                        statusText = "In Review",
                        statusColor = Color(0xFF1D4ED8),
                        icon = { ApplicationsNavIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF1D4ED8)) }
                    )

                    // Card 3: Alumni
                    ProfileMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Alumni",
                        count = "14",
                        statusText = "Hired Total",
                        statusColor = Color(0xFF475569),
                        icon = { GraduationCapIcon(modifier = Modifier.size(18.dp), tint = darkNavy) }
                    )
                }

                // SECTION 1: Recruitment Team & POC
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IdCardIcon(modifier = Modifier.size(20.dp), tint = darkNavy)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Recruitment Team & POC",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textBlack
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFE0E7FF)
                            ) {
                                Text(
                                    text = "Primary Liaison",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF3730A3),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // POC Member Row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFFE0E7FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "MB",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E3A8A)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Chikondi Mapira",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textBlack
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Head of University Talent Acquisition",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Contact Info Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Email Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        EmailIcon(modifier = Modifier.size(16.dp), tint = Color(0xFF1E3A8A))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "c.mapira@apextech.com",
                                            fontSize = 12.sp,
                                            color = Color(0xFF334155),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = "Copy",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8),
                                        modifier = Modifier.clickable { }
                                    )
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    color = Color(0xFFE2E8F0),
                                    thickness = 1.dp
                                )

                                // Phone Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        PhoneIcon(modifier = Modifier.size(16.dp), tint = Color(0xFF1E3A8A))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "+265 882378295",
                                            fontSize = 12.sp,
                                            color = Color(0xFF334155)
                                        )
                                    }
                                    Text(
                                        text = "Call",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8),
                                        modifier = Modifier.clickable { }
                                    )
                                }
                            }
                        }
                    }
                }

                // SECTION 2: Internship Preferences Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SlidersIcon(modifier = Modifier.size(20.dp), tint = darkNavy)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Internship Preferences",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textBlack
                                )
                            }

                            InfoIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF64748B))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Target Academic Majors:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Computer Science", "Data Science", "Software Eng").forEach { major ->
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFFEFF6FF),
                                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                                ) {
                                    Text(
                                        text = major,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1E3A8A),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Min GPA Cutoff Card
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = "Min. GPA Cutoff", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "3.2 / 4.0", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textBlack)
                                }
                            }

                            // Work Arrangement Card
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = "Work Arrangement", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Hybrid / Remote", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                }
                            }
                        }
                    }
                }

                // SECTION 3: Settings & Management List Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column {
                        // Row 1: Manage Representatives & Permissions
                        SettingsActionRow(
                            iconBgColor = Color(0xFFEFF6FF),
                            icon = { GroupSettingsIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF1D4ED8)) },
                            title = "Manage Representatives & Permissions",
                            subtitle = "4 Active Recruiters"
                        )

                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        // Row 2: Billing & Partnership Tiers
                        SettingsActionRow(
                            iconBgColor = Color(0xFFECFDF5),
                            icon = { MedalIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF047857)) },
                            title = "Billing & Partnership Tiers",
                            badgeText = "ENTERPRISE",
                            subtitle = "Annual University Sponsorship Plan"
                        )

                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        // Row 3: Notification & Digest Preferences
                        SettingsActionRow(
                            iconBgColor = Color(0xFFEFF6FF),
                            icon = { BellIcon(modifier = Modifier.size(20.dp), tint = darkNavy) },
                            title = "Notification & Digest Preferences",
                            subtitle = "Weekly candidate digest • Instant alerts"
                        )
                    }
                }

                // SECTION 4: Logout Button
                Button(
                    onClick = onLogOutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2)),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        LogoutIcon(modifier = Modifier.size(18.dp), tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Out of Employer Portal",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
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
                    icon = { HomeNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 0) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 0
                        onNavigateHome()
                    }
                )

                BottomNavItem(
                    label = "Internships",
                    isSelected = selectedBottomNav == 1,
                    icon = { BriefcaseIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 1) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 1
                        onNavigateInternships()
                    }
                )

                BottomNavItem(
                    label = "Applicants",
                    isSelected = selectedBottomNav == 2,
                    icon = { ApplicationsNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 2) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 2
                        onNavigateApplications()
                    }
                )

                BottomNavItem(
                    label = "Profile",
                    isSelected = selectedBottomNav == 3,
                    icon = { ProfileNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 3) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 3
                        onNavigateProfile()
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    count: String,
    statusText: String,
    statusColor: Color,
    icon: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()
                Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF475569))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = count, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2C59))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = statusText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
        }
    }
}

@Composable
private fun SettingsActionRow(
    iconBgColor: Color,
    icon: @Composable () -> Unit,
    title: String,
    badgeText: String? = null,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBgColor, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.weight(1f, fill = false)
                )

                badgeText?.let {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF0F2C59)
                    ) {
                        Text(
                            text = it,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        RightChevronIcon(
            modifier = Modifier.size(16.dp),
            tint = Color(0xFF94A3B8)
        )
    }
}

// Custom Icons
@Composable
private fun HamburgerMenuIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF000000)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.12f

        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.28f), end = Offset(w * 0.85f, h * 0.28f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.50f), end = Offset(w * 0.85f, h * 0.50f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.72f), end = Offset(w * 0.85f, h * 0.72f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun EditPencilIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF334155)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f

        val pencil = Path().apply {
            moveTo(w * 0.75f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.25f)
            lineTo(w * 0.35f, h * 0.75f)
            lineTo(w * 0.20f, h * 0.80f)
            lineTo(w * 0.25f, h * 0.65f)
            close()
        }
        drawPath(pencil, color = tint, style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun SettingsGearIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF334155)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f

        drawCircle(color = tint, radius = w * 0.28f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(stroke))
        drawCircle(color = tint, radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.5f))
    }
}

@Composable
private fun SlidersIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F2C59)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f

        drawLine(color = tint, start = Offset(w * 0.20f, h * 0.20f), end = Offset(w * 0.80f, h * 0.20f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.20f, h * 0.50f), end = Offset(w * 0.80f, h * 0.50f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.20f, h * 0.80f), end = Offset(w * 0.80f, h * 0.80f), strokeWidth = stroke, cap = StrokeCap.Round)

        drawCircle(color = tint, radius = w * 0.08f, center = Offset(w * 0.35f, h * 0.20f))
        drawCircle(color = tint, radius = w * 0.08f, center = Offset(w * 0.65f, h * 0.50f))
        drawCircle(color = tint, radius = w * 0.08f, center = Offset(w * 0.45f, h * 0.80f))
    }
}

@Composable
private fun GroupSettingsIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1D4ED8)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f

        drawCircle(color = tint, radius = w * 0.18f, center = Offset(w * 0.40f, h * 0.35f), style = Stroke(stroke))
        drawCircle(color = tint, radius = w * 0.14f, center = Offset(w * 0.70f, h * 0.35f), style = Stroke(stroke))

        val body = Path().apply {
            moveTo(w * 0.15f, h * 0.80f)
            cubicTo(w * 0.15f, h * 0.60f, w * 0.65f, h * 0.60f, w * 0.65f, h * 0.80f)
        }
        drawPath(body, color = tint, style = Stroke(stroke, cap = StrokeCap.Round))
    }
}

@Composable
private fun MedalIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF047857)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f

        drawCircle(color = tint, radius = w * 0.28f, center = Offset(w * 0.5f, h * 0.40f), style = Stroke(stroke))

        val ribbon = Path().apply {
            moveTo(w * 0.35f, h * 0.65f)
            lineTo(w * 0.30f, h * 0.90f)
            lineTo(w * 0.50f, h * 0.80f)
            lineTo(w * 0.70f, h * 0.90f)
            lineTo(w * 0.65f, h * 0.65f)
        }
        drawPath(ribbon, color = tint, style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmployerProfileScreenPreview() {
    UniversityIntershipAppTheme {
        EmployerProfileScreen()
    }
}
