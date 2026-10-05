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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

enum class PortalPathway {
    STUDENT,
    EMPLOYER
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onGetStartedClick: (PortalPathway) -> Unit = {},
    onLogInClick: () -> Unit = {},
) {
    var selectedPathway by remember { mutableStateOf(PortalPathway.STUDENT) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE8F3FA),
            Color(0xFFF7FAFC),
            Color(0xFFEEF4FA),
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. App Logo Icon Header
            AppLogoIcon()

            Spacer(modifier = Modifier.height(16.dp))

            // 2. "UniIntern Nexus" Badge
            Surface(
                shape = CircleShape,
                color = Color(0xFFEEF2FF),
                border = BorderStroke(1.dp, Color(0xFFE0E7FF))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NetworkNodesIcon(
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF1E3A8A)
                    )
                    Text(
                        text = "UniIntern Nexus",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E3A8A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Main Title
            Text(
                text = "University Internship\nManagement System",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2C59),
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Subtitle
            Text(
                text = "Find internships. Gain experience. Build your future.",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 5. Pathway Selection Title
            Text(
                text = "Select your portal pathway",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334155),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 6. Student Pathway Card
            PathwayCard(
                title = "I am a Student",
                tagText = "Student Portal",
                tagBackgroundColor = Color(0xFF99F6E4),
                tagTextColor = Color(0xFF0F766E),
                description = "Discover curated internships, submit applications, track review status",
                isSelected = selectedPathway == PortalPathway.STUDENT,
                onClick = { selectedPathway = PortalPathway.STUDENT },
                icon = {
                    GraduationCapIcon(
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF0F2C59)
                    )
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 7. Employer Pathway Card
            PathwayCard(
                title = "I am an Employer /\nCompany",
                tagText = "Recruiter",
                tagBackgroundColor = Color(0xFFE0E7FF),
                tagTextColor = Color(0xFF4338CA),
                description = "Post internship openings, screen student talent, manage hiring pipeline",
                isSelected = selectedPathway == PortalPathway.EMPLOYER,
                onClick = { selectedPathway = PortalPathway.EMPLOYER },
                icon = {
                    BuildingIcon(
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF0F2C59)
                    )
                }
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(32.dp))

            // 8. "Get Started" Button
            Button(
                onClick = { onGetStartedClick(selectedPathway) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F2C59)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    RightArrowIcon(
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 9. Account Footer Text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Log In",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2C59),
                    modifier = Modifier.clickable { onLogInClick() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun AppLogoIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(96.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(Color(0xFF0F2C59), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(40.dp)) {
                        val w = size.width
                        val h = size.height
                        val capColor = Color(0xFF38BDF8)

                        val capTop = Path().apply {
                            moveTo(w * 0.5f, h * 0.12f)
                            lineTo(w * 0.92f, h * 0.32f)
                            lineTo(w * 0.5f, h * 0.52f)
                            lineTo(w * 0.08f, h * 0.32f)
                            close()
                        }
                        drawPath(capTop, color = capColor)

                        val capBottom = Path().apply {
                            moveTo(w * 0.22f, h * 0.42f)
                            lineTo(w * 0.22f, h * 0.62f)
                            cubicTo(w * 0.22f, h * 0.78f, w * 0.78f, h * 0.78f, w * 0.78f, h * 0.62f)
                            lineTo(w * 0.78f, h * 0.42f)
                            close()
                        }
                        drawPath(capBottom, color = capColor)

                        val tassel = Path().apply {
                            moveTo(w * 0.85f, h * 0.35f)
                            lineTo(w * 0.85f, h * 0.68f)
                        }
                        drawPath(tassel, color = capColor, style = Stroke(width = w * 0.07f, cap = StrokeCap.Round))

                        val checkPath = Path().apply {
                            moveTo(w * 0.38f, h * 0.32f)
                            lineTo(w * 0.47f, h * 0.40f)
                            lineTo(w * 0.62f, h * 0.26f)
                        }
                        drawPath(
                            checkPath,
                            color = Color.White,
                            style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 2.dp, y = 2.dp)
                .size(24.dp)
                .background(Color(0xFF0F2C59), CircleShape)
                .border(2.5.dp, Color.White, CircleShape)
        )
    }
}

@Composable
fun PathwayCard(
    title: String,
    tagText: String,
    tagBackgroundColor: Color,
    tagTextColor: Color,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF0F2C59) else Color(0xFFE2E8F0)
        ),
        shadowElevation = if (isSelected) 3.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = tagBackgroundColor
                    ) {
                        Text(
                            text = tagText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = tagTextColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            PathwayRadioButton(isSelected = isSelected)
        }
    }
}

@Composable
fun PathwayRadioButton(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(22.dp)
            .border(
                width = if (isSelected) 2.dp else 1.5.dp,
                color = if (isSelected) Color(0xFF0F2C59) else Color(0xFFCBD5E1),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFF0F2C59), CircleShape)
            )
        }
    }
}

@Composable
fun GraduationCapIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F2C59)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val capPath = Path().apply {
            moveTo(w * 0.5f, h * 0.18f)
            lineTo(w * 0.9f, h * 0.38f)
            lineTo(w * 0.5f, h * 0.58f)
            lineTo(w * 0.1f, h * 0.38f)
            close()
        }
        drawPath(capPath, color = tint)

        val capBottom = Path().apply {
            moveTo(w * 0.25f, h * 0.48f)
            lineTo(w * 0.25f, h * 0.68f)
            cubicTo(w * 0.25f, h * 0.82f, w * 0.75f, h * 0.82f, w * 0.75f, h * 0.68f)
            lineTo(w * 0.75f, h * 0.48f)
            close()
        }
        drawPath(capBottom, color = tint)

        val tassel = Path().apply {
            moveTo(w * 0.82f, h * 0.42f)
            lineTo(w * 0.82f, h * 0.72f)
        }
        drawPath(
            tassel,
            color = tint,
            style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun BuildingIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F2C59)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val building = Path().apply {
            moveTo(w * 0.2f, h * 0.85f)
            lineTo(w * 0.2f, h * 0.18f)
            lineTo(w * 0.8f, h * 0.18f)
            lineTo(w * 0.8f, h * 0.85f)
            close()
        }
        drawPath(building, color = tint, style = Stroke(width = w * 0.08f))

        val windowW = w * 0.12f
        val windowH = h * 0.12f
        val x1 = w * 0.32f
        val x2 = w * 0.56f
        val y1 = h * 0.30f
        val y2 = h * 0.48f
        val y3 = h * 0.66f

        drawRect(color = tint, topLeft = Offset(x1, y1), size = Size(windowW, windowH))
        drawRect(color = tint, topLeft = Offset(x2, y1), size = Size(windowW, windowH))
        drawRect(color = tint, topLeft = Offset(x1, y2), size = Size(windowW, windowH))
        drawRect(color = tint, topLeft = Offset(x2, y2), size = Size(windowW, windowH))
        drawRect(color = tint, topLeft = Offset(x1, y3), size = Size(windowW, windowH))
        drawRect(color = tint, topLeft = Offset(x2, y3), size = Size(windowW, windowH))
    }
}

@Composable
fun NetworkNodesIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E3A8A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val center = Offset(w * 0.35f, h * 0.5f)
        val topR = Offset(w * 0.75f, h * 0.25f)
        val botR = Offset(w * 0.75f, h * 0.75f)

        drawLine(color = tint, start = center, end = topR, strokeWidth = w * 0.1f, cap = StrokeCap.Round)
        drawLine(color = tint, start = center, end = botR, strokeWidth = w * 0.1f, cap = StrokeCap.Round)

        drawCircle(color = tint, radius = w * 0.14f, center = center)
        drawCircle(color = tint, radius = w * 0.12f, center = topR)
        drawCircle(color = tint, radius = w * 0.12f, center = botR)
    }
}

@Composable
fun RightArrowIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.5f)
            lineTo(w * 0.78f, h * 0.5f)
            moveTo(w * 0.52f, h * 0.25f)
            lineTo(w * 0.8f, h * 0.5f)
            lineTo(w * 0.52f, h * 0.75f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = w * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    UniversityIntershipAppTheme {
        HomeScreen()
    }
}
