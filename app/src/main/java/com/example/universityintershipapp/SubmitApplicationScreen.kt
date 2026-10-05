package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

private val NavyPrimaryContainer = Color(0xFFEFF6FF)
private val OutlineBorder = Color(0xFFE2E8F0)
private val MintSuccessContainer = Color(0xFFCCFBF1)
private val RedAccent = Color(0xFFE11D48)
private val RedAccentContainer = Color(0xFFFFE4E6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitApplicationScreen(
    onCloseClick: () -> Unit = {},
    onSaveDraftClick: () -> Unit = {},
    onSubmitApplicationClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Submit Application",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "UniIntern Portal • Step 2 of 2",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        CloseIcon(modifier = Modifier.size(20.dp), tint = TextPrimary)
                    }
                },
                actions = {
                    Surface(
                        color = NavyPrimaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Draft Saved",
                            color = NavyPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceBackground)
            )
        },
        bottomBar = {
            Surface(
                color = CardBackground,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onSaveDraftClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, OutlineBorder)
                    ) {
                        Text(
                            text = "Save Draft",
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = onSubmitApplicationClick,
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Submit Application",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            RightArrowIcon(
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        },
        containerColor = SurfaceBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Internship Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, OutlineBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MintSuccessContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "INTERNSHIP",
                                color = MintSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Text(
                            text = "Deadline: May 15, 2025",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Software Engineering Intern",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Google LLC • Mountain View, CA (Hybrid)",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // 2. Verified Academic Profile Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, OutlineBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SchoolIcon(
                                modifier = Modifier.size(20.dp),
                                tint = NavyPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Verified Academic Profile",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Surface(
                            color = MintSuccessContainer,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VerifiedUserIcon(
                                    modifier = Modifier.size(13.dp),
                                    tint = MintSuccess
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Registrar Approved",
                                    color = MintSuccess,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Student Name", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "Alex Chen",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Student ID", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "U2021-0842",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1.3f)) {
                            Text(text = "Degree & Major", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "B.Sc. Computer Science (Yr 3)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                        Column(modifier = Modifier.weight(0.7f)) {
                            Text(text = "Cumulative GPA", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "3.84 / 4.00",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                        }
                    }
                }
            }

            // 3. Required Credentials & Attached Documents
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Required Credentials (2 files attached)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // Document 1: Resume
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, OutlineBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(RedAccentContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            DescriptionIcon(
                                modifier = Modifier.size(22.dp),
                                tint = RedAccent
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Resume_Alex_Chen_2025.pdf",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "1.2 MB • Uploaded May 10, 2025",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        CheckCircleIcon(
                            modifier = Modifier.size(20.dp),
                            tint = MintSuccess
                        )
                    }
                }

                // Document 2: Official Transcript
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, OutlineBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            SchoolIcon(
                                modifier = Modifier.size(22.dp),
                                tint = NavyPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Official_Transcript_Verified.pdf",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Verified directly by Registrar System",
                                fontSize = 11.sp,
                                color = MintSuccess,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        CheckCircleIcon(
                            modifier = Modifier.size(20.dp),
                            tint = MintSuccess
                        )
                    }
                }
            }

            // 4. Academic Credit Notice
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimaryContainer.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, OutlineBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    InfoNoticeIcon(
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp),
                        tint = NavyPrimary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "By submitting, your academic profile and credentials will be transmitted directly to Google LLC and logged with your university internship supervisor.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun SchoolIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val capTop = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            lineTo(w * 0.9f, h * 0.38f)
            lineTo(w * 0.5f, h * 0.60f)
            lineTo(w * 0.1f, h * 0.38f)
            close()
        }
        drawPath(capTop, color = tint)

        val capBottom = Path().apply {
            moveTo(w * 0.25f, h * 0.48f)
            lineTo(w * 0.25f, h * 0.68f)
            cubicTo(w * 0.25f, h * 0.82f, w * 0.75f, h * 0.82f, w * 0.75f, h * 0.68f)
            lineTo(w * 0.75f, h * 0.48f)
            close()
        }
        drawPath(capBottom, color = tint)
    }
}

@Composable
fun VerifiedUserIcon(
    modifier: Modifier = Modifier,
    tint: Color = MintSuccess
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
        drawPath(shield, color = tint)

        val check = Path().apply {
            moveTo(w * 0.35f, h * 0.50f)
            lineTo(w * 0.48f, h * 0.62f)
            lineTo(w * 0.68f, h * 0.40f)
        }
        drawPath(check, color = Color.White, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun DescriptionIcon(
    modifier: Modifier = Modifier,
    tint: Color = RedAccent
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val doc = Path().apply {
            moveTo(w * 0.22f, h * 0.15f)
            lineTo(w * 0.78f, h * 0.15f)
            lineTo(w * 0.78f, h * 0.85f)
            lineTo(w * 0.22f, h * 0.85f)
            close()
        }
        drawPath(doc, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.35f), end = Offset(w * 0.65f, h * 0.35f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.50f), end = Offset(w * 0.65f, h * 0.50f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.35f, h * 0.65f), end = Offset(w * 0.52f, h * 0.65f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SubmitApplicationPreview() {
    UniversityIntershipAppTheme {
        SubmitApplicationScreen()
    }
}
