package com.example.periodsaathi.ui.screens.wardrobe

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class Accessory(val id: String, val name: String, val emoji: String, val cost: Int, val isUnlocked: Boolean, val isEquipped: Boolean)

@HiltViewModel
class WardrobeViewModel @Inject constructor() : ViewModel() {

    private val _accessories = MutableStateFlow(
        listOf(
            Accessory("1", "Crown", "👑", 500, true, false),
            Accessory("2", "Bow", "🎀", 300, true, false),
            Accessory("3", "Glasses", "👓", 200, true, false),
            Accessory("4", "Hat", "🎩", 400, false, false),
            Accessory("5", "Scarf", "🧣", 350, false, false),
            Accessory("6", "Tiara", "💫", 600, false, false),
            Accessory("7", "Wings", "🪽", 800, false, false),
            Accessory("8", "Halo", "😇", 1000, false, false)
        )
    )
    val accessories: StateFlow<List<Accessory>> = _accessories.asStateFlow()

    private val _equippedItems = MutableStateFlow<Set<String>>(emptySet())
    val equippedItems: StateFlow<Set<String>> = _equippedItems.asStateFlow()

    private val _totalPoints = MutableStateFlow(250)
    val totalPoints: StateFlow<Int> = _totalPoints.asStateFlow()

    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    fun equipAccessory(id: String) {
        _equippedItems.value = if (id in _equippedItems.value) {
            _equippedItems.value - id
        } else {
            _equippedItems.value + id
        }
    }

    fun unlockAccessory(id: String, cost: Int) {
        if (_totalPoints.value >= cost) {
            _totalPoints.value -= cost
            _accessories.value = _accessories.value.map { accessory ->
                if (accessory.id == id) accessory.copy(isUnlocked = true) else accessory
            }
            _showConfetti.value = true
        }
    }

    fun dismissConfetti() {
        _showConfetti.value = false
    }
}