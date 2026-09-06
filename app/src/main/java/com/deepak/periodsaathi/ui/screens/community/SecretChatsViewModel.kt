package com.deepak.periodsaathi.ui.screens.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.model.ForumComment
import com.deepak.periodsaathi.data.model.ForumNotification
import com.deepak.periodsaathi.data.model.ForumPost
import com.deepak.periodsaathi.data.repository.SecretChatRepository
import com.deepak.periodsaathi.data.sync.DeviceIdentityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SecretChatsUiState(
    val selectedCategory: String = "all",
    val isComposingPost: Boolean = false,
    val draftTitle: String = "",
    val draftContent: String = "",
    val draftImageUrl: String? = null,
    val expandedPostId: String? = null,
    val commentDraft: String = "",
    val isPosting: Boolean = false,
    val error: String? = null,
    val showNotifications: Boolean = false,
    val viewImageUrl: String? = null
)

@HiltViewModel
class SecretChatsViewModel @Inject constructor(
    private val repository: SecretChatRepository,
    private val deviceIdentity: DeviceIdentityManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecretChatsUiState())
    val uiState: StateFlow<SecretChatsUiState> = _uiState.asStateFlow()

    val allPosts: StateFlow<List<ForumPost>> = repository.getAllPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<ForumNotification>> = repository.getNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> = repository.getUnreadNotificationCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val myAlias = deviceIdentity.getAnonymousAlias()

    init {
        viewModelScope.launch {
            repository.fetchRemotePosts()
            repository.subscribeRealtime()
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnectRealtime()
    }

    fun setCategory(cat: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = cat)
    }

    fun startComposing() {
        _uiState.value = _uiState.value.copy(isComposingPost = true)
    }

    fun cancelComposing() {
        _uiState.value = _uiState.value.copy(
            isComposingPost = false, draftTitle = "", draftContent = "", draftImageUrl = null
        )
    }

    fun updateDraftTitle(text: String) {
        _uiState.value = _uiState.value.copy(draftTitle = text)
    }

    fun updateDraftContent(text: String) {
        _uiState.value = _uiState.value.copy(draftContent = text)
    }

    fun updateDraftImageUrl(url: String?) {
        _uiState.value = _uiState.value.copy(draftImageUrl = url)
    }

    fun submitPost() {
        val s = _uiState.value
        if (s.draftTitle.isBlank() || s.draftContent.isBlank()) return
        _uiState.value = s.copy(isPosting = true)
        viewModelScope.launch {
            try {
                val imageUrl = if (s.draftImageUrl != null && s.draftImageUrl.startsWith("content://")) {
                    repository.uploadImage(s.draftImageUrl)
                } else {
                    s.draftImageUrl
                }

                val post = ForumPost(
                    anonymousAlias = myAlias,
                    title = s.draftTitle.trim(),
                    content = s.draftContent.trim(),
                    imageUrl = imageUrl,
                    category = s.selectedCategory.takeIf { it != "all" } ?: "general"
                )
                repository.insertPost(post)
                _uiState.value = _uiState.value.copy(
                    isComposingPost = false, draftTitle = "", draftContent = "",
                    draftImageUrl = null, isPosting = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isPosting = false, error = "Failed to post. Please try again."
                )
            }
        }
    }

    fun toggleUpvote(post: ForumPost) {
        viewModelScope.launch {
            if (post.isUpvotedByMe) {
                repository.unvotePost(post.id)
            } else {
                repository.upvotePost(post.id)
                repository.insertNotification(ForumNotification(
                    postId = post.id, type = "upvote",
                    message = "${post.anonymousAlias} received a like 💕"
                ))
            }
        }
    }

    fun expandPost(postId: String) {
        _uiState.value = _uiState.value.copy(
            expandedPostId = if (_uiState.value.expandedPostId == postId) null else postId
        )
    }

    fun updateCommentDraft(text: String) {
        _uiState.value = _uiState.value.copy(commentDraft = text)
    }

    fun submitComment(postId: String) {
        val draft = _uiState.value.commentDraft.trim()
        if (draft.isBlank()) return
        viewModelScope.launch {
            try {
                repository.insertComment(ForumComment(
                    postId = postId, anonymousAlias = myAlias, content = draft
                ))
                repository.incrementCommentCount(postId)
                repository.insertNotification(ForumNotification(
                    postId = postId, type = "reply",
                    message = "${myAlias} replied to a post 💬"
                ))
                _uiState.value = _uiState.value.copy(commentDraft = "")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to post comment.")
            }
        }
    }

    fun getCommentsForPost(postId: String): StateFlow<List<ForumComment>> =
        repository.getCommentsForPost(postId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun toggleBookmark(postId: String) {
        viewModelScope.launch {
            if (repository.isPostBookmarked(postId)) {
                repository.removeBookmark(postId)
            } else {
                repository.insertBookmark(postId)
            }
        }
    }

    fun getBookmarkedIds() = repository.getAllBookmarkedPostIds()

    fun markAllNotificationsRead() {
        viewModelScope.launch { repository.markAllNotificationsRead() }
    }

    fun toggleNotifications() {
        val newVal = !_uiState.value.showNotifications
        _uiState.value = _uiState.value.copy(showNotifications = newVal)
        if (newVal) {
            viewModelScope.launch { repository.markAllNotificationsRead() }
        }
    }

    fun dismissNotifications() {
        _uiState.value = _uiState.value.copy(showNotifications = false)
    }

    fun viewImage(url: String?) {
        _uiState.value = _uiState.value.copy(viewImageUrl = url)
    }

    fun dismissImagePreview() {
        _uiState.value =         _uiState.value.copy(viewImageUrl = null)
    }
}
