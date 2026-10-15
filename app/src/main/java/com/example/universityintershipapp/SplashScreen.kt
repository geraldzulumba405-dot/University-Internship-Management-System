package com.example.universityintershipapp

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
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
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onSplashFinished: () -> Unit = {}
) {
    val scaleAnim = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
        delay(1200) // Total duration: 2000 ms before navigating to LoginScreen
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1B3B80))
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scaleAnim.value)
        ) {
            // App Logo Icon
            AppSplashLogoIcon(modifier = Modifier.size(140.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // App Name & Subtitle
            Text(
                text = "UniIntern",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = Color(0xFF29B6F6),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "PORTAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B3B80),
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "University Internship Management System",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF93C5FD)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Minimal Progress Indicator
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color(0xFF29B6F6),
                strokeWidth = 2.5.dp
            )
        }
    }
}

@Composable
fun AppSplashLogoIcon(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Outer Squircle Card Background (Navy Blue)
        val corner = w * 0.22f
        drawRoundRect(
            color = Color(0xFF1E3A8A),
            topLeft = Offset.Zero,
            size = Size(w, h),
            cornerRadius = CornerRadius(corner, corner)
        )

        // 2. White Shield / U-Shape Base
        val shieldPath = Path().apply {
            moveTo(w * 0.27f, h * 0.44f)
            lineTo(w * 0.27f, h * 0.62f)
            cubicTo(w * 0.27f, h * 0.82f, w * 0.73f, h * 0.82f, w * 0.73f, h * 0.62f)
            lineTo(w * 0.73f, h * 0.44f)
            lineTo(w * 0.50f, h * 0.53f)
            close()
        }
        drawPath(shieldPath, color = Color(0xFFF8FAFC))

        // 3. Dark Blue Verified Checkmark inside Shield
        val checkPath = Path().apply {
            moveTo(w * 0.40f, h * 0.59f)
            lineTo(w * 0.47f, h * 0.66f)
            lineTo(w * 0.59f, h * 0.53f)
        }
        drawPath(
            path = checkPath,
            color = Color(0xFF1E3A8A),
            style = Stroke(width = w * 0.07f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // 4. Bright Cyan Graduation Cap Top
        val capTop = Path().apply {
            moveTo(w * 0.50f, h * 0.21f)
            lineTo(w * 0.80f, h * 0.35f)
            lineTo(w * 0.50f, h * 0.52f)
            lineTo(w * 0.20f, h * 0.35f)
            close()
        }
        drawPath(capTop, color = Color(0xFF29B6F6))

        // 5. Bright Cyan Right Tassel Line & Ball
        val tasselLine = Path().apply {
            moveTo(w * 0.79f, h * 0.36f)
            lineTo(w * 0.79f, h * 0.59f)
        }
        drawPath(
            path = tasselLine,
            color = Color(0xFF29B6F6),
            style = Stroke(width = w * 0.035f, cap = StrokeCap.Round)
        )

        drawCircle(
            color = Color(0xFF29B6F6),
            radius = w * 0.035f,
            center = Offset(w * 0.79f, h * 0.62f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    UniversityIntershipAppTheme {
        SplashScreen()
    }
}
