package com.deepak.periodsaathi.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.theme.DeepRose
import com.deepak.periodsaathi.ui.theme.Primary
import kotlinx.coroutines.delay

@Composable
fun MascotReactionGuide(
    emotion: MascotEmotion,
    size: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    var showBubble by remember { mutableStateOf(true) }

    LaunchedEffect(emotion) {
        showBubble = true
        delay(4000)
        showBubble = false
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = showBubble,
            enter = slideInVertically { it / 2 } + fadeIn(),
            exit = fadeOut()
        ) {
            SpeechBubble(
                text = encouragementText(emotion),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        SaathiMascot(
            emotion = emotion,
            size = size
        )
    }
}

@Composable
private fun SpeechBubble(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DeepRose.copy(alpha = 0.1f),
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 260.dp)
        ) {
            Text(
                text = text,
                color = Primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}

private fun encouragementText(emotion: MascotEmotion): String = when (emotion) {
    MascotEmotion.HAPPY -> "Yay! Let's do this together!"
    MascotEmotion.LISTENING -> "I'm listening... tell me more!"
    MascotEmotion.EXCITED -> "Almost there! You're doing great!"
    MascotEmotion.SLEEPING -> "Rest is important too!"
    MascotEmotion.SAD -> "I'm here for you 💕"
    MascotEmotion.PAIN -> "Let's figure this out together"
    MascotEmotion.HUGGING -> "You've got this! 🤗"
}
