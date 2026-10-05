package com.example.universityintershipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.universityintershipapp.ui.theme.UniversityIntershipAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniversityIntershipAppTheme {
                var currentScreen by remember { mutableStateOf("home") }

                if (currentScreen != "home") {
                    BackHandler {
                        currentScreen = "home"
                    }
                }

                when (currentScreen) {
                    "home" -> HomeScreen(
                        onGetStartedClick = { pathway ->
                            if (pathway == PortalPathway.STUDENT) {
                                currentScreen = "login"
                            }
                        },
                        onLogInClick = {
                            currentScreen = "login"
                        }
                    )
                    "login" -> LoginScreen(
                        onBackClick = {
                            currentScreen = "home"
                        }
                    )
                }
            }
        }
    }
}
