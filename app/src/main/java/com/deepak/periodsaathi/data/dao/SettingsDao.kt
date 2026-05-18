package com.deepak.periodsaathi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deepak.periodsaathi.data.model.CycleSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM cycle_settings WHERE id = 1")
    fun getSettings(): Flow<CycleSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: CycleSettings)

    @Query("DELETE FROM cycle_settings")
    suspend fun deleteAll()
}

