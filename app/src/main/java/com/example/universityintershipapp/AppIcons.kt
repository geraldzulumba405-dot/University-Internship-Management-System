package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
fun PlusIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F2C59)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.12f

        drawLine(color = tint, start = Offset(w * 0.5f, h * 0.2f), end = Offset(w * 0.5f, h * 0.8f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.2f, h * 0.5f), end = Offset(w * 0.8f, h * 0.5f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
fun BriefcaseIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E3A8A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val rect = Path().apply {
            moveTo(w * 0.15f, h * 0.35f)
            lineTo(w * 0.85f, h * 0.35f)
            lineTo(w * 0.85f, h * 0.82f)
            lineTo(w * 0.15f, h * 0.82f)
            close()
        }
        drawPath(rect, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val handle = Path().apply {
            moveTo(w * 0.35f, h * 0.35f)
            lineTo(w * 0.35f, h * 0.20f)
            lineTo(w * 0.65f, h * 0.20f)
            lineTo(w * 0.65f, h * 0.35f)
        }
        drawPath(handle, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round))
    }
}

@Composable
fun CheckCircleIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E3A8A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(color = tint, radius = w * 0.38f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.08f))
        val check = Path().apply {
            moveTo(w * 0.32f, h * 0.50f)
            lineTo(w * 0.45f, h * 0.62f)
            lineTo(w * 0.68f, h * 0.38f)
        }
        drawPath(check, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun UniInternAppLogoIcon(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Outer Squircle Card Background (Navy Blue)
        val corner = w * 0.22f
        drawRoundRect(
            color = Color(0xFF1B3B80),
            topLeft = Offset.Zero,
            size = Size(w, h),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
        )

        // 2. White Shield / U-Shape Base
        val shieldPath = Path().apply {
            moveTo(w * 0.28f, h * 0.44f)
            lineTo(w * 0.28f, h * 0.62f)
            cubicTo(w * 0.28f, h * 0.82f, w * 0.72f, h * 0.82f, w * 0.72f, h * 0.62f)
            lineTo(w * 0.72f, h * 0.44f)
            close()
        }
        drawPath(shieldPath, color = Color(0xFFF8FAFC))

        // 3. Dark Blue Verified Checkmark
        val checkPath = Path().apply {
            moveTo(w * 0.39f, h * 0.58f)
            lineTo(w * 0.47f, h * 0.66f)
            lineTo(w * 0.61f, h * 0.51f)
        }
        drawPath(
            path = checkPath,
            color = Color(0xFF1B3B80),
            style = Stroke(width = w * 0.075f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // 4. Bright Cyan Graduation Cap Top
        val capTop = Path().apply {
            moveTo(w * 0.50f, h * 0.21f)
            lineTo(w * 0.88f, h * 0.39f)
            lineTo(w * 0.50f, h * 0.57f)
            lineTo(w * 0.12f, h * 0.39f)
            close()
        }
        drawPath(capTop, color = Color(0xFF38BDF8))

        // 5. Bright Cyan Tassel & Ball
        val tasselLine = Path().apply {
            moveTo(w * 0.80f, h * 0.41f)
            lineTo(w * 0.80f, h * 0.62f)
        }
        drawPath(
            path = tasselLine,
            color = Color(0xFF38BDF8),
            style = Stroke(width = w * 0.05f, cap = StrokeCap.Round)
        )

        drawCircle(
            color = Color(0xFF38BDF8),
            radius = w * 0.04f,
            center = Offset(w * 0.80f, h * 0.65f)
        )
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
        drawPath(path, color = tint, style = Stroke(width = w * 0.1f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun EmailIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF3B82F6)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val rect = Path().apply {
            addRect(androidx.compose.ui.geometry.Rect(w * 0.1f, h * 0.25f, w * 0.9f, h * 0.75f))
        }
        drawPath(rect, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val envelope = Path().apply {
            moveTo(w * 0.1f, h * 0.28f)
            lineTo(w * 0.5f, h * 0.55f)
            lineTo(w * 0.9f, h * 0.28f)
        }
        drawPath(envelope, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun IdBadgeIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF3B82F6)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val card = Path().apply {
            addRoundRect(androidx.compose.ui.geometry.RoundRect(w * 0.1f, h * 0.2f, w * 0.9f, h * 0.8f, CornerRadius(w * 0.15f, w * 0.15f)))
        }
        drawPath(card, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(color = tint, radius = w * 0.15f, center = Offset(w * 0.35f, h * 0.45f))
        drawLine(color = tint, start = Offset(w * 0.55f, h * 0.40f), end = Offset(w * 0.75f, h * 0.40f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.55f, h * 0.58f), end = Offset(w * 0.75f, h * 0.58f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
    }
}

@Composable
fun PhoneIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFF64748B)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.08f
        val phone = Path().apply {
            moveTo(w * 0.32f, h * 0.18f)
            lineTo(w * 0.68f, h * 0.18f)
            lineTo(w * 0.68f, h * 0.82f)
            lineTo(w * 0.32f, h * 0.82f)
            close()
        }
        drawPath(phone, color = tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(color = tint, radius = w * 0.04f, center = Offset(w * 0.50f, h * 0.74f))
        drawLine(color = tint, start = Offset(w * 0.42f, h * 0.25f), end = Offset(w * 0.58f, h * 0.25f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
fun IdCardIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFF64748B)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.08f
        drawRect(color = tint, topLeft = Offset(w * 0.12f, h * 0.22f), size = Size(w * 0.76f, h * 0.56f), style = Stroke(stroke))
        drawRect(color = tint, topLeft = Offset(w * 0.22f, h * 0.35f), size = Size(w * 0.22f, h * 0.30f), style = Stroke(stroke))
        drawLine(color = tint, start = Offset(w * 0.52f, h * 0.40f), end = Offset(w * 0.76f, h * 0.40f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.52f, h * 0.58f), end = Offset(w * 0.72f, h * 0.58f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}



@Composable
fun LogoutIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF1E3A8A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val stroke = w * 0.1f
        drawRect(
            color = tint,
            topLeft = Offset(w * 0.1f, h * 0.15f),
            size = Size(w * 0.5f, h * 0.7f),
            style = Stroke(stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.45f, h * 0.5f),
            end = Offset(w * 0.9f, h * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        val arrowHead = Path().apply {
            moveTo(w * 0.7f, h * 0.3f)
            lineTo(w * 0.9f, h * 0.5f)
            lineTo(w * 0.7f, h * 0.7f)
        }
        drawPath(arrowHead, color = tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
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
            Surface(
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.TopEnd),
                shape = CircleShape,
                color = Color(0xFFDC2626),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
