package com.example.universityintershipapp

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

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
