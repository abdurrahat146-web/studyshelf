package com.studyshelf.app.notifications

import android.content.Context
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.studyshelf.app.data.model.UserAccount
import com.studyshelf.app.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

private val Context.pollDataStore by preferencesDataStore(name = "shared_book_poll")
private val SEEN_IDS_KEY = stringSetPreferencesKey("seen_shared_ids")

/**
 * Periodically checks the `shared_books` table for entries addressed to the
 * currently logged-in user that we haven't notified about yet, and fires a
 * local notification for each new one. Scheduled by [NotificationScheduler].
 */
class SharedBookPollWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val session = SessionStore.getCurrentUser(applicationContext) ?: return Result.success()

        return try {
            val db = SupabaseClientProvider.client
            val inbox = db.from("shared_books")
                .select { filter { eq("to_user_id", session.id) } }
                .decodeList<com.studyshelf.app.data.model.SharedBook>()

            val prefs = applicationContext.pollDataStore.data.first()
            val seen = prefs[SEEN_IDS_KEY] ?: emptySet()

            val unseen = inbox.filter { it.id.toString() !in seen }

            unseen.forEach { shared ->
                NotificationHelper.notifySharedBook(
                    context = applicationContext,
                    fromUsername = shared.fromUsername,
                    bookTitle = shared.bookTitle,
                    notificationId = shared.id.toInt()
                )
            }

            if (unseen.isNotEmpty()) {
                val newSeen = seen + unseen.map { it.id.toString() }
                applicationContext.pollDataStore.edit { it[SEEN_IDS_KEY] = newSeen }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

/** Minimal session accessor so the worker can find out who is logged in without a full ViewModel. */
object SessionStore {
    private val KEY = androidx.datastore.preferences.core.stringPreferencesKey("current_user_json")
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getCurrentUser(context: Context): UserAccount? {
        val prefs = context.pollDataStore.data.first()
        val raw = prefs[KEY] ?: return null
        return try {
            json.decodeFromString<UserAccount>(raw)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun setCurrentUser(context: Context, user: UserAccount?) {
        context.pollDataStore.edit { prefs ->
            if (user == null) prefs.remove(KEY)
            else prefs[KEY] = json.encodeToString(user)
        }
    }
}
