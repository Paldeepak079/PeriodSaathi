package com.deepak.periodsaathi.monitoring

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.deepak.periodsaathi.BuildConfig
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BugReportManager @Inject constructor(
    private val context: Context,
    private val crashlyticsManager: CrashlyticsManager
) {

    private var onBugReportTriggered: (() -> Unit)? = null

    fun setOnBugReportTriggered(callback: () -> Unit) {
        onBugReportTriggered = callback
    }

    fun triggerBugReport() {
        onBugReportTriggered?.invoke()
    }

    fun sendBugReport(
        activity: Activity,
        includeScreenshot: Boolean = false,
        screenshot: Bitmap? = null
    ) {
        val report = buildReportString()
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("bugreport@periodsaathi.app"))
            putExtra(Intent.EXTRA_SUBJECT, "Period Saathi Bug Report — v${BuildConfig.VERSION_NAME}")
            putExtra(Intent.EXTRA_TEXT, report)
        }

        if (includeScreenshot && screenshot != null) {
            val screenshotUri = saveScreenshotToCache(screenshot)
            emailIntent.apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, screenshotUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        activity.startActivity(Intent.createChooser(emailIntent, "Send Bug Report"))
    }

    private fun buildReportString(): String {
        return buildString {
            appendLine("=== PERIOD SAATHI BUG REPORT ===")
            appendLine("Response SLA: Within 48 hours")
            appendLine()
            appendLine("--- App Info ---")
            appendLine("Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            appendLine("Debug: ${BuildConfig.DEBUG}")
            appendLine()
            appendLine("--- Device Info ---")
            appendLine("Android: ${android.os.Build.VERSION.RELEASE}")
            appendLine("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
            appendLine()
            appendLine("--- User Description ---")
            appendLine("[Please describe what happened here]")
            appendLine()
            appendLine("--- Steps to Reproduce ---")
            appendLine("[Please describe the steps to reproduce]")
        }
    }

    private fun saveScreenshotToCache(bitmap: Bitmap): android.net.Uri {
        val file = File(context.cacheDir, "bug_report_screenshot.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 90, it) }
        return androidx.core.content.FileProvider.getUriForFile(
            context, "${context.packageName}.provider", file
        )
    }
}
