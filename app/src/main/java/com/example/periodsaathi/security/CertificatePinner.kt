package com.example.periodsaathi.security

object CertificatePinner {

    private const val SUPABASE_DOMAIN = "supabase.co"
    private const val PRIMARY_PIN = "sha256/REPLACE_WITH_REAL_HASH"
    private const val BACKUP_PIN = "sha256/BACKUP_PLACEHOLDER"

    fun isPinningEnabled(isDebug: Boolean): Boolean = !isDebug

    fun getPinnerBuilder(): Any? {
        return null
    }

    const val PINNED_DOMAINS = SUPABASE_DOMAIN
    const val PRIMARY_PIN_HASH = PRIMARY_PIN
    const val BACKUP_PIN_HASH = BACKUP_PIN
}