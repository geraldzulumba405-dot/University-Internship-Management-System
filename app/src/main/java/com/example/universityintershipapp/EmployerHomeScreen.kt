package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

@Composable
fun EmployerHomeScreen(
    modifier: Modifier = Modifier,
    companyName: String = "Google LLC",
    onPostInternshipClick: () -> Unit = {},
    onViewApplicantsClick: (String) -> Unit = {},
    onNavigateApplicants: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onLogOutClick: () -> Unit = {}
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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Company Building Logo Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF0F2C59), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    BuildingIcon(
                        modifier = Modifier.size(22.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = companyName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )
                    Text(
                        text = "Employer Recruiter Portal",
                        fontSize = 12.sp,
                        color = Color(0xFF0F766E),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Notification Bell
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    BellIcon(
                        modifier = Modifier.size(22.dp),
                        badgeCount = 3,
                        tint = Color(0xFF0F2C59)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Logout Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFFFFE4E6), CircleShape)
                        .clickable { onLogOutClick() },
                    contentAlignment = Alignment.Center
                ) {
                    LogoutIcon(
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFFDC2626)
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
                // 2. Welcome Banner & Post Internship Action
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F2C59)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Hire Top Student Talent",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Post internship positions, review verified academic credentials, and schedule candidate interviews.",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onPostInternshipClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2DD4BF))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PlusIcon(modifier = Modifier.size(16.dp), tint = Color(0xFF0F2C59))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Post New Internship",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F2C59)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Metrics Summary Grid
                Text(
                    text = "Recruitment Overview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EmployerMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Active Postings",
                        count = "6",
                        subtext = "openings",
                        icon = { BriefcaseIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF0F2C59)) }
                    )

                    EmployerMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Applications",
                        count = "38",
                        subtext = "students",
                        icon = { ApplicationsNavIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF0F766E)) }
                    )

                    EmployerMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Interviews",
                        count = "5",
                        subtext = "set",
                        icon = { CalendarIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF1E3A8A)) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Manage Active Listings Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Internship Listings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "View All (6)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E),
                        modifier = Modifier.clickable { }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Listing 1
                EmployerListingCard(
                    title = "Software Engineering Intern",
                    locationType = "Mountain View, CA • Hybrid",
                    applicantsCount = 14,
                    deadline = "May 15, 2025",
                    onViewApplicants = { onViewApplicantsClick("SE-01") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Listing 2
                EmployerListingCard(
                    title = "Data Science & AI Intern",
                    locationType = "New York, NY • On-site",
                    applicantsCount = 8,
                    deadline = "June 01, 2025",
                    onViewApplicants = { onViewApplicantsClick("DS-02") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Recent Student Applicants Section
                Text(
                    text = "Recent Verified Applicants",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(12.dp))

                ApplicantCard(
                    studentName = "Gerald Zulumba",
                    program = "B.Sc. Computer Science (Yr 3)",
                    gpa = "GPA 3.84",
                    appliedFor = "Software Engineering Intern",
                    onReviewClick = { onViewApplicantsClick("SE-01") }
                )

                Spacer(modifier = Modifier.height(10.dp))

                ApplicantCard(
                    studentName = "Alex Chen",
                    program = "B.Sc. Software Engineering (Yr 3)",
                    gpa = "GPA 3.90",
                    appliedFor = "Data Science & AI Intern",
                    onReviewClick = { onViewApplicantsClick("DS-02") }
                )

                Spacer(modifier = Modifier.height(20.dp))
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
                BottomNavItem(
                    label = "Dashboard",
                    isSelected = selectedBottomNav == 0,
                    icon = { HomeNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 0) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = { selectedBottomNav = 0 }
                )

                BottomNavItem(
                    label = "Post Job",
                    isSelected = selectedBottomNav == 1,
                    icon = { PlusIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 1) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 1
                        onPostInternshipClick()
                    }
                )

                BottomNavItem(
                    label = "Applicants",
                    isSelected = selectedBottomNav == 2,
                    icon = { ApplicationsNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 2) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 2
                        onNavigateApplicants()
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
private fun EmployerMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    count: String,
    subtext: String,
    icon: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
                icon()
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = count,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2C59)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = subtext,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun EmployerListingCard(
    title: String,
    locationType: String,
    applicantsCount: Int,
    deadline: String,
    onViewApplicants: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = locationType,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }

                Surface(
                    color = Color(0xFFCCFBF1),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Active",
                        color = Color(0xFF0F766E),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👥 $applicantsCount Student Applicants",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
                Text(
                    text = "Deadline: $deadline",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onViewApplicants,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "View Applicants ($applicantsCount)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F2C59)
                )
            }
        }
    }
}

@Composable
private fun ApplicantCard(
    studentName: String,
    program: String,
    gpa: String,
    appliedFor: String,
    onReviewClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Profile Icon adjacent to the user
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFEEF2FF), CircleShape)
                            .border(1.dp, Color(0xFFC7D2FE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        PersonIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF1E3A8A))
                    }

                    Text(
                        text = studentName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // GPA Pill Badge
                    Surface(
                        color = Color(0xFFCCFBF1),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color(0xFF99F6E4))
                    ) {
                        Text(
                            text = gpa,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F766E),
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = program,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155)
                )
                Text(
                    text = "Applied: $appliedFor",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onReviewClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2C59)),
                modifier = Modifier.height(34.dp)
            ) {
                Text(text = "Review", fontSize = 11.sp, color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmployerHomeScreenPreview() {
    UniversityIntershipAppTheme {
        EmployerHomeScreen()
    }
}
