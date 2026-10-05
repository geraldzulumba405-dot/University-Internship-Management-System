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
import androidx.compose.material3.HorizontalDivider
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

@Composable
fun StudentApplicationScreen(
    modifier: Modifier = Modifier,
    title: String = "Software Engineering Intern",
    company: String = "Google",
    location: String = "New York, USA",
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onOneTapApplyClick: () -> Unit = {},
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isSaved by remember { mutableStateOf(value = false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    BackArrowIcon(
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Internship Detail",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CheckBadgeIcon(modifier = Modifier.size(12.dp), tint = Color(0xFF0F766E))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "UniApproved Milestone",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0F766E)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { isSaved = !isSaved },
                    contentAlignment = Alignment.Center
                ) {
                    BookmarkIcon(
                        modifier = Modifier.size(20.dp),
                        tint = if (isSaved) Color(0xFF0F2C59) else Color(0xFF334155)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onShareClick() },
                    contentAlignment = Alignment.Center
                ) {
                    ShareIcon(
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF334155)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // Scrollable Body Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // 2. Main Hero Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            CompanyLogoG(modifier = Modifier.size(52.dp))

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFFEF3C7),
                                border = BorderStroke(1.dp, Color(0xFFFDE68A))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    ClockIcon(modifier = Modifier.size(13.dp), tint = Color(0xFFB45309))
                                    Text(
                                        text = "4 days remaining!",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB45309)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        Spacer(modifier = Modifier.height(14.dp))

                        // Details Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Location
                            Row(verticalAlignment = Alignment.Top) {
                                LocationPinIcon(
                                    modifier = Modifier.size(18.dp),
                                    tint = Color(0xFF1E3A8A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(text = "Location", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text(
                                        text = location,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(text = "Hybrid / On-site", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }

                            // Duration & Pace
                            Row(verticalAlignment = Alignment.Top) {
                                CalendarIcon(
                                    modifier = Modifier.size(18.dp),
                                    tint = Color(0xFF0F766E)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(text = "Duration & Pace", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text(
                                        text = "12 Weeks",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(text = "Full-time (40 hrs/wk)", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Application Deadline Banner Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CalendarIcon(modifier = Modifier.size(16.dp), tint = Color(0xFF1E3A8A))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Application Deadline:",
                                        fontSize = 12.sp,
                                        color = Color(0xFF475569)
                                    )
                                }

                                Text(
                                    text = "October 15, 2026",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E3A8A)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Info Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InfoStatCard(
                        modifier = Modifier.weight(1f),
                        icon = { DollarIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF0F766E)) },
                        iconBg = Color(0xFFCCFBF1),
                        label = "Rate",
                        value = "$48.00 / hr"
                    )

                    InfoStatCard(
                        modifier = Modifier.weight(1f),
                        icon = { GraduationCapIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF1E3A8A)) },
                        iconBg = Color(0xFFDBEAFE),
                        label = "Academic Credit",
                        value = "Eligible (3-4 GPA)",
                        valueColor = Color(0xFF1E3A8A)
                    )

                    InfoStatCard(
                        modifier = Modifier.weight(1f),
                        icon = { BriefcaseIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF1E3A8A)) },
                        iconBg = Color(0xFFDBEAFE),
                        label = "Standing",
                        value = "Jr. / Sr. Only"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4. Tab Switcher
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TabItem(
                            label = "Description",
                            isSelected = selectedTab == 0,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 0 }
                        )
                        TabItem(
                            label = "Requirements",
                            isSelected = selectedTab == 1,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 1 }
                        )
                        TabItem(
                            label = "Instructions",
                            isSelected = selectedTab == 2,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 2 }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Role Overview & Responsibilities Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                ApplicationsNavIcon(
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF1E3A8A)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Role Overview & Responsibilities",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "As a Software Engineering Intern at $company, you will work on core enterprise architecture and user-facing features that impact billions of worldwide users. You will be matched with an industry mentor and integrated into an active agile engineering team.",
                            fontSize = 13.sp,
                            color = Color(0xFF475569),
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Responsibility Item 1
                        ResponsibilityBox(
                            icon = { CheckCircleIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF0F766E)) },
                            boldText = "Enterprise Development: ",
                            description = "Design, test, deploy, and maintain large-scale services and APIs across cloud infrastructures."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Responsibility Item 2
                        ResponsibilityBox(
                            icon = { MentorIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF0F766E)) },
                            boldText = "Senior Mentorship: ",
                            description = "1-on-1 technical guidance from Principal Engineers, weekly review sprints, and university coordinator oversight."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Responsibility Item 3
                        ResponsibilityBox(
                            icon = { CloudIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF0F766E)) },
                            boldText = "Scalable Cloud Architecture: ",
                            description = "Optimize high-throughput microservices using modern storage engines and containerized workloads."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 6. University Pre-Approved Curriculum Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ShieldCheckIcon(
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF1E3A8A)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "University Pre-Approved Curriculum",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F2C59)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "This position fulfills the required 480 fieldwork contact hours for the College of Engineering & Applied Sciences summer internship credit (ENGR-4990).",
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // 7. Fixed Bottom Action Bar
            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(52.dp)
                        .clickable { isSaved = !isSaved },
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        BookmarkIcon(
                            modifier = Modifier.size(22.dp),
                            tint = if (isSaved) Color(0xFF0F2C59) else Color(0xFF475569)
                        )
                    }
                }

                Button(
                    onClick = onOneTapApplyClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F2C59)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        LightningIcon(
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFF38BDF8)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "One-Tap Apply",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoStatCard(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    iconBg: Color,
    label: String,
    value: String,
    valueColor: Color = Color(0xFF0F172A)
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@Composable
fun TabItem(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (isSelected) {
        Surface(
            modifier = modifier.clickable { onClick() },
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(
                modifier = Modifier.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2C59)
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .clickable { onClick() }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun ResponsibilityBox(
    icon: @Composable () -> Unit,
    boldText: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            icon()
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = buildString {
                    append(boldText)
                    append(description)
                },
                fontSize = 12.sp,
                color = Color(0xFF334155),
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
fun CompanyLogoG(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(32.dp)) {
                val w = size.width
                val h = size.height

                // Colored G logo
                drawArc(
                    color = Color(0xFFEA4335), // Red
                    startAngle = 180f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = Offset(w * 0.1f, h * 0.1f),
                    size = Size(w * 0.8f, h * 0.8f),
                    style = Stroke(width = w * 0.18f)
                )
                drawArc(
                    color = Color(0xFFFBBC05), // Yellow
                    startAngle = 120f,
                    sweepAngle = 60f,
                    useCenter = false,
                    topLeft = Offset(w * 0.1f, h * 0.1f),
                    size = Size(w * 0.8f, h * 0.8f),
                    style = Stroke(width = w * 0.18f)
                )
                drawArc(
                    color = Color(0xFF34A853), // Green
                    startAngle = 0f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = Offset(w * 0.1f, h * 0.1f),
                    size = Size(w * 0.8f, h * 0.8f),
                    style = Stroke(width = w * 0.18f)
                )
                drawArc(
                    color = Color(0xFF4285F4), // Blue
                    startAngle = -60f,
                    sweepAngle = 60f,
                    useCenter = false,
                    topLeft = Offset(w * 0.1f, h * 0.1f),
                    size = Size(w * 0.8f, h * 0.8f),
                    style = Stroke(width = w * 0.18f)
                )

                drawLine(
                    color = Color(0xFF4285F4),
                    start = Offset(w * 0.45f, h * 0.5f),
                    end = Offset(w * 0.85f, h * 0.5f),
                    strokeWidth = w * 0.18f,
                    cap = StrokeCap.Square
                )
            }
        }
    }
}

@Composable
fun CheckBadgeIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F766E)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(color = tint, radius = w * 0.45f, center = Offset(w * 0.5f, h * 0.5f))

        val check = Path().apply {
            moveTo(w * 0.30f, h * 0.50f)
            lineTo(w * 0.45f, h * 0.65f)
            lineTo(w * 0.70f, h * 0.35f)
        }
        drawPath(check, color = Color.White, style = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ShareIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF334155)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val p1 = Offset(w * 0.75f, h * 0.25f)
        val p2 = Offset(w * 0.25f, h * 0.50f)
        val p3 = Offset(w * 0.75f, h * 0.75f)

        drawLine(color = tint, start = p2, end = p1, strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawLine(color = tint, start = p2, end = p3, strokeWidth = w * 0.08f, cap = StrokeCap.Round)

        drawCircle(color = tint, radius = w * 0.12f, center = p1)
        drawCircle(color = tint, radius = w * 0.12f, center = p2)
        drawCircle(color = tint, radius = w * 0.12f, center = p3)
    }
}

@Composable
fun LocationPinIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E3A8A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val pin = Path().apply {
            moveTo(w * 0.5f, h * 0.85f)
            cubicTo(w * 0.2f, h * 0.55f, w * 0.15f, h * 0.35f, w * 0.5f, h * 0.15f)
            cubicTo(w * 0.85f, h * 0.35f, w * 0.8f, h * 0.55f, w * 0.5f, h * 0.85f)
            close()
        }
        drawPath(pin, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawCircle(color = tint, radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.35f))
    }
}

@Composable
fun DollarIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F766E)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val sPath = Path().apply {
            moveTo(w * 0.65f, h * 0.30f)
            cubicTo(w * 0.35f, h * 0.20f, w * 0.35f, h * 0.45f, w * 0.50f, h * 0.50f)
            cubicTo(w * 0.65f, h * 0.55f, w * 0.65f, h * 0.80f, w * 0.35f, h * 0.70f)
        }
        drawPath(sPath, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round))

        drawLine(color = tint, start = Offset(w * 0.5f, h * 0.15f), end = Offset(w * 0.5f, h * 0.85f), strokeWidth = w * 0.10f, cap = StrokeCap.Round)
    }
}

@Composable
fun CheckCircleIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F766E)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(color = tint, radius = w * 0.42f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.09f))

        val check = Path().apply {
            moveTo(w * 0.32f, h * 0.50f)
            lineTo(w * 0.45f, h * 0.62f)
            lineTo(w * 0.68f, h * 0.38f)
        }
        drawPath(check, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun MentorIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F766E)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(color = tint, radius = w * 0.18f, center = Offset(w * 0.35f, h * 0.35f), style = Stroke(width = w * 0.09f))
        drawCircle(color = tint, radius = w * 0.14f, center = Offset(w * 0.70f, h * 0.45f), style = Stroke(width = w * 0.09f))

        val body1 = Path().apply {
            moveTo(w * 0.15f, h * 0.82f)
            cubicTo(w * 0.15f, h * 0.62f, w * 0.55f, h * 0.62f, w * 0.55f, h * 0.82f)
        }
        drawPath(body1, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round))
    }
}

@Composable
fun CloudIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F766E)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val cloud = Path().apply {
            moveTo(w * 0.20f, h * 0.72f)
            lineTo(w * 0.80f, h * 0.72f)
            cubicTo(w * 0.92f, h * 0.72f, w * 0.95f, h * 0.55f, w * 0.82f, h * 0.48f)
            cubicTo(w * 0.85f, h * 0.28f, w * 0.62f, h * 0.22f, w * 0.50f, h * 0.35f)
            cubicTo(w * 0.38f, h * 0.28f, w * 0.22f, h * 0.38f, w * 0.20f, h * 0.50f)
            cubicTo(w * 0.10f, h * 0.55f, w * 0.10f, h * 0.72f, w * 0.20f, h * 0.72f)
            close()
        }
        drawPath(cloud, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ShieldCheckIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E3A8A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val shield = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.28f)
            lineTo(w * 0.85f, h * 0.55f)
            cubicTo(w * 0.85f, h * 0.78f, w * 0.5f, h * 0.88f, w * 0.5f, h * 0.88f)
            cubicTo(w * 0.5f, h * 0.88f, w * 0.15f, h * 0.78f, w * 0.15f, h * 0.55f)
            lineTo(w * 0.15f, h * 0.28f)
            close()
        }
        drawPath(shield, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val check = Path().apply {
            moveTo(w * 0.35f, h * 0.50f)
            lineTo(w * 0.48f, h * 0.62f)
            lineTo(w * 0.68f, h * 0.40f)
        }
        drawPath(check, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun LightningIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF38BDF8)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val bolt = Path().apply {
            moveTo(w * 0.55f, h * 0.12f)
            lineTo(w * 0.22f, h * 0.52f)
            lineTo(w * 0.48f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.88f)
            lineTo(w * 0.78f, h * 0.48f)
            lineTo(w * 0.52f, h * 0.48f)
            close()
        }
        drawPath(bolt, color = tint)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentApplicationScreenPreview() {
    UniversityIntershipAppTheme {
        StudentApplicationScreen()
    }
}
