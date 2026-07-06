package com.deepak.periodsaathi.ui.screens.partner

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.data.model.QuizAnswerEntity
import com.deepak.periodsaathi.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun PartnerQuizDetailScreen(
    quizId: String,
    viewModel: PartnerViewModel,
    onBack: () -> Unit
) {
    val quiz = viewModel.quizzesList.firstOrNull { it.id == quizId }
    val quizAnswers by viewModel.quizAnswers.collectAsStateWithLifecycle()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()

    val isPrimary = when (val state = connectionState) {
        is ConnectionUIState.Connected -> state.isPrimary
        else -> true
    }

    if (quiz == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Quiz not found")
        }
        return
    }

    // Filter local answers
    val answersForThisQuiz = quizAnswers.filter { it.quizId == quizId }
    val myRole = if (isPrimary) "PRIMARY" else "PARTNER"
    val partnerRole = if (isPrimary) "PARTNER" else "PRIMARY"
    
    val myAnswers = answersForThisQuiz.filter { it.answeredBy == myRole }
    val partnerAnswers = answersForThisQuiz.filter { it.answeredBy == partnerRole }

    // Track active question index
    var currentQuestionIndex by remember(myAnswers.size) {
        mutableStateOf(myAnswers.size.coerceAtMost(quiz.questions.size - 1))
    }

    val isQuizFinished = myAnswers.size == quiz.questions.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Surface,
                        WarmCream,
                        BlushPink.copy(alpha = 0.2f)
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
            // Header
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
                    text = quiz.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepRose
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!isQuizFinished) {
                // Progress indicator
                Text(
                    text = "Question ${currentQuestionIndex + 1} of ${quiz.questions.size}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Outline
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // ProgressBar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(GlassWhite, CircleShape)
                ) {
                    val progress = (currentQuestionIndex.toFloat() / quiz.questions.size)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(DeepRose, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                val question = quiz.questions[currentQuestionIndex]

                // Slide animation container for active question card
                AnimatedContent(
                    targetState = question,
                    transitionSpec = {
                        slideInHorizontally(animationSpec = spring()) togetherWith slideOutHorizontally(animationSpec = spring())
                    },
                    label = "questionSlide"
                ) { targetQuestion ->
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = targetQuestion.text,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(28.dp))

                            // Options layout list
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                targetQuestion.options.forEachIndexed { optIndex, optionText ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(GlassWhite)
                                            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                                            .clickable {
                                                viewModel.submitQuizAnswer(
                                                    quizId = quizId,
                                                    questionId = targetQuestion.id,
                                                    answerIndex = optIndex,
                                                    isPrimary = isPrimary
                                                )
                                            }
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = optionText,
                                            fontSize = 14.sp,
                                            color = OnSurface,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Results comparison reveal
                QuizResultsComparisonView(
                    quiz = quiz,
                    myAnswers = myAnswers,
                    partnerAnswers = partnerAnswers,
                    onBack = onBack
                )
            }
        }
    }
}

@Composable
fun QuizResultsComparisonView(
    quiz: Quiz,
    myAnswers: List<QuizAnswerEntity>,
    partnerAnswers: List<QuizAnswerEntity>,
    onBack: () -> Unit
) {
    var matches = 0
    quiz.questions.forEach { q ->
        val myAns = myAnswers.firstOrNull { it.questionId == q.id }?.answerIndex
        val partAns = partnerAnswers.firstOrNull { it.questionId == q.id }?.answerIndex
        if (myAns == partAns && myAns != null) matches++
    }

    val matchPercentage = (matches.toFloat() / quiz.questions.size * 100).toInt()

    // Animate match score sweep
    val animatedPercentage = remember { Animatable(0f) }
    LaunchedEffect(matchPercentage) {
        animatedPercentage.animateTo(
            targetValue = matchPercentage.toFloat(),
            animationSpec = tween(1500, easing = EaseOutBack)
        )
    }

    // Confetti particles for high score
    val confettis = remember { mutableStateListOf<ConfettiParticle>() }
    LaunchedEffect(matchPercentage) {
        if (matchPercentage >= 66) {
            for (i in 1..35) {
                confettis.add(
                    ConfettiParticle(
                        x = 0.5f,
                        y = 0.3f,
                        color = listOf(BlushPink, MintGreen, SoftLavender, ButterYellow).random(),
                        angle = Random.nextFloat() * 360f,
                        speed = Random.nextFloat() * 12f + 4f,
                        size = Random.nextFloat() * 8f + 5f,
                        alpha = 1f
                    )
                )
            }
        }
    }

    // Physics of Confetti
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(confettis.size) {
        if (confettis.isNotEmpty()) {
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(2200, easing = LinearOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Confetti rendering
        Canvas(modifier = Modifier.fillMaxWidth().height(250.dp)) {
            confettis.forEach { p ->
                val progress = animatedProgress.value
                val rad = Math.toRadians(p.angle.toDouble())
                val xDist = Math.cos(rad).toFloat() * p.speed * progress * 20f
                val yDist = Math.sin(rad).toFloat() * p.speed * progress * 20f + (progress * progress * 130f)
                
                val currentX = (p.x * size.width) + xDist
                val currentY = (p.y * size.height) + yDist
                
                if (currentY < size.height) {
                    drawRect(
                        color = p.color.copy(alpha = 1f - progress),
                        topLeft = androidx.compose.ui.geometry.Offset(currentX, currentY),
                        size = androidx.compose.ui.geometry.Size(p.size, p.size)
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = "Quiz Finished! 🎉",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepRose
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Bouncy Match Percentage circle
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(BlushPink.copy(alpha = 0.4f), Color.Transparent)))
                            .border(2.dp, GlassBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${animatedPercentage.value.toInt()}%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepRose
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Matching Badges
                    val (badgeTitle, badgeEmoji) = when {
                        matchPercentage == 100 -> Pair("Communication Kings", "👑")
                        matchPercentage >= 66 -> Pair("Soulmates Synced", "💕")
                        else -> Pair("Texting Apprentices", "📚")
                    }

                    Box(
                        modifier = Modifier
                            .background(MintGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$badgeTitle $badgeEmoji",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Results Comparison:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Outline,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    // Detailed question breakdown
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        quiz.questions.forEachIndexed { index, q ->
                            val myIdx = myAnswers.firstOrNull { it.questionId == q.id }?.answerIndex
                            val partIdx = partnerAnswers.firstOrNull { it.questionId == q.id }?.answerIndex

                            val isMatch = myIdx == partIdx && myIdx != null

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isMatch) MintGreen.copy(alpha = 0.2f) else BlushPink.copy(alpha = 0.1f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .border(0.5.dp, GlassBorder, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${index + 1}. ${q.text}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val myChoice = q.options.getOrNull(myIdx ?: 0) ?: ""
                                    val partChoice = q.options.getOrNull(partIdx ?: 0) ?: ""
                                    Text("You chose: \"$myChoice\"", fontSize = 11.sp, color = Outline)
                                    Text("Partner chose: \"$partChoice\"", fontSize = 11.sp, color = Outline)
                                }
                                Text(
                                    text = if (isMatch) "✅" else "❌",
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepRose),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Back to Quizzes List 💑", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
