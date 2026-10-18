package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

@Composable
fun EmployerReceivedApplications(
    modifier: Modifier = Modifier,
    onApplicantClick: (String) -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigatePostJob: () -> Unit = {},
    onNavigateApplications: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {
    var selectedFilterTab by remember { mutableIntStateOf(0) } // 0: All, 1: Pending, 2: Accepted
    var selectedBottomNav by remember { mutableIntStateOf(2) } // Applicants selected

    val textBlack = Color(0xFF000000)
    val textDarkNavy = Color(0xFF0F2C59)

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
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Received Applications",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDarkNavy,
                    modifier = Modifier.weight(1f)
                )

                // Search Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    SearchNavIcon(
                        modifier = Modifier.size(22.dp),
                        tint = textBlack
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Filter / Menu Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    FilterIcon(
                        modifier = Modifier.size(22.dp),
                        tint = textBlack
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // 2. Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Filter Tabs Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterTabPill(
                        label = "All",
                        count = "18",
                        isSelected = selectedFilterTab == 0,
                        onClick = { selectedFilterTab = 0 }
                    )
                    FilterTabPill(
                        label = "Pending Review",
                        count = "9",
                        isSelected = selectedFilterTab == 1,
                        onClick = { selectedFilterTab = 1 }
                    )
                    FilterTabPill(
                        label = "Accepted",
                        count = "4",
                        isSelected = selectedFilterTab == 2,
                        onClick = { selectedFilterTab = 2 }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Applicant Cards List
                val applicantsList = listOf(
                    ApplicantData("Francis Jere", "GPA 3.84", "B.Sc. Software Engineering • May 2", "Pending", Color(0xFFFEF3C7), Color(0xFFD97706)),
                    ApplicantData("Emily Makanda", "GPA 3.95", "B.Sc. Data Science • May 1", "Accepted", Color(0xFFD1FAE5), Color(0xFF059669)),
                    ApplicantData("Alex Howa", "GPA 3.72", "B.Sc. Computer Engineering • Apr 29", "Pending", Color(0xFFFEF3C7), Color(0xFFD97706)),
                    ApplicantData("Chimwemwe Phiri", "GPA 3.65", "B.Sc. Information Systems • Apr 28", "Rejected", Color(0xFFFEE2E2), Color(0xFFDC2626)),
                    ApplicantData("Talandira Banda", "GPA 3.88", "B.Sc. Computer Science • Apr 27", "Accepted", Color(0xFFD1FAE5), Color(0xFF059669)),
                    ApplicantData("Sipho Ndhlovu", "GPA 3.78", "B.Sc. Electrical & Electronics • Apr 26", "Pending", Color(0xFFFEF3C7), Color(0xFFD97706)),
                    ApplicantData("Natasha Mwale", "GPA 3.92", "B.Sc. Artificial Intelligence • Apr 25", "Accepted", Color(0xFFD1FAE5), Color(0xFF059669)),
                    ApplicantData("Kelvin Tembo", "GPA 3.69", "B.Sc. Cyber Security • Apr 25", "Pending", Color(0xFFFEF3C7), Color(0xFFD97706))
                )

                val filteredList = when (selectedFilterTab) {
                    1 -> applicantsList.filter { it.statusText == "Pending" }
                    2 -> applicantsList.filter { it.statusText == "Accepted" }
                    else -> applicantsList
                }

                filteredList.forEach { applicant ->
                    ReceivedApplicantCard(
                        applicant = applicant,
                        onClick = { onApplicantClick(applicant.name) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
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
                    label = "Post Job",
                    isSelected = selectedBottomNav == 1,
                    icon = { PlusIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 1) Color(0xFF0F766E) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 1
                        onNavigatePostJob()
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

private data class ApplicantData(
    val name: String,
    val gpa: String,
    val programAndDate: String,
    val statusText: String,
    val statusBg: Color,
    val statusTextCol: Color
)

@Composable
private fun FilterTabPill(
    label: String,
    count: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0xFF0F2C59) else Color.White,
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF0F2C59) else Color(0xFFCBD5E1))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else Color(0xFF334155)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                shape = CircleShape,
                color = if (isSelected) Color(0xFF1E3A8A) else Color(0xFFF1F5F9)
            ) {
                Text(
                    text = count,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else Color(0xFF475569),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun ReceivedApplicantCard(
    applicant: ApplicantData,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Blank Profile Avatar Box (Replaced photo with neutral blank profile icon)
            Surface(
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF1F5F9),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    PersonIcon(
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = applicant.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = applicant.gpa,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = applicant.programAndDate,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(50),
                color = applicant.statusBg
            ) {
                Text(
                    text = applicant.statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = applicant.statusTextCol,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            RightChevronIcon(
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF94A3B8)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmployerReceivedApplicationsPreview() {
    UniversityIntershipAppTheme {
        EmployerReceivedApplications()
    }
}
