package com.deepak.periodsaathi.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.data.model.OnboardingResponse
import com.deepak.periodsaathi.ui.components.*
import com.deepak.periodsaathi.ui.theme.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onComplete: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { state.totalSteps })
    val haptic = LocalHapticFeedback.current

    // ── KEY FIX: sync pager with ViewModel step ──────────────────────────
    // Without this, goToNextStep() updates the state but the HorizontalPager
    // never scrolls — making the Continue button appear dead.
    LaunchedEffect(state.currentStep) {
        if (pagerState.currentPage != state.currentStep) {
            pagerState.animateScrollToPage(
                page = state.currentStep,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    val blobColor by animateColorAsState(
        targetValue = when (state.mascotEmotion) {
            MascotEmotion.HAPPY -> BlushPink.copy(alpha = 0.3f)
            MascotEmotion.LISTENING -> SoftLavender.copy(alpha = 0.3f)
            MascotEmotion.EXCITED -> ButterYellow.copy(alpha = 0.3f)
            else -> Primary.copy(alpha = 0.15f)
        },
        animationSpec = tween(600), label = "blob"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Box(Modifier.align(Alignment.TopStart).offset((-60).dp, (-60).dp).size(320.dp)
            .background(Brush.radialGradient(listOf(blobColor, Color.Transparent)), CircleShape)
            .blur(50.dp))
        Box(Modifier.align(Alignment.BottomEnd).offset(60.dp, 60.dp).size(280.dp)
            .background(Brush.radialGradient(listOf(blobColor, Color.Transparent)), CircleShape)
            .blur(50.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = state.currentStep > 0 && !state.isSummaryStep) {
                    IconButton(onClick = { viewModel.goToPreviousStep() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Primary)
                    }
                }
                if (state.currentStep == 0) Spacer(Modifier.width(48.dp))

                if (!state.isSummaryStep) {
                    LinearProgressIndicator(
                        progress = { (state.currentStep + 1).toFloat() / state.totalSteps },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Primary,
                        trackColor = OutlineVariant
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${state.currentStep + 1}/${state.totalSteps}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }

                AnimatedVisibility(visible = !state.isSummaryStep && state.currentStep < state.totalSteps - 2) {
                    TextButton(onClick = { viewModel.completeOnboarding(onDone = onComplete) }) {
                        Text("Skip", color = Primary.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelLarge)
                    }
                }
                if (state.isSummaryStep) Spacer(Modifier.width(48.dp))
            }

            if (!state.isSummaryStep) {
                CycleInteractionCanvas(
                    biologicalState = state.biologicalState,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                userScrollEnabled = false
            ) { page ->
                if (page < state.questions.size) {
                    QuestionPageContent(
                        question = state.questions[page],
                        state = state,
                        viewModel = viewModel
                    )
                } else {
                    SummaryPage(
                        state = state,
                        onComplete = {
                            viewModel.completeOnboarding(onDone = onComplete)
                        }
                    )
                }
            }

            if (!state.isSummaryStep) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp)
                ) {
                    PrimaryButton(
                        text = if (state.currentStep < state.totalSteps - 1) "Continue" else "Start Your Journey",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.goToNextStep()
                        },
                        enabled = state.canGoNext && !state.isSaving,
                        isLoading = state.isSaving
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionPageContent(
    question: OnboardingQuestion,
    state: OnboardingUiState,
    viewModel: OnboardingViewModel
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        when (question) {
            is OnboardingQuestion.MultiChoice -> {
                MultiChoiceContent(question = question, state = state, viewModel = viewModel)
            }
            is OnboardingQuestion.SingleChoice -> {
                if (question.id == "last_period") {
                    DatePickerContent(question = question, state = state, viewModel = viewModel)
                } else {
                    SingleChoiceContent(question = question, state = state, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun MultiChoiceContent(
    question: OnboardingQuestion.MultiChoice,
    state: OnboardingUiState,
    viewModel: OnboardingViewModel
) {
    QuestionCard(
        title = question.title,
        subtitle = question.subtitle,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            question.options.forEach { option ->
                OptionPill(
                    text = option,
                    isSelected = option in state.selectedGoals,
                    onClick = { viewModel.selectMultiOption(option) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SingleChoiceContent(
    question: OnboardingQuestion.SingleChoice,
    state: OnboardingUiState,
    viewModel: OnboardingViewModel
) {
    val currentSelection = when (question.id) {
        "birth_control" -> state.selectedBirthControl
        "cycle_length" -> state.selectedCycleLength
        "period_length" -> state.selectedPeriodLength
        else -> null
    }

    QuestionCard(
        title = question.title,
        subtitle = question.subtitle,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            question.options.forEach { option ->
                OptionPill(
                    text = option,
                    isSelected = option == currentSelection,
                    onClick = { viewModel.selectSingleOption(question.id, option) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DatePickerContent(
    question: OnboardingQuestion.SingleChoice,
    state: OnboardingUiState,
    viewModel: OnboardingViewModel
) {
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    val currentDate = try {
        LocalDate.parse(state.lastPeriodDate)
    } catch (_: Exception) { LocalDate.now().minusDays(14) }

    QuestionCard(
        title = question.title,
        subtitle = question.subtitle,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = currentDate.format(formatter),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(onClick = {
                    viewModel.setLastPeriodDate(currentDate.minusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE))
                }) { Text("-7 days") }
                OutlinedButton(onClick = {
                    viewModel.setLastPeriodDate(currentDate.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                }) { Text("-1 day") }
                OutlinedButton(onClick = {
                    val next = currentDate.plusDays(1)
                    if (!next.isAfter(LocalDate.now())) {
                        viewModel.setLastPeriodDate(next.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    }
                }) { Text("+1 day") }
            }

            Spacer(Modifier.height(16.dp))

            val shortcuts = listOf(
                "Today" to 0L, "3 days ago" to 3L, "1 week ago" to 7L,
                "2 weeks ago" to 14L, "3 weeks ago" to 21L
            )
            shortcuts.forEach { (label, daysAgo) ->
                val date = LocalDate.now().minusDays(daysAgo)
                val selected = currentDate == date
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) PrimaryContainer else MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                    onClick = {
                        viewModel.setLastPeriodDate(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, style = MaterialTheme.typography.bodyMedium,
                            color = if (selected) Primary else MaterialTheme.colorScheme.onSurface)
                        if (selected) Icon(Icons.Rounded.Check, null, tint = Primary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryPage(
    state: OnboardingUiState,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(1f))

        SaathiMascot(
            emotion = MascotEmotion.EXCITED,
            size = 140.dp
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "You're all set!",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold, fontSize = 28.sp
            ),
            color = Primary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Saathi has learned about your cycle preferences. Let's start your wellness journey!",
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )

        Spacer(Modifier.height(32.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Your Preferences",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Primary
                )
                state.selectedGoals.forEach { goal ->
                    SummaryRow(emoji = if (goal.contains("Cycle")) "🌸" else if (goal.contains("Pregnant")) "🤰" else "👶", text = goal)
                }
                state.selectedCycleLength?.let { SummaryRow(emoji = "📅", text = "Cycle: $it") }
                state.selectedPeriodLength?.let { SummaryRow(emoji = "🩸", text = "Period: $it") }
            }
        }

        Spacer(Modifier.weight(1f))

        PrimaryButton(
            text = "Start Your Journey",
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun SummaryRow(emoji: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = emoji, fontSize = 18.sp)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
        )
    }
}
