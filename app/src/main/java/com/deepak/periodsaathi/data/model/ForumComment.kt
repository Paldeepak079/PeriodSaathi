package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "forum_comments")
data class ForumComment(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val postId: String,
    val anonymousAlias: String,
    val content: String,
    val upvotes: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isUpvotedByMe: Boolean = false,
    val synced: Boolean = false
)
