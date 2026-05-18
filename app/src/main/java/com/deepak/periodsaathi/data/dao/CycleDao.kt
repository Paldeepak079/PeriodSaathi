package com.deepak.periodsaathi.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.deepak.periodsaathi.data.model.CycleEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: CycleEntry): Long

    @Update
    suspend fun updateEntry(entry: CycleEntry)

    @Delete
    suspend fun deleteEntry(entry: CycleEntry)

    @Query("SELECT * FROM cycle_entries WHERE date >= :start AND date <= :end ORDER BY date ASC")
    fun getEntriesBetweenDates(start: Long, end: Long): Flow<List<CycleEntry>>

    @Query("SELECT * FROM cycle_entries WHERE date = :date LIMIT 1")
    fun getEntryByDate(date: Long): Flow<CycleEntry?>

    @Query("SELECT * FROM cycle_entries ORDER BY date DESC LIMIT :n")
    fun getLastNEntries(n: Int): Flow<List<CycleEntry>>

    @Query("SELECT * FROM cycle_entries WHERE flowIntensity IS NOT NULL ORDER BY date DESC")
    fun getPeriodEntries(): Flow<List<CycleEntry>>

    @Query("SELECT COALESCE(SUM(waterGlasses), 0) FROM cycle_entries WHERE date = :date")
    fun getWaterTotalForDate(date: Long): Flow<Int>

    @Query("SELECT * FROM cycle_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<CycleEntry>>

    @Query("DELETE FROM cycle_entries")
    suspend fun deleteAll()
}

