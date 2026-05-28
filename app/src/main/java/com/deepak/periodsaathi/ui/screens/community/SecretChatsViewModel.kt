package com.deepak.periodsaathi.ui.screens.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.ForumDao
import com.deepak.periodsaathi.data.model.ForumComment
import com.deepak.periodsaathi.data.model.ForumPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private val ANONYMOUS_ALIASES = listOf(
    "Cosmic Lily", "Gentle Moon", "Wild Poppy", "Silver Fern",
    "Calm Lotus", "Brave Dahlia", "Quiet Willow", "Bright Star",
    "Free Spirit", "Soft Cloud", "Kind Sage", "Bold Iris"
)

data class SecretChatsUiState(
    val selectedCategory: String = "all",
    val isComposingPost: Boolean = false,
    val draftTitle: String = "",
    val draftContent: String = "",
    val draftImageUrl: String? = null,
    val expandedPostId: String? = null,
    val commentDraft: String = "",
    val isPosting: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SecretChatsViewModel @Inject constructor(
    private val forumDao: ForumDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecretChatsUiState())
    val uiState: StateFlow<SecretChatsUiState> = _uiState.asStateFlow()

    val allPosts: StateFlow<List<ForumPost>> = forumDao.getAllPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val myAlias = ANONYMOUS_ALIASES.random()

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
                forumDao.insertPost(
                    ForumPost(
                        anonymousAlias = myAlias,
                        title = s.draftTitle.trim(),
                        content = s.draftContent.trim(),
                        imageUrl = s.draftImageUrl,
                        category = s.selectedCategory.takeIf { it != "all" } ?: "general"
                    )
                )
                _uiState.value = _uiState.value.copy(
                    isComposingPost = false,
                    draftTitle = "",
                    draftContent = "",
                    draftImageUrl = null,
                    isPosting = false
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
                forumDao.unvotePost(post.id)
            } else {
                forumDao.upvotePost(post.id)
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
                forumDao.insertComment(
                    ForumComment(postId = postId, anonymousAlias = myAlias, content = draft)
                )
                forumDao.incrementCommentCount(postId)
                _uiState.value = _uiState.value.copy(commentDraft = "")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to post comment.")
            }
        }
    }

    fun getCommentsForPost(postId: String) = forumDao.getCommentsForPost(postId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
