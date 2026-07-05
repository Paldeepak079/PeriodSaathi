package com.deepak.periodsaathi.ui.screens.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.ChallengeDao
import com.deepak.periodsaathi.data.model.ChallengeProgressEntity
import com.deepak.periodsaathi.domain.usecase.CHALLENGE_DEFINITIONS
import com.deepak.periodsaathi.domain.usecase.ChallengeProgressManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val challengeDao: ChallengeDao,
    private val challengeProgressManager: ChallengeProgressManager
) : ViewModel() {

    private val _activeChallenges = MutableStateFlow<List<Challenge>>(emptyList())
    val activeChallenges: StateFlow<List<Challenge>> = _activeChallenges.asStateFlow()

    private val _completedChallenges = MutableStateFlow<List<Challenge>>(emptyList())
    val completedChallenges: StateFlow<List<Challenge>> = _completedChallenges.asStateFlow()

    private val _availableChallenges = MutableStateFlow<List<Challenge>>(emptyList())
    val availableChallenges: StateFlow<List<Challenge>> = _availableChallenges.asStateFlow()

    init {
        viewModelScope.launch {
            val existing = challengeDao.getAllProgress().first()
            if (existing.isEmpty()) {
                challengeDao.upsertProgress(ChallengeProgressEntity(challengeId = "4", progress = 1f, completed = true))
                challengeDao.upsertProgress(ChallengeProgressEntity(challengeId = "5", progress = 1f, completed = true))
            }
        }

        viewModelScope.launch {
            challengeDao.getAllProgress().collect { progressList ->
                challengeProgressManager.refreshAllProgress()
            }
        }

        viewModelScope.launch {
            challengeDao.getAllProgress().collect { progressList ->
                val progressMap = progressList.associateBy { it.challengeId }

                val active = CHALLENGE_DEFINITIONS.filter { def ->
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

                val completed = CHALLENGE_DEFINITIONS.filter { def ->
                    val prog = progressMap[def.id]
                    prog != null && prog.completed
                }.map { def ->
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

                val available = CHALLENGE_DEFINITIONS.filter { def ->
                    progressMap[def.id] == null
                }.map { def ->
                    Challenge(
                        id = def.id,
                        title = def.title,
                        description = def.description,
                        emoji = def.emoji,
                        duration = def.duration,
                        pointsReward = def.pointsReward,
                        progress = 0f,
                        isActive = false,
                        isCompleted = false
                    )
                }

                _activeChallenges.value = active
                _completedChallenges.value = completed
                _availableChallenges.value = available
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

    fun refresh() {
        viewModelScope.launch {
            challengeProgressManager.refreshAllProgress()
        }
    }
}
