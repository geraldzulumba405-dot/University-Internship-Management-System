package com.example.universityintershipapp

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onBackClick: () -> Unit = {},
    onLoginSubmit: (String, String) -> Unit = { _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(value = false) }
    var rememberUsername by remember { mutableStateOf(value = false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // 1. Top Bar Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(36.dp))

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF1B3B80), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val w = size.width
                        val h = size.height

                        val capTop = Path().apply {
                            moveTo(w * 0.5f, h * 0.15f)
                            lineTo(w * 0.9f, h * 0.38f)
                            lineTo(w * 0.5f, h * 0.60f)
                            lineTo(w * 0.1f, h * 0.38f)
                            close()
                        }
                        drawPath(capTop, color = Color.White)

                        val capBottom = Path().apply {
                            moveTo(w * 0.25f, h * 0.48f)
                            lineTo(w * 0.25f, h * 0.68f)
                            cubicTo(w * 0.25f, h * 0.82f, w * 0.75f, h * 0.82f, w * 0.75f, h * 0.68f)
                            lineTo(w * 0.75f, h * 0.48f)
                            close()
                        }
                        drawPath(capBottom, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "UniIntern",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )
                    Text(
                        text = "PORTAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E),
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onHelpClick() },
                contentAlignment = Alignment.Center
            ) {
                HelpIcon(
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF334155)
                )
            }
        }

        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

        // 2. Main Scrollable Form Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Text(
                text = "Log in with your university credentials or employer email",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Username Field
            Text(
                text = "Email",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                ),
                placeholder = {
                    Text(
                        text = "Enter student or employer email",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                },
                leadingIcon = {
                    PersonIcon(
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF0F2C59)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF0F172A),
                    unfocusedTextColor = Color(0xFF0F172A),
                    cursorColor = Color(0xFF0F2C59),
                    focusedBorderColor = Color(0xFF0F2C59),
                    unfocusedBorderColor = Color(0xFF475569),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                InfoIcon(
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF0F2C59)
                )
                Text(
                    text = "Use your institution-issued email or work email",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Password Field
            Text(
                text = "Password",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                ),
                placeholder = {
                    Text(
                        text = "Enter your password",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                },
                leadingIcon = {
                    LockIcon(
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF0F2C59)
                    )
                },
                trailingIcon = {
                    Box(
                        modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible },
                        contentAlignment = Alignment.Center
                    ) {
                        EyeIcon(
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF0F2C59)
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF0F172A),
                    unfocusedTextColor = Color(0xFF0F172A),
                    cursorColor = Color(0xFF0F2C59),
                    focusedBorderColor = Color(0xFF0F2C59),
                    unfocusedBorderColor = Color(0xFF475569),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Remember username & Forgot password
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberUsername = !rememberUsername }
                ) {
                    Checkbox(
                        checked = rememberUsername,
                        onCheckedChange = { rememberUsername = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF1B3B80),
                            uncheckedColor = Color(0xFF475569)
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Remember username",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Text(
                    text = "Forgot Password?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B3B80),
                    modifier = Modifier.clickable { onForgotPasswordClick() }
                )
            }

            if (!errorMessage.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFDC2626)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Log In Button
            Button(
                onClick = { onLoginSubmit(username, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B3B80)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Log In",
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

            Spacer(modifier = Modifier.height(24.dp))

            // Don't have an account? Sign Up
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Sign Up",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B3B80),
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }
    }
}

@Composable
fun BackArrowIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF0F172A)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.8f, h * 0.5f)
            lineTo(w * 0.2f, h * 0.5f)
            moveTo(w * 0.45f, h * 0.25f)
            lineTo(w * 0.18f, h * 0.5f)
            lineTo(w * 0.45f, h * 0.75f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun HelpIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF334155)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(
            color = tint,
            radius = w * 0.42f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = w * 0.08f)
        )

        val qPath = Path().apply {
            moveTo(w * 0.38f, h * 0.38f)
            cubicTo(w * 0.38f, h * 0.28f, w * 0.62f, h * 0.28f, w * 0.62f, h * 0.38f)
            cubicTo(w * 0.62f, h * 0.48f, w * 0.50f, h * 0.45f, w * 0.50f, h * 0.56f)
        }
        drawPath(
            path = qPath,
            color = tint,
            style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
        )
        drawCircle(
            color = tint,
            radius = w * 0.05f,
            center = Offset(w * 0.50f, h * 0.68f)
        )
    }
}

@Composable
fun PersonIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(
            color = tint,
            radius = w * 0.22f,
            center = Offset(w * 0.5f, h * 0.32f),
            style = Stroke(width = w * 0.08f)
        )

        val bodyPath = Path().apply {
            moveTo(w * 0.2f, h * 0.82f)
            cubicTo(w * 0.2f, h * 0.60f, w * 0.8f, h * 0.60f, w * 0.8f, h * 0.82f)
        }
        drawPath(
            path = bodyPath,
            color = tint,
            style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun InfoIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(
            color = tint,
            radius = w * 0.42f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = w * 0.08f)
        )

        drawCircle(
            color = tint,
            radius = w * 0.05f,
            center = Offset(w * 0.5f, h * 0.32f)
        )

        drawLine(
            color = tint,
            start = Offset(w * 0.5f, h * 0.46f),
            end = Offset(w * 0.5f, h * 0.70f),
            strokeWidth = w * 0.08f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun LockIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val shacklePath = Path().apply {
            moveTo(w * 0.32f, h * 0.48f)
            lineTo(w * 0.32f, h * 0.32f)
            cubicTo(w * 0.32f, h * 0.15f, w * 0.68f, h * 0.15f, w * 0.68f, h * 0.32f)
            lineTo(w * 0.68f, h * 0.48f)
        }
        drawPath(
            path = shacklePath,
            color = tint,
            style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
        )

        val bodyRect = Path().apply {
            moveTo(w * 0.22f, h * 0.48f)
            lineTo(w * 0.78f, h * 0.48f)
            lineTo(w * 0.78f, h * 0.85f)
            lineTo(w * 0.22f, h * 0.85f)
            close()
        }
        drawPath(
            path = bodyRect,
            color = tint,
            style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        drawCircle(
            color = tint,
            radius = w * 0.06f,
            center = Offset(w * 0.5f, h * 0.64f)
        )
    }
}

@Composable
fun EyeIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val eyePath = Path().apply {
            moveTo(w * 0.12f, h * 0.5f)
            cubicTo(w * 0.32f, h * 0.22f, w * 0.68f, h * 0.22f, w * 0.88f, h * 0.5f)
            cubicTo(w * 0.68f, h * 0.78f, w * 0.32f, h * 0.78f, w * 0.12f, h * 0.5f)
            close()
        }
        drawPath(
            path = eyePath,
            color = tint,
            style = Stroke(width = w * 0.08f)
        )

        drawCircle(
            color = tint,
            radius = w * 0.14f,
            center = Offset(w * 0.5f, h * 0.5f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    UniversityIntershipAppTheme {
        LoginScreen()
    }
}
