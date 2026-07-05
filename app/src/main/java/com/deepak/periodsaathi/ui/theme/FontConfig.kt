package com.deepak.periodsaathi.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

enum class FontOption(
    val displayName: String,
    val resName: String,
    val previewText: String,
    val category: FontCategory
) {
    POPPINS("Poppins", "poppins", "Aa - The quick brown fox jumps over the lazy dog.", FontCategory.ELEGANT),
    NUNITO("Nunito", "nunito", "Aa - The quick brown fox jumps over the lazy dog.", FontCategory.SOFT),
    QUICKSAND("Quicksand", "quicksand", "Aa - The quick brown fox jumps over the lazy dog.", FontCategory.FRIENDLY),
    INTER("Inter", "inter", "Aa - The quick brown fox jumps over the lazy dog.", FontCategory.PROFESSIONAL),
    DM_SANS("DM Sans", "dm_sans", "Aa - The quick brown fox jumps over the lazy dog.", FontCategory.MINIMAL),
    MONTSERRAT("Montserrat", "montserrat", "Aa - The quick brown fox jumps over the lazy dog.", FontCategory.BOLD_CUTE),
    PLAYFAIR_DISPLAY("Playfair Display", "playfair_display", "Aa - The quick brown fox jumps over the lazy dog.", FontCategory.PREMIUM),
    MERRIWEATHER("Merriweather", "merriweather", "Aa - The quick brown fox jumps over the lazy dog.", FontCategory.COZY),
    OUTFIT("Outfit", "outfit", "Aa - The quick brown fox jumps over the lazy dog.", FontCategory.PLAYFUL),
    LEXEND("Lexend", "lexend", "Aa - The quick brown fox jumps over the lazy dog.", FontCategory.CUTE);

    companion object {
        val DEFAULT = POPPINS
    }
}

enum class FontCategory(val displayName: String, val emoji: String) {
    CUTE("Cute", "🌸"),
    SOFT("Soft", "🫧"),
    ELEGANT("Elegant", "✨"),
    PREMIUM("Premium", "💎"),
    MINIMAL("Minimal", "◻️"),
    FRIENDLY("Friendly", "😊"),
    PROFESSIONAL("Professional", "💼"),
    CURSIVE("Cursive", "🖋️"),
    PLAYFUL("Playful", "🎈"),
    BOLD_CUTE("Bold Cute", "💖"),
    COZY("Cozy", "🧸")
}

@Composable
fun rememberFontFamily(fontOption: FontOption): FontFamily {
    return remember(fontOption) {
        val resId = try {
            com.deepak.periodsaathi.R.font::class.java
                .getField(fontOption.resName)
                .getInt(null)
        } catch (_: Exception) { 0 }

        if (resId != 0) {
            FontFamily(
                Font(resId, weight = FontWeight.Normal),
                Font(resId, weight = FontWeight.Bold),
                Font(resId, weight = FontWeight.SemiBold)
            )
        } else {
            FontFamily.Default
        }
    }
}
