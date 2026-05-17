package com.example.periodsaathi.ui.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

enum class HapticType { LIGHT, MEDIUM, HEAVY, SUCCESS, ERROR }

fun performHaptic(context: Context, type: HapticType) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vm.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    val effect = when (type) {
        HapticType.LIGHT -> VibrationEffect.createOneShot(30, 80)
        HapticType.MEDIUM -> VibrationEffect.createOneShot(60, 150)
        HapticType.HEAVY -> VibrationEffect.createOneShot(100, 255)
        HapticType.SUCCESS -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            } else {
                VibrationEffect.createWaveform(longArrayOf(0, 30, 50, 30), intArrayOf(0, 200, 0, 150), -1)
            }
        }
        HapticType.ERROR -> VibrationEffect.createWaveform(
            longArrayOf(0, 50, 30, 50, 30, 50),
            intArrayOf(0, 255, 0, 255, 0, 255),
            -1
        )
    }
    vibrator.vibrate(effect)
}
