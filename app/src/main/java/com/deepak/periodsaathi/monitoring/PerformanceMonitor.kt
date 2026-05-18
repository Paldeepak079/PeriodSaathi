package com.deepak.periodsaathi.monitoring

import android.os.Handler
import android.os.Looper

class PerformanceMonitor {

    fun startTrace(traceName: String): Any? = null

    fun stopTrace(trace: Any?) {}

    fun startAnrWatchdog() {
        AnrWatchdog(5000L).start()
    }

    companion object {
        const val TRACE_STARTUP = "app_startup"
        const val TRACE_HOME_RENDER = "home_screen_render"
    }
}

class AnrWatchdog(private val timeout: Long = 5000L) : Thread("anr-watchdog") {
    @Volatile private var tick = 0

    init { isDaemon = true }

    override fun run() {
        val handler = Handler(Looper.getMainLooper())
        while (!isInterrupted) {
            val postedTick = ++tick
            handler.post { tick = postedTick }
            sleep(timeout)
            if (tick != postedTick) {
            }
        }
    }
}
