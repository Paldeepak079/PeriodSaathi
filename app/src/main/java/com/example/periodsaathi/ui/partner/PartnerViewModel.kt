package com.example.periodsaathi.ui.partner

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed class SendState {
    data object Idle : SendState()
    data object Sending : SendState()
    data object Sent : SendState()
    data class Error(val message: String) : SendState()
}

data class CareRequest(
    val id: String,
    val emoji: String,
    val text: String
)

data class PartnerSettings(
    val partnerName: String = "",
    val partnerPhone: String = "",
    val userName: String = ""
)

class PartnerViewModel : ViewModel() {

    private val _selectedRequests = MutableStateFlow<Set<String>>(emptySet())
    val selectedRequests: StateFlow<Set<String>> = _selectedRequests.asStateFlow()

    private val _customMessage = MutableStateFlow("")
    val customMessage: StateFlow<String> = _customMessage.asStateFlow()

    private val _sendState = MutableStateFlow<SendState>(SendState.Idle)
    val sendState: StateFlow<SendState> = _sendState.asStateFlow()

    private val _partnerName = MutableStateFlow("")
    val partnerName: StateFlow<String> = _partnerName.asStateFlow()

    private val _isPartnerLinked = MutableStateFlow(false)
    val isPartnerLinked: StateFlow<Boolean> = _isPartnerLinked.asStateFlow()

    private val _partnerCode = MutableStateFlow<String?>(null)
    val partnerCode: StateFlow<String?> = _partnerCode.asStateFlow()

    private val _showPrivacyExplanation = MutableStateFlow(false)
    val showPrivacyExplanation: StateFlow<Boolean> = _showPrivacyExplanation.asStateFlow()

    val careRequests = listOf(
        CareRequest("1", "🍫", "Need chocolate & a hug"),
        CareRequest("2", "🎧", "Just want quiet time"),
        CareRequest("3", "💆", "Could use a massage"),
        CareRequest("4", "🍵", "Make me ginger tea"),
        CareRequest("5", "👂", "Just listen, don't fix"),
        CareRequest("6", "🛌", "I need to rest today"),
        CareRequest("7", "😭", "Having a rough time"),
        CareRequest("8", "🌸", "Send me good vibes"),
        CareRequest("9", "🫂", "Need a hug right now"),
        CareRequest("10", "🎬", "Watch something together")
    )

    fun toggleRequest(requestId: String) {
        _selectedRequests.update { current ->
            if (current.contains(requestId)) {
                current - requestId
            } else {
                current + requestId
            }
        }
    }

    fun updateCustomMessage(msg: String) {
        _customMessage.value = msg.take(50)
    }

    fun togglePrivacyExplanation() {
        _showPrivacyExplanation.update { !it }
    }

    fun sendCareRequest(context: Context) {
        viewModelScope.launch {
            _sendState.value = SendState.Sending
            delay(1500)

            try {
                val requests = careRequests.filter { _selectedRequests.value.contains(it.id) }
                val message = buildMessage(requests, _customMessage.value)

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:")
                    putExtra("sms_body", message)
                }

                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                    _sendState.value = SendState.Sent
                } else {
                    val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("sms:")
                        putExtra("sms_body", message)
                    }
                    context.startActivity(smsIntent)
                    _sendState.value = SendState.Sent
                }

                delay(3000)
                _sendState.value = SendState.Idle
                _selectedRequests.value = emptySet()
                _customMessage.value = ""
            } catch (e: Exception) {
                _sendState.value = SendState.Error("Failed to send message")
                delay(2000)
                _sendState.value = SendState.Idle
            }
        }
    }

    private fun buildMessage(requests: List<CareRequest>, customMsg: String): String {
        val userName = _partnerName.value.ifEmpty { "Your friend" }
        val partnerDisplay = _partnerName.value.ifEmpty { "partner" }

        val sb = StringBuilder()
        sb.append("Hey $partnerDisplay! $userName needs some care right now:\n\n")

        requests.forEach { request ->
            sb.append("• ${request.emoji} ${request.text}\n")
        }

        if (customMsg.isNotEmpty()) {
            sb.append("\n${customMsg}")
        }

        sb.append("\n\n💕 Sent from Period Saathi")

        return sb.toString()
    }

    fun generatePartnerCode(): String {
        val code = Random.nextInt(100000, 999999).toString()
        _partnerCode.value = code
        _isPartnerLinked.value = true
        _partnerName.value = "Partner"
        return code
    }

    fun setPartnerInfo(name: String, code: String) {
        _partnerName.value = name
        _partnerCode.value = code
        _isPartnerLinked.value = true
    }

    fun resetSendState() {
        _sendState.value = SendState.Idle
    }
}