package com.deepak.periodsaathi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deepak.periodsaathi.data.model.FriendEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Query("SELECT * FROM synced_friends ORDER BY connectedAt DESC")
    fun getAllFriends(): Flow<List<FriendEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendEntity)

    @Query("DELETE FROM synced_friends WHERE id = :friendId")
    suspend fun deleteFriend(friendId: String)

    @Query("DELETE FROM synced_friends")
    suspend fun clearAllFriends()
}
