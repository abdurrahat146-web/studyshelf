package com.studyshelf.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.studyshelf.app.data.model.UserAccount
import com.studyshelf.app.data.repository.Result
import com.studyshelf.app.data.repository.StudyShelfRepository
import com.studyshelf.app.notifications.SessionStore
import com.studyshelf.app.ui.theme.StudyShelfGold
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repo = remember { StudyShelfRepository() }
    val scope = rememberCoroutineScope()

    var isSignup by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    fun submit() {
        error = null
        loading = true
        scope.launch {
            val result = if (isSignup) {
                repo.signup(name, username, password)
            } else {
                repo.login(username, password)
            }
            loading = false
            when (result) {
                is Result.Success -> {
                    SessionStore.setCurrentUser(context, result.data as UserAccount)
                    navController.navigate("library") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
                is Result.Failure -> error = result.message
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "StudyShelf",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = StudyShelfGold
        )
        Spacer(Modifier.height(4.dp))
        Text("Your Academic Helper", fontSize = 13.sp)
        Spacer(Modifier.height(32.dp))

        if (isSignup) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { submit() },
            enabled = !loading && username.isNotBlank() && password.isNotBlank() && (!isSignup || name.isNotBlank()),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Please wait..." else if (isSignup) "Create Account" else "Sign In")
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = { isSignup = !isSignup; error = null }) {
            Text(if (isSignup) "Already have an account? Sign in" else "New here? Create an account")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("library") }) {
            Text("Continue without an account →")
        }
    }
}
