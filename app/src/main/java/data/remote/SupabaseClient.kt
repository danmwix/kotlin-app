package com.example.maternitymanagement.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://ctrzjxreotqndejcqbhp.supabase.co",
            supabaseKey = "sb_publishable_z0rQKY1OHft2vVpGHqW-pw_tbnHKl6Z"
        ) {
            install(Auth)        // ✅ REQUIRED for authentication
            install(Postgrest)   // ✅ For database access
        }
    }
}