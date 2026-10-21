package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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

// Additional Color Tokens for Applications Screen
private val NavyDark = Color(0xFF0F1E4A)
private val LightNavyContainer = Color(0xFFEFF4FF)
private val TealPrimary = Color(0xFF0F766E)
private val RedAlert = Color(0xFFDC2626)
private val RedAlertLight = Color(0xFFFEF2F2)
private val RedAlertBorder = Color(0xFFFECACA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplicationsScreen(
    modifier: Modifier = Modifier,
    onNavigateHome: () -> Unit = {},
    onNavigateSearch: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onViewDetails: (String) -> Unit = {},
    onPrepInterview: (String) -> Unit = {}
) {
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filterTabs = listOf("All 4", "Under Review 2", "Interview 1", "Offered 1")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top App Bar Header
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

                    Column {
                        Text(
                            text = "My Applications",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "Semester 2 • 2024/2025",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TealPrimary
                        )
                    }
                }

                Box(modifier = Modifier.padding(end = 4.dp)) {
                    IconButton(onClick = {}) {
                        BellIcon(modifier = Modifier.size(24.dp), tint = NavyPrimary)
                    }
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(RedAlert)
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
                    )
                }
            }

            HorizontalDivider(color = OutlineVariant, thickness = 1.dp)

            // Scrollable Content Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Metric Stat Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Applied Metric
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Applied",
                        count = "4",
                        subtext = "roles",
                        icon = { SendIcon(modifier = Modifier.size(14.dp), tint = NavyPrimary) },
                        iconColor = NavyPrimary,
                        borderColor = OutlineVariant,
                        bgColor = CardBackground,
                        onClick = { selectedFilterIndex = 0 }
                    )

                    // Active Metric
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Active",
                        count = "3",
                        subtext = "reviewing",
                        icon = { CalendarIcon(modifier = Modifier.size(14.dp), tint = TealPrimary) },
                        iconColor = TealPrimary,
                        borderColor = OutlineVariant,
                        bgColor = CardBackground,
                        onClick = { selectedFilterIndex = 1 }
                    )

                    // Action Urgent Metric
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Action",
                        count = "1",
                        subtext = "urgent",
                        icon = { WarningIcon(modifier = Modifier.size(14.dp), tint = RedAlert) },
                        iconColor = RedAlert,
                        borderColor = RedAlertBorder,
                        bgColor = RedAlertLight,
                        onClick = { selectedFilterIndex = 2 }
                    )
                }

                // 2. Horizontal Filter Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterTabs.forEachIndexed { index, title ->
                        val isSelected = selectedFilterIndex == index
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilterIndex = index },
                            label = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyDark,
                                containerColor = CardBackground
                            ),
                            border = BorderStroke(1.dp, if (isSelected) NavyDark else OutlineVariant)
                        )
                    }
                }

                // --- DYNAMICALLY FILTERED APPLICATION CARDS ---

                // CARD 1: Interview Stage (Software Engineering Intern) -> Show when All (0) or Interview (2) selected
                if (selectedFilterIndex == 0 || selectedFilterIndex == 2) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(LightNavyContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CodeIcon(modifier = Modifier.size(24.dp), tint = NavyPrimary)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Software Engineering Int...",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Google LLC • Mountain View, CA\n(Remote Hybrid)",
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Surface(
                                    color = NavyDark,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "• STAGE 3 OF 4",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interview Scheduled Box
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = LightNavyContainer.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, OutlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CalendarIcon(modifier = Modifier.size(18.dp), tint = TealPrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Interview Scheduled",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Date: June 14, 2025 at 2:00 PM CAT (Google Meet)",
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Multi-step lifecycle status bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StepIndicator(text = "Applied", isDone = true)
                                StepIndicator(text = "Review", isDone = true)
                                Surface(
                                    color = LightNavyContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Interview",
                                        color = NavyPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                StepIndicator(text = "Decision", isDone = false)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Segmented color bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            ) {
                                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(TealPrimary))
                                Spacer(modifier = Modifier.width(2.dp))
                                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(TealPrimary))
                                Spacer(modifier = Modifier.width(2.dp))
                                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(NavyDark))
                                Spacer(modifier = Modifier.width(2.dp))
                                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(LightNavyContainer))
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Placement Approval Badge
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, OutlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        ShieldIcon(modifier = Modifier.size(16.dp), tint = TealPrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Prof. Banda approved placement",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TealPrimary
                                        )
                                    }
                                    Text(
                                        text = "App ID: #UNI-8842",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Dual Actions: View Details & Prep Interview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onViewDetails("UNI-8842") },
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .height(46.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, OutlineVariant)
                                ) {
                                    VisibilityIcon(modifier = Modifier.size(16.dp), tint = NavyPrimary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "View Details",
                                        fontSize = 13.sp,
                                        color = NavyPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Button(
                                    onClick = { onPrepInterview("UNI-8842") },
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .height(46.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                                ) {
                                    VideocamIcon(modifier = Modifier.size(18.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Prep Interview",
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // CARD 2: Under Review 1 (Data Science & AI Intern) -> Show when All (0) or Under Review (1) selected
                if (selectedFilterIndex == 0 || selectedFilterIndex == 1) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(LightNavyContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        BarChartIcon(modifier = Modifier.size(24.dp), tint = TealPrimary)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Data Science & AI Intern",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "BD Tech Consulting • Zomba",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                                Surface(
                                    color = LightNavyContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Under Review",
                                        color = NavyPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Candidate Screening (50%)",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "HR Assessment",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NavyPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { 0.5f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = TealPrimary,
                                trackColor = LightNavyContainer
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp)

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🕒 Sent: May 10, 2025",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onViewDetails("DS-AI-01") }
                                ) {
                                    Text(
                                        text = "Review Details",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    RightArrowIcon(modifier = Modifier.size(13.dp), tint = NavyPrimary)
                                }
                            }
                        }
                    }
                }

                // CARD 3: Under Review 2 (Frontend UI/UX Intern) -> Show when All (0) or Under Review (1) selected
                if (selectedFilterIndex == 0 || selectedFilterIndex == 1) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(LightNavyContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CodeIcon(modifier = Modifier.size(24.dp), tint = TealPrimary)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Frontend UI/UX Design Intern",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "EcoNet Wireless • Lilongwe",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                                Surface(
                                    color = LightNavyContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Under Review",
                                        color = NavyPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Portfolio Review (25%)",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Application Received",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NavyPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { 0.25f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = TealPrimary,
                                trackColor = LightNavyContainer
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp)

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🕒 Sent: May 14, 2025",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onViewDetails("UI-UX-02") }
                                ) {
                                    Text(
                                        text = "Review Details",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    RightArrowIcon(modifier = Modifier.size(13.dp), tint = NavyPrimary)
                                }
                            }
                        }
                    }
                }

                // CARD 4: Offered 1 (Cloud & DevOps Intern) -> Show when All (0) or Offered (3) selected
                if (selectedFilterIndex == 0 || selectedFilterIndex == 3) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFD1FAE5)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        SendIcon(modifier = Modifier.size(24.dp), tint = Color(0xFF047857))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Cloud & DevOps Intern",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "MalSwitch Systems • Blantyre",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                                Surface(
                                    color = Color(0xFFD1FAE5),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Offered",
                                        color = Color(0xFF047857),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Offer Box
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFECFDF5),
                                border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        ShieldIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF047857))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Official Offer Received 🎉",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF047857)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Placement terms approved by HR. Please accept before June 1, 2025.",
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Actions: Accept Offer & View Terms
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onViewDetails("DEV-OPS-03") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, OutlineVariant)
                                ) {
                                    Text(
                                        text = "View Terms",
                                        fontSize = 13.sp,
                                        color = NavyPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Button(
                                    onClick = { onViewDetails("DEV-OPS-03") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857))
                                ) {
                                    Text(
                                        text = "Accept Offer",
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. Academic Placement Help Footer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Need help with academic credits or approval?",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Contact Career Placement Office",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        modifier = Modifier.clickable { }
                    )
                }
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
                    isSelected = true,
                    icon = { ApplicationsNavIcon(modifier = Modifier.size(20.dp), tint = TealPrimary) },
                    onClick = { }
                )

                BottomNavItem(
                    label = "Profile",
                    isSelected = false,
                    icon = { ProfileNavIcon(modifier = Modifier.size(20.dp), tint = TextSecondary) },
                    onClick = onNavigateProfile
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    label: String,
    count: String,
    subtext: String,
    icon: @Composable () -> Unit,
    iconColor: Color,
    borderColor: Color,
    bgColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, fontSize = 11.sp, color = TextSecondary)
                icon()
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = count,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (iconColor == RedAlert) RedAlert else NavyPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = subtext,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(text: String, isDone: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isDone) {
            CheckIcon(modifier = Modifier.size(14.dp), tint = TealPrimary)
            Spacer(modifier = Modifier.width(3.dp))
        }
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDone) TealPrimary else TextSecondary
        )
    }
}

@Composable
fun MenuIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f

        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.28f), end = Offset(w * 0.85f, h * 0.28f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.50f), end = Offset(w * 0.85f, h * 0.50f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.15f, h * 0.72f), end = Offset(w * 0.85f, h * 0.72f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
fun SendIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyPrimary
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
        drawPath(plane, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun WarningIcon(
    modifier: Modifier = Modifier,
    tint: Color = RedAlert
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val tri = Path().apply {
            moveTo(w * 0.50f, h * 0.15f)
            lineTo(w * 0.88f, h * 0.82f)
            lineTo(w * 0.12f, h * 0.82f)
            close()
        }
        drawPath(tri, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = tint, start = Offset(w * 0.5f, h * 0.38f), end = Offset(w * 0.5f, h * 0.58f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.70f))
    }
}

@Composable
fun CodeIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val left = Path().apply {
            moveTo(w * 0.38f, h * 0.28f)
            lineTo(w * 0.18f, h * 0.50f)
            lineTo(w * 0.38f, h * 0.72f)
        }
        drawPath(left, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val right = Path().apply {
            moveTo(w * 0.62f, h * 0.28f)
            lineTo(w * 0.82f, h * 0.50f)
            lineTo(w * 0.62f, h * 0.72f)
        }
        drawPath(right, color = tint, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun VisibilityIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val eye = Path().apply {
            moveTo(w * 0.12f, h * 0.5f)
            cubicTo(w * 0.32f, h * 0.22f, w * 0.68f, h * 0.22f, w * 0.88f, h * 0.5f)
            cubicTo(w * 0.68f, h * 0.78f, w * 0.32f, h * 0.78f, w * 0.12f, h * 0.5f)
            close()
        }
        drawPath(eye, color = tint, style = Stroke(width = w * 0.08f))

        drawCircle(color = tint, radius = w * 0.14f, center = Offset(w * 0.5f, h * 0.5f))
    }
}

@Composable
fun VideocamIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val body = Path().apply {
            moveTo(w * 0.15f, h * 0.28f)
            lineTo(w * 0.62f, h * 0.28f)
            lineTo(w * 0.62f, h * 0.72f)
            lineTo(w * 0.15f, h * 0.72f)
            close()
        }
        drawPath(body, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val lens = Path().apply {
            moveTo(w * 0.62f, h * 0.42f)
            lineTo(w * 0.85f, h * 0.28f)
            lineTo(w * 0.85f, h * 0.72f)
            lineTo(w * 0.62f, h * 0.58f)
            close()
        }
        drawPath(lens, color = tint)
    }
}

@Composable
fun BarChartIcon(
    modifier: Modifier = Modifier,
    tint: Color = TealPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.12f

        drawLine(color = tint, start = Offset(w * 0.25f, h * 0.50f), end = Offset(w * 0.25f, h * 0.80f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.50f, h * 0.25f), end = Offset(w * 0.50f, h * 0.80f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.75f, h * 0.38f), end = Offset(w * 0.75f, h * 0.80f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyApplicationsPreview() {
    UniversityIntershipAppTheme {
        MyApplicationsScreen()
    }
}
