package com.deepak.periodsaathi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.deepak.periodsaathi.data.model.HabitCompletion
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertHabitCompletion(habitCompletion: HabitCompletion): Long

    @Update
    suspend fun updateHabitCompletion(habitCompletion: HabitCompletion)

    @Query("SELECT * FROM habit_completions WHERE date = :date")
    fun getHabitCompletionsByDate(date: Long): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND date = :date LIMIT 1")
    fun getHabitCompletionByIdAndDate(habitId: String, date: Long): Flow<HabitCompletion?>
}