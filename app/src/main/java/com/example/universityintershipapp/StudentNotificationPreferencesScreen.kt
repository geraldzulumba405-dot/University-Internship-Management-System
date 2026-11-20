package com.example.universityintershipapp

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private val DarkNavy = Color(0xFF0F2C59)
private val PrimaryBlue = Color(0xFF1B3B80)
private val SurfaceBg = Color(0xFFF8FAFC)
private val StudTextPrimary = Color(0xFF0F172A)
private val StudTextSecondary = Color(0xFF64748B)

@Composable
fun StudentNotificationPreferencesScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var newInternships by remember { mutableStateOf(true) }
    var applicationUpdates by remember { mutableStateOf(true) }
    var internshipReminders by remember { mutableStateOf(true) }
    var pushNotifications by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val doc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
                if (doc.exists()) {
                    val prefs = doc.get("notificationPreferences") as? Map<*, *>
                    if (prefs != null) {
                        newInternships = prefs["newInternships"] as? Boolean ?: true
                        applicationUpdates = prefs["applicationUpdates"] as? Boolean ?: true
                        internshipReminders = prefs["internshipReminders"] as? Boolean ?: true
                        pushNotifications = prefs["pushNotifications"] as? Boolean ?: true
                        emailNotifications = prefs["emailNotifications"] as? Boolean ?: false
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to load preferences: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
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
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    BackArrowIcon(modifier = Modifier.size(20.dp), tint = StudTextPrimary)
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Notification Preferences",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavy
                    )
                }

                Spacer(modifier = Modifier.size(36.dp))
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // Content
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Customise Notifications",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Choose which notifications and alerts you would like to receive regarding your applications and new internships.",
                            fontSize = 12.sp,
                            color = StudTextSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StudentPrefSwitchRow(
                                title = "New Internships",
                                subtitle = "Notify when new internship opportunities are published",
                                checked = newInternships,
                                onCheckedChange = { newInternships = it }
                            )
                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            StudentPrefSwitchRow(
                                title = "Application Updates",
                                subtitle = "Notify when employers accept or reject your applications",
                                checked = applicationUpdates,
                                onCheckedChange = { applicationUpdates = it }
                            )
                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            StudentPrefSwitchRow(
                                title = "Internship Reminders",
                                subtitle = "Notify about approaching application deadlines",
                                checked = internshipReminders,
                                onCheckedChange = { internshipReminders = it }
                            )
                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            StudentPrefSwitchRow(
                                title = "Push Notifications",
                                subtitle = "Receive alerts on your device via FCM",
                                checked = pushNotifications,
                                onCheckedChange = { pushNotifications = it }
                            )
                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            StudentPrefSwitchRow(
                                title = "Email Notifications",
                                subtitle = "Receive notifications via registered email",
                                checked = emailNotifications,
                                onCheckedChange = { emailNotifications = it }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (isSaving) return@Button
                            isSaving = true
                            scope.launch {
                                try {
                                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                                    if (uid != null) {
                                        val prefsMap = mapOf(
                                            "newInternships" to newInternships,
                                            "applicationUpdates" to applicationUpdates,
                                            "internshipReminders" to internshipReminders,
                                            "pushNotifications" to pushNotifications,
                                            "emailNotifications" to emailNotifications
                                        )
                                        FirebaseFirestore.getInstance().collection("users")
                                            .document(uid)
                                            .set(mapOf("notificationPreferences" to prefsMap), SetOptions.merge())
                                            .await()

                                        Toast.makeText(context, "Notification preferences saved successfully!", Toast.LENGTH_SHORT).show()
                                        onBackClick()
                                    } else {
                                        Toast.makeText(context, "User not authenticated.", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Failed to save: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text(
                                text = "Save Changes",
                                fontSize = 14.sp,
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

@Composable
private fun StudentPrefSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = StudTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = StudTextSecondary,
                lineHeight = 15.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryBlue
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentNotificationPreferencesScreenPreview() {
    UniversityIntershipAppTheme {
        StudentNotificationPreferencesScreen()
    }
}
