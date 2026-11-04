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
import androidx.compose.runtime.LaunchedEffect
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

data class InternshipSearchItem(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val workType: String, // "Remote", "On-site"
    val isPaid: Boolean,
    val deadline: String,
    val tags: List<TagData>
)

data class TagData(
    val label: String,
    val backgroundColor: Color,
    val textColor: Color,
    val showClock: Boolean = false
)

@Composable
fun StudentInternshipScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onHomeNavClick: () -> Unit = {},
    onApplicationsNavClick: () -> Unit = {},
    onProfileNavClick: () -> Unit = {},
    onApplyClick: (String, String, String) -> Unit = { _, _, _ -> },
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedBottomNav by remember { mutableIntStateOf(1) }

    // Mutually exclusive single-choice filter state ("Remote", "On-site", or null)
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    var firestoreListings by remember { mutableStateOf<List<InternshipSearchItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        val remoteJobs = InternshipRepository.getInternships()
        if (remoteJobs.isNotEmpty()) {
            firestoreListings = remoteJobs.map { item ->
                val workMode = item.workMode.ifEmpty {
                    if (item.location.contains("Remote", ignoreCase = true)) "Remote"
                    else if (item.location.contains("Hybrid", ignoreCase = true)) "Hybrid"
                    else "On-site"
                }
                InternshipSearchItem(
                    id = item.id,
                    title = item.title,
                    company = item.companyName,
                    location = item.location,
                    workType = workMode,
                    isPaid = true,
                    deadline = item.deadline,
                    tags = listOf(
                        TagData(workMode, Color(0xFFDBEAFE), Color(0xFF1E40AF)),
                        TagData(item.duration, Color(0xFFE0E7FF), Color(0xFF4338CA)),
                        TagData(item.fieldOfStudy, Color(0xFFF1F5F9), Color(0xFF475569))
                    )
                )
            }
        }
    }

    val allListings = firestoreListings

    // Reactive Filtered Results
    val filteredListings = remember(searchQuery, selectedFilter) {
        allListings.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.company.contains(searchQuery, ignoreCase = true) ||
                    item.location.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Remote" -> item.workType.equals("Remote", ignoreCase = true)
                "On-site" -> item.workType.equals("On-site", ignoreCase = true)
                "Hybrid" -> item.workType.equals("Hybrid", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

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
                        text = "My Internships",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
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
                            text = "Search by role, company or skills",
                            color = Color(0xFF475569),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
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

                // 3. Filter Chips Row (Interactive Buttons)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isRemoteSelected = selectedFilter == "Remote"
                    val isOnSiteSelected = selectedFilter == "On-site"
                    val isHybridSelected = selectedFilter == "Hybrid"

                    // Chip 1: Remote
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isRemoteSelected) Color(0xFFBFDBFE) else Color.White,
                        border = BorderStroke(1.dp, if (isRemoteSelected) Color(0xFF1D4ED8) else Color(0xFFCBD5E1)),
                        modifier = Modifier.clickable {
                            selectedFilter = if (isRemoteSelected) null else "Remote"
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Remote",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isRemoteSelected) Color(0xFF1E3A8A) else Color(0xFF334155)
                            )
                        }
                    }

                    // Chip 2: On-site
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isOnSiteSelected) Color(0xFFBFDBFE) else Color.White,
                        border = BorderStroke(1.dp, if (isOnSiteSelected) Color(0xFF1D4ED8) else Color(0xFFCBD5E1)),
                        modifier = Modifier.clickable {
                            selectedFilter = if (isOnSiteSelected) null else "On-site"
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "On-site",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOnSiteSelected) Color(0xFF1E3A8A) else Color(0xFF334155)
                            )
                        }
                    }

                    // Chip 3: Hybrid
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isHybridSelected) Color(0xFFBFDBFE) else Color.White,
                        border = BorderStroke(1.dp, if (isHybridSelected) Color(0xFF1D4ED8) else Color(0xFFCBD5E1)),
                        modifier = Modifier.clickable {
                            selectedFilter = if (isHybridSelected) null else "Hybrid"
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Hybrid",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHybridSelected) Color(0xFF1E3A8A) else Color(0xFF334155)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 5. Filtered Search Results
                if (filteredListings.isEmpty()) {
                    // Empty Filtered State
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No available internships to apply",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                } else {
                    filteredListings.forEachIndexed { index, item ->
                        SearchResultCard(
                            title = item.title,
                            company = item.company,
                            location = item.location,
                            tags = item.tags,
                            deadline = item.deadline,
                            onApplyClick = { onApplyClick(item.title, item.company, item.location) }
                        )

                        if (index < filteredListings.size - 1) {
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }
                }

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
                    icon = { HomeNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 0) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 0
                        onHomeNavClick()
                    }
                )

                BottomNavItem(
                    label = "Internships",
                    isSelected = selectedBottomNav == 1,
                    icon = { BriefcaseIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 1) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
                    onClick = { selectedBottomNav = 1 }
                )

                BottomNavItem(
                    label = "Applications",
                    isSelected = selectedBottomNav == 2,
                    icon = { ApplicationsNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 2) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 2
                        onApplicationsNavClick()
                    }
                )

                BottomNavItem(
                    label = "Profile",
                    isSelected = selectedBottomNav == 3,
                    icon = { ProfileNavIcon(modifier = Modifier.size(20.dp), tint = if (selectedBottomNav == 3) Color(0xFF1E3A8A) else Color(0xFF64748B)) },
                    onClick = {
                        selectedBottomNav = 3
                        onProfileNavClick()
                    }
                )
            }
        }
    }
}

@Composable
fun SearchResultCard(
    title: String,
    company: String,
    location: String,
    tags: List<TagData>,
    deadline: String,
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
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentInternshipScreenPreview() {
    UniversityIntershipAppTheme {
        StudentInternshipScreen()
    }
}
