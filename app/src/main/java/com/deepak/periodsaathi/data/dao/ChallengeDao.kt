package com.deepak.periodsaathi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.deepak.periodsaathi.data.model.ChallengeProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {

    @Query("SELECT * FROM challenge_progress")
    fun getAllProgress(): Flow<List<ChallengeProgressEntity>>

    @Query("SELECT * FROM challenge_progress WHERE challengeId = :challengeId LIMIT 1")
    fun getProgressByChallengeId(challengeId: String): Flow<ChallengeProgressEntity?>

    @Query("SELECT * FROM challenge_progress WHERE challengeId = :challengeId LIMIT 1")
    suspend fun getProgressByChallengeIdOnce(challengeId: String): ChallengeProgressEntity?

    @Query("SELECT * FROM challenge_progress WHERE completed = 0")
    fun getActiveProgress(): Flow<List<ChallengeProgressEntity>>

    @Query("SELECT * FROM challenge_progress WHERE completed = 1")
    fun getCompletedProgress(): Flow<List<ChallengeProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ChallengeProgressEntity)

    @Update
    suspend fun updateProgress(progress: ChallengeProgressEntity)

    @Query("UPDATE challenge_progress SET progress = :progress, completed = :completed, lastUpdated = :lastUpdated WHERE challengeId = :challengeId")
    suspend fun updateProgressByChallengeId(challengeId: String, progress: Float, completed: Boolean, lastUpdated: Long)

    @Query("DELETE FROM challenge_progress")
    suspend fun deleteAll()
}
