package com.deepak.periodsaathi.ui.screens.remedies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.RemedyDao
import com.deepak.periodsaathi.data.model.RemedyEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Remedy(val id: String, val name: String, val emoji: String, val ingredients: String, val steps: String, val helpsWith: String, val isFavorited: Boolean = false)

private val SEED_REMEDIES = listOf(
    RemedyEntity("1", "Ginger Tea", "\uD83E\uDD5A", "1 inch ginger, 1 cup water, honey", "Boil ginger 5 min, strain, add honey", "Bloatedness, cramps"),
    RemedyEntity("2", "Turmeric Milk", "\uD83E\uDD5B", "1 cup milk, 1 tsp turmeric, pinch black pepper", "Warm milk, add turmeric, stir well", "Inflammation, mood"),
    RemedyEntity("3", "Hot Compress", "\uD83D\uDD25", "Hot water bottle or heating pad", "Apply to lower abdomen for 15-20 mins", "Cramps, back pain"),
    RemedyEntity("4", "Magnesium Foods", "\uD83E\uDD6C", "Leafy greens, nuts, seeds, dark chocolate", "Incorporate into daily meals", "Muscle tension, mood"),
    RemedyEntity("5", "Dark Chocolate", "\uD83C\uDF6B", "70%+ dark chocolate", "Enjoy in moderation", "Mood boost, cravings"),
    RemedyEntity("6", "Chamomile Tea", "\uD83C\uDF3C", "1 tsp chamomile, hot water, honey", "Steep 5 mins, strain, enjoy", "Anxiety, sleep"),
    RemedyEntity("7", "Fennel Seeds", "\uD83C\uDF3F", "1 tsp fennel, hot water", "Steep 10 mins, sip slowly", "Bloating, digestion"),
    RemedyEntity("8", "Heating Pad", "\uD83D\uDD0C", "Electric or microwavable heating pad", "Use on low-medium setting", "Cramps relief")
)

@HiltViewModel
class RemediesViewModel @Inject constructor(
    private val remedyDao: RemedyDao
) : ViewModel() {

    private val _remedies = MutableStateFlow<List<Remedy>>(emptyList())
    val remedies: StateFlow<List<Remedy>> = _remedies.asStateFlow()

    private val _flippedCards = MutableStateFlow<Set<String>>(emptySet())
    val flippedCards: StateFlow<Set<String>> = _flippedCards.asStateFlow()

    private val _hotBagPosition = MutableStateFlow(0.5f)
    val hotBagPosition: StateFlow<Float> = _hotBagPosition.asStateFlow()

    init {
        viewModelScope.launch {
            val existing = remedyDao.getAllRemedies().first()
            if (existing.isEmpty()) {
                remedyDao.upsertAll(SEED_REMEDIES)
            }
        }
        viewModelScope.launch {
            remedyDao.getAllRemedies().collect { entities ->
                _remedies.value = entities.map { entity ->
                    Remedy(
                        id = entity.id,
                        name = entity.name,
                        emoji = entity.emoji,
                        ingredients = entity.ingredients,
                        steps = entity.steps,
                        helpsWith = entity.helpsWith,
                        isFavorited = entity.isFavorited
                    )
                }
            }
        }
    }

    fun flipCard(id: String) {
        _flippedCards.value = if (id in _flippedCards.value) {
            _flippedCards.value - id
        } else {
            _flippedCards.value + id
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            val entity = remedyDao.getRemedyById(id) ?: return@launch
            remedyDao.setFavorited(id, !entity.isFavorited)
        }
    }

    fun updateHotBagPosition(position: Float) {
        _hotBagPosition.value = position.coerceIn(0f, 1f)
    }
}
