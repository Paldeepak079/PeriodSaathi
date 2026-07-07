package com.deepak.periodsaathi.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseClientWrapper @Inject constructor(
    val client: SupabaseClient
) {
    suspend fun insertPost(post: Map<String, Any>) {
        client.from("forum_posts").insert(post)
    }

    suspend fun insertComment(comment: Map<String, Any>) {
        client.from("forum_comments").insert(comment)
    }

    suspend fun fetchPosts(): List<Map<String, Any>> {
        return client.from("forum_posts").select {
            order("created_at", Order.DESCENDING)
        }.decodeList()
    }

    suspend fun fetchComments(postId: String): List<Map<String, Any>> {
        return client.from("forum_comments").select {
            filter { eq("post_id", postId) }
            order("created_at", Order.ASCENDING)
        }.decodeList()
    }

    suspend fun updatePostUpvotes(postId: String, upvotes: Int) {
        client.from("forum_posts").update(
            mapOf("upvotes" to upvotes)
        ) { filter { eq("id", postId) } }
    }

    suspend fun updatePostCommentCount(postId: String, count: Int) {
        client.from("forum_posts").update(
            mapOf("comment_count" to count)
        ) { filter { eq("id", postId) } }
    }
}
