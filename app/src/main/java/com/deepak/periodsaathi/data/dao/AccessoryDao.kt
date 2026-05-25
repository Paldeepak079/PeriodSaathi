package com.deepak.periodsaathi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.deepak.periodsaathi.data.model.AccessoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessoryDao {

    @Query("SELECT * FROM accessories ORDER BY pointsCost ASC")
    fun getAllAccessories(): Flow<List<AccessoryEntity>>

    @Query("SELECT * FROM accessories WHERE equipped = 1")
    fun getEquippedAccessories(): Flow<List<AccessoryEntity>>

    @Query("SELECT * FROM accessories WHERE id = :id")
    suspend fun getAccessoryById(id: String): AccessoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAccessory(accessory: AccessoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(accessories: List<AccessoryEntity>)

    @Update
    suspend fun updateAccessory(accessory: AccessoryEntity)

    @Query("UPDATE accessories SET equipped = :equipped WHERE id = :id")
    suspend fun setEquipped(id: String, equipped: Boolean)

    @Query("UPDATE accessories SET unlocked = :unlocked WHERE id = :id")
    suspend fun setUnlocked(id: String, unlocked: Boolean)

    @Query("SELECT COUNT(*) FROM accessories WHERE equipped = 1")
    fun getEquippedCount(): Flow<Int>

    @Query("DELETE FROM accessories")
    suspend fun deleteAll()
}
