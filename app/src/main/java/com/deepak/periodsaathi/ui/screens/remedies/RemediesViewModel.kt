package com.deepak.periodsaathi.ui.screens.remedies

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class Remedy(val id: String, val name: String, val emoji: String, val ingredients: String, val steps: String, val helpsWith: String)

@HiltViewModel
class RemediesViewModel @Inject constructor() : ViewModel() {

    private val _flippedCards = MutableStateFlow<Set<String>>(emptySet())
    val flippedCards: StateFlow<Set<String>> = _flippedCards.asStateFlow()

    private val _hotBagPosition = MutableStateFlow(0.5f)
    val hotBagPosition: StateFlow<Float> = _hotBagPosition.asStateFlow()

    val remedies = listOf(
        Remedy("1", "Ginger Tea", "🫚", "1 inch ginger, 1 cup water, honey", "Boil ginger 5 min, strain, add honey", "Bloatedness, cramps"),
        Remedy("2", "Turmeric Milk", "🥛", "1 cup milk, 1 tsp turmeric, pinch black pepper", "Warm milk, add turmeric, stir well", "Inflammation, mood"),
        Remedy("3", "Hot Compress", "🔥", "Hot water bottle or heating pad", "Apply to lower abdomen for 15-20 mins", "Cramps, back pain"),
        Remedy("4", "Magnesium Foods", "🥬", "Leafy greens, nuts, seeds, dark chocolate", "Incorporate into daily meals", "Muscle tension, mood"),
        Remedy("5", "Dark Chocolate", "🍫", "70%+ dark chocolate", "Enjoy in moderation", "Mood boost, cravings"),
        Remedy("6", "Chamomile Tea", "🌼", "1 tsp chamomile, hot water, honey", "Steep 5 mins, strain, enjoy", "Anxiety, sleep"),
        Remedy("7", "Fennel Seeds", "🌿", "1 tsp fennel, hot water", "Steep 10 mins, sip slowly", "Bloating, digestion"),
        Remedy("8", "Heating Pad", "🔌", "Electric or microwavable heating pad", "Use on low-medium setting", "Cramps relief")
    )

    fun flipCard(id: String) {
        _flippedCards.value = if (id in _flippedCards.value) {
            _flippedCards.value - id
        } else {
            _flippedCards.value + id
        }
    }

    fun updateHotBagPosition(position: Float) {
        _hotBagPosition.value = position.coerceIn(0f, 1f)
    }
}
