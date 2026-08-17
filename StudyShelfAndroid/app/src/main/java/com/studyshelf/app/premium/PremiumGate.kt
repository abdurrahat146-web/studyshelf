package com.studyshelf.app.premium

/**
 * Tier-specific behavior lives behind this interface. The `free` and
 * `premium` product flavors each provide their own implementation in
 * app/src/free/.../PremiumGateImpl.kt and app/src/premium/.../PremiumGateImpl.kt.
 *
 * This keeps all shared logic (auth, sharing, notifications, routines) in
 * app/src/main/ untouched by tier differences — only this one seam changes
 * between builds.
 */
interface PremiumGate {

    /** True for the premium flavor; false for free (before any unlock-key logic). */
    val isPremiumBuild: Boolean

    /** Daily AI credit allowance. -1 means unlimited. */
    val dailyAiCredits: Int

    /** Whether ads should be shown anywhere in the UI. */
    val showAds: Boolean

    /**
     * Free flavor: validates an unlock key purchased via bKash and persists
     * the unlock so [isUnlocked] returns true from then on.
     * Premium flavor: always returns true immediately (already the premium app).
     */
    suspend fun redeemKey(key: String): Boolean

    /** Whether this install currently has premium features unlocked. */
    suspend fun isUnlocked(): Boolean
}
