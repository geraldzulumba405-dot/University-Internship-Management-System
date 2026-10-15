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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

@Composable
fun StudentSearchScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onHomeNavClick: () -> Unit = {},
    onApplicationsNavClick: () -> Unit = {},
    onProfileNavClick: () -> Unit = {},
    onApplyClick: (String) -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedBottomNav by remember { mutableIntStateOf(1) }

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
                        text = "Search & Explore",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )
                    Text(
                        text = "University Verified Roles",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onSavedClick() },
                    contentAlignment = Alignment.Center
                ) {
                    BookmarkIcon(
                        modifier = Modifier.size(22.dp),
                        tint = Color(0xFF334155)
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
                // 2. Search Input Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    ),
                    placeholder = {
                        Text(
                            text = "Search by role, company, or skills...",
                            color = Color(0xFF475569),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    leadingIcon = {
                        SearchNavIcon(
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF0F2C59)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { searchQuery = "" },
                                contentAlignment = Alignment.Center
                            ) {
                                ClearCircleIcon(
                                    modifier = Modifier.size(20.dp),
                                    tint = Color(0xFF64748B)
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF0F172A),
                        unfocusedTextColor = Color(0xFF0F172A),
                        focusedBorderColor = Color(0xFF0F2C59),
                        unfocusedBorderColor = Color(0xFF94A3B8),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chip 1: Filters (3)
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFF0F2C59)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterIcon(modifier = Modifier.size(15.dp), tint = Color.White)
                            Text(
                                text = "Filters",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(Color(0xFF38BDF8), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "3",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F2C59)
                                )
                            }
                        }
                    }

                    // Chip 2: CS & Software [x]
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFF1E3A8A))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "CS & Software",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                            ClearIcon(modifier = Modifier.size(11.dp), tint = Color(0xFF1E3A8A))
                        }
                    }

                    // Chip 3: Remote & Hybrid
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SignalIcon(modifier = Modifier.size(13.dp), tint = Color(0xFF0F766E))
                            Text(
                                text = "Remote & Hybrid",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                        }
                    }

                    // Chip 4: Paid Only
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFCCFBF1),
                        border = BorderStroke(1.dp, Color(0xFF99F6E4))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Paid Only",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F766E)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))

                // 4. Results Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF059669), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Showing 28 verified internships",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { }
                    ) {
                        Text(
                            text = "Relevance",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        DownChevronIcon(modifier = Modifier.size(14.dp), tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Result Card 1: Cloud Infrastructure Intern
                SearchResultCard(
                    title = "Cloud Infrastructure Intern",
                    company = "Sparc Systems",
                    location = "Lilongwe",
                    tags = listOf(
                        TagData("Hybrid", Color(0xFF99F6E4), Color(0xFF0F766E)),
                        TagData("3 Months", Color(0xFFE0E7FF), Color(0xFF4338CA)),
                        TagData("5 days left", Color(0xFFFEE2E2), Color(0xFFDC2626), showClock = true),
                        TagData("DevOps & Cloud", Color(0xFFF1F5F9), Color(0xFF475569))
                    ),
                    deadline = "December 20, 2026",
                    icon = { TerminalIcon(modifier = Modifier.size(22.dp), tint = Color(0xFF1E3A8A)) },
                    onApplyClick = { onApplyClick("Cloud Infrastructure Intern") }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Result Card 2: Frontend Engineer Intern
                SearchResultCard(
                    title = "Frontend Engineer Intern",
                    company = "INFI-TECH",
                    location = "Lilongwe",
                    tags = listOf(
                        TagData("Hybrid", Color(0xFF99F6E4), Color(0xFF0F766E)),
                        TagData("4 Months", Color(0xFFE0E7FF), Color(0xFF4338CA)),
                        TagData("Web Development", Color(0xFFF1F5F9), Color(0xFF475569))
                    ),
                    deadline = "October 24, 2026",
                    icon = { MonitorIcon(modifier = Modifier.size(22.dp), tint = Color(0xFF0F766E)) },
                    onApplyClick = { onApplyClick("Frontend Engineer Intern") }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Result Card 3: Cybersecurity Analyst Intern
                SearchResultCard(
                    title = "Cybersecurity Analyst Intern",
                    company = "NITEL",
                    location = "Lilongwe",
                    tags = listOf(
                        TagData("On-site", Color(0xFFE0E7FF), Color(0xFF4338CA)),
                        TagData("3 Months", Color(0xFFE0E7FF), Color(0xFF4338CA)),
                        TagData("Information Security", Color(0xFFF1F5F9), Color(0xFF475569))
                    ),
                    deadline = "November 15, 2026",
                    icon = { ShieldIcon(modifier = Modifier.size(22.dp), tint = Color(0xFF1E3A8A)) },
                    onApplyClick = { onApplyClick("Cybersecurity Analyst Intern") }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // 6. Bottom Navigation Bar
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
                    label = "Home",
                    isSelected = selectedBottomNav == 0,
                    icon = { HomeNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 0) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 0
                        onHomeNavClick()
                    }
                )

                BottomNavItem(
                    label = "Search",
                    isSelected = selectedBottomNav == 1,
                    icon = { SearchNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 1) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = { selectedBottomNav = 1 }
                )

                BottomNavItem(
                    label = "Applications",
                    isSelected = selectedBottomNav == 2,
                    icon = { ApplicationsNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 2) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 2
                        onApplicationsNavClick()
                    }
                )

                BottomNavItem(
                    label = "Profile",
                    isSelected = selectedBottomNav == 3,
                    icon = { ProfileNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 3) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 3
                        onProfileNavClick()
                    }
                )
            }
        }
    }
}

data class TagData(
    val label: String,
    val backgroundColor: Color,
    val textColor: Color,
    val showClock: Boolean = false
)

@Composable
fun SearchResultCard(
    title: String,
    company: String,
    location: String,
    tags: List<TagData>,
    deadline: String,
    icon: @Composable () -> Unit,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSaved by remember { mutableStateOf(value = false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$company • $location",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { isSaved = !isSaved },
                    contentAlignment = Alignment.Center
                ) {
                    BookmarkIcon(
                        modifier = Modifier.size(20.dp),
                        tint = if (isSaved) Color(0xFF0F2C59) else Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tag Chips Flow/Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tags.forEach { (label, backgroundColor, textColor, showClock) ->
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = backgroundColor
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (showClock) {
                                ClockIcon(modifier = Modifier.size(12.dp), tint = textColor)
                            }
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Deadline: ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                    Text(
                        text = deadline,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Button(
                    onClick = onApplyClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2C59)),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(
                        text = "Apply Now",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun BookmarkIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF334155)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(w * 0.22f, h * 0.15f)
            lineTo(w * 0.78f, h * 0.15f)
            lineTo(w * 0.78f, h * 0.85f)
            lineTo(w * 0.50f, h * 0.65f)
            lineTo(w * 0.22f, h * 0.85f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ClearCircleIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF94A3B8)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(color = Color(0xFFE2E8F0), radius = w * 0.45f, center = Offset(w * 0.5f, h * 0.5f))

        val stroke = w * 0.08f
        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.35f), end = Offset(w * 0.65f, h * 0.65f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.65f, h * 0.35f), end = Offset(w * 0.35f, h * 0.65f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
fun ClearIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E3A8A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val stroke = w * 0.15f
        drawLine(color = tint, start = Offset(w * 0.2f, h * 0.2f), end = Offset(w * 0.8f, h * 0.8f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.8f, h * 0.2f), end = Offset(w * 0.2f, h * 0.8f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
fun TerminalIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E3A8A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val rect = Path().apply {
            moveTo(w * 0.15f, h * 0.22f)
            lineTo(w * 0.85f, h * 0.22f)
            lineTo(w * 0.85f, h * 0.78f)
            lineTo(w * 0.15f, h * 0.78f)
            close()
        }
        drawPath(rect, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val prompt = Path().apply {
            moveTo(w * 0.28f, h * 0.40f)
            lineTo(w * 0.42f, h * 0.50f)
            lineTo(w * 0.28f, h * 0.60f)
        }
        drawPath(prompt, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = tint, start = Offset(w * 0.48f, h * 0.60f), end = Offset(w * 0.68f, h * 0.60f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
    }
}

@Composable
fun MonitorIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F766E)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val screen = Path().apply {
            moveTo(w * 0.15f, h * 0.22f)
            lineTo(w * 0.85f, h * 0.22f)
            lineTo(w * 0.85f, h * 0.68f)
            lineTo(w * 0.15f, h * 0.68f)
            close()
        }
        drawPath(screen, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = tint, start = Offset(w * 0.50f, h * 0.68f), end = Offset(w * 0.50f, h * 0.82f), strokeWidth = w * 0.08f)
        drawLine(color = tint, start = Offset(w * 0.32f, h * 0.82f), end = Offset(w * 0.68f, h * 0.82f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
    }
}

@Composable
fun ShieldIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E3A8A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val shield = Path().apply {
            moveTo(w * 0.5f, h * 0.18f)
            lineTo(w * 0.82f, h * 0.28f)
            lineTo(w * 0.82f, h * 0.55f)
            cubicTo(w * 0.82f, h * 0.78f, w * 0.5f, h * 0.88f, w * 0.5f, h * 0.88f)
            cubicTo(w * 0.5f, h * 0.88f, w * 0.18f, h * 0.78f, w * 0.18f, h * 0.55f)
            lineTo(w * 0.18f, h * 0.28f)
            close()
        }
        drawPath(shield, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun SignalIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F766E)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(color = tint, radius = w * 0.08f, center = Offset(w * 0.5f, h * 0.5f))
        drawArc(
            color = tint,
            startAngle = -45f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(w * 0.30f, h * 0.30f),
            size = Size(w * 0.40f, h * 0.40f),
            style = Stroke(width = w * 0.10f, cap = StrokeCap.Round)
        )
        drawArc(
            color = tint,
            startAngle = 135f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(w * 0.30f, h * 0.30f),
            size = Size(w * 0.40f, h * 0.40f),
            style = Stroke(width = w * 0.10f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun DownChevronIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(w * 0.2f, h * 0.35f)
            lineTo(w * 0.5f, h * 0.68f)
            lineTo(w * 0.8f, h * 0.35f)
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.15f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentSearchScreenPreview() {
    UniversityIntershipAppTheme {
        StudentSearchScreen()
    }
}
