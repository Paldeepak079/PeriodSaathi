package com.deepak.periodsaathi.wellness.data.local

import androidx.room.*
import com.deepak.periodsaathi.wellness.data.local.entities.CoinTransactionEntity
import com.deepak.periodsaathi.wellness.data.local.entities.SolutionEntity
import com.deepak.periodsaathi.wellness.data.local.entities.TipEntity
import com.deepak.periodsaathi.wellness.data.local.entities.UserWellnessStatsEntity
import com.deepak.periodsaathi.wellness.data.local.entities.WellnessLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WellnessDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: WellnessLogEntity)

    @Query("SELECT * FROM wellness_logs WHERE date = :date LIMIT 1")
    fun getLogForDate(date: Long): Flow<WellnessLogEntity?>

    @Query("SELECT * FROM wellness_logs WHERE date = :date LIMIT 1")
    suspend fun getLogForDateSync(date: Long): WellnessLogEntity?

    @Query("SELECT * FROM wellness_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<WellnessLogEntity>>

    @Query("SELECT * FROM wellness_logs WHERE date >= :sinceDate ORDER BY date ASC")
    fun getLogsSince(sinceDate: Long): Flow<List<WellnessLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTips(tips: List<TipEntity>)

    @Query("SELECT * FROM wellness_tips")
    fun getTips(): Flow<List<TipEntity>>

    @Query("UPDATE wellness_tips SET read = 1 WHERE id = :tipId")
    suspend fun markTipAsRead(tipId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinTransaction(tx: CoinTransactionEntity)

    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC")
    fun getCoinTransactions(): Flow<List<CoinTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: UserWellnessStatsEntity)

    @Query("SELECT * FROM user_wellness_stats WHERE id = 'singleton' LIMIT 1")
    fun getStats(): Flow<UserWellnessStatsEntity?>

    @Query("SELECT * FROM user_wellness_stats WHERE id = 'singleton' LIMIT 1")
    suspend fun getStatsSync(): UserWellnessStatsEntity?

    @Query("SELECT * FROM wellness_logs WHERE synced = 0")
    suspend fun getUnsyncedLogs(): List<WellnessLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolutions(solutions: List<SolutionEntity>)

    @Query("SELECT COUNT(*) FROM solutions")
    suspend fun getSolutionCount(): Int

    @Query("SELECT * FROM solutions")
    fun getAllSolutions(): Flow<List<SolutionEntity>>

    @Query("SELECT * FROM solutions WHERE category = :category")
    fun getSolutionsByCategory(category: String): Flow<List<SolutionEntity>>

    @Query("SELECT * FROM solutions WHERE symptomType = :symptom OR symptomType = 'general'")
    fun getSolutionsBySymptom(symptom: String): Flow<List<SolutionEntity>>

    @Query("SELECT * FROM solutions WHERE severity = :severity OR severity = 'all'")
    fun getSolutionsBySeverity(severity: String): Flow<List<SolutionEntity>>

    @Query("SELECT * FROM solutions WHERE category = :category AND (symptomType = :symptom OR symptomType = 'general') AND (severity = :severity OR severity = 'all')")
    fun getFilteredSolutions(category: String, symptom: String, severity: String): Flow<List<SolutionEntity>>

    @Query("UPDATE solutions SET isFavorite = :fav WHERE id = :id")
    suspend fun toggleFavorite(id: String, fav: Boolean)

    @Query("SELECT * FROM solutions ORDER BY isFavorite DESC, id ASC")
    fun getAllSolutionsSorted(): Flow<List<SolutionEntity>>
}
