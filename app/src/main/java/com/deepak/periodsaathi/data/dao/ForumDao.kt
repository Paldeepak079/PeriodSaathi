package com.deepak.periodsaathi.data.dao

import androidx.room.*
import com.deepak.periodsaathi.data.model.ForumBookmark
import com.deepak.periodsaathi.data.model.ForumComment
import com.deepak.periodsaathi.data.model.ForumNotification
import com.deepak.periodsaathi.data.model.ForumPost
import kotlinx.coroutines.flow.Flow

@Dao
interface ForumDao {

    // ── Posts ──────────────────────────────────────────────
    @Query("SELECT * FROM forum_posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<ForumPost>>

    @Query("SELECT * FROM forum_posts WHERE category = :category ORDER BY createdAt DESC")
    fun getPostsByCategory(category: String): Flow<List<ForumPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: ForumPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: ForumComment)

    @Query("SELECT * FROM forum_comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun getCommentsForPost(postId: String): Flow<List<ForumComment>>

    @Update
    suspend fun updatePost(post: ForumPost)

    @Delete
    suspend fun deletePost(post: ForumPost)

    @Query("UPDATE forum_posts SET upvotes = upvotes + 1, isUpvotedByMe = 1 WHERE id = :postId")
    suspend fun upvotePost(postId: String)

    @Query("UPDATE forum_posts SET upvotes = upvotes - 1, isUpvotedByMe = 0 WHERE id = :postId AND upvotes > 0")
    suspend fun unvotePost(postId: String)

    @Query("UPDATE forum_posts SET commentCount = commentCount + 1 WHERE id = :postId")
    suspend fun incrementCommentCount(postId: String)

    @Query("SELECT * FROM forum_posts WHERE synced = 0")
    suspend fun getUnsyncedPosts(): List<ForumPost>

    @Query("UPDATE forum_posts SET synced = 1 WHERE id = :postId")
    suspend fun markPostSynced(postId: String)

    @Query("SELECT * FROM forum_posts WHERE id = :postId LIMIT 1")
    suspend fun getPostByIdSync(postId: String): ForumPost?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: com.deepak.periodsaathi.data.model.ForumBookmark)

    @Query("DELETE FROM forum_bookmarks WHERE postId = :postId")
    suspend fun removeBookmark(postId: String)

    @Query("SELECT postId FROM forum_bookmarks")
    fun getAllBookmarkedPostIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM forum_bookmarks WHERE postId = :postId)")
    suspend fun isPostBookmarked(postId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: com.deepak.periodsaathi.data.model.ForumNotification)

    @Query("SELECT * FROM forum_notifications ORDER BY createdAt DESC")
    fun getNotifications(): Flow<List<com.deepak.periodsaathi.data.model.ForumNotification>>

    @Query("SELECT COUNT(*) FROM forum_notifications WHERE read = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    @Query("UPDATE forum_notifications SET read = 1 WHERE read = 0")
    suspend fun markAllNotificationsRead()
}
