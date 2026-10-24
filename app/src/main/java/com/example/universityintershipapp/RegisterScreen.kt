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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onCreateAccountSuccess: () -> Unit = {},
    onCreateAccountSuccessWithRole: (String) -> Unit = {},
) {
    var accountType by remember { mutableStateOf("student") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Student Form Fields
    var fullName by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var programme by remember { mutableStateOf("") }
    var yearOfStudy by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(value = false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(value = false) }
    var programmeDropdownExpanded by remember { mutableStateOf(value = false) }
    var yearDropdownExpanded by remember { mutableStateOf(value = false) }

    // Employer Form Fields
    var employerContactName by remember { mutableStateOf("") }
    var employerCompanyName by remember { mutableStateOf("") }
    var employerEmail by remember { mutableStateOf("") }
    var employerPhone by remember { mutableStateOf("") }
    var employerSector by remember { mutableStateOf("") }
    var employerLocation by remember { mutableStateOf("") }
    var employerWebsite by remember { mutableStateOf("") }
    var employerDescription by remember { mutableStateOf("") }
    var employerPassword by remember { mutableStateOf("") }
    var employerConfirmPassword by remember { mutableStateOf("") }

    var isEmployerPasswordVisible by remember { mutableStateOf(value = false) }
    var isEmployerConfirmPasswordVisible by remember { mutableStateOf(value = false) }
    var employerSectorDropdownExpanded by remember { mutableStateOf(value = false) }

    // Validation State
    var showErrors by remember { mutableStateOf(value = false) }
    var validationErrorMsg by remember { mutableStateOf("") }

    val programmeOptions = listOf(
        "B.Sc. Computer Science",
        "B.Sc. Software Engineering",
        "B.Sc. Information Technology",
        "B.Sc. Business Administration",
        "B.Sc. Data Science & Analytics",
        "B.Sc. Electrical Engineering",
    )

    val yearOptions = listOf(
        "Year 1",
        "Year 2",
        "Year 3",
        "Year 4"
    )

    val sectorOptions = listOf(
        "Information Technology",
        "Software Development",
        "Telecommunications",
        "Banking and Finance",
        "Education",
        "Healthcare",
        "Agriculture",
        "Engineering and Construction",
        "Manufacturing",
        "Retail and Commerce",
        "Government and Public Sector",
        "Non-Governmental Organization (NGO)",
        "Hospitality and Tourism",
        "Other"
    )

    fun validateStudentForm(): Boolean {
        return when (val res = ValidationUtils.validateStudentRegistrationInput(
            fullName, studentId, email, phoneNumber, programme, yearOfStudy, password, confirmPassword
        )) {
            is ValidationResult.Error -> {
                validationErrorMsg = res.message
                false
            }
            is ValidationResult.Success -> {
                validationErrorMsg = ""
                true
            }
        }
    }

    fun validateEmployerForm(): Boolean {
        return when (val res = ValidationUtils.validateEmployerRegistrationInput(
            employerContactName, employerCompanyName, employerEmail, employerPhone, employerSector, employerLocation, employerPassword, employerConfirmPassword
        )) {
            is ValidationResult.Error -> {
                validationErrorMsg = res.message
                false
            }
            is ValidationResult.Success -> {
                validationErrorMsg = ""
                true
            }
        }
    }

    fun validateAndSubmit() {
        showErrors = true
        val isValid = if (accountType == "student") validateStudentForm() else validateEmployerForm()
        if (isValid && !isLoading) {
            isLoading = true
            scope.launch {
                val res = if (accountType == "student") {
                    AuthRepository.registerStudent(
                        fullName = fullName,
                        studentId = studentId,
                        email = email,
                        phoneNumber = phoneNumber,
                        programme = programme,
                        yearOfStudy = yearOfStudy,
                        skills = skills,
                        password = password
                    )
                } else {
                    AuthRepository.registerEmployer(
                        contactName = employerContactName,
                        companyName = employerCompanyName,
                        email = employerEmail,
                        phoneNumber = employerPhone,
                        sector = employerSector,
                        location = employerLocation,
                        website = employerWebsite,
                        description = employerDescription,
                        password = employerPassword
                    )
                }
                isLoading = false
                when (res) {
                    is AuthResult.Success -> {
                        validationErrorMsg = ""
                        onCreateAccountSuccessWithRole(res.role)
                        onCreateAccountSuccess()
                    }
                    is AuthResult.Error -> {
                        validationErrorMsg = res.message
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    BackArrowIcon(
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Create your account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000)
                    )
                    Text(
                        text = "University Internship Management System",
                        fontSize = 12.sp,
                        color = Color(0xFF404040)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // 2. Scrollable Form Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // Account Type Selection Header
                Text(
                    text = "Register as",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Account Type Radio Selectable Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Option 1: Student
                    AccountTypeCard(
                        modifier = Modifier.weight(1f),
                        title = "Student",
                        isSelected = accountType == "student",
                        onClick = {
                            accountType = "student"
                            showErrors = false
                            validationErrorMsg = ""
                        }
                    )

                    // Option 2: Employer
                    AccountTypeCard(
                        modifier = Modifier.weight(1f),
                        title = "Employer",
                        isSelected = accountType == "employer",
                        onClick = {
                            accountType = "employer"
                            showErrors = false
                            validationErrorMsg = ""
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (accountType == "employer") {
                    // Employer Details Section Heading
                    Text(
                        text = "Employer details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // A. Contact person's full name *
                    FormFieldLabel(label = "Contact person's full name *")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = employerContactName,
                        onValueChange = { employerContactName = it },
                        placeholder = { Text("Enter the contact person's full name", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { PersonIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        isError = showErrors && employerContactName.isBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // B. Company or organization name *
                    FormFieldLabel(label = "Company or organization name *")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = employerCompanyName,
                        onValueChange = { employerCompanyName = it },
                        placeholder = { Text("Enter your company or organization name", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { BuildingIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        isError = showErrors && employerCompanyName.isBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // C. Organization email address *
                    FormFieldLabel(label = "Organization email address *")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = employerEmail,
                        onValueChange = { employerEmail = it },
                        placeholder = { Text("e.g. careers@company.com", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { EmailIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = showErrors && (employerEmail.isBlank() || !employerEmail.contains("@")),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // D. Phone number *
                    FormFieldLabel(label = "Phone number *")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = employerPhone,
                        onValueChange = { employerPhone = it },
                        placeholder = { Text("e.g. +265 999 123 456", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { PhoneIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = showErrors && employerPhone.isBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // E. Industry or sector * (Dropdown)
                    FormFieldLabel(label = "Industry or sector *")
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = employerSector,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Select sector", color = Color(0xFF525252), fontSize = 14.sp) },
                            leadingIcon = { CategoryIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                            trailingIcon = {
                                Box(
                                    modifier = Modifier.clickable { employerSectorDropdownExpanded = !employerSectorDropdownExpanded },
                                    contentAlignment = Alignment.Center
                                ) {
                                    DownChevronIcon(modifier = Modifier.size(16.dp), tint = Color(0xFF64748B))
                                }
                            },
                            isError = showErrors && employerSector.isBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { employerSectorDropdownExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors()
                        )

                        DropdownMenu(
                            expanded = employerSectorDropdownExpanded,
                            onDismissRequest = { employerSectorDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            sectorOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 14.sp, color = Color(0xFF000000)) },
                                    onClick = {
                                        employerSector = option
                                        employerSectorDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // F. Organization location *
                    FormFieldLabel(label = "Organization location *")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = employerLocation,
                        onValueChange = { employerLocation = it },
                        placeholder = { Text("Enter city, district, or physical location", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { LocationPinIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        isError = showErrors && employerLocation.isBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // G. Company website (optional)
                    FormFieldLabel(label = "Company website (optional)")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = employerWebsite,
                        onValueChange = { employerWebsite = it },
                        placeholder = { Text("https://www.example.com", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { GlobeIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // H. Company description (optional)
                    FormFieldLabel(label = "Company description (optional)")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = employerDescription,
                        onValueChange = { employerDescription = it },
                        placeholder = { Text("Briefly describe your organization and the work it does", color = Color(0xFF525252), fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // I. Password *
                    FormFieldLabel(label = "Password *")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = employerPassword,
                        onValueChange = { employerPassword = it },
                        placeholder = { Text("Create a password (minimum 6 characters)", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { LockIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        trailingIcon = {
                            Box(
                                modifier = Modifier.clickable { isEmployerPasswordVisible = !isEmployerPasswordVisible },
                                contentAlignment = Alignment.Center
                            ) {
                                EyeIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B))
                            }
                        },
                        visualTransformation = if (isEmployerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = showErrors && (employerPassword.isBlank() || employerPassword.length < 6),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // J. Confirm password *
                    FormFieldLabel(label = "Confirm password *")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = employerConfirmPassword,
                        onValueChange = { employerConfirmPassword = it },
                        placeholder = { Text("Re-enter your password", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { LockIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        trailingIcon = {
                            Box(
                                modifier = Modifier.clickable { isEmployerConfirmPasswordVisible = !isEmployerConfirmPasswordVisible },
                                contentAlignment = Alignment.Center
                            ) {
                                EyeIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B))
                            }
                        },
                        visualTransformation = if (isEmployerConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = showErrors && (employerConfirmPassword.isBlank() || employerConfirmPassword != employerPassword),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )
                } else {
                    // Student Details Section Heading
                    Text(
                        text = "Student details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Full name *
                    FormFieldLabel(label = "Full name *")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = { Text("Enter your full name", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { PersonIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        isError = showErrors && fullName.isBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Student ID *
                    FormFieldLabel(label = "Student ID *")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        placeholder = { Text("bsc-inf-18-23", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { IdCardIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        isError = showErrors && studentId.isBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Email address *
                    FormFieldLabel(label = "Email address *")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("bsc-inf-18-23@unima.ac.mw", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { EmailIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = showErrors && (email.isBlank() || !email.contains("@")),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Phone number *
                    FormFieldLabel(label = "Phone number *")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        placeholder = { Text("+265 999 123 456", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { PhoneIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = showErrors && phoneNumber.isBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 5. Programme of study * (Dropdown)
                    FormFieldLabel(label = "Programme of study *")
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = programme,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Select your programme", color = Color(0xFF525252), fontSize = 14.sp) },
                            leadingIcon = { GraduationCapIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                            trailingIcon = {
                                Box(
                                    modifier = Modifier.clickable { programmeDropdownExpanded = !programmeDropdownExpanded },
                                    contentAlignment = Alignment.Center
                                ) {
                                    DownChevronIcon(modifier = Modifier.size(16.dp), tint = Color(0xFF64748B))
                                }
                            },
                            isError = showErrors && programme.isBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { programmeDropdownExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors()
                        )

                        DropdownMenu(
                            expanded = programmeDropdownExpanded,
                            onDismissRequest = { programmeDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            programmeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 14.sp, color = Color(0xFF000000)) },
                                    onClick = {
                                        programme = option
                                        programmeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 6. Year of study * (Dropdown)
                    FormFieldLabel(label = "Year of study *")
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = yearOfStudy,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Select year", color = Color(0xFF525252), fontSize = 14.sp) },
                            leadingIcon = { CalendarIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                            trailingIcon = {
                                Box(
                                    modifier = Modifier.clickable { yearDropdownExpanded = !yearDropdownExpanded },
                                    contentAlignment = Alignment.Center
                                ) {
                                    DownChevronIcon(modifier = Modifier.size(16.dp), tint = Color(0xFF64748B))
                                }
                            },
                            isError = showErrors && yearOfStudy.isBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { yearDropdownExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors()
                        )

                        DropdownMenu(
                            expanded = yearDropdownExpanded,
                            onDismissRequest = { yearDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            yearOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 14.sp, color = Color(0xFF000000)) },
                                    onClick = {
                                        yearOfStudy = option
                                        yearDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 7. Skills (optional)
                    FormFieldLabel(label = "Skills (optional)")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = skills,
                        onValueChange = { skills = it },
                        placeholder = { Text("Describe your skills (e.g. Kotlin, Python, UI Design)", color = Color(0xFF525252), fontSize = 14.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 8. Password *
                    FormFieldLabel(label = "Password *")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Create a password (min. 6 chars)", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { LockIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        trailingIcon = {
                            Box(
                                modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible },
                                contentAlignment = Alignment.Center
                            ) {
                                EyeIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B))
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = showErrors && (password.isBlank() || password.length < 6),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 9. Confirm password *
                    FormFieldLabel(label = "Confirm password *")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = { Text("Re-enter your password", color = Color(0xFF525252), fontSize = 14.sp) },
                        leadingIcon = { LockIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B)) },
                        trailingIcon = {
                            Box(
                                modifier = Modifier.clickable { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                                contentAlignment = Alignment.Center
                            ) {
                                EyeIcon(modifier = Modifier.size(20.dp), tint = Color(0xFF64748B))
                            }
                        },
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = showErrors && (confirmPassword.isBlank() || confirmPassword != password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )
                }

                // Error Message Display
                if (validationErrorMsg.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = validationErrorMsg,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFB91C1C)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Primary Button: Create Account
                Button(
                    onClick = { validateAndSubmit() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B3B80),
                        disabledContainerColor = Color(0xFF94A3B8)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Creating Account...",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Create Account",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            RightArrowIcon(modifier = Modifier.size(18.dp), tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Secondary Link: "Already have an account? Log in"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        fontSize = 14.sp,
                        color = Color(0xFF404040)
                    )
                    Text(
                        text = "Log in",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B3B80),
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun AccountTypeCard(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF0F2C59) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF0F2C59),
                    unselectedColor = Color(0xFFCBD5E1)
                ),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = Color(0xFF000000)
            )
        }
    }
}

@Composable
private fun FormFieldLabel(label: String) {
    Text(
        text = label,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF000000)
    )
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF000000),
    unfocusedTextColor = Color(0xFF000000),
    focusedBorderColor = Color(0xFF0F2C59),
    unfocusedBorderColor = Color(0xFF94A3B8),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    errorBorderColor = Color(0xFFB91C1C)
)

@Composable
fun IdCardIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.08f

        drawRect(
            color = tint,
            topLeft = Offset(w * 0.12f, h * 0.22f),
            size = Size(w * 0.76f, h * 0.56f),
            style = Stroke(stroke)
        )

        drawRect(
            color = tint,
            topLeft = Offset(w * 0.22f, h * 0.35f),
            size = Size(w * 0.22f, h * 0.30f),
            style = Stroke(stroke)
        )

        drawLine(color = tint, start = Offset(w * 0.52f, h * 0.40f), end = Offset(w * 0.76f, h * 0.40f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.52f, h * 0.58f), end = Offset(w * 0.72f, h * 0.58f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
fun EmailIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.08f

        drawRect(
            color = tint,
            topLeft = Offset(w * 0.12f, h * 0.25f),
            size = Size(w * 0.76f, h * 0.50f),
            style = Stroke(stroke)
        )

        val flap = Path().apply {
            moveTo(w * 0.14f, h * 0.27f)
            lineTo(w * 0.50f, h * 0.55f)
            lineTo(w * 0.86f, h * 0.27f)
        }
        drawPath(flap, color = tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun PhoneIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
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
fun GlobeIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.08f

        drawCircle(color = tint, radius = w * 0.40f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(stroke))
        drawLine(color = tint, start = Offset(w * 0.10f, h * 0.5f), end = Offset(w * 0.90f, h * 0.5f), strokeWidth = stroke)
        drawArc(
            color = tint,
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.30f, h * 0.10f),
            size = Size(w * 0.40f, h * 0.80f),
            style = Stroke(stroke)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    UniversityIntershipAppTheme {
        RegisterScreen()
    }
}
