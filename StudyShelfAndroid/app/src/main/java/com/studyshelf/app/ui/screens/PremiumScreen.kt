package com.studyshelf.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.studyshelf.app.StudyShelfApp
import com.studyshelf.app.ui.theme.StudyShelfGold
import com.studyshelf.app.ui.theme.StudyShelfGreen
import kotlinx.coroutines.launch

/**
 * Single screen that adapts to whichever flavor is running via the shared
 * [com.studyshelf.app.premium.PremiumGate] interface — no if/else on flavor
 * name anywhere here, just calls to gate.isPremiumBuild / gate.isUnlocked().
 */
@Composable
fun PremiumScreen(navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as StudyShelfApp
    val gate = app.premiumGate
    val scope = rememberCoroutineScope()

    var unlocked by remember { mutableStateOf(false) }
    var keyInput by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var checking by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        unlocked = gate.isUnlocked()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Premium") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (gate.isPremiumBuild || unlocked) {
                Spacer(Modifier.height(40.dp))
                Text("🎉", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Premium Active",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyShelfGreen
                )
                Spacer(Modifier.height(8.dp))
                Text("Unlimited AI · No ads · Lifetime access", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            } else {
                Text(
                    "Unlock Premium",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyShelfGold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Unlimited AI credits, no ads, and lifetime access for ৳150.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))

                Text("Have a key already?", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    label = { Text("SS-XXXX-XXXX-XXXX") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                error?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        checking = true
                        error = null
                        scope.launch {
                            val ok = gate.redeemKey(keyInput)
                            checking = false
                            if (ok) unlocked = true
                            else error = "Invalid key. Please check and try again."
                        }
                    },
                    enabled = !checking && keyInput.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (checking) "Checking..." else "🔓 Unlock Premium")
                }

                Spacer(Modifier.height(24.dp))
                Divider()
                Spacer(Modifier.height(16.dp))

                Text("Don't have a key yet?", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("💳 Pay via bKash", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("1. Open bKash app")
                        Text("2. Send ৳150 to 01966309238")
                        Text("3. Reference: SSPREMIUM")
                        Text("4. Send screenshot — receive key within 24hrs")
                    }
                }
            }
        }
    }
}
