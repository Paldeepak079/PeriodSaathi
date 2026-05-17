package com.example.periodsaathi.ui.screens.partner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CareRequest(
    val id: String,
    val emoji: String,
    val text: String
)

sealed class SendState {
    data object Idle : SendState()
    data object Sending : SendState()
    data object Sent : SendState()
}

@HiltViewModel
class PartnerViewModel @Inject constructor() : ViewModel() {

    private val _selectedRequests = MutableStateFlow<Set<String>>(emptySet())
    val selectedRequests: StateFlow<Set<String>> = _selectedRequests.asStateFlow()

    private val _customMessage = MutableStateFlow("")
    val customMessage: StateFlow<String> = _customMessage.asStateFlow()

    private val _sendState = MutableStateFlow<SendState>(SendState.Idle)
    val sendState: StateFlow<SendState> = _sendState.asStateFlow()

    private val _partnerName = MutableStateFlow("Aryan")
    val partnerName: StateFlow<String> = _partnerName.asStateFlow()

    val careRequests = listOf(
        CareRequest("1", "🤗", "I need a hug"),
        CareRequest("2", "🍫", "Bring me chocolate"),
        CareRequest("3", "☕", "Get me tea"),
        CareRequest("4", "🛌", "I need rest"),
        CareRequest("5", "💆", "Give me a massage"),
        CareRequest("6", "🎵", "Sing for me"),
        CareRequest("7", "🍕", "Order my favorite"),
        CareRequest("8", "💊", "Get my medicine"),
        CareRequest("9", "🚗", "Drive me somewhere"),
        CareRequest("10", "👂", "Just listen to me")
    )

    fun toggleRequest(id: String) {
        _selectedRequests.value = if (id in _selectedRequests.value) {
            _selectedRequests.value - id
        } else {
            _selectedRequests.value + id
        }
    }

    fun updateCustomMessage(message: String) {
        _customMessage.value = message
    }

    fun sendViaShareIntent() {
        _sendState.value = SendState.Sending
        viewModelScope.launch {
            delay(1500)
            _sendState.value = SendState.Sent
        }
    }

    fun resetState() {
        _sendState.value = SendState.Idle
        _selectedRequests.value = emptySet()
        _customMessage.value = ""
    }
}