package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_answers")
data class QuizAnswerEntity(
    @PrimaryKey
    val id: String, // Composite key: quizId_questionId_answeredBy
    val quizId: String,
    val questionId: String,
    val answerIndex: Int,
    val answeredBy: String, // "PRIMARY" or "PARTNER"
    val answeredAt: Long
)
