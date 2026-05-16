package com.example.periodsaathi.ui.screens.wardrobe

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class Accessory(val id: String, val emoji: String, val name: String, val cost: Int, val isUnlocked: Boolean, val isEquipped: Boolean)

@HiltViewModel
class WardrobeViewModel @Inject constructor() : ViewModel() {
    private val _accessories = MutableStateFlow(listOf(
        Accessory("1", "🕶️", "Sunglasses", 50, true, false),
        Accessory("2", "🌸", "Flower Crown", 100, true, true),
        Accessory("3", "🎩", "Party Hat", 75, false, false),
        Accessory("4", "👑", "Gold Crown", 200, false, false),
        Accessory("5", "🎀", "Pink Bow", 30, true, false)
    ))
    val accessories: StateFlow<List<Accessory>> = _accessories.asStateFlow()
    private val _points = MutableStateFlow(150)
    val points: StateFlow<Int> = _points.asStateFlow()

    fun equip(id: String) {
        _accessories.value = _accessories.value.map { it.copy(isEquipped = it.id == id) }
    }

    fun unlock(id: String) {
        val accessory = _accessories.value.find { it.id == id } ?: return
        if (_points.value >= accessory.cost) {
            _points.value -= accessory.cost
            _accessories.value = _accessories.value.map { if (it.id == id) it.copy(isUnlocked = true) else it }
        }
    }
}