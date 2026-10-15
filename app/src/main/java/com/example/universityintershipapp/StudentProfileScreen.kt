package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

private val MintContainer = Color(0xFFCCFBF1)
private val LightNavyBg = Color(0xFFEFF4FF)
private val RedAlertColor = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(
    modifier: Modifier = Modifier,
    studentName: String = "Gerald Zulumba",
    onNavigateHome: () -> Unit = {},
    onNavigateSearch: () -> Unit = {},
    onNavigateApplications: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onLogOut: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
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
                    IconButton(onClick = {}) {
                        MenuIcon(modifier = Modifier.size(22.dp), tint = NavyPrimary)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Student Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEditProfile) {
                        EditIcon(modifier = Modifier.size(20.dp), tint = NavyPrimary)
                    }
                    Box(modifier = Modifier.padding(end = 4.dp)) {
                        IconButton(onClick = {}) {
                            BellIcon(modifier = Modifier.size(24.dp), tint = NavyPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(RedAlertColor)
                                .align(Alignment.TopEnd)
                                .offset(x = (-8).dp, y = 8.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = OutlineVariant, thickness = 1.dp)

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Identity & Academic Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(studentName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "B.Sc. Computer Science & Software Engineering, Year 3",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "🏛️ National University / Institute of Technology",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Metrics Grid (GPA, Credits, Eligibility)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricBox(
                                modifier = Modifier.weight(1f),
                                label = "Cumulative GPA",
                                value = "3.84",
                                sublabel = "Scale 4.00",
                                valColor = NavyPrimary
                            )
                            MetricBox(
                                modifier = Modifier.weight(1f),
                                label = "Earned Credits",
                                value = "96",
                                sublabel = "Req. 120 Total",
                                valColor = NavyPrimary
                            )
                            MetricBox(
                                modifier = Modifier.weight(1f),
                                label = "Eligibility",
                                value = "100%",
                                sublabel = "Intern Ready",
                                valColor = SuccessDark
                            )
                        }
                    }
                }

                // 2. Credentials & Documents
                Text(
                    "📁 Credentials & Documents",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                DocumentCard(
                    title = "Official Academic Transcript",
                    subtitle = "Direct API link to University Registrar",
                    icon = { SchoolIcon(modifier = Modifier.size(20.dp), tint = NavyPrimary) },
                    trailingBadge = "Synced"
                )

                DocumentCard(
                    title = "${studentName.replace(" ", "_")}_Resume_2...",
                    subtitle = "🕒 Updated 2 days ago",
                    icon = { DescriptionIcon(modifier = Modifier.size(20.dp), tint = NavyPrimary) },
                    trailingButtonText = "Update"
                )

                DocumentCard(
                    title = "Dean Clearance Letter",
                    subtitle = "Valid for Summer 2025 Work Term",
                    icon = { ShieldIcon(modifier = Modifier.size(20.dp), tint = NavyPrimary) },
                    trailingIconButton = {
                        IconButton(onClick = {}) {
                            FileDownloadIcon(modifier = Modifier.size(20.dp), tint = NavyPrimary)
                        }
                    }
                )

                // 3. Account & Security
                Text(
                    "👤 Account & Security",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column {
                        SettingsRow(
                            icon = { BellIcon(modifier = Modifier.size(20.dp), tint = NavyPrimary) },
                            title = "Notification Preferences",
                            subtitle = "Push notifications & email alerts"
                        )
                        HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp)
                        SettingsRow(
                            icon = { VpnKeyIcon(modifier = Modifier.size(20.dp), tint = NavyPrimary) },
                            title = "Institutional SSO Connection",
                            subtitle = "Active (OAuth2: National EduNet)"
                        )
                        HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp)
                        SettingsRow(
                            icon = { HelpIcon(modifier = Modifier.size(20.dp), tint = NavyPrimary) },
                            title = "Help & Career Services Support",
                            subtitle = "Student FAQ & 24/7 Placement Desk"
                        )
                    }
                }

                // 4. Log Out CTA
                OutlinedButton(
                    onClick = onLogOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFFEF2F2))
                ) {
                    LogoutIcon(modifier = Modifier.size(18.dp), tint = RedAlertColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", color = RedAlertColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bottom Navigation Bar
            HorizontalDivider(color = OutlineVariant, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground)
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    label = "Home",
                    isSelected = false,
                    icon = { HomeNavIcon(modifier = Modifier.size(20.dp), tint = TextSecondary) },
                    onClick = onNavigateHome
                )

                BottomNavItem(
                    label = "Search",
                    isSelected = false,
                    icon = { SearchNavIcon(modifier = Modifier.size(20.dp), tint = TextSecondary) },
                    onClick = onNavigateSearch
                )

                BottomNavItem(
                    label = "Applications",
                    isSelected = false,
                    icon = { ApplicationsNavIcon(modifier = Modifier.size(20.dp), tint = TextSecondary) },
                    onClick = onNavigateApplications
                )

                BottomNavItem(
                    label = "Profile",
                    isSelected = true,
                    icon = { ProfileNavIcon(modifier = Modifier.size(20.dp), tint = SuccessDark) },
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun MetricBox(
    modifier: Modifier,
    label: String,
    value: String,
    sublabel: String,
    valColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = LightNavyBg
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                sublabel,
                fontSize = 9.sp,
                color = if (valColor == SuccessDark) SuccessDark else TextSecondary
            )
        }
    }
}

@Composable
private fun DocumentCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    trailingBadge: String? = null,
    trailingButtonText: String? = null,
    trailingIconButton: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, OutlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightNavyBg),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(subtitle, fontSize = 11.sp, color = TextSecondary)
            }
            if (trailingBadge != null) {
                Surface(color = MintContainer, shape = RoundedCornerShape(20.dp)) {
                    Text(
                        trailingBadge,
                        color = SuccessDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                VisibilityIcon(modifier = Modifier.size(18.dp), tint = NavyPrimary)
            }
            if (trailingButtonText != null) {
                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(trailingButtonText, fontSize = 11.sp, color = NavyPrimary)
                }
            }
            if (trailingIconButton != null) {
                trailingIconButton()
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
        }
        RightChevronIcon(modifier = Modifier.size(14.dp), tint = TextSecondary)
    }
}

@Composable
fun EditIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(w * 0.65f, h * 0.20f)
            lineTo(w * 0.80f, h * 0.35f)
            lineTo(w * 0.35f, h * 0.80f)
            lineTo(w * 0.20f, h * 0.80f)
            lineTo(w * 0.20f, h * 0.65f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun FileDownloadIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawLine(color = tint, start = Offset(w * 0.50f, h * 0.18f), end = Offset(w * 0.50f, h * 0.62f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        val arrowHead = Path().apply {
            moveTo(w * 0.30f, h * 0.45f)
            lineTo(w * 0.50f, h * 0.62f)
            lineTo(w * 0.70f, h * 0.45f)
        }
        drawPath(arrowHead, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val tray = Path().apply {
            moveTo(w * 0.20f, h * 0.72f)
            lineTo(w * 0.20f, h * 0.82f)
            lineTo(w * 0.80f, h * 0.82f)
            lineTo(w * 0.80f, h * 0.72f)
        }
        drawPath(tray, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun VpnKeyIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(color = tint, radius = w * 0.22f, center = Offset(w * 0.32f, h * 0.5f), style = Stroke(width = w * 0.09f))

        drawLine(color = tint, start = Offset(w * 0.54f, h * 0.5f), end = Offset(w * 0.85f, h * 0.5f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.72f, h * 0.5f), end = Offset(w * 0.72f, h * 0.68f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.85f, h * 0.5f), end = Offset(w * 0.85f, h * 0.68f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    }
}

@Composable
fun LogoutIcon(
    modifier: Modifier = Modifier,
    tint: Color = RedAlertColor
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val door = Path().apply {
            moveTo(w * 0.55f, h * 0.20f)
            lineTo(w * 0.22f, h * 0.20f)
            lineTo(w * 0.22f, h * 0.80f)
            lineTo(w * 0.55f, h * 0.80f)
        }
        drawPath(door, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = tint, start = Offset(w * 0.40f, h * 0.50f), end = Offset(w * 0.85f, h * 0.50f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        val arrow = Path().apply {
            moveTo(w * 0.68f, h * 0.35f)
            lineTo(w * 0.85f, h * 0.50f)
            lineTo(w * 0.68f, h * 0.65f)
        }
        drawPath(arrow, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentProfilePreview() {
    UniversityIntershipAppTheme {
        StudentProfileScreen()
    }
}
