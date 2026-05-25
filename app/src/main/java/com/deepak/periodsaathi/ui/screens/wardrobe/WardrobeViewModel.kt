package com.deepak.periodsaathi.ui.screens.wardrobe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.AccessoryDao
import com.deepak.periodsaathi.data.model.AccessoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Accessory(val id: String, val name: String, val emoji: String, val cost: Int, val isUnlocked: Boolean, val isEquipped: Boolean)

data class ThemeData(
    val name: String,
    val cost: Int,
    val isUnlocked: Boolean,
    val isActive: Boolean
)

enum class SeasonalItemState {
    ACTIVE,
    LOCKED,
    LOCKED_LIMITED,
    LOCKED_GRAYED,
    PLACEHOLDER
}

data class SeasonalAccessory(
    val id: String,
    val name: String,
    val emoji: String,
    val cost: Int = 0,
    val state: SeasonalItemState,
    val subtitle: String = "",
    val badgeText: String? = null,
    val actionLabel: String? = null,
    val hasGoldGlow: Boolean = false
)

@HiltViewModel
class WardrobeViewModel @Inject constructor(
    private val accessoryDao: AccessoryDao
) : ViewModel() {

    private val _accessories = MutableStateFlow<List<Accessory>>(emptyList())
    val accessories: StateFlow<List<Accessory>> = _accessories.asStateFlow()

    private val _totalPoints = MutableStateFlow(250)
    val totalPoints: StateFlow<Int> = _totalPoints.asStateFlow()

    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    private val _themes = MutableStateFlow(DEFAULT_THEMES)
    val themes: StateFlow<List<ThemeData>> = _themes.asStateFlow()

    private val _seasonalAccessories = MutableStateFlow(SEASONAL_ACCESSORIES)
    val seasonalAccessories: StateFlow<List<SeasonalAccessory>> = _seasonalAccessories.asStateFlow()

    private val _wardrobePoints = MutableStateFlow(1250)
    val wardrobePoints: StateFlow<Int> = _wardrobePoints.asStateFlow()

    private val _unlockedCount = MutableStateFlow(12)
    val unlockedCount: StateFlow<Int> = _unlockedCount.asStateFlow()

    companion object {
        private val DEFAULT_ACCESSORIES = listOf(
            AccessoryEntity("1", "Sunglasses", "\uD83D\uDE0E", 50, false, false),
            AccessoryEntity("2", "Flower Crown", "\uD83C\uDF38", 0, true, true),
            AccessoryEntity("3", "Party Hat", "\uD83C\uDF89", 75, false, false)
        )

        private val DEFAULT_THEMES = listOf(
            ThemeData("Mint Dream", 0, true, true),
            ThemeData("Peach Sunset", 250, false, false),
            ThemeData("Rose Gold", 500, false, false)
        )

        val SEASONAL_ACCESSORIES = listOf(
            SeasonalAccessory("s1", "Diwali Sparkle", "\uD83C\uDF86", 500, SeasonalItemState.LOCKED, "Locked Item", hasGoldGlow = true),
            SeasonalAccessory("s2", "Monsoon Teal", "\uD83D\uDC57", 0, SeasonalItemState.ACTIVE, "Currently Wearing", "Active"),
            SeasonalAccessory("s3", "Cozy Winter", "\u2744\uFE0F", 0, SeasonalItemState.LOCKED_LIMITED, "Locked Item", "Limited Time", "View Quest"),
            SeasonalAccessory("s4", "Spring Scarf", "\uD83E\uDDE3", 0, SeasonalItemState.LOCKED_GRAYED, "Collection 2023"),
            SeasonalAccessory("s5", "Beach Sunhat", "\uD83D\uDC52", 0, SeasonalItemState.LOCKED_GRAYED, "Collection 2023"),
            SeasonalAccessory("s6", "More styles coming soon", "", 0, SeasonalItemState.PLACEHOLDER)
        )
    }

    init {
        viewModelScope.launch {
            val existing = accessoryDao.getAllAccessories().first()
            if (existing.isEmpty()) {
                accessoryDao.upsertAll(DEFAULT_ACCESSORIES)
            }
        }
        viewModelScope.launch {
            accessoryDao.getAllAccessories().collect { entities ->
                _accessories.value = entities.map { entity ->
                    Accessory(
                        id = entity.id,
                        name = entity.name,
                        emoji = entity.emoji,
                        cost = entity.pointsCost,
                        isUnlocked = entity.unlocked,
                        isEquipped = entity.equipped
                    )
                }
            }
        }
    }

    fun equipAccessory(id: String) {
        viewModelScope.launch {
            val accessory = accessoryDao.getAccessoryById(id) ?: return@launch
            accessoryDao.setEquipped(id, !accessory.equipped)
        }
    }

    fun unlockAccessory(id: String, cost: Int) {
        viewModelScope.launch {
            if (_totalPoints.value >= cost) {
                _totalPoints.value -= cost
                accessoryDao.setUnlocked(id, true)
                _showConfetti.value = true
            }
        }
    }

    fun dismissConfetti() {
        _showConfetti.value = false
    }
}
