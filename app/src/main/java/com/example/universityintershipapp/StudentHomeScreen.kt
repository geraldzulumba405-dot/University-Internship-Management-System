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
fun StudentHomeScreen(
    modifier: Modifier = Modifier,
    studentName: String = "Gerald Zulumba",
    onSearchClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onViewAllRecommended: () -> Unit = {},
    onApplyClick: (String) -> Unit = {},
    onApplicationsClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedBottomNav by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Top Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Logo
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF1B3B80), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val w = size.width
                        val h = size.height

                        val capTop = Path().apply {
                            moveTo(w * 0.5f, h * 0.15f)
                            lineTo(w * 0.9f, h * 0.38f)
                            lineTo(w * 0.5f, h * 0.60f)
                            lineTo(w * 0.1f, h * 0.38f)
                            close()
                        }
                        drawPath(capTop, color = Color.White)

                        val capBottom = Path().apply {
                            moveTo(w * 0.25f, h * 0.48f)
                            lineTo(w * 0.25f, h * 0.68f)
                            cubicTo(w * 0.25f, h * 0.82f, w * 0.75f, h * 0.82f, w * 0.75f, h * 0.68f)
                            lineTo(w * 0.75f, h * 0.48f)
                            close()
                        }
                        drawPath(capBottom, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name Greeting
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UniIntern",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )
                    Text(
                        text = "Welcome back, $studentName!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }

                // Notification Bell
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    BellIcon(
                        modifier = Modifier.size(24.dp),
                        badgeCount = 2,
                        tint = Color(0xFF0F2C59)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Profile Avatar Circle
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFFDBEAFE), CircleShape)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    PersonIcon(
                        modifier = Modifier.size(22.dp),
                        tint = Color(0xFF1E3A8A)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // Scrollable Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // 2. Search & Filter Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0F172A)
                        ),
                        placeholder = {
                            Text(
                                text = "Search internships by role, company...",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            SearchNavIcon(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { onSearchClick() },
                                tint = Color(0xFF334155)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
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

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { onFilterClick() },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            FilterIcon(
                                modifier = Modifier.size(22.dp),
                                tint = Color(0xFF0F2C59)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Urgent Alert Banner
                UrgentAlertBanner()

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Application Overview Section
                Text(
                    text = "Application Overview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1: Available Internships
                    OverviewCard(
                        modifier = Modifier.weight(1f),
                        topIcon = { BriefcaseIcon(modifier = Modifier.size(20.dp)) },
                        trendIcon = { TrendUpIcon(modifier = Modifier.size(18.dp)) },
                        countText = "142",
                        labelText = "Available Internships"
                    )

                    // Card 2: Submitted
                    OverviewCard(
                        modifier = Modifier.weight(1f),
                        topIcon = { PaperPlaneIcon(modifier = Modifier.size(20.dp)) },
                        topTagText = "Total",
                        countText = "4",
                        labelText = "Submitted"
                    )

                    // Card 3: Under Review
                    OverviewCard(
                        modifier = Modifier.weight(1f),
                        topIcon = { ClockIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF0D9488)) },
                        statusDotColor = Color(0xFF34D399),
                        countText = "2",
                        labelText = "Under Review"
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 5. Recommended for You Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Recommended for You",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F2C59)
                        )
                        Text(
                            text = "Computer Science & Engineering",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155)
                        )
                    }

                    Row(
                        modifier = Modifier.clickable { onViewAllRecommended() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View All",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F766E)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        RightChevronIcon(
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF0F766E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Internship Card 1
                InternshipCard(
                    title = "Software Engineering Intern",
                    companyLocation = "INF TECH INC • Lilongwe (Remote)",
                    duration = "3 Months",
                    deadline = "May 15, 2...",
                    field = "Software Development",
                    onApplyClick = { onApplyClick("Software Engineering Intern") }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Internship Card 2
                InternshipCard(
                    title = "Data Science & AI Research Intern",
                    companyLocation = "BD Tech Consulting • Zomba (On-site)",
                    duration = "6 Months",
                    deadline = "November...",
                    field = "Artificial Intelligence",
                    onApplyClick = { onApplyClick("Data Science & AI Research Intern") }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 6. Latest Postings Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Latest Postings",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )
                    Text(
                        text = "Updated today",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Latest Posting Card
                PostingCard(
                    title = "UX/UI Product Design Intern",
                    isNew = true,
                    companyLocation = "TechMalawi • Blantyre (On-site)",
                    duration = "4 Months",
                    deadline = "June 10, 2...",
                    onDetailsClick = { onApplyClick("UX/UI Product Design Intern") }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // 7. Bottom Navigation Bar
            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nav Item 0: Home
                BottomNavItem(
                    label = "Home",
                    isSelected = selectedBottomNav == 0,
                    icon = { HomeNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 0) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = { selectedBottomNav = 0 }
                )

                // Nav Item 1: Search
                BottomNavItem(
                    label = "Search",
                    isSelected = selectedBottomNav == 1,
                    icon = { SearchNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 1) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 1
                        onSearchClick()
                    }
                )

                // Nav Item 2: Applications
                BottomNavItem(
                    label = "Applications",
                    isSelected = selectedBottomNav == 2,
                    icon = { ApplicationsNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 2) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 2
                        onApplicationsClick()
                    }
                )

                // Nav Item 3: Profile
                BottomNavItem(
                    label = "Profile",
                    isSelected = selectedBottomNav == 3,
                    icon = { ProfileNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 3) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 3
                        onProfileClick()
                    }
                )
            }
        }
    }
}

@Composable
fun UrgentAlertBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left dark teal vertical stripe
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(110.dp)
                    .background(Color(0xFF0F766E), RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        ClockIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF1E3A8A))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFFEE2E2)
                    ) {
                        Text(
                            text = "URGENT ALERT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "• 3 days remaining",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Business Management Internship closes in 3 days!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2C59)),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Finish Form",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            RightArrowIcon(modifier = Modifier.size(12.dp), tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Dismiss",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        modifier = Modifier.clickable { }
                    )
                }
            }
        }
    }
}

@Composable
fun OverviewCard(
    modifier: Modifier = Modifier,
    topIcon: @Composable () -> Unit,
    trendIcon: (@Composable () -> Unit)? = null,
    topTagText: String? = null,
    statusDotColor: Color? = null,
    countText: String,
    labelText: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                topIcon()

                when {
                    trendIcon != null -> trendIcon()
                    topTagText != null -> {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFE0E7FF)
                        ) {
                            Text(
                                text = topTagText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4338CA),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    statusDotColor != null -> {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(statusDotColor, CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = countText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2C59)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = labelText,
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun InternshipCard(
    title: String,
    companyLocation: String,
    duration: String,
    deadline: String,
    field: String,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = companyLocation,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F5F9)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CalendarIcon(modifier = Modifier.size(14.dp), tint = Color(0xFF334155))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Duration: ", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
                            Text(text = duration, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CalendarIcon(modifier = Modifier.size(14.dp), tint = Color(0xFF334155))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Deadline: ", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
                            Text(text = deadline, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CategoryIcon(modifier = Modifier.size(14.dp), tint = Color(0xFF334155))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Field: ", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
                        Text(text = field, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

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

@Composable
fun PostingCard(
    title: String,
    isNew: Boolean,
    companyLocation: String,
    duration: String,
    deadline: String,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.weight(1f)
                )

                if (isNew) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFE0E7FF)
                    ) {
                        Text(
                            text = "New",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4338CA),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = companyLocation,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F5F9)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CalendarIcon(modifier = Modifier.size(14.dp), tint = Color(0xFF334155))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Duration: ", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
                        Text(text = duration, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CalendarIcon(modifier = Modifier.size(14.dp), tint = Color(0xFF334155))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Deadline: ", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
                        Text(text = deadline, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onDetailsClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                modifier = Modifier.height(38.dp)
            ) {
                Text(
                    text = "Details",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    isSelected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    if (isSelected) {
        Surface(
            shape = RoundedCornerShape(50),
            color = Color(0xFF99F6E4),
            modifier = Modifier.clickable { onClick() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                icon()
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F766E)
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun BellIcon(
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    tint: Color = Color(0xFF0F2C59)
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val bellPath = Path().apply {
                moveTo(w * 0.5f, h * 0.15f)
                cubicTo(w * 0.35f, h * 0.15f, w * 0.25f, h * 0.32f, w * 0.25f, h * 0.55f)
                lineTo(w * 0.15f, h * 0.70f)
                lineTo(w * 0.85f, h * 0.70f)
                lineTo(w * 0.75f, h * 0.55f)
                cubicTo(w * 0.75f, h * 0.32f, w * 0.65f, h * 0.15f, w * 0.5f, h * 0.15f)
                close()
            }
            drawPath(bellPath, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round))

            drawArc(
                color = tint,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(w * 0.40f, h * 0.72f),
                size = Size(w * 0.20f, h * 0.15f),
                style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
            )
        }

        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .size(15.dp)
                    .background(Color(0xFFDC2626), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FilterIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F2C59)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.09f

        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.30f), end = Offset(w * 0.85f, h * 0.30f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawCircle(color = Color.White, radius = w * 0.10f, center = Offset(w * 0.35f, h * 0.30f))
        drawCircle(color = tint, radius = w * 0.10f, center = Offset(w * 0.35f, h * 0.30f), style = Stroke(stroke))

        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.70f), end = Offset(w * 0.85f, h * 0.70f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawCircle(color = Color.White, radius = w * 0.10f, center = Offset(w * 0.65f, h * 0.70f))
        drawCircle(color = tint, radius = w * 0.10f, center = Offset(w * 0.65f, h * 0.70f), style = Stroke(stroke))
    }
}

@Composable
fun ClockIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E3A8A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(color = tint, radius = w * 0.42f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.09f))

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.25f)
            lineTo(w * 0.5f, h * 0.5f)
            lineTo(w * 0.68f, h * 0.5f)
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}



@Composable
fun TrendUpIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF059669)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.75f)
            lineTo(w * 0.45f, h * 0.45f)
            lineTo(w * 0.62f, h * 0.60f)
            lineTo(w * 0.85f, h * 0.25f)
            moveTo(w * 0.62f, h * 0.25f)
            lineTo(w * 0.85f, h * 0.25f)
            lineTo(w * 0.85f, h * 0.48f)
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun PaperPlaneIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val plane = Path().apply {
            moveTo(w * 0.15f, h * 0.50f)
            lineTo(w * 0.85f, h * 0.18f)
            lineTo(w * 0.52f, h * 0.85f)
            lineTo(w * 0.40f, h * 0.58f)
            close()
        }
        drawPath(plane, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun CalendarIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val body = Path().apply {
            moveTo(w * 0.18f, h * 0.28f)
            lineTo(w * 0.82f, h * 0.28f)
            lineTo(w * 0.82f, h * 0.85f)
            lineTo(w * 0.18f, h * 0.85f)
            close()
        }
        drawPath(body, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.15f), end = Offset(w * 0.35f, h * 0.28f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.65f, h * 0.15f), end = Offset(w * 0.65f, h * 0.28f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.18f, h * 0.45f), end = Offset(w * 0.82f, h * 0.45f), strokeWidth = w * 0.07f)
    }
}

@Composable
fun CategoryIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.18f)
            lineTo(w * 0.82f, h * 0.38f)
            lineTo(w * 0.5f, h * 0.58f)
            lineTo(w * 0.18f, h * 0.38f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.08f, join = StrokeJoin.Round))

        val path2 = Path().apply {
            moveTo(w * 0.18f, h * 0.55f)
            lineTo(w * 0.5f, h * 0.72f)
            lineTo(w * 0.82f, h * 0.55f)
        }
        drawPath(path2, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun RightChevronIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F766E)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.35f, h * 0.2f)
            lineTo(w * 0.70f, h * 0.5f)
            lineTo(w * 0.35f, h * 0.8f)
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.15f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun HomeNavIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F766E)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val roof = Path().apply {
            moveTo(w * 0.15f, h * 0.48f)
            lineTo(w * 0.50f, h * 0.18f)
            lineTo(w * 0.85f, h * 0.48f)
        }
        drawPath(roof, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val base = Path().apply {
            moveTo(w * 0.25f, h * 0.45f)
            lineTo(w * 0.25f, h * 0.82f)
            lineTo(w * 0.75f, h * 0.82f)
            lineTo(w * 0.75f, h * 0.45f)
        }
        drawPath(base, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun SearchNavIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(color = tint, radius = w * 0.30f, center = Offset(w * 0.42f, h * 0.42f), style = Stroke(width = w * 0.09f))
        drawLine(color = tint, start = Offset(w * 0.62f, h * 0.62f), end = Offset(w * 0.85f, h * 0.85f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    }
}

@Composable
fun ApplicationsNavIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val doc = Path().apply {
            moveTo(w * 0.22f, h * 0.18f)
            lineTo(w * 0.78f, h * 0.18f)
            lineTo(w * 0.78f, h * 0.82f)
            lineTo(w * 0.22f, h * 0.82f)
            close()
        }
        drawPath(doc, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.35f), end = Offset(w * 0.65f, h * 0.35f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.50f), end = Offset(w * 0.65f, h * 0.50f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.65f), end = Offset(w * 0.52f, h * 0.65f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
    }
}

@Composable
fun ProfileNavIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(color = tint, radius = w * 0.20f, center = Offset(w * 0.5f, h * 0.32f), style = Stroke(width = w * 0.08f))

        val body = Path().apply {
            moveTo(w * 0.22f, h * 0.82f)
            cubicTo(w * 0.22f, h * 0.62f, w * 0.78f, h * 0.62f, w * 0.78f, h * 0.82f)
        }
        drawPath(body, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentHomeScreenPreview() {
    UniversityIntershipAppTheme {
        StudentHomeScreen()
    }
}
