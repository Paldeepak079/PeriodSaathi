package com.deepak.periodsaathi.ui.screens.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.AccessoryDao
import com.deepak.periodsaathi.data.gamification.GamificationManager
import com.deepak.periodsaathi.data.gamification.REWARD_CATALOG
import com.deepak.periodsaathi.data.gamification.Reward
import com.deepak.periodsaathi.data.gamification.RewardType
import com.deepak.periodsaathi.data.repository.CycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RewardItem(
    val reward: Reward,
    val unlocked: Boolean,
    val recentlyClaimed: Boolean = false
)

@HiltViewModel
class RewardsViewModel @Inject constructor(
    private val cycleRepository: CycleRepository,
    private val gamificationManager: GamificationManager,
    private val accessoryDao: AccessoryDao
) : ViewModel() {

    private val _totalPoints = MutableStateFlow(0)
    val totalPoints: StateFlow<Int> = _totalPoints.asStateFlow()

    private val _themes = MutableStateFlow<List<RewardItem>>(emptyList())
    val themes: StateFlow<List<RewardItem>> = _themes.asStateFlow()

    private val _accessories = MutableStateFlow<List<RewardItem>>(emptyList())
    val accessories: StateFlow<List<RewardItem>> = _accessories.asStateFlow()

    private val _badges = MutableStateFlow<List<RewardItem>>(emptyList())
    val badges: StateFlow<List<RewardItem>> = _badges.asStateFlow()

    private val _claimingId = MutableStateFlow<String?>(null)
    val claimingId: StateFlow<String?> = _claimingId.asStateFlow()

    init {
        viewModelScope.launch {
            cycleRepository.getSettings().collect { settings ->
                _totalPoints.value = settings.totalPoints
            }
        }
        viewModelScope.launch {
            accessoryDao.getAllAccessories().collect { equipped ->
                val unlockedIds = equipped.filter { it.unlocked }.map { it.id }.toSet()
                val allRewards = REWARD_CATALOG.map { reward ->
                    RewardItem(
                        reward = reward,
                        unlocked = unlockedIds.contains(reward.id)
                    )
                }
                _themes.value = allRewards.filter { it.reward.type == RewardType.THEME }
                _accessories.value = allRewards.filter { it.reward.type == RewardType.MASCOT_ACCESSORY }
                _badges.value = allRewards.filter { it.reward.type == RewardType.BADGE }
            }
        }
    }

    fun claimReward(reward: Reward) {
        viewModelScope.launch {
            if (_totalPoints.value < reward.pointsRequired) return@launch
            _claimingId.value = reward.id

            val newTotal = _totalPoints.value - reward.pointsRequired
            val settings = cycleRepository.getSettings().first()
            cycleRepository.updateSettings(settings.copy(totalPoints = newTotal))
            _totalPoints.value = newTotal

            gamificationManager.markRewardUnlocked(reward.id)

            accessoryDao.upsertAccessory(
                com.deepak.periodsaathi.data.model.AccessoryEntity(
                    id = reward.id,
                    name = reward.name,
                    emoji = reward.emoji,
                    pointsCost = reward.pointsRequired,
                    unlocked = true,
                    equipped = reward.type != RewardType.BADGE
                )
            )

            updateItemState(reward.id, unlocked = true)
            _claimingId.value = null
        }
    }

    private fun updateItemState(id: String, unlocked: Boolean) {
        val updateList = { items: List<RewardItem> ->
            items.map { if (it.reward.id == id) it.copy(unlocked = unlocked, recentlyClaimed = true) else it }
        }
        _themes.value = updateList(_themes.value)
        _accessories.value = updateList(_accessories.value)
        _badges.value = updateList(_badges.value)
    }
}
