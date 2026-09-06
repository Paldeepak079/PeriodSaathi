package com.deepak.periodsaathi.data.repository

import com.deepak.periodsaathi.data.dao.FriendDao
import com.deepak.periodsaathi.data.model.FriendEntity
import com.deepak.periodsaathi.security.PartnerSyncManager
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface FriendRepository {
    fun getAllFriends(): Flow<List<FriendEntity>>
    suspend fun getInviteCode(): String
    suspend fun linkFriend(code: String, name: String): Boolean
    suspend fun removeFriend(friendId: String)
}

@Singleton
class FriendRepositoryImpl @Inject constructor(
    private val friendDao: FriendDao,
    private val partnerSyncManager: PartnerSyncManager
) : FriendRepository {

    override fun getAllFriends(): Flow<List<FriendEntity>> {
        return friendDao.getAllFriends()
    }

    override suspend fun getInviteCode(): String {
        return partnerSyncManager.getOrCreateCode()
    }

    override suspend fun linkFriend(code: String, name: String): Boolean {
        val cleaned = code.trim().uppercase()
        if (cleaned.length != 6 || cleaned.isBlank()) return false

        val friend = FriendEntity(
            id = UUID.randomUUID().toString(),
            name = name.ifBlank { "Friend" },
            inviteCode = cleaned,
            status = "SYNCED",
            connectedAt = System.currentTimeMillis(),
            cyclePhase = "",
            cycleDay = 0,
            totalDays = 28
        )
        friendDao.insertFriend(friend)
        return true
    }

    override suspend fun removeFriend(friendId: String) {
        friendDao.deleteFriend(friendId)
    }
}
