package com.studyshelf.app.data.remote

import com.studyshelf.app.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

/**
 * Single shared Supabase client for the whole app.
 * Uses the same project/tables as the existing StudyShelf web app
 * (users, shared_books, admins) so accounts and shares are cross-platform.
 */
object SupabaseClientProvider {

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Realtime)
        }
    }
}
