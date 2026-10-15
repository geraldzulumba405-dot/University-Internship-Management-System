package com.example.universityintershipapp

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
