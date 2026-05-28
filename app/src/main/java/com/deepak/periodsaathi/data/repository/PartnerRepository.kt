package com.deepak.periodsaathi.data.repository

import com.deepak.periodsaathi.data.dao.PartnerDao
import com.deepak.periodsaathi.data.model.PartnerConnectionEntity
import com.deepak.periodsaathi.data.model.QuizAnswerEntity
import com.deepak.periodsaathi.domain.model.CyclePhase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

// Sealed representation of the synced read-only insights for the partner
data class PartnerCycleInsights(
    val cycleDay: Int,
    val phase: CyclePhase,
    val ovulationDaysAway: Int,
    val conceptionChance: String, // Low, Medium, High
    val expectedPeriodDaysAway: Int,
    val lastSyncTime: Long
)

interface PartnerRepository {
    fun getActiveConnection(): Flow<PartnerConnectionEntity?>
    suspend fun generateInviteCode(partnerName: String): String
    suspend fun connectWithCode(code: String, partnerName: String): Boolean
    suspend fun revokeConnection()
    fun getQuizAnswers(quizId: String): Flow<List<QuizAnswerEntity>>
    fun getAllQuizAnswers(): Flow<List<QuizAnswerEntity>>
    suspend fun submitQuizAnswer(quizId: String, questionId: String, answerIndex: Int, answeredBy: String)
    fun getPartnerCycleInsights(): Flow<PartnerCycleInsights>
}

@Singleton
class PartnerRepositoryImpl @Inject constructor(
    private val partnerDao: PartnerDao,
    private val cycleRepository: CycleRepository
) : PartnerRepository {

    // Simulating Firestore Real-time synchronization stream for the dashboard
    private val _realtimeSyncTrigger = MutableStateFlow(System.currentTimeMillis())
    
    override fun getActiveConnection(): Flow<PartnerConnectionEntity?> {
        return partnerDao.getActiveConnection()
    }

    override suspend fun generateInviteCode(partnerName: String): String {
        val code = (100000..999999).random().toString()
        val connection = PartnerConnectionEntity(
            inviteCode = code,
            partnerUserId = UUID.randomUUID().toString(),
            partnerName = partnerName,
            status = "PENDING",
            connectedAt = System.currentTimeMillis(),
            isPrimary = true
        )
        partnerDao.insertConnection(connection)
        return code
    }

    override suspend fun connectWithCode(code: String, partnerName: String): Boolean {
        // In real-world apps, this would check Firestore for the shared connection document.
        // We simulate a network handshake delay, then activate the connection in Room.
        delay(1200) 
        if (code.length == 6 && code.all { it.isDigit() }) {
            val connection = PartnerConnectionEntity(
                inviteCode = code,
                partnerUserId = UUID.randomUUID().toString(),
                partnerName = partnerName,
                status = "CONNECTED",
                connectedAt = System.currentTimeMillis(),
                isPrimary = false
            )
            partnerDao.insertConnection(connection)
            return true
        }
        return false
    }

    override suspend fun revokeConnection() {
        partnerDao.clearConnection()
        partnerDao.clearAllQuizAnswers()
        _realtimeSyncTrigger.value = System.currentTimeMillis()
    }

    override fun getQuizAnswers(quizId: String): Flow<List<QuizAnswerEntity>> {
        return partnerDao.getAnswersForQuiz(quizId)
    }

    override fun getAllQuizAnswers(): Flow<List<QuizAnswerEntity>> {
        return partnerDao.getAllAnswers()
    }

    override suspend fun submitQuizAnswer(
        quizId: String,
        questionId: String,
        answerIndex: Int,
        answeredBy: String
    ) {
        val compositeId = "${quizId}_${questionId}_$answeredBy"
        val answer = QuizAnswerEntity(
            id = compositeId,
            quizId = quizId,
            questionId = questionId,
            answerIndex = answerIndex,
            answeredBy = answeredBy,
            answeredAt = System.currentTimeMillis()
        )
        partnerDao.insertAnswer(answer)
    }

    override fun getPartnerCycleInsights(): Flow<PartnerCycleInsights> = flow {
        // Standard masked, highly private insights mapping for Partner Mode
        while (true) {
            val currentDay = cycleRepository.getCurrentCycleDay().firstOrNull() ?: 22
            val currentPhase = cycleRepository.getCurrentPhase().firstOrNull() ?: CyclePhase.LUTEAL
            val prediction = cycleRepository.predictNextPeriod().firstOrNull()
            
            // Map Phase to ovulation countdown and conception chance
            val averageCycle = 28
            val midCycle = averageCycle / 2
            val ovulationDaysAway = (midCycle - currentDay).coerceAtLeast(0)
            
            val conceptionChance = when {
                currentDay in (midCycle - 4)..(midCycle + 1) -> "High"
                currentDay in (midCycle - 6)..(midCycle + 2) -> "Medium"
                else -> "Low"
            }
            
            val expectedPeriodDaysAway = prediction?.daysUntil ?: 6

            emit(
                PartnerCycleInsights(
                    cycleDay = currentDay,
                    phase = currentPhase,
                    ovulationDaysAway = ovulationDaysAway,
                    conceptionChance = conceptionChance,
                    expectedPeriodDaysAway = expectedPeriodDaysAway,
                    lastSyncTime = System.currentTimeMillis()
                )
            )
            // Poll for updates every 15 seconds to simulate high-fidelity network sync
            delay(15000)
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class PartnerRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPartnerRepository(impl: PartnerRepositoryImpl): PartnerRepository
}
