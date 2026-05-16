package com.example.periodsaathi.ui.screens.remedies

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class RemedyCard(
    val id: String,
    val emoji: String,
    val name: String,
    val ingredients: String,
    val steps: String,
    val helpfulFor: String
)

@HiltViewModel
class RemediesViewModel @Inject constructor() : ViewModel() {

    private val _flippedCards = MutableStateFlow<Set<String>>(emptySet())
    val flippedCards: StateFlow<Set<String>> = _flippedCards.asStateFlow()

    private val _hotBagPosition = MutableStateFlow(0.5f)
    val hotBagPosition: StateFlow<Float> = _hotBagPosition.asStateFlow()

    val remedies = listOf(
        RemedyCard(
            "1", "🫚", "Ginger Tea",
            "Fresh ginger, honey, hot water",
            "1. Boil water 2. Add sliced ginger 3. Simmer 5 min 4. Add honey",
            "Cramps, Nausea"
        ),
        RemedyCard(
            "2", "🥛", "Turmeric Milk",
            "Milk, turmeric, black pepper, honey",
            "1. Warm milk 2. Add 1/2 tsp turmeric 3. Pinch of black pepper 4. Sweeten",
            "Inflammation, Mood"
        ),
        RemedyCard(
            "3", "🌿", "Chamomile",
            "Chamomile tea bags, hot water",
            "1. Boil water 2. Steep chamomile 3. Drink warm",
            "Anxiety, Cramps"
        ),
        RemedyCard(
            "4", "🍫", "Dark Chocolate",
            "70%+ dark chocolate",
            "1. Enjoy 1-2 squares 2. Pair with nuts",
            "Mood, Energy"
        ),
        RemedyCard(
            "5", "🌿", "Fennel Seeds",
            "Fennel seeds, hot water",
            "1. Boil water 2. Add 1 tsp fennel 3. Steep 5 min 4. Strain and drink",
            "Bloating, Digestion"
        ),
        RemedyCard(
            "6", "🧘", "Heating Pad",
            "Warm compress or heating pad",
            "1. Apply to lower abdomen 2. Use for 15-20 min 3. Repeat as needed",
            "Cramps, Back pain"
        )
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