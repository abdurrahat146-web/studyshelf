package com.studyshelf.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.studyshelf.app.StudyShelfApp
import com.studyshelf.app.data.model.Book
import com.studyshelf.app.data.repository.Result
import com.studyshelf.app.data.repository.StudyShelfRepository
import com.studyshelf.app.notifications.SessionStore
import kotlinx.coroutines.launch

/** Placeholder catalog — in the full app this comes from Supabase/local seed data,
 * mirroring the web app's book list. Kept static here to focus this pass on
 * the notification + sharing plumbing that was asked for. */
private val sampleBooks = listOf(
    Book(id = "b1", title = "Calculus Made Easy", author = "Silvanus Thompson", category = "Math", emoji = "📐"),
    Book(id = "b2", title = "Organic Chemistry", author = "Unknown", category = "Science", emoji = "⚗️"),
    Book(id = "b3", title = "Cell Biology Essentials", author = "Unknown", category = "Biology", emoji = "🌿"),
    Book(id = "b4", title = "World History Vol. I", author = "Unknown", category = "History", emoji = "📜"),
)

@Composable
fun LibraryScreen(navController: NavController) {
    val context = LocalContext.current
    val repo = remember { StudyShelfRepository() }
    val scope = rememberCoroutineScope()

    var currentUsername by remember { mutableStateOf<String?>(null) }
    var shareTarget by remember { mutableStateOf<Book?>(null) }

    LaunchedEffect(Unit) {
        currentUsername = SessionStore.getCurrentUser(context)?.username
    }

    val app = context.applicationContext as StudyShelfApp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (app.premiumGate.isPremiumBuild) "StudyShelf Premium" else "StudyShelf") },
                actions = {
                    IconButton(onClick = { navController.navigate("premium") }) {
                        Icon(Icons.Filled.Star, contentDescription = "Premium")
                    }
                    IconButton(onClick = { navController.navigate("inbox") }) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Shared with you")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleBooks) { book ->
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(book.emoji ?: "📚", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(book.category ?: "", style = MaterialTheme.typography.labelSmall)
                            Text(book.title, style = MaterialTheme.typography.titleMedium)
                            Text(book.author ?: "Unknown", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (currentUsername != null) {
                            IconButton(onClick = { shareTarget = book }) {
                                Icon(Icons.Filled.Share, contentDescription = "Share")
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { navController.navigate("routine") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📅 Study Routine")
                }
            }
        }
    }

    shareTarget?.let { book ->
        ShareBookDialog(
            book = book,
            fromUsername = currentUsername.orEmpty(),
            onDismiss = { shareTarget = null },
            repo = repo,
            scope = scope
        )
    }
}

@Composable
private fun ShareBookDialog(
    book: Book,
    fromUsername: String,
    onDismiss: () -> Unit,
    repo: StudyShelfRepository,
    scope: kotlinx.coroutines.CoroutineScope
) {
    var recipient by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }
    var sent by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share \"${book.title}\"") },
        text = {
            Column {
                if (sent) {
                    Text("🔗 Shared with $recipient!")
                } else {
                    OutlinedTextField(
                        value = recipient,
                        onValueChange = { recipient = it },
                        label = { Text("Recipient username") },
                        singleLine = true
                    )
                    error?.let {
                        Spacer(Modifier.height(6.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            if (!sent) {
                TextButton(
                    enabled = !sending && recipient.isNotBlank(),
                    onClick = {
                        sending = true
                        error = null
                        scope.launch {
                            val result = repo.shareBook(
                                fromUsername = fromUsername,
                                bookId = book.id,
                                bookTitle = book.title,
                                bookEmoji = book.emoji ?: "📚",
                                toUsername = recipient
                            )
                            sending = false
                            when (result) {
                                is Result.Success -> sent = true
                                is Result.Failure -> error = result.message
                            }
                        }
                    }
                ) { Text(if (sending) "Sending..." else "Send") }
            } else {
                TextButton(onClick = onDismiss) { Text("Done") }
            }
        },
        dismissButton = {
            if (!sent) TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
