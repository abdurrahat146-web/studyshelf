package com.studyshelf.app.premium

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.premiumDataStore by preferencesDataStore(name = "premium_gate")
private val UNLOCKED_KEY = booleanPreferencesKey("premium_unlocked")

/**
 * Free-flavor implementation of [PremiumGate]. Lives ONLY in the `free`
 * source set (app/src/free/...) — Gradle compiles this class in when
 * building the `free` flavor and the premium-flavor equivalent
 * (app/src/premium/.../PremiumGateImpl.kt) when building `premium`.
 * Both have the identical fully-qualified name
 * `com.studyshelf.app.premium.PremiumGateImpl`, so shared code in
 * app/src/main/ can reference just one class name and get whichever
 * flavor's version is active — no if/else tier checks needed anywhere else.
 *
 * Ships with 10 daily AI credits, shows ads, and gates premium features
 * behind a key purchased via bKash — mirroring the web app's Premium Gate
 * overlay (same base64+reversed obfuscation, same encoded key set, so keys
 * sold for the web app work here too).
 */
class PremiumGateImpl(private val context: Context) : PremiumGate {

    override val isPremiumBuild: Boolean = false
    override val dailyAiCredits: Int = 10
    override val showAds: Boolean = true

    private val encodedKeys = listOf(
        listOf("R0hYSC1M", "QlpMLUFN", "Rk8tU1M="),
        listOf("OVNNRS1G", "RlVBLUtD", "VVUtU1M="),
        listOf("QjY4OS1X", "VkpBLUVY", "TE4tU1M="),
        listOf("Nk9NTi1H", "VVFELUEy", "WkUtU1M="),
        listOf("UElPSS1Y", "REdGLVBM", "T1ktU1M="),
        listOf("V1VSVC1H", "UzgwLVBD", "V0ctU1M="),
        listOf("M0ZTWi1U", "N0pPLVhO", "ODktU1M="),
        listOf("VE9GMS1O", "RDRFLUNE", "QlQtU1M="),
        listOf("RzRDOS1C", "REk3LTFG", "WUEtU1M="),
        listOf("MzVIQi1C", "TzJSLTNE", "WEYtU1M=")
    )

    private fun decodeKey(parts: List<String>): String {
        val joined = parts.joinToString("") { String(Base64.decode(it, Base64.DEFAULT)) }
        return joined.reversed()
    }

    override suspend fun redeemKey(key: String): Boolean {
        val normalized = key.trim().uppercase()
        val valid = encodedKeys.any { decodeKey(it) == normalized }
        if (valid) {
            context.premiumDataStore.edit { it[UNLOCKED_KEY] = true }
        }
        return valid
    }

    override suspend fun isUnlocked(): Boolean {
        val prefs = context.premiumDataStore.data.first()
        return prefs[UNLOCKED_KEY] ?: false
    }
}
