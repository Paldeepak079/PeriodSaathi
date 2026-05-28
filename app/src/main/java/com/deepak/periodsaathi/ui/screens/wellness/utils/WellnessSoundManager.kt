package com.deepak.periodsaathi.wellness.ui.wellness.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.sin

class WellnessSoundManager(private val context: Context) {
    private val TAG = "WellnessSoundManager"
    private var audioTrack: AudioTrack? = null
    private val isPlaying = AtomicBoolean(false)
    private var synthThread: Thread? = null
    private var volume = 0.5f

    /**
     * Start playing synthesized peaceful binaural healing frequencies offline.
     * Generates a 200Hz carrier wave with a subtle 6Hz brainwave theta pulse.
     */
    fun playZenMusic() {
        if (isPlaying.get()) return
        isPlaying.set(true)

        synthThread = Thread {
            val sampleRate = 44100
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            try {
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.setVolume(volume)
                audioTrack?.play()

                val buffer = ShortArray(1024)
                var angle = 0.0
                val frequency = 220.0 // Peace frequency A3
                val modFrequency = 6.0  // Theta brainwave relaxation modulation

                while (isPlaying.get()) {
                    for (i in buffer.indices) {
                        // Modulate amplitude with theta frequency to create a breathing sound pulse
                        val lfo = 0.5 + 0.5 * sin(2.0 * Math.PI * modFrequency * angle / sampleRate)
                        val sample = sin(2.0 * Math.PI * frequency * angle / sampleRate) * 32767.0 * lfo * 0.3
                        buffer[i] = sample.toInt().toShort()
                        angle += 1.0
                    }
                    audioTrack?.write(buffer, 0, buffer.size)
                }

                audioTrack?.stop()
                audioTrack?.release()
                audioTrack = null
            } catch (e: Exception) {
                Log.e(TAG, "Audio Track synthesizer initialization or writing failed", e)
            }
        }.apply {
            name = "ZenAudioSynth"
            start()
        }
    }

    /**
     * Pause the peaceful music.
     */
    fun pauseZenMusic() {
        isPlaying.set(false)
        synthThread?.interrupt()
        synthThread = null
    }

    /**
     * Set the volume level (0.0f to 1.0f).
     */
    fun setVolume(vol: Float) {
        volume = vol.coerceIn(0f, 1f)
        audioTrack?.setVolume(volume)
    }

    /**
     * Fully releases audio resources.
     */
    fun release() {
        pauseZenMusic()
    }
}
