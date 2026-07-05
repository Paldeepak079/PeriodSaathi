package com.deepak.periodsaathi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deepak.periodsaathi.data.model.RemedyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RemedyDao {

    @Query("SELECT * FROM remedies ORDER BY name ASC")
    fun getAllRemedies(): Flow<List<RemedyEntity>>

    @Query("SELECT * FROM remedies WHERE isFavorited = 1")
    fun getFavoriteRemedies(): Flow<List<RemedyEntity>>

    @Query("SELECT * FROM remedies WHERE id = :id")
    suspend fun getRemedyById(id: String): RemedyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRemedy(remedy: RemedyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(remedies: List<RemedyEntity>)

    @Query("UPDATE remedies SET isFavorited = :favorited WHERE id = :id")
    suspend fun setFavorited(id: String, favorited: Boolean)

    @Query("DELETE FROM remedies")
    suspend fun deleteAll()
}
