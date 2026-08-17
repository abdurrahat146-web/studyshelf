package com.studyshelf.app

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.studyshelf.app.ui.screens.AuthScreen
import com.studyshelf.app.ui.screens.InboxScreen
import com.studyshelf.app.ui.screens.LibraryScreen
import com.studyshelf.app.ui.screens.PremiumScreen
import com.studyshelf.app.ui.screens.RoutineScreen
import com.studyshelf.app.ui.theme.StudyShelfTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op — user's choice either way, app degrades gracefully */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        maybeRequestExactAlarmPermission()

        val deepLink = intent.getStringExtra("deep_link")

        setContent {
            StudyShelfTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    LaunchedEffect(deepLink) {
                        when (deepLink) {
                            "inbox" -> navController.navigate("inbox")
                            "routine" -> navController.navigate("routine")
                            "premium" -> navController.navigate("premium")
                        }
                    }

                    NavHost(navController = navController, startDestination = "auth") {
                        composable("auth") { AuthScreen(navController) }
                        composable("library") { LibraryScreen(navController) }
                        composable("inbox") { InboxScreen(navController) }
                        composable("routine") { RoutineScreen(navController) }
                        composable("premium") { PremiumScreen(navController) }
                    }
                }
            }
        }
    }

    /** Android 12+ requires the user to explicitly grant exact-alarm scheduling in Settings. */
    private fun maybeRequestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }
}
