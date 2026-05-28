package com.deepak.periodsaathi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deepak.periodsaathi.data.model.PartnerConnectionEntity
import com.deepak.periodsaathi.data.model.QuizAnswerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartnerDao {
    @Query("SELECT * FROM partner_connection WHERE id = 'active_connection' LIMIT 1")
    fun getActiveConnection(): Flow<PartnerConnectionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnection(connection: PartnerConnectionEntity)

    @Query("DELETE FROM partner_connection WHERE id = 'active_connection'")
    suspend fun clearConnection()

    @Query("SELECT * FROM quiz_answers WHERE quizId = :quizId")
    fun getAnswersForQuiz(quizId: String): Flow<List<QuizAnswerEntity>>

    @Query("SELECT * FROM quiz_answers")
    fun getAllAnswers(): Flow<List<QuizAnswerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: QuizAnswerEntity)

    @Query("DELETE FROM quiz_answers WHERE quizId = :quizId")
    suspend fun deleteAnswersForQuiz(quizId: String)

    @Query("DELETE FROM quiz_answers")
    suspend fun clearAllQuizAnswers()
}
