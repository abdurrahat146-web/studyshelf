package com.studyshelf.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserAccount(
    val id: String,
    val name: String,
    val username: String,
    val password: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class AdminAccount(
    val id: String,
    val name: String,
    val username: String,
    val password: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class SharedBook(
    val id: Long = 0,
    @SerialName("book_id") val bookId: String,
    @SerialName("book_title") val bookTitle: String,
    @SerialName("book_emoji") val bookEmoji: String? = "📚",
    @SerialName("from_username") val fromUsername: String,
    @SerialName("to_user_id") val toUserId: String,
    @SerialName("shared_at") val sharedAt: String? = null
)

/** Local-only model — study routine entries, stored via DataStore/Room-lite (kept simple here as JSON). */
@Serializable
data class RoutineEntry(
    val id: String,
    val title: String,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: List<Int>, // 1=Sun ... 7=Sat, matches Calendar.DAY_OF_WEEK
    val enabled: Boolean = true
)

/** Book catalog entry (matches the web app's book shape closely). */
@Serializable
data class Book(
    val id: String,
    val title: String,
    val author: String? = null,
    val category: String? = null,
    val emoji: String? = "📚",
    @SerialName("pdf_url") val pdfUrl: String? = null
)
