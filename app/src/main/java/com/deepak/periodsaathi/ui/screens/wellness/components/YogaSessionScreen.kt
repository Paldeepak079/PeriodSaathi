package com.deepak.periodsaathi.ui.screens.wellness.components

import android.net.Uri
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.PrimaryButton
import com.deepak.periodsaathi.ui.theme.Primary
import com.deepak.periodsaathi.ui.screens.wellness.ExerciseItem

import kotlinx.coroutines.delay
import java.util.Locale

private val Mint = Color(0xFFB8F0DC)

@OptIn(UnstableApi::class)
@Composable
fun YogaSessionScreen(
    exercise: ExerciseItem,
    onClose: () -> Unit,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val TAG = "YogaSession"

    // 1. Setup ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
        }
    }

    // Load local raw video resource dynamically
    LaunchedEffect(exercise) {
        try {
            val rawResId = context.resources.getIdentifier(
                exercise.animationPath,
                "raw",
                context.packageName
            )
            if (rawResId != 0) {
                val videoUri = Uri.parse("android.resource://${context.packageName}/$rawResId")
                val mediaItem = MediaItem.fromUri(videoUri)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load raw video resource", e)
        }
    }

    // 2. Setup TextToSpeech
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var ttsReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                ttsReady = true
                
                // Speak intro
                tts?.speak("Starting ${exercise.name}. Follow the steps shown.", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    // Clean up players
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            tts?.stop()
            tts?.shutdown()
        }
    }

    // Clocks and timers
    var timeRemaining by remember { mutableStateOf(exercise.durationMinutes * 60) }
    var isPlaying by remember { mutableStateOf(true) }

    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (timeRemaining > 0 && isPlaying) {
            delay(1000)
            timeRemaining--
        }
        if (timeRemaining <= 0) {
            tts?.speak("Congratulations! Workout complete.", TextToSpeech.QUEUE_FLUSH, null, null)
            delay(1000)
            onComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0A14)) // Cinematic dark theme
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                Text(
                    text = exercise.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Voice assist toggle
                IconButton(
                    onClick = {
                        if (ttsReady) {
                            tts?.speak("Take deep slow breaths through your nose.", TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    }
                ) {
                    Text("🗣️", fontSize = 20.sp)
                }
            }

            // ExoPlayer Demonstration Screen
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .border(1.5.dp, Color.White.copy(0.2f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Steps Checklist (Speakable when clicked)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(exercise.steps) { index, step ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (ttsReady) {
                                    tts?.speak(step, TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                            },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Primary.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = step,
                                fontSize = 13.sp,
                                color = Color.White.copy(0.9f),
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Clocks & Play/Pause controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val mins = timeRemaining / 60
                val secs = timeRemaining % 60
                Text(
                    text = String.format("%02d:%02d remaining", mins, secs),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { isPlaying = !isPlaying },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlaying) Color(0xFFFFB5C8) else Color(0xFFB8F0DC)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isPlaying) "Pause" else "Resume",
                            color = Color(0xFF0C0A14),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Skip to Finish", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
