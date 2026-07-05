package com.deepak.periodsaathi.ui.screens.wardrobe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.AccessoryDao
import com.deepak.periodsaathi.data.gamification.GamificationManager
import com.deepak.periodsaathi.data.gamification.RewardType
import com.deepak.periodsaathi.data.model.AccessoryEntity
import com.deepak.periodsaathi.data.repository.CycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Accessory(val id: String, val name: String, val emoji: String, val cost: Int, val isUnlocked: Boolean, val isEquipped: Boolean)

data class ThemeData(val name: String, val cost: Int, val isUnlocked: Boolean, val isActive: Boolean)

enum class SeasonalItemState { ACTIVE, LOCKED, LOCKED_LIMITED, LOCKED_GRAYED, PLACEHOLDER }

data class SeasonalAccessory(
    val id: String, val name: String, val emoji: String, val cost: Int = 0,
    val state: SeasonalItemState, val subtitle: String = "",
    val badgeText: String? = null, val actionLabel: String? = null, val hasGoldGlow: Boolean = false
)

@HiltViewModel
class WardrobeViewModel @Inject constructor(
    private val accessoryDao: AccessoryDao,
    private val cycleRepository: CycleRepository,
    private val gamificationManager: GamificationManager
) : ViewModel() {

    private val _accessories = MutableStateFlow<List<Accessory>>(emptyList())
    val accessories: StateFlow<List<Accessory>> = _accessories.asStateFlow()

    private val _totalPoints = MutableStateFlow(0)
    val totalPoints: StateFlow<Int> = _totalPoints.asStateFlow()

    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    private val _themes = MutableStateFlow(DEFAULT_THEMES)
    val themes: StateFlow<List<ThemeData>> = _themes.asStateFlow()

    private val _seasonalAccessories = MutableStateFlow(SEASONAL_ACCESSORIES)
    val seasonalAccessories: StateFlow<List<SeasonalAccessory>> = _seasonalAccessories.asStateFlow()

    companion object {
        private const val THEME_MINT_DREAM = "theme_mint_dream"
        private const val THEME_PEACH_SUNSET = "theme_peach_sunset"
        private const val THEME_ROSE_GOLD = "theme_rose_gold"

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
            combine(
                accessoryDao.getAllAccessories(),
                cycleRepository.getSettings()
            ) { entities, settings ->
                _totalPoints.value = settings.totalPoints
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

                val accessoryIds = entities.map { it.id }.toSet()
                _themes.value = DEFAULT_THEMES.map { theme ->
                    val themeId = themeIdForName(theme.name)
                    val unlocked = accessoryIds.contains(themeId) && entities.find { it.id == themeId }?.unlocked == true
                    val active = unlocked && (entities.find { it.id == themeId }?.equipped == true)
                    theme.copy(isUnlocked = unlocked, isActive = active)
                }
            }.collect {}
        }
    }

    fun equipAccessory(id: String) {
        viewModelScope.launch {
            val accessory = accessoryDao.getAccessoryById(id) ?: return@launch
            val newEquipped = !accessory.equipped
            accessoryDao.setEquipped(id, newEquipped)
            if (newEquipped && accessory.unlocked) {
                gamificationManager.markRewardUnlocked(id)
            }
        }
    }

    fun unlockAccessory(id: String, cost: Int) {
        viewModelScope.launch {
            val settings = cycleRepository.getSettings().first()
            if (settings.totalPoints >= cost) {
                cycleRepository.updateSettings(settings.copy(totalPoints = settings.totalPoints - cost))
                accessoryDao.setUnlocked(id, true)
                _showConfetti.value = true
            }
        }
    }

    fun toggleTheme(name: String) {
        viewModelScope.launch {
            val themeId = themeIdForName(name)
            val existing = accessoryDao.getAccessoryById(themeId)
            val settings = cycleRepository.getSettings().first()

            if (existing == null || !existing.unlocked) {
                val defaultTheme = DEFAULT_THEMES.find { it.name == name } ?: return@launch
                if (settings.totalPoints < defaultTheme.cost) return@launch
                cycleRepository.updateSettings(settings.copy(totalPoints = settings.totalPoints - defaultTheme.cost))
                accessoryDao.upsertAccessory(
                    AccessoryEntity(id = themeId, name = name, emoji = themeEmoji(name), pointsCost = defaultTheme.cost, unlocked = true, equipped = true)
                )
                _showConfetti.value = true
            } else {
                accessoryDao.setEquipped(themeId, !existing.equipped)
            }
        }
    }

    fun toggleSeasonalAccessory(name: String) {
        val updated = _seasonalAccessories.value.map { item ->
            if (item.name == name) {
                when (item.state) {
                    SeasonalItemState.LOCKED, SeasonalItemState.LOCKED_LIMITED -> {
                        item.copy(state = SeasonalItemState.ACTIVE)
                    }
                    SeasonalItemState.ACTIVE -> item.copy(state = SeasonalItemState.LOCKED)
                    else -> item
                }
            } else item
        }
        _seasonalAccessories.value = updated
    }

    fun dismissConfetti() { _showConfetti.value = false }

    private fun themeIdForName(name: String): String = when (name) {
        "Mint Dream" -> THEME_MINT_DREAM
        "Peach Sunset" -> THEME_PEACH_SUNSET
        "Rose Gold" -> THEME_ROSE_GOLD
        else -> name.lowercase().replace(" ", "_")
    }

    private fun themeEmoji(name: String): String = when (name) {
        "Mint Dream" -> "\uD83C\uDF3F"
        "Peach Sunset" -> "\uD83C\uDF05"
        "Rose Gold" -> "\uD83C\uDF39"
        else -> "\uD83C\uDFA8"
    }

    private val DEFAULT_ACCESSORIES = listOf(
        AccessoryEntity("1", "Sunglasses", "\uD83D\uDE0E", 50, false, false),
        AccessoryEntity("2", "Flower Crown", "\uD83C\uDF38", 0, true, true),
        AccessoryEntity("3", "Party Hat", "\uD83C\uDF89", 75, false, false)
    )
}
