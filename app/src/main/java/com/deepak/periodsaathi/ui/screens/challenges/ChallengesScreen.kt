package com.deepak.periodsaathi.ui.screens.challenges

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*

@Composable
fun ChallengesScreen(
    viewModel: ChallengesViewModel = hiltViewModel()
) {
    val activeChallenges by viewModel.activeChallenges.collectAsState()
    val completedChallenges by viewModel.completedChallenges.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Text(text = "Challenges", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurface)

        Spacer(modifier = Modifier.height(24.dp))

        // Active challenges horizontal scroll
        Text(text = "Active Challenges", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(activeChallenges) { challenge ->
                ActiveChallengeCard(
                    challenge = challenge,
                    onTap = { viewModel.checkDailyProgress(challenge.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Completed challenges
        Text(text = "Completed", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(completedChallenges) { challenge ->
                CompletedChallengeCard(challenge = challenge)
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun ActiveChallengeCard(challenge: Challenge, onTap: () -> Unit) {
    val animatedProgress by animateFloatAsState(
        targetValue = challenge.progress,
        animationSpec = tween(1000),
        label = "progress"
    )

    val bounceScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "bounce"
    )

    GlassCard(
        modifier = Modifier
            .width(200.dp)
            .scale(bounceScale)
            .clickable { onTap() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = challenge.emoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = challenge.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Text(text = challenge.duration, fontSize = 10.sp, color = OnSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = challenge.description, fontSize = 11.sp, color = OnSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = BlushPink,
                trackColor = OnSurfaceVariant.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${(animatedProgress * 100).toInt()}% complete", fontSize = 10.sp, color = OnSurfaceVariant)
                Text(text = "+${challenge.pointsReward} pts", fontSize = 10.sp, color = WarmGold, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CompletedChallengeCard(challenge: Challenge) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = challenge.emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = challenge.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                Text(text = challenge.description, fontSize = 12.sp, color = OnSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "\uD83C\uDFC6", fontSize = 20.sp)
                Text(text = "+${challenge.pointsReward}", color = WarmGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

