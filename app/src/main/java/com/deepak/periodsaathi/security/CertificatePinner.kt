package com.deepak.periodsaathi.security

object CertificatePinner {

    private const val SUPABASE_DOMAIN = "supabase.co"
    private const val PRIMARY_PIN = "sha256/JU3XlQjzoVF9cJ0zUQx8K5Y5z5z5z5z5z5z5z5z5z5z5z5z5z5z5z5z5z5z5z5z5z5z5z5"
    private const val BACKUP_PIN = "sha256/YLh1dUR9y6Kja30RrAn7JKn4o4o4o4o4o4o4o4o4o4o4o4o4o4o4o4o4o4o4o4o4o4o"

    fun isPinningEnabled(isDebug: Boolean): Boolean = !isDebug

    fun getPinnerBuilder(): Any? = null

    const val PINNED_DOMAINS = SUPABASE_DOMAIN
    const val PRIMARY_PIN_HASH = PRIMARY_PIN
    const val BACKUP_PIN_HASH = BACKUP_PIN
}
