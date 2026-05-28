package com.deepak.periodsaathi.ui.screens.partner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*

@Composable
fun PartnerQuizScreen(
    viewModel: PartnerViewModel,
    onBack: () -> Unit,
    onNavigateToQuizDetail: (String) -> Unit
) {
    val quizAnswers by viewModel.quizAnswers.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Surface,
                        WarmCream,
                        SecondaryContainer.copy(alpha = 0.2f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                        .border(1.dp, GlassBorder, CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("⬅️", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Couples Quizzes 💑",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepRose
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Take lighthearted quizzes together to improve mutual communication, texting harmony, and understanding.",
                fontSize = 13.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Quizzes Grid/List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                viewModel.quizzesList.forEach { quiz ->
                    // Calculate quiz completion & match status
                    val answersForQuiz = quizAnswers.filter { it.quizId == quiz.id }
                    val primaryAnswers = answersForQuiz.filter { it.answeredBy == "PRIMARY" }
                    val partnerAnswers = answersForQuiz.filter { it.answeredBy == "PARTNER" }
                    
                    val isCompletedByMe = primaryAnswers.size == quiz.questions.size
                    val isCompletedByPartner = partnerAnswers.size == quiz.questions.size

                    val statusBadge = when {
                        isCompletedByMe && isCompletedByPartner -> {
                            var matches = 0
                            quiz.questions.forEach { q ->
                                val primAns = primaryAnswers.firstOrNull { it.questionId == q.id }?.answerIndex
                                val partAns = partnerAnswers.firstOrNull { it.questionId == q.id }?.answerIndex
                                if (primAns == partAns && primAns != null) matches++
                            }
                            val matchPercentage = (matches.toFloat() / quiz.questions.size * 100).toInt()
                            "$matchPercentage% Synced 🎉"
                        }
                        isCompletedByMe -> "Waiting for partner ⏳"
                        isCompletedByPartner -> "Partner answered! Answer now 🔥"
                        else -> "Not started 💬"
                    }

                    val badgeColor = when {
                        statusBadge.contains("Synced") -> MintGreen.copy(alpha = 0.8f)
                        statusBadge.contains("Waiting") -> ButterYellow.copy(alpha = 0.8f)
                        statusBadge.contains("Partner answered") -> BlushPink.copy(alpha = 0.8f)
                        else -> GlassWhite
                    }

                    BentoQuizCard(
                        quiz = quiz,
                        statusText = statusBadge,
                        badgeColor = badgeColor,
                        onClick = { onNavigateToQuizDetail(quiz.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun BentoQuizCard(
    quiz: Quiz,
    statusText: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(BlushPink.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = quiz.emoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quiz.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepRose
                )
                Text(
                    text = quiz.subtitle,
                    fontSize = 12.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Status badge tag
                Box(
                    modifier = Modifier
                        .background(badgeColor, RoundedCornerShape(8.dp))
                        .border(1.dp, GlassBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                }
            }

            Text("➔", fontSize = 16.sp, color = Outline, modifier = Modifier.padding(start = 8.dp))
        }
    }
}
