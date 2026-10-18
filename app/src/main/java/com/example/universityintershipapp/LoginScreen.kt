package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
    onBackClick: () -> Unit = {},
    onLoginSubmit: (String, String) -> Unit = { _, _ -> },
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(value = false) }
    var rememberUsername by remember { mutableStateOf(value = false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // 2. Centered Logo and App Name
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UniInternAppLogo(modifier = Modifier.size(120.dp))

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "UniIntern",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2C59)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

        // 3. Main Scrollable Form Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Log in to your account to continue",
                fontSize = 13.sp,
                color = Color(0xFF171717),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Error Banner if invalid credentials or empty fields
            errorMessage?.let { errorText ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFECACA))
                ) {
                    Text(
                        text = errorText,
                        fontSize = 13.sp,
                        color = Color(0xFFB91C1C),
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Email Field
            Text(
                text = "Email",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF000000)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    errorMessage = null
                },
                placeholder = {
                    Text(
                        text = "Enter your username or email",
                        color = Color(0xFF525252),
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    PersonIcon(
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF525252)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0F2C59),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            Text(
                text = "Password",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF000000)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                placeholder = {
                    Text(
                        text = "Enter your password",
                        color = Color(0xFF525252),
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    LockIcon(
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF525252)
                    )
                },
                trailingIcon = {
                    Box(
                        modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible },
                        contentAlignment = Alignment.Center
                    ) {
                        EyeIcon(
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0F2C59),
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
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Remember username",
                        fontSize = 13.sp,
                        color = Color(0xFF171717)
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

            Spacer(modifier = Modifier.height(28.dp))

            // Log In Button
            Button(
                onClick = {
                    val cleanEmail = username.trim().lowercase()
                    when {
                        cleanEmail.isBlank() -> {
                            errorMessage = "Please enter your email address."
                        }
                        password.isBlank() -> {
                            errorMessage = "Please enter your password."
                        }
                        cleanEmail == "student@unima.ac.mw" || cleanEmail == "employer@company.com" -> {
                            errorMessage = null
                            onLoginSubmit(cleanEmail, password)
                        }
                        else -> {
                            errorMessage = "Unrecognized email or invalid test credentials.\nPlease use student@unima.ac.mw or employer@company.com"
                        }
                    }
                },
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

            // Register Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 13.sp,
                    color = Color(0xFF171717)
                )
                Text(
                    text = "Sign Up",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2C59),
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }
        }
    }
}

@Composable
fun UniInternAppLogo(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = Color(0xFF1B3B80)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(82.dp)) {
                val w = size.width
                val h = size.height
                val capBlue = Color(0xFF38BDF8)
                val whiteBg = Color.White
                val darkNavy = Color(0xFF1B3B80)

                // White U-shaped cap base
                val capBase = Path().apply {
                    moveTo(w * 0.22f, h * 0.42f)
                    lineTo(w * 0.22f, h * 0.60f)
                    cubicTo(w * 0.22f, h * 0.86f, w * 0.78f, h * 0.86f, w * 0.78f, h * 0.60f)
                    lineTo(w * 0.78f, h * 0.42f)
                    close()
                }
                drawPath(capBase, color = whiteBg)

                // Checkmark inside white base
                val checkPath = Path().apply {
                    moveTo(w * 0.38f, h * 0.58f)
                    lineTo(w * 0.48f, h * 0.68f)
                    lineTo(w * 0.64f, h * 0.50f)
                }
                drawPath(
                    path = checkPath,
                    color = darkNavy,
                    style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Diamond Cap Top
                val capTop = Path().apply {
                    moveTo(w * 0.50f, h * 0.20f)
                    lineTo(w * 0.92f, h * 0.40f)
                    lineTo(w * 0.50f, h * 0.60f)
                    lineTo(w * 0.08f, h * 0.40f)
                    close()
                }
                drawPath(capTop, color = capBlue)

                // Tassel hanging on the right
                val tasselLine = Path().apply {
                    moveTo(w * 0.82f, h * 0.42f)
                    lineTo(w * 0.82f, h * 0.66f)
                }
                drawPath(
                    path = tasselLine,
                    color = capBlue,
                    style = Stroke(width = w * 0.07f, cap = StrokeCap.Round)
                )
                drawCircle(
                    color = capBlue,
                    radius = w * 0.05f,
                    center = Offset(w * 0.82f, h * 0.68f)
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
