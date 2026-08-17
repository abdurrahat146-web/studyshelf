package com.studyshelf.app.data.repository

import com.studyshelf.app.data.model.SharedBook
import com.studyshelf.app.data.model.UserAccount
import com.studyshelf.app.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val message: String) : Result<Nothing>()
}

class StudyShelfRepository {

    private val db = SupabaseClientProvider.client

    // ── Users ──

    suspend fun login(username: String, password: String): Result<UserAccount> =
        withContext(Dispatchers.IO) {
            try {
                val users = db.from("users")
                    .select {
                        filter {
                            eq("username", username)
                            eq("password", password)
                        }
                    }
                    .decodeList<UserAccount>()
                if (users.isEmpty()) Result.Failure("Incorrect username or password.")
                else Result.Success(users.first())
            } catch (e: Exception) {
                Result.Failure(e.message ?: "Login failed.")
            }
        }

    suspend fun signup(name: String, username: String, password: String): Result<UserAccount> =
        withContext(Dispatchers.IO) {
            try {
                val existing = db.from("users")
                    .select { filter { eq("username", username) } }
                    .decodeList<UserAccount>()
                if (existing.isNotEmpty()) return@withContext Result.Failure("Username already taken.")

                val newUser = UserAccount(
                    id = UUID.randomUUID().toString().take(12),
                    name = name,
                    username = username,
                    password = password
                )
                db.from("users").insert(newUser)
                Result.Success(newUser)
            } catch (e: Exception) {
                Result.Failure(e.message ?: "Could not create account.")
            }
        }

    // ── Book sharing ──

    suspend fun shareBook(
        fromUsername: String,
        bookId: String,
        bookTitle: String,
        bookEmoji: String,
        toUsername: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (toUsername.equals(fromUsername, ignoreCase = true)) {
                return@withContext Result.Failure("You can't share with yourself!")
            }
            val target = db.from("users")
                .select { filter { eq("username", toUsername) } }
                .decodeList<UserAccount>()
                .firstOrNull() ?: return@withContext Result.Failure("User \"$toUsername\" not found.")

            val already = db.from("shared_books")
                .select {
                    filter {
                        eq("book_id", bookId)
                        eq("to_user_id", target.id)
                        eq("from_username", fromUsername)
                    }
                }
                .decodeList<SharedBook>()
            if (already.isNotEmpty()) return@withContext Result.Failure("Already shared this book with them.")

            db.from("shared_books").insert(
                SharedBook(
                    bookId = bookId,
                    bookTitle = bookTitle,
                    bookEmoji = bookEmoji,
                    fromUsername = fromUsername,
                    toUserId = target.id
                )
            )
            Result.Success(target.username)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "Failed to share.")
        }
    }

    suspend fun getInbox(userId: String): Result<List<SharedBook>> = withContext(Dispatchers.IO) {
        try {
            val inbox = db.from("shared_books")
                .select {
                    filter { eq("to_user_id", userId) }
                    order("shared_at", Order.DESCENDING)
                }
                .decodeList<SharedBook>()
            Result.Success(inbox)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "Could not load inbox.")
        }
    }

    suspend fun dismissShared(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.from("shared_books").delete { filter { eq("id", id) } }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.message ?: "Could not dismiss.")
        }
    }
}
