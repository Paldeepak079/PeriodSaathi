package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forum_bookmarks")
data class ForumBookmark(
    @PrimaryKey val postId: String,
    val createdAt: Long = System.currentTimeMillis()
)
