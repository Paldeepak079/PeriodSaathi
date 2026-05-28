package com.deepak.periodsaathi.data.dao

import androidx.room.*
import com.deepak.periodsaathi.data.model.ForumComment
import com.deepak.periodsaathi.data.model.ForumPost
import kotlinx.coroutines.flow.Flow

@Dao
interface ForumDao {

    @Query("SELECT * FROM forum_posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<ForumPost>>

    @Query("SELECT * FROM forum_posts WHERE category = :category ORDER BY createdAt DESC")
    fun getPostsByCategory(category: String): Flow<List<ForumPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: ForumPost)

    @Update
    suspend fun updatePost(post: ForumPost)

    @Delete
    suspend fun deletePost(post: ForumPost)

    @Query("SELECT * FROM forum_comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun getCommentsForPost(postId: String): Flow<List<ForumComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: ForumComment)

    @Update
    suspend fun updateComment(comment: ForumComment)

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
}
