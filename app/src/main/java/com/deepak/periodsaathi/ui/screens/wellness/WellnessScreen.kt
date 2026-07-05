package com.deepak.periodsaathi.ui.screens.wellness

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.PrimaryButton
import com.deepak.periodsaathi.ui.components.springClickable
import com.deepak.periodsaathi.ui.theme.*
import com.deepak.periodsaathi.ui.screens.wellness.components.*
import com.deepak.periodsaathi.wellness.data.local.entities.SolutionEntity

private val Mint = Color(0xFFB8F0DC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellnessScreen(
    viewModel: WellnessViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Sub-screen overlays
    var activeSubScreen by remember { mutableStateOf<String?>(null) } // "breathing", "yoga" or null
    var selectedExerciseForYoga by remember { mutableStateOf<ExerciseItem?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF5F7), // Blush Cream
                        Color(0xFFF9F3FF), // Soft Lavender Cream
                        Color(0xFFF5FCF9)  // Mint Cream
                    )
                )
            )
    ) {
        // Blurred premium background blobs for depth
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-80).dp, y = 50.dp)
                .size(300.dp)
                .background(Brush.radialGradient(listOf(Color(0x30FFB5C8), Color.Transparent)), CircleShape)
                .blur(80.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .size(280.dp)
                .background(Brush.radialGradient(listOf(Color(0x24C9B8FF), Color.Transparent)), CircleShape)
                .blur(80.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Premium Top Bar with Petal Coin Balance
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Wellness Hub",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary
                        )
                        Text(
                            text = "Self-care, recovery & healing",
                            fontSize = 13.sp,
                            color = OnSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }

                    // Petal coin balance
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.65f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.8f)),
                        modifier = Modifier.clip(CircleShape)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌸",
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${state.totalCoins}",
                                color = Color(0xFFE91E63),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            // 2. Interactive Score Card & Mascot reactions
            item {
                WellnessScoreCard(
                    score = state.dailyScore,
                    streak = state.currentStreak,
                    onConfigureClick = { viewModel.showCarePopup(true) }
                )
            }

            // 3. Daily Quests checklist
            item {
                Text(
                    text = "Daily Quests 🌸",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        QuestItem(
                            title = "Stay Hydrated (8 Glasses)",
                            emoji = "💧",
                            isCompleted = state.quests.drinkWater
                        )
                        QuestItem(
                            title = "Complete Yoga Stretch Flow",
                            emoji = "🧘",
                            isCompleted = state.quests.doYoga
                        )
                        QuestItem(
                            title = "Do a Breathing Meditation",
                            emoji = "💨",
                            isCompleted = state.quests.meditateBreathing
                        )
                        QuestItem(
                            title = "Log a Nutritious Sync-Meal",
                            emoji = "🥗",
                            isCompleted = state.quests.logHealthyMeal
                        )
                        QuestItem(
                            title = "Rest Well (7+ Hours)",
                            emoji = "🌙",
                            isCompleted = state.quests.logSleep
                        )
                        QuestItem(
                            title = "Record Your Mental Mood",
                            emoji = "🧠",
                            isCompleted = state.quests.trackMood
                        )
                    }
                }
            }

            // 4. Personalized Suggestions & AI Remedies (Offline Engine)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Recovery Plan ✨",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SoftCoral.copy(alpha = 0.2f),
                            modifier = Modifier.clickable { viewModel.showSeverityRecommendations() }
                        ) {
                            Text(
                                text = "Pain Guide",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftCoral,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Lavender.copy(alpha = 0.2f),
                            modifier = Modifier.clickable { viewModel.showCarePopup(true) }
                        ) {
                            Text(
                                text = "Log Symptoms",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Symptom Quick-Action Buttons
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(
                        listOf(
                            "Cramps" to "🩸", "Bloating" to "🫧",
                            "Headache" to "🤕", "Fatigue" to "😴"
                        )
                    ) { (symptom, emoji) ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BlushPink.copy(alpha = 0.4f)),
                            modifier = Modifier.clickable {
                                viewModel.showSolutionsForSymptom(symptom.lowercase())
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(emoji, fontSize = 18.sp)
                                Spacer(Modifier.width(6.dp))
                                Text(symptom, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            }
                        }
                    }
                }
            }

            // Remedies & Suggestions List
            item {
                if (state.remedies.isEmpty()) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Info, null, tint = Primary.copy(0.7f), modifier = Modifier.size(32.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "No active symptoms logged today.",
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Text(
                                "Log your pain and energy levels to unlock specialized remedies and active recovery sessions.",
                                fontSize = 12.sp,
                                color = OnSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            PrimaryButton(
                                text = "Setup Care Plan",
                                onClick = { viewModel.showCarePopup(true) }
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        state.remedies.take(3).forEach { tip ->
                            TipCard(
                                tip = tip,
                                onRead = { viewModel.readTip(tip.id) }
                            )
                        }
                        
                        if (state.remedies.size > 3) {
                            ViewAllButton(title = "Remedies", emoji = "🌿", onClick = { viewModel.showCategoryView("remedy") })
                        }
                        
                        state.exercises.take(2).forEach { exe ->
                            ExerciseCard(
                                exercise = exe,
                                onStartClick = {
                                    selectedExerciseForYoga = exe
                                    activeSubScreen = "yoga"
                                }
                            )
                        }
                        
                        if (state.exercises.size > 2) {
                            ViewAllButton(title = "Exercises", emoji = "🤸", onClick = { viewModel.showCategoryView("exercise") })
                        }
                    }
                }
            }

            // 5. Breathing Therapy Mode Action Banner
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFE8D5F5).copy(alpha = 0.5f),
                                        Color(0xFFFFD1DC).copy(alpha = 0.5f)
                                    )
                                )
                            )
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Breath & Relax",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Primary
                            )
                            Text(
                                text = "3 Min Guided breathing session to relieve spasms and tension.",
                                fontSize = 12.sp,
                                color = OnSurfaceVariant
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { activeSubScreen = "breathing" },
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Start Session (+3 🌸)", fontSize = 12.sp)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("💨", fontSize = 48.sp)
                    }
                }
            }

            // === SUGGESTED FOR YOU ===
            if (state.suggestedSolutions.isNotEmpty()) {
                item {
                    Text(
                        text = "Suggested for You 🌸",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            state.suggestedSolutions.take(3).forEach { sol ->
                                SuggestedSolutionItem(
                                    solution = sol,
                                    onClick = { viewModel.showRecipeCard(sol) }
                                )
                            }
                            if (state.suggestedSolutions.size > 3) {
                                Spacer(Modifier.height(8.dp))
                                ViewAllButton(title = "Suggested", emoji = "✨", onClick = { viewModel.showCategoryView("remedy") })
                            }
                        }
                    }
                }
            }

            // === WELLNESS CATEGORIES (View All previews) ===
            item {
                Text(
                    text = "Wellness Library 📚",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    wellnessCategories.take(4).forEach { cat ->
                        val catSolutions = state.allSolutions.filter { it.category == cat.key }
                        if (catSolutions.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${cat.emoji} ${cat.title}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            }
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(catSolutions.take(3)) { sol ->
                                    PreviewCard(
                                        solution = sol,
                                        onClick = { viewModel.showRecipeCard(sol) }
                                    )
                                }
                            }
                            ViewAllButton(
                                title = "${cat.title}s",
                                emoji = cat.emoji,
                                onClick = { viewModel.showCategoryView(cat.key) }
                            )
                        }
                    }
                }
            }

            // 6. Detailed logs section: Diet, Hydration, Sleep, Mood
            item {
                Text(
                    text = "Health Ledgers 📝",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Beaker / Hydration Tracker
            item {
                HydrationTracker(
                    currentGlasses = state.waterGlasses,
                    waterReminderEnabled = state.waterReminderEnabled,
                    onAddWater = { viewModel.logWater(1) },
                    onAdd500ml = { viewModel.logWater(2) },
                    onToggleReminder = { viewModel.toggleWaterReminder() }
                )
            }

            // Diet recommendation
            item {
                DietTracker(
                    diet = state.diet,
                    customMeals = state.customMeals,
                    onLogMeal = { viewModel.logCustomFood(it) }
                )
            }

            // Sleep logs
            item {
                SleepTracker(
                    sleepHours = state.sleepHours,
                    rating = state.sleepRating,
                    onLogSleep = { hrs, stars -> viewModel.logSleep(hrs, stars) }
                )
            }

            // Mood logs
            item {
                MoodTracker(
                    currentMood = state.mood,
                    onMoodSelect = { viewModel.logMood(it) }
                )
            }

            // PDF report exports
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Download Health Ledger 📝",
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                        Text(
                            text = "Generate a medically detailed PDF report of your wellness and self-care logs for the past 3 months.",
                            fontSize = 12.sp,
                            color = OnSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Button(
                            onClick = { viewModel.exportReportToPDF() },
                            colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Export PDF Report", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- SUB-SCREEN OVERLAYS ---

        // 1. Guided Zen Breathing Overlay
        if (activeSubScreen == "breathing") {
            BreathingSessionScreen(
                onClose = { activeSubScreen = null },
                onComplete = {
                    viewModel.completeBreathingSession()
                    activeSubScreen = null
                }
            )
        }

        // 2. Interactive Yoga Workout Overlay
        if (activeSubScreen == "yoga" && selectedExerciseForYoga != null) {
            YogaSessionScreen(
                exercise = selectedExerciseForYoga!!,
                onClose = { activeSubScreen = null },
                onComplete = {
                    viewModel.completeYogaSession(selectedExerciseForYoga!!.durationMinutes)
                    activeSubScreen = null
                }
            )
        }

        // 3. Personalized Care Selector bottom sheet
        if (state.showCarePopup) {
            PersonalizedCarePopup(
                currentPainType = state.painType,
                currentPainLevel = state.painLevel,
                currentFlowLevel = state.flowLevel,
                currentEnergyLevel = state.energyLevel,
                onDismiss = { viewModel.showCarePopup(false) },
                onSave = { pain, lvl, flow, energy ->
                    viewModel.logPersonalCare(pain, lvl, flow, energy)
                }
            )
        }

        // 4. Coin Reward Sparkling Animation Popup
        if (state.showCoinAnimation) {
            CoinRewardAnimation(
                amount = state.coinEarnedAmount,
                onAnimationComplete = { viewModel.dismissCoinAnimation() }
            )
        }

        // Confetti Celebration
        ConfettiOverlay(
            visible = state.showConfetti,
            onComplete = { viewModel.dismissConfetti() }
        )

        // 5. Problem-Based Solutions Bottom Sheet
        if (state.showSolutionsSheet && state.selectedSymptom.isNotEmpty()) {
            val symptomSolutions = state.allSolutions.filter {
                it.symptomType == state.selectedSymptom || it.symptomType == "general"
            }
            SolutionsBottomSheet(
                symptom = state.selectedSymptom,
                solutions = symptomSolutions,
                onDismiss = { viewModel.dismissSolutionsSheet() },
                onSolutionClick = { viewModel.showRecipeCard(it) }
            )
        }

        // 6. Severity Recommendations Bottom Sheet
        if (state.showSeveritySheet) {
            SeverityRecommendationSheet(
                currentPainLevel = state.painLevel,
                onDismiss = { viewModel.dismissSeveritySheet() },
                onSelect = { level ->
                    viewModel.logPersonalCare(state.painType, level, state.flowLevel, state.energyLevel)
                    viewModel.dismissSeveritySheet()
                }
            )
        }

        // 7. Recipe Card Overlay
        if (state.showRecipe != null) {
            RecipeCardOverlay(
                solution = state.showRecipe!!,
                onDismiss = { viewModel.dismissRecipeCard() }
            )
        }

        // 8. View All Category Screen
        if (state.showCategoryView && state.selectedCategory.isNotEmpty()) {
            ViewAllCategoryScreen(
                category = state.selectedCategory,
                allSolutions = state.allSolutions,
                symptom = state.painType.lowercase(),
                severity = state.painLevel.lowercase(),
                onSolutionClick = { viewModel.showRecipeCard(it) },
                onFavoriteToggle = { id, fav -> viewModel.toggleFavorite(id, fav) },
                onBack = { viewModel.dismissCategoryView() }
            )
        }
    }
}

@Composable
fun SuggestedSolutionItem(
    solution: SolutionEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(BlushPink.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(solution.emoji.ifEmpty { "🌸" }, fontSize = 24.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(solution.title, fontWeight = FontWeight.Bold, color = OnSurface, fontSize = 14.sp)
            Text(solution.subtitle.ifEmpty { solution.category }, fontSize = 11.sp, color = OnSurfaceVariant)
        }
        if (solution.isRecipe) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SoftCoral.copy(0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("📖", fontSize = 14.sp)
            }
        }
    }
    HorizontalDivider(color = BlushPink.copy(alpha = 0.15f))
}

@Composable
fun PreviewCard(
    solution: SolutionEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.width(140.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BlushPink.copy(0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(solution.emoji.ifEmpty { "🌸" }, fontSize = 24.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                solution.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            if (solution.durationMinutes > 0) {
                Spacer(Modifier.height(4.dp))
                Text("${solution.durationMinutes} min", fontSize = 10.sp, color = Lavender)
            }
        }
    }
}

@Composable
fun QuestItem(
    title: String,
    emoji: String,
    isCompleted: Boolean
) {
    val checkScale by animateFloatAsState(
        targetValue = if (isCompleted) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "checkScale"
    )

    val textColor by animateColorAsState(
        targetValue = if (isCompleted) OnSurfaceVariant.copy(0.6f) else OnSurface,
        label = "textColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sparkling interactive checkbox
        Box(
            modifier = Modifier
                .size(32.dp)
                .scale(checkScale)
                .clip(CircleShape)
                .background(
                    if (isCompleted) Mint.copy(0.3f) else Color.White.copy(0.5f)
                )
                .border(
                    width = 1.5.dp,
                    color = if (isCompleted) Mint else Color.White.copy(0.9f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Text(
                    text = "🌸",
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))
        Text(text = emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
fun ConfettiOverlay(
    visible: Boolean,
    onComplete: () -> Unit
) {
    if (!visible) return

    val confettiColors = listOf(
        Color(0xFFE87DAC), Color(0xFF82B3D9), Color(0xFFA89DD4),
        Color(0xFFFFD700), Color(0xFF5B9B7A), Color(0xFFE8A0B4)
    )

    // Create stable particle data remembered across recompositions
    val particles = remember {
        List(50) {
            mapOf(
                "x" to (Math.random() * 1000).toFloat(),
                "y" to (-50f - (Math.random() * 200).toFloat()),
                "color" to confettiColors[(Math.random() * confettiColors.size).toInt()],
                "size" to (4f + (Math.random() * 8).toFloat()),
                "speed" to (3f + (Math.random() * 5).toFloat())
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti")
    val animOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500)
        onComplete()
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {} // Absorb touches during animation
    ) {
        particles.forEach { p ->
            val x = (p["x"] as Float) % size.width
            val y = ((p["y"] as Float) + animOffset) % (size.height + 300)
            val color = p["color"] as Color
            val sz = p["size"] as Float
            drawCircle(color = color, radius = sz, center = Offset(x, y))
        }
    }
}
