package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "forum_notifications")
data class ForumNotification(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val postId: String,
    val type: String, // "upvote" | "reply"
    val message: String,
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
