package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "forum_posts")
data class ForumPost(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val anonymousAlias: String,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val upvotes: Int = 0,
    val commentCount: Int = 0,
    /** general | cramps | mood | fertility | vent */
    val category: String = "general",
    val createdAt: Long = System.currentTimeMillis(),
    val isUpvotedByMe: Boolean = false,
    val synced: Boolean = false
)
