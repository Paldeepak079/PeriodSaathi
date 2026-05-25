package com.deepak.periodsaathi.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.deepak.periodsaathi.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntry): Long

    @Update
    suspend fun updateEntry(entry: JournalEntry)

    @Delete
    suspend fun deleteEntry(entry: JournalEntry)

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): JournalEntry?

    @Query("SELECT * FROM journal_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    @Query(
        """
        SELECT * FROM journal_entries 
        WHERE isTimeCapsule = 1 
        AND capsuleRevealDate IS NOT NULL 
        AND capsuleRevealDate <= :currentDate 
        AND isRevealed = 0
        """
    )
    fun getTimeCapsuleForReveal(currentDate: Long): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE date >= :start AND date <= :end ORDER BY date ASC")
    fun getEntriesByDateRange(start: Long, end: Long): Flow<List<JournalEntry>>

    @Query("DELETE FROM journal_entries")
    suspend fun deleteAll()
}

