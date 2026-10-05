package com.example.universityintershipapp

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

// Color Tokens
val NavyPrimary = Color(0xFF1E3A8A)
val MintSuccess = Color(0xFF2DD4BF)
val SuccessDark = Color(0xFF0D9488)
val SurfaceBackground = Color(0xFFF8F9FF)
val CardBackground = Color(0xFFFFFFFF)
val OutlineVariant = Color(0xFFE2E8F0)
val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Color(0xFF64748B)
val LightBlueContainer = Color(0xFFEFF6FF)

@Composable
fun ApplicationConfirmationScreen(
    onDismissPreview: () -> Unit = {},
    onViewApplications: () -> Unit = {},
) {
    var showDialog by remember { mutableStateOf(value = true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBackground)
    ) {
        // Underlying Application Review Screen (Blurred Background)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .blur(if (showDialog) 12.dp else 0.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismissPreview) {
                    CloseIcon(modifier = Modifier.size(20.dp), tint = TextPrimary)
                }
                Text(
                    text = "Submit Application",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "Step 2 of 2",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Surface(
                        color = LightBlueContainer,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "INTERNSHIP",
                            color = NavyPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Software Engineering Intern",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Google LLC • Mountain View, CA (Hybrid)",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Verified Academic Profile",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Alex Chen • Student ID: U2021-0842",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Bottom Sheet / Modal Dialog Overlay
        if (showDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Success Badge Icon
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MintSuccess.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(SuccessDark),
                                contentAlignment = Alignment.Center
                            ) {
                                CheckIcon(modifier = Modifier.size(24.dp), tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Application Submitted!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your university credential package has been securely dispatched to Google LLC.",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(LightBlueContainer)
                                .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reference ID:",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#APP-2025-0842",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                ContentCopyIcon(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { },
                                    tint = NavyPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceBackground)
                                .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            InfoNoticeIcon(
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(top = 2.dp),
                                tint = SuccessDark
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Faculty supervisor approval has been automatically notified. Track your milestone reviews in My Applications.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 17.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                showDialog = false
                                onViewApplications()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "View My Applications",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                RightArrowIcon(
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = {
                                showDialog = false
                                onDismissPreview()
                            }
                        ) {
                            Text(
                                text = "Dismiss Preview",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CloseIcon(
    modifier: Modifier = Modifier,
    tint: Color = TextPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.12f
        drawLine(color = tint, start = Offset(w * 0.25f, h * 0.25f), end = Offset(w * 0.75f, h * 0.75f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.75f, h * 0.25f), end = Offset(w * 0.25f, h * 0.75f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
fun CheckIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.25f, h * 0.50f)
            lineTo(w * 0.45f, h * 0.68f)
            lineTo(w * 0.75f, h * 0.32f)
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ContentCopyIcon(
    modifier: Modifier = Modifier,
    tint: Color = NavyPrimary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.09f

        drawRect(
            color = tint,
            topLeft = Offset(w * 0.35f, h * 0.15f),
            size = Size(w * 0.50f, h * 0.50f),
            style = Stroke(stroke)
        )
        drawRect(
            color = tint,
            topLeft = Offset(w * 0.15f, h * 0.35f),
            size = Size(w * 0.50f, h * 0.50f),
            style = Stroke(stroke)
        )
    }
}

@Composable
fun InfoNoticeIcon(
    modifier: Modifier = Modifier,
    tint: Color = SuccessDark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(color = tint, radius = w * 0.42f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.08f))
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.32f))
        drawLine(color = tint, start = Offset(w * 0.5f, h * 0.46f), end = Offset(w * 0.5f, h * 0.70f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ApplicationConfirmationPreview() {
    UniversityIntershipAppTheme {
        ApplicationConfirmationScreen()
    }
}
