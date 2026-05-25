package com.deepak.periodsaathi.ui.screens.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.ChallengeDao
import com.deepak.periodsaathi.data.model.ChallengeProgressEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val duration: String,
    val pointsReward: Int,
    val progress: Float,
    val isActive: Boolean,
    val isCompleted: Boolean
)

data class ChallengeDefinition(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val duration: String,
    val pointsReward: Int,
    val targetProgress: Float = 1f
)

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val challengeDao: ChallengeDao
) : ViewModel() {

    private val challengeDefinitions = listOf(
        ChallengeDefinition("1", "Hydration Hero", "Log 8 glasses of water for 7 days", "\uD83D\uDCA7", "7 days", 100),
        ChallengeDefinition("2", "Mood Tracker", "Log your mood for 14 days", "\uD83D\uDE0A", "14 days", 150),
        ChallengeDefinition("3", "Symptom Detective", "Log symptoms for 5 days", "\uD83D\uDD0D", "5 days", 75),
        ChallengeDefinition("4", "First Step", "Complete onboarding", "\uD83D\uDC4B", "1 day", 50),
        ChallengeDefinition("5", "Cycle Logger", "Log first period", "\uD83E\uDE78", "1 day", 50)
    )

    private val _activeChallenges = MutableStateFlow<List<Challenge>>(emptyList())
    val activeChallenges: StateFlow<List<Challenge>> = _activeChallenges.asStateFlow()

    private val _completedChallenges = MutableStateFlow<List<Challenge>>(emptyList())
    val completedChallenges: StateFlow<List<Challenge>> = _completedChallenges.asStateFlow()

    init {
        viewModelScope.launch {
            val existing = challengeDao.getAllProgress().first()
            if (existing.isEmpty()) {
                challengeDao.upsertProgress(
                    ChallengeProgressEntity(challengeId = "1", progress = 0.4f)
                )
                challengeDao.upsertProgress(
                    ChallengeProgressEntity(challengeId = "2", progress = 0.2f)
                )
                challengeDao.upsertProgress(
                    ChallengeProgressEntity(challengeId = "3", progress = 0.6f)
                )
                challengeDao.upsertProgress(
                    ChallengeProgressEntity(challengeId = "4", progress = 1f, completed = true)
                )
                challengeDao.upsertProgress(
                    ChallengeProgressEntity(challengeId = "5", progress = 1f, completed = true)
                )
            }
        }

        viewModelScope.launch {
            challengeDao.getAllProgress().collect { progressList ->
                val progressMap = progressList.associateBy { it.challengeId }

                val active = challengeDefinitions.filter { def ->
                    val prog = progressMap[def.id]
                    prog != null && !prog.completed
                }.map { def ->
                    val prog = progressMap[def.id]!!
                    Challenge(
                        id = def.id,
                        title = def.title,
                        description = def.description,
                        emoji = def.emoji,
                        duration = def.duration,
                        pointsReward = def.pointsReward,
                        progress = prog.progress,
                        isActive = true,
                        isCompleted = false
                    )
                }

                val completed = challengeDefinitions.filter { def ->
                    val prog = progressMap[def.id]
                    prog != null && prog.completed
                }.map { def ->
                    val prog = progressMap[def.id]!!
                    Challenge(
                        id = def.id,
                        title = def.title,
                        description = def.description,
                        emoji = def.emoji,
                        duration = def.duration,
                        pointsReward = def.pointsReward,
                        progress = 1f,
                        isActive = false,
                        isCompleted = true
                    )
                }

                _activeChallenges.value = active
                _completedChallenges.value = completed
            }
        }
    }

    fun acceptChallenge(id: String) {
        viewModelScope.launch {
            val existing = challengeDao.getProgressByChallengeIdOnce(id)
            if (existing == null) {
                challengeDao.upsertProgress(
                    ChallengeProgressEntity(challengeId = id)
                )
            }
        }
    }

    fun checkDailyProgress(challengeId: String) {
        viewModelScope.launch {
            val progress = challengeDao.getProgressByChallengeIdOnce(challengeId) ?: return@launch
            if (progress.completed) return@launch

            val today = LocalDate.now()
            val lastUpdate = progress.lastUpdated.let {
                LocalDate.ofEpochDay(it / 86400000)
            }

            val canProgress = lastUpdate != today

            val newProgress = if (canProgress) {
                (progress.progress + 0.1f).coerceAtMost(1f)
            } else {
                progress.progress
            }

            val isCompleted = newProgress >= 1f

            challengeDao.updateProgressByChallengeId(
                challengeId = challengeId,
                progress = newProgress,
                completed = isCompleted,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }
}
