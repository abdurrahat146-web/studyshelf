package com.studyshelf.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.studyshelf.app.data.model.SharedBook
import com.studyshelf.app.data.repository.Result
import com.studyshelf.app.data.repository.StudyShelfRepository
import com.studyshelf.app.notifications.SessionStore
import kotlinx.coroutines.launch

/**
 * Shows books other users have shared with the current account.
 * This is the screen a notification tap (from [SharedBookPollWorker]) deep-links into.
 */
@Composable
fun InboxScreen(navController: NavController) {
    val context = LocalContext.current
    val repo = remember { StudyShelfRepository() }
    val scope = rememberCoroutineScope()

    var inbox by remember { mutableStateOf<List<SharedBook>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun refresh() {
        scope.launch {
            loading = true
            val user = SessionStore.getCurrentUser(context)
            if (user == null) {
                error = "Sign in to see shared books."
                loading = false
                return@launch
            }
            when (val result = repo.getInbox(user.id)) {
                is Result.Success -> inbox = result.data
                is Result.Failure -> error = result.message
            }
            loading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shared with You") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text(
                    error ?: "",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                )
                inbox.isEmpty() -> Text(
                    "No shared books yet.\nAsk a friend to share one!",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                )
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(inbox, key = { it.id }) { item ->
                        Card {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.bookEmoji ?: "📚", style = MaterialTheme.typography.titleLarge)
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(item.bookTitle, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "From ${item.fromUsername}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                TextButton(onClick = {
                                    scope.launch {
                                        repo.dismissShared(item.id)
                                        refresh()
                                    }
                                }) { Text("✕") }
                            }
                        }
                    }
                }
            }
        }
    }
}
