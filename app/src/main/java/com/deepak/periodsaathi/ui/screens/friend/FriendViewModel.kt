package com.deepak.periodsaathi.ui.screens.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.model.FriendEntity
import com.deepak.periodsaathi.data.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository
) : ViewModel() {

    private val _inviteCode = MutableStateFlow("")
    val inviteCode: StateFlow<String> = _inviteCode.asStateFlow()

    val friends: StateFlow<List<FriendEntity>> = friendRepository.getAllFriends()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLinking = MutableStateFlow(false)
    val isLinking: StateFlow<Boolean> = _isLinking.asStateFlow()

    private val _linkError = MutableStateFlow<String?>(null)
    val linkError: StateFlow<String?> = _linkError.asStateFlow()

    init {
        loadInviteCode()
    }

    private fun loadInviteCode() {
        viewModelScope.launch {
            _inviteCode.value = friendRepository.getInviteCode()
        }
    }

    fun linkFriend(code: String, name: String) {
        if (code.isBlank()) {
            _linkError.value = "Please enter a friend's invite code"
            return
        }
        viewModelScope.launch {
            _isLinking.value = true
            _linkError.value = null
            val success = friendRepository.linkFriend(code, name)
            _isLinking.value = false
            if (success) {
                _linkError.value = null
            } else {
                _linkError.value = "Invalid invite code. Must be 6 characters."
            }
        }
    }

    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            friendRepository.removeFriend(friendId)
        }
    }

    fun clearError() {
        _linkError.value = null
    }
}
