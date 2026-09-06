package com.deepak.periodsaathi.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.deepak.periodsaathi.data.dao.ForumDao
import com.deepak.periodsaathi.data.model.ForumComment
import com.deepak.periodsaathi.data.model.ForumPost
import com.deepak.periodsaathi.data.remote.PocketBaseService
import com.deepak.periodsaathi.data.sync.DeviceIdentityManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.contentOrNull
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecretChatRepository @Inject constructor(
    private val forumDao: ForumDao,
    private val supabase: SupabaseClient,
    private val deviceIdentity: DeviceIdentityManager,
    private val pocketBaseService: PocketBaseService,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    @Volatile
    private var realtimeConnected = false

    fun getAllPosts(): Flow<List<ForumPost>> = forumDao.getAllPosts()
    fun getCommentsForPost(postId: String) = forumDao.getCommentsForPost(postId)

    suspend fun uploadImage(uriString: String): String {
        val pbUrl = pocketBaseService.uploadImage(uriString)
        if (pbUrl != null) return pbUrl

        return try {
            val uri = Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return uriString
            val original = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val maxSize = 800
            val scale = minOf(maxSize.toFloat() / original.width, maxSize.toFloat() / original.height, 1f)
            val resized = if (scale < 1f) {
                Bitmap.createScaledBitmap(
                    original,
                    (original.width * scale).toInt(),
                    (original.height * scale).toInt(),
                    true
                )
            } else original

            val dir = File(context.filesDir, "forum_images")
            dir.mkdirs()
            val fileName = "post_${UUID.randomUUID()}.jpg"
            val file = File(dir, fileName)
            file.outputStream().use { out ->
                resized.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }

            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            Log.e("SecretChatRepo", "Image save failed", e)
            uriString
        }
    }

    suspend fun insertPost(post: ForumPost) {
        forumDao.insertPost(post)
        try {
            supabase.from("forum_posts").insert(mapOf(
                "id" to post.id,
                "anonymous_alias" to post.anonymousAlias,
                "title" to post.title,
                "content" to post.content,
                "image_url" to (post.imageUrl ?: ""),
                "upvotes" to post.upvotes,
                "comment_count" to post.commentCount,
                "category" to post.category,
                "created_at" to post.createdAt,
                "device_id" to deviceIdentity.getDeviceUid()
            ))
            forumDao.markPostSynced(post.id)
        } catch (e: Exception) {
            Log.e("SecretChatRepo", "sync post failed", e)
        }
    }

    suspend fun insertComment(comment: ForumComment) {
        forumDao.insertComment(comment)
        try {
            supabase.from("forum_comments").insert(mapOf(
                "id" to comment.id,
                "post_id" to comment.postId,
                "anonymous_alias" to comment.anonymousAlias,
                "content" to comment.content,
                "upvotes" to comment.upvotes,
                "created_at" to comment.createdAt,
                "device_id" to deviceIdentity.getDeviceUid()
            ))
        } catch (e: Exception) {
            Log.e("SecretChatRepo", "sync comment failed", e)
        }
    }

    suspend fun fetchRemotePosts() {
        try {
            val rows = supabase.from("forum_posts").select().decodeList<JsonObject>()
            for (row in rows) {
                val id = row["id"]?.jsonPrimitive?.contentOrNull ?: continue
                if (forumDao.getPostByIdSync(id) == null) {
                    forumDao.insertPost(ForumPost(
                        id = id,
                        anonymousAlias = row["anonymous_alias"]?.jsonPrimitive?.contentOrNull ?: "Anonymous",
                        title = row["title"]?.jsonPrimitive?.contentOrNull ?: "",
                        content = row["content"]?.jsonPrimitive?.contentOrNull ?: "",
                        imageUrl = row["image_url"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotEmpty() },
                        upvotes = row["upvotes"]?.jsonPrimitive?.intOrNull ?: 0,
                        commentCount = row["comment_count"]?.jsonPrimitive?.intOrNull ?: 0,
                        category = row["category"]?.jsonPrimitive?.contentOrNull ?: "general",
                        createdAt = row["created_at"]?.jsonPrimitive?.longOrNull ?: System.currentTimeMillis(),
                        isUpvotedByMe = false,
                        synced = true
                    ))
                }
            }
        } catch (e: Exception) {
            Log.e("SecretChatRepo", "fetch remote posts failed", e)
        }
    }

    fun subscribeRealtime() {
        scope.launch {
            try {
                supabase.realtime.connect()
                realtimeConnected = true
                val channel = supabase.channel("forum-posts-channel")
                channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                    table = "forum_posts"
                }.collectLatest { action ->
                    val record = action.record
                    val id = record["id"]?.jsonPrimitive?.contentOrNull ?: return@collectLatest
                    if (forumDao.getPostByIdSync(id) == null) {
                        forumDao.insertPost(ForumPost(
                            id = id,
                            anonymousAlias = record["anonymous_alias"]?.jsonPrimitive?.contentOrNull ?: "Anonymous",
                            title = record["title"]?.jsonPrimitive?.contentOrNull ?: "",
                            content = record["content"]?.jsonPrimitive?.contentOrNull ?: "",
                            imageUrl = record["image_url"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotEmpty() },
                            upvotes = record["upvotes"]?.jsonPrimitive?.intOrNull ?: 0,
                            commentCount = record["comment_count"]?.jsonPrimitive?.intOrNull ?: 0,
                            category = record["category"]?.jsonPrimitive?.contentOrNull ?: "general",
                            createdAt = record["created_at"]?.jsonPrimitive?.longOrNull ?: System.currentTimeMillis(),
                            isUpvotedByMe = false,
                            synced = true
                        ))
                    }
                }
            } catch (e: Exception) {
                Log.e("SecretChatRepo", "realtime subscribe failed", e)
            }
        }
    }

    fun disconnectRealtime() {
        if (!realtimeConnected) return
        scope.launch {
            try {
                supabase.realtime.disconnect()
                realtimeConnected = false
            } catch (_: Exception) {}
        }
    }

    suspend fun upvotePost(postId: String) = forumDao.upvotePost(postId)
    suspend fun unvotePost(postId: String) = forumDao.unvotePost(postId)
    suspend fun incrementCommentCount(postId: String) = forumDao.incrementCommentCount(postId)
    suspend fun isPostBookmarked(postId: String): Boolean = forumDao.isPostBookmarked(postId)
    suspend fun insertBookmark(postId: String) = forumDao.insertBookmark(com.deepak.periodsaathi.data.model.ForumBookmark(postId = postId))
    suspend fun removeBookmark(postId: String) = forumDao.removeBookmark(postId)
    fun getAllBookmarkedPostIds() = forumDao.getAllBookmarkedPostIds()
    suspend fun insertNotification(notification: com.deepak.periodsaathi.data.model.ForumNotification) = forumDao.insertNotification(notification)
    fun getNotifications() = forumDao.getNotifications()
    fun getUnreadNotificationCount() = forumDao.getUnreadNotificationCount()
    suspend fun markAllNotificationsRead() = forumDao.markAllNotificationsRead()
}
