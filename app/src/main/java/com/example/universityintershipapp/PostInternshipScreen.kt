package com.example.universityintershipapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import android.app.DatePickerDialog
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostInternshipScreen(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit = {},
    onPostSuccess: () -> Unit = {},
) {
    // 1. Form States
    var title by remember { mutableStateOf("") }
    var fieldOfStudy by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var workMode by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val workModeOptions = listOf("On-site", "Remote", "Hybrid")
    var workModeDropdownExpanded by remember { mutableStateOf(false) }

    val skillsList = remember {
        mutableStateListOf<String>()
    }
    var showAddSkillField by remember { mutableStateOf(false) }
    var newSkillText by remember { mutableStateOf("") }

    var reqTranscript by remember { mutableStateOf(true) }
    var reqResume by remember { mutableStateOf(true) }
    var reqPortfolio by remember { mutableStateOf(true) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                deadline = dateFormat.format(selectedDate.time)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnCancelListener { showDatePicker = false }
        }.show()
    }

    fun submitPost() {
        when {
            title.isBlank() -> errorMessage = "Please enter an internship title."
            fieldOfStudy.isBlank() -> errorMessage = "Please select a field of study."
            location.isBlank() -> errorMessage = "Please enter the internship location."
            duration.isBlank() -> errorMessage = "Please enter the internship duration."
            deadline.isBlank() -> errorMessage = "Please enter an application deadline."
            workMode.isBlank() -> errorMessage = "Please select a work mode."
            !isLoading -> {
                isLoading = true
                errorMessage = null
                scope.launch {
                    val res = InternshipRepository.postInternship(
                        title = title,
                        fieldOfStudy = fieldOfStudy,
                        location = location,
                        duration = duration,
                        deadline = deadline,
                        description = description,
                        skills = skillsList.toList(),
                        reqTranscript = reqTranscript,
                        reqResume = reqResume,
                        reqPortfolio = reqPortfolio,
                        workMode = workMode
                    )
                    isLoading = false
                    when (res) {
                        is InternshipResult.Success -> {
                            onPostSuccess()
                        }
                        is InternshipResult.Error -> {
                            errorMessage = res.message
                        }
                    }
                }
            }
        }
    }

    val darkNavy = Color(0xFF0F2C59)
    val textBlack = Color(0xFF000000)
    val textHelperGrey = Color(0xFF404040)
    val textPlaceholderGrey = Color(0xFF525252)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onCloseClick() },
                    contentAlignment = Alignment.Center
                ) {
                    CloseXIcon(
                        modifier = Modifier.size(20.dp),
                        tint = textBlack
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Post New Internship",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkNavy
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // Scrollable Form Container
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // SECTION 1: Role Basics
                SectionCardContainer {
                    SectionHeader(
                        number = "1",
                        title = "Role Basics",
                        tagText = "Required",
                        tagBgColor = Color(0xFFEFF6FF),
                        tagTextColor = Color(0xFF1D4ED8)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Internship Title
                    FormLabel("Internship Title")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Software Engineering Intern", color = textPlaceholderGrey, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = formTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Spacer(modifier = Modifier.height(14.dp))

                    // Field / Area of Study
                    FormLabel("Field / Area of Study")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = fieldOfStudy,
                        onValueChange = { fieldOfStudy = it },
                        placeholder = { Text("Computer Science & Software Engineering", color = textPlaceholderGrey, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = formTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Location
                    FormLabel("Location")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text("Lilongwe, Malawi", color = textPlaceholderGrey, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = formTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Internship Duration
                    FormLabel("Internship Duration")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        placeholder = { Text("12 Weeks", color = textPlaceholderGrey, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = formTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Work Mode *
                    FormLabel("Work Mode *")
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = workMode,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Select work mode", color = textPlaceholderGrey, fontSize = 14.sp) },
                            trailingIcon = {
                                Box(
                                    modifier = Modifier
                                        .clickable { workModeDropdownExpanded = !workModeDropdownExpanded }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    DownChevronIcon(modifier = Modifier.size(16.dp), tint = Color(0xFF64748B))
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { workModeDropdownExpanded = true },
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = formTextFieldColors()
                        )

                        DropdownMenu(
                            expanded = workModeDropdownExpanded,
                            onDismissRequest = { workModeDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            workModeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 14.sp, color = Color(0xFF000000)) },
                                    onClick = {
                                        workMode = option
                                        workModeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Application Deadline
                    FormLabel("Application Deadline")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        placeholder = { Text("June 1, 2026", color = textPlaceholderGrey, fontSize = 14.sp) },
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .clickable { showDatePicker = true }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CalendarIcon(modifier = Modifier.size(18.dp), tint = Color(0xFF64748B))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = formTextFieldColors()
                    )
                }

                // SECTION 2: Role Description
                SectionCardContainer {
                    SectionHeader(
                        number = "2",
                        title = "Role Description"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FormLabel("Summary & Deliverables")
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Briefly describe the internship role, key responsibilities and expected deliverables...", color = textPlaceholderGrey, fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 4,
                        colors = formTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val wordCount = if (description.isBlank()) 0 else description.trim().split("\\s+".toRegex()).size
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Minimum recommended: 150 words",
                            fontSize = 12.sp,
                            color = textHelperGrey
                        )
                        Text(
                            text = "$wordCount / 600 words",
                            fontSize = 12.sp,
                            color = textHelperGrey
                        )
                    }
                }

                // SECTION 3: Required Skills & Competencies
                SectionCardContainer {
                    SectionHeader(
                        number = "3",
                        title = "Required Skills & Competencies",
                        tagText = "${skillsList.size} Added",
                        tagBgColor = Color(0xFFEFF6FF),
                        tagTextColor = Color(0xFF1E3A8A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Selected tags are matched against student academic resumes & verified course credits.",
                        fontSize = 12.sp,
                        color = textHelperGrey,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tags FlowRow
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        skillsList.forEach { skill ->
                            SkillChip(
                                skill = skill,
                                onRemove = { skillsList.remove(skill) }
                            )
                        }

                        // Add Skill Pill Button
                        Surface(
                            onClick = { showAddSkillField = !showAddSkillField },
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "+ Add Skill",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = darkNavy
                                )
                            }
                        }
                    }

                    if (showAddSkillField) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newSkillText,
                                onValueChange = { newSkillText = it },
                                placeholder = { Text("e.g. Docker, Python", fontSize = 13.sp, color = textPlaceholderGrey) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = formTextFieldColors()
                            )
                            Button(
                                onClick = {
                                    if (newSkillText.isNotBlank()) {
                                        skillsList.add(newSkillText.trim())
                                        newSkillText = ""
                                        showAddSkillField = false
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = darkNavy)
                            ) {
                                Text("Add", fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }
                }

                // SECTION 4: Application Requirements
                SectionCardContainer {
                    SectionHeader(
                        number = "4",
                        title = "Application Requirements"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Specify mandatory documents university students must attach when submitting:",
                        fontSize = 12.sp,
                        color = textHelperGrey,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Requirement Item 1: Official University Transcript
                    RequirementCheckCard(
                        title = "Official University Transcript",
                        subtitle = "Direct encrypted verification from Registrar's Office",
                        isChecked = reqTranscript,
                        onCheckedChange = { reqTranscript = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Requirement Item 2: Resume / CV
                    RequirementCheckCard(
                        title = "Resume / CV",
                        subtitle = "PDF format with recent project milestones",
                        isChecked = reqResume,
                        onCheckedChange = { reqResume = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Requirement Item 3: GitHub / Portfolio Link
                    RequirementCheckCard(
                        title = "GitHub / Portfolio Link",
                        subtitle = "Public code repository or product design portfolio",
                        isChecked = reqPortfolio,
                        onCheckedChange = { reqPortfolio = it }
                    )
                }

                // Error Message Banner
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
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom Sticky Action Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Button(
                        onClick = { submitPost() },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = darkNavy,
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
                                text = "Posting Internship...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                PaperPlaneIcon(
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Post Internship",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCardContainer(
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun SectionHeader(
    number: String,
    title: String,
    tagText: String? = null,
    tagBgColor: Color = Color(0xFFEFF6FF),
    tagTextColor: Color = Color(0xFF1D4ED8)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Number Badge
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(Color(0xFF0F2C59), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2C59),
            modifier = Modifier.weight(1f)
        )

        tagText?.let {
            Surface(
                shape = RoundedCornerShape(50),
                color = tagBgColor
            ) {
                Text(
                    text = it,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tagTextColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF000000)
    )
}

@Composable
private fun SkillChip(
    skill: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFEFF6FF),
        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = skill,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E3A8A)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "×",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
            }
        }
    }
}

@Composable
private fun RequirementCheckCard(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) },
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF0F2C59),
                    uncheckedColor = Color(0xFF94A3B8)
                ),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF404040),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun formTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF000000),
    unfocusedTextColor = Color(0xFF000000),
    focusedBorderColor = Color(0xFF0F2C59),
    unfocusedBorderColor = Color(0xFFCBD5E1),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White
)

@Composable
private fun CloseXIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF000000)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.12f

        drawLine(color = tint, start = Offset(w * 0.2f, h * 0.2f), end = Offset(w * 0.8f, h * 0.8f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(w * 0.8f, h * 0.2f), end = Offset(w * 0.2f, h * 0.8f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PostInternshipScreenPreview() {
    UniversityIntershipAppTheme {
        PostInternshipScreen()
    }
}
