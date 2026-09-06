package com.deepak.periodsaathi.ui.screens.wellness

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.CycleDao
import com.deepak.periodsaathi.data.dao.HabitDao
import com.deepak.periodsaathi.data.repository.CycleRepository
import com.deepak.periodsaathi.notification.WaterReminderWorker
import com.deepak.periodsaathi.wellness.data.local.SolutionRepository
import com.deepak.periodsaathi.wellness.data.local.WellnessDao
import com.deepak.periodsaathi.wellness.data.local.entities.CoinTransactionEntity
import com.deepak.periodsaathi.wellness.data.local.entities.SolutionEntity
import com.deepak.periodsaathi.wellness.data.local.entities.TipEntity
import com.deepak.periodsaathi.wellness.data.local.entities.UserWellnessStatsEntity
import com.deepak.periodsaathi.wellness.data.local.entities.WellnessLogEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

data class ExerciseItem(
    val name: String,
    val durationMinutes: Int,
    val steps: List<String>,
    val benefits: String,
    val painLevelsSupported: List<String>,
    val animationPath: String
)

data class DietRecommendation(
    val meals: List<String>,
    val benefits: String
)

data class DailyQuests(
    val drinkWater: Boolean = false,
    val doYoga: Boolean = false,
    val meditateBreathing: Boolean = false,
    val logHealthyMeal: Boolean = false,
    val logSleep: Boolean = false,
    val trackMood: Boolean = false
)

data class MoodHistoryEntry(val date: Long, val mood: String, val moodScore: Int)

data class WellnessState(
    val dailyScore: Int = 0,
    val totalCoins: Int = 0,
    val currentStreak: Int = 0,
    
    // Trackers
    val waterGlasses: Int = 0,
    val sleepHours: Float = 0f,
    val sleepRating: Int = 0,
    val exerciseMinutes: Int = 0,
    val mood: String = "",
    val customMeals: List<String> = emptyList(),
    
    // Mood trend history
    val moodHistory: List<MoodHistoryEntry> = emptyList(),
    
    // Selection for Personalization
    val painType: String = "None",
    val painLevel: String = "Mild",
    val flowLevel: String = "Medium",
    val energyLevel: String = "Normal",
    
    // Personalization Outputs
    val remedies: List<TipEntity> = emptyList(),
    val exercises: List<ExerciseItem> = emptyList(),
    val diet: DietRecommendation = DietRecommendation(emptyList(), ""),
    
    // Quest checklist
    val quests: DailyQuests = DailyQuests(),
    
    // Dialog states
    val showCarePopup: Boolean = false,
    val showCoinAnimation: Boolean = false,
    val coinEarnedAmount: Int = 0,
    val showConfetti: Boolean = false,
    
    // ===== NEW: Solutions =====
    val allSolutions: List<SolutionEntity> = emptyList(),
    val suggestedSolutions: List<SolutionEntity> = emptyList(),
    val selectedSymptom: String = "",
    val showSolutionsSheet: Boolean = false,
    val showSeveritySheet: Boolean = false,
    val showRecipe: SolutionEntity? = null,
    val selectedCategory: String = "",
    val showCategoryView: Boolean = false,
    
    // Water reminder
    val waterReminderEnabled: Boolean = false
)

@HiltViewModel
class WellnessViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wellnessDao: WellnessDao,
    private val cycleDao: CycleDao,
    private val habitDao: HabitDao,
    private val cycleRepository: CycleRepository,
    private val solutionRepository: SolutionRepository
) : ViewModel() {

    private val TAG = "WellnessViewModel"
    private val _state = MutableStateFlow(WellnessState())
    val state: StateFlow<WellnessState> = _state.asStateFlow()

    private var allTips: List<TipEntity> = emptyList()
    private var allExercises: List<ExerciseItem> = emptyList()

    init {
        loadDataFromRaw()
        observeData()
        loadMoodHistory()
        seedSolutions()
        observeSolutions()
        checkWaterReminderStatus()
    }

    private fun loadDataFromRaw() {
        viewModelScope.launch {
            try {
                val jsonString = context.resources.openRawResource(
                    context.resources.getIdentifier("wellness_data", "raw", context.packageName)
                ).bufferedReader().use { it.readText() }

                val gson = Gson()
                val rootMapType = object : TypeToken<Map<String, Any>>() {}.type
                val rootMap: Map<String, Any> = gson.fromJson(jsonString, rootMapType)

                // Load and Insert remedies/tips
                val tipsJson = gson.toJson(rootMap["tips"])
                val tipsType = object : TypeToken<List<TipEntity>>() {}.type
                val tips: List<TipEntity> = gson.fromJson(tipsJson, tipsType)
                wellnessDao.insertTips(tips)
                allTips = tips

                // Load exercises
                val exercisesJson = gson.toJson(rootMap["exercises"])
                val exercisesType = object : TypeToken<List<ExerciseItem>>() {}.type
                allExercises = gson.fromJson(exercisesJson, exercisesType)
            } catch (e: Exception) {
                Log.e(TAG, "Failed loading wellness_data JSON from raw", e)
            }
        }
    }

    private fun observeData() {
        val todayEpoch = getTodayEpoch()

        // 1. Seed initial stats if missing
        viewModelScope.launch {
            if (wellnessDao.getStatsSync() == null) {
                wellnessDao.insertStats(UserWellnessStatsEntity(totalCoins = 250))
            }
        }

        // 2. Observe stats
        viewModelScope.launch {
            wellnessDao.getStats().collect { stats ->
                val currentStats = stats ?: UserWellnessStatsEntity(totalCoins = 250)
                _state.update {
                    it.copy(
                        totalCoins = currentStats.totalCoins,
                        currentStreak = currentStats.currentStreak
                    )
                }
            }
        }

        // 2. Observe local log for today
        viewModelScope.launch {
            wellnessDao.getLogForDate(todayEpoch).collect { log ->
                val currentLog = log ?: WellnessLogEntity(date = todayEpoch)
                _state.update {
                    val meals = try {
                        Gson().fromJson<List<String>>(
                            currentLog.mealsLoggedJson,
                            object : TypeToken<List<String>>() {}.type
                        ) ?: emptyList()
                    } catch (e: Exception) {
                        emptyList()
                    }

                    it.copy(
                        waterGlasses = currentLog.waterIntake,
                        sleepHours = currentLog.sleepHours,
                        exerciseMinutes = currentLog.exerciseMinutes,
                        mood = currentLog.mood,
                        painType = if (currentLog.painType.isEmpty()) "None" else currentLog.painType,
                        painLevel = if (currentLog.painLevel.isEmpty()) "Mild" else currentLog.painLevel,
                        flowLevel = if (currentLog.flowLevel.isEmpty()) "Medium" else currentLog.flowLevel,
                        energyLevel = if (currentLog.energyLevel.isEmpty()) "Normal" else currentLog.energyLevel,
                        customMeals = meals
                    )
                }
                updatePersonalizedEngine()
                calculateWellnessScore()
            }
        }
    }

    private fun loadMoodHistory() {
        viewModelScope.launch {
            val sevenDaysAgo = LocalDate.now().minusDays(14)
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            wellnessDao.getLogsSince(sevenDaysAgo).collect { logs ->
                val moodEntries = logs
                    .filter { it.mood.isNotEmpty() }
                    .map { log ->
                        val moodScore = when (log.mood.lowercase()) {
                            "sad" -> 1
                            "anxious" -> 2
                            "irritated" -> 3
                            "emotional" -> 4
                            "calm" -> 5
                            "happy" -> 6
                            "energetic" -> 7
                            else -> 0
                        }
                        MoodHistoryEntry(date = log.date, mood = log.mood, moodScore = moodScore)
                    }
                    .sortedBy { it.date }
                _state.update { it.copy(moodHistory = moodEntries) }
            }
        }
    }

    // AI offline rule-engine
    private fun updatePersonalizedEngine() {
        val currentState = _state.value
        val painLvl = currentState.painLevel.lowercase()

        // Filter Desi Remedies based on pain intensity
        val matchedRemedies = allTips.filter { it.painLevel.lowercase() == painLvl }

        // Filter Yoga exercises based on pain levels supported
        val matchedExercises = allExercises.filter { it.painLevelsSupported.contains(painLvl) }

        // Generate dynamic dietary guides based on pain intensity
        val dietPlan = when (painLvl) {
            "severe" -> DietRecommendation(
                meals = listOf("Warming Bone Broth or Lentil Soup", "Ginger Turmeric Herbal Rice", "Warm Chamomile Tea", "Steamed Dark Leafy Greens"),
                benefits = "Eases painful uterine spasms, lowers inflammatory prostaglandin levels, and improves gut blood flow."
            )
            "moderate" -> DietRecommendation(
                meals = listOf("Steamed Salmon or Baked Tofu", "Roasted Pumpkin Seeds", "Sauteed Kale & Spinach", "Warm Berry Compote"),
                benefits = "Replenishes essential magnesium and omega-3 fatty acids to naturally reduce severe muscle cramping."
            )
            else -> DietRecommendation(
                meals = listOf("Creamy Oatmeal with Walnuts", "Fresh Antioxidant Blueberries", "Avocado & Seed Toast", "Steamed Quinoa"),
                benefits = "Restores lost iron stores, balances baseline insulin, and delivers high slow-burn energy."
            )
        }

        _state.update {
            it.copy(
                remedies = matchedRemedies,
                exercises = matchedExercises,
                diet = dietPlan
            )
        }
    }

    private fun calculateWellnessScore() {
        val currentState = _state.value
        
        // Calculators
        val hydrationPoints = (currentState.waterGlasses * 1.875f).toInt().coerceAtMost(15) // Max 15 (8 glasses)
        val exercisePoints = if (currentState.exerciseMinutes >= 20) 20 else if (currentState.exerciseMinutes > 0) 10 else 0 // Max 20
        val breathingPoints = if (currentState.quests.meditateBreathing) 20 else 0 // Max 20
        val dietPoints = if (currentState.customMeals.isNotEmpty()) 15 else 0 // Max 15
        val sleepPoints = if (currentState.sleepHours in 7.0f..9.0f) 10 else if (currentState.sleepHours > 0) 5 else 0 // Max 10
        val symptomPoints = if (currentState.painType != "None" && currentState.painType.isNotEmpty()) 10 else 0 // Max 10
        val moodPoints = if (currentState.mood.isNotEmpty()) 10 else 0 // Max 10

        val totalScore = (hydrationPoints + exercisePoints + breathingPoints + dietPoints + sleepPoints + symptomPoints + moodPoints).coerceIn(0, 100)

        // Update Daily Quests progress
        val quests = DailyQuests(
            drinkWater = currentState.waterGlasses >= 8,
            doYoga = currentState.exerciseMinutes >= 15,
            meditateBreathing = currentState.quests.meditateBreathing,
            logHealthyMeal = currentState.customMeals.isNotEmpty(),
            logSleep = currentState.sleepHours >= 7f,
            trackMood = currentState.mood.isNotEmpty()
        )

        val completedBefore = _state.value.quests
        val bonusAwarded = totalScore == 100 && _state.value.dailyScore < 100

        _state.update {
            it.copy(
                dailyScore = totalScore,
                quests = quests,
                showConfetti = totalScore == 100
            )
        }

        if (bonusAwarded) {
            awardCoins(10, "Achieved 100% daily wellness score bonus!")
        }
    }

    fun showCarePopup(show: Boolean) {
        _state.update { it.copy(showCarePopup = show) }
    }

    fun logPersonalCare(
        painType: String,
        painLevel: String,
        flowLevel: String,
        energyLevel: String
    ) {
        viewModelScope.launch {
            val today = getTodayEpoch()
            val existing = wellnessDao.getLogForDateSync(today) ?: WellnessLogEntity(date = today)
            
            val updated = existing.copy(
                painType = painType,
                painLevel = painLevel,
                flowLevel = flowLevel,
                energyLevel = energyLevel
            )
            wellnessDao.insertLog(updated)
            showCarePopup(false)
        }
    }

    fun logWater(amount: Int) {
        viewModelScope.launch {
            val today = getTodayEpoch()
            val existing = wellnessDao.getLogForDateSync(today) ?: WellnessLogEntity(date = today)
            val newWater = (existing.waterIntake + amount).coerceAtLeast(0)
            
            wellnessDao.insertLog(existing.copy(waterIntake = newWater))
            
            // Check if goal reached (8 glasses)
            if (newWater >= 8 && existing.waterIntake < 8) {
                awardCoins(1, "Hydration goal achieved!")
                Toast.makeText(context, "Hydration Goal Achieved! 💧", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun logSleep(hours: Float, rating: Int) {
        viewModelScope.launch {
            val today = getTodayEpoch()
            val existing = wellnessDao.getLogForDateSync(today) ?: WellnessLogEntity(date = today)
            wellnessDao.insertLog(existing.copy(sleepHours = hours))
            _state.update { it.copy(sleepRating = rating) }
            if (hours >= 7f) {
                awardCoins(2, "Logged sleep: ${hours}h")
            }
        }
    }

    fun logCustomFood(food: String) {
        if (food.isBlank()) return
        viewModelScope.launch {
            val today = getTodayEpoch()
            val existing = wellnessDao.getLogForDateSync(today) ?: WellnessLogEntity(date = today)
            val currentList = _state.value.customMeals.toMutableList()
            currentList.add(food)
            
            val json = Gson().toJson(currentList)
            wellnessDao.insertLog(existing.copy(mealsLoggedJson = json))
            awardCoins(2, "Logged meal: $food")
        }
    }

    fun logMood(moodName: String) {
        viewModelScope.launch {
            val today = getTodayEpoch()
            val existing = wellnessDao.getLogForDateSync(today) ?: WellnessLogEntity(date = today)
            wellnessDao.insertLog(existing.copy(mood = moodName))
            awardCoins(2, "Logged mood: $moodName")
        }
    }

    fun completeBreathingSession() {
        _state.update {
            it.copy(
                quests = it.quests.copy(meditateBreathing = true)
            )
        }
        calculateWellnessScore()
        awardCoins(3, "Completed Breathing Therapy Session")
        _state.update { it.copy(showConfetti = true) }
    }

    fun onWorkoutCompleted(workoutName: String, durationMinutes: Int) {
        viewModelScope.launch {
            // Log the exercise minutes
            completeYogaSession(durationMinutes)
            // Award extra coins for completion
            awardCoins(5, "Completed: $workoutName")
            // Always show confetti on workout completion (not just at 100%)
            _state.update { it.copy(showConfetti = true) }
        }
    }

    fun completeYogaSession(minutes: Int) {
        viewModelScope.launch {
            val today = getTodayEpoch()
            val existing = wellnessDao.getLogForDateSync(today) ?: WellnessLogEntity(date = today)
            val newMins = existing.exerciseMinutes + minutes
            
            wellnessDao.insertLog(existing.copy(exerciseMinutes = newMins))
            awardCoins(5, "Completed Yoga/Exercise Workout")
        }
    }

    fun readTip(tipId: String) {
        viewModelScope.launch {
            val tipsList = _state.value.remedies
            val tip = tipsList.firstOrNull { it.id == tipId }
            if (tip != null && !tip.read) {
                wellnessDao.markTipAsRead(tipId)
                awardCoins(1, "Read: ${tip.title}")
            }
        }
    }

    fun awardCoins(amount: Int, description: String) {
        viewModelScope.launch {
            val stats = wellnessDao.getStatsSync() ?: UserWellnessStatsEntity(totalCoins = 250)
            val updatedStats = stats.copy(totalCoins = stats.totalCoins + amount)
            wellnessDao.insertStats(updatedStats)

            val tx = CoinTransactionEntity(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                amount = amount,
                description = description,
                type = "EARN"
            )
            wellnessDao.insertCoinTransaction(tx)

            _state.update {
                it.copy(
                    totalCoins = updatedStats.totalCoins,
                    showCoinAnimation = true,
                    coinEarnedAmount = amount
                )
            }

            // Sync total coins to global settings (set, not add)
            try {
                val globalSettings = cycleRepository.getSettings().first()
                cycleRepository.updateSettings(globalSettings.copy(totalPoints = updatedStats.totalCoins))
            } catch (e: Exception) {
                Log.e(TAG, "Failed syncing points to global CycleSettings", e)
            }
        }
    }

    fun dismissCoinAnimation() {
        _state.update { it.copy(showCoinAnimation = false) }
    }

    fun dismissConfetti() {
        _state.update { it.copy(showConfetti = false) }
    }

    private fun seedSolutions() {
        viewModelScope.launch {
            solutionRepository.seedSolutions()
        }
    }

    private fun observeSolutions() {
        viewModelScope.launch {
            solutionRepository.allSolutions.collect { solutions ->
                val state = _state.value
                val suggested = if (state.painType != "None") {
                    filterSuggestions(solutions, state.painType.lowercase(), state.painLevel.lowercase(), state.energyLevel.lowercase())
                } else emptyList()
                _state.update { it.copy(allSolutions = solutions, suggestedSolutions = suggested) }
            }
        }
    }

    private fun checkWaterReminderStatus() {
        _state.update { it.copy(waterReminderEnabled = WaterReminderWorker.isScheduled(context)) }
    }

    private fun filterSuggestions(
        solutions: List<SolutionEntity>,
        symptom: String,
        severity: String,
        energy: String
    ): List<SolutionEntity> {
        val symptomMatch = solutions.filter { it.symptomType == symptom || it.symptomType == "general" }
        val severityMatch = symptomMatch.filter { it.severity == severity || it.severity == "all" }
        return severityMatch.sortedByDescending { if (it.symptomType == symptom) 1 else 0 }
    }

    fun showSolutionsForSymptom(symptom: String) {
        _state.update { it.copy(selectedSymptom = symptom, showSolutionsSheet = true) }
    }

    fun dismissSolutionsSheet() {
        _state.update { it.copy(showSolutionsSheet = false) }
    }

    fun showSeverityRecommendations() {
        _state.update { it.copy(showSeveritySheet = true) }
    }

    fun dismissSeveritySheet() {
        _state.update { it.copy(showSeveritySheet = false) }
    }

    fun showRecipeCard(solution: SolutionEntity) {
        _state.update { it.copy(showRecipe = solution) }
    }

    fun dismissRecipeCard() {
        _state.update { it.copy(showRecipe = null) }
    }

    fun showCategoryView(category: String) {
        _state.update { it.copy(selectedCategory = category, showCategoryView = true) }
    }

    fun dismissCategoryView() {
        _state.update { it.copy(showCategoryView = false) }
    }

    fun toggleFavorite(id: String, fav: Boolean) {
        viewModelScope.launch { solutionRepository.toggleFavorite(id, fav) }
    }

    fun toggleWaterReminder() {
        val newState = !_state.value.waterReminderEnabled
        WaterReminderWorker.setEnabled(context, newState)
        _state.update { it.copy(waterReminderEnabled = newState) }
        if (newState) {
            Toast.makeText(context, "Water reminder set! 💧 Every 2 hours, 9AM-9PM", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Water reminder disabled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTodayEpoch(): Long {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
