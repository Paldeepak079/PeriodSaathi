package com.deepak.periodsaathi.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

enum class RiskLevel { LOW, MEDIUM, HIGH }

data class HealthQueryResult(
    val question: String,
    val riskLevel: RiskLevel,
    val headline: String,
    val explanation: String,
    val actionItems: List<String>,
    val disclaimer: String = "This information is for educational purposes only and does not constitute medical advice. Always consult a qualified healthcare provider for personal medical concerns."
)

data class HealthQueryInput(
    val queryType: QueryType,
    val lastPeriodDate: LocalDate?,
    val cycleLength: Int = 28,
    val periodLength: Int = 5,
    val usedProtection: Boolean? = null,
    val stressLevel: StressLevel? = null,
    val weightChange: WeightChange? = null,
    val onMedication: Boolean? = null,
    val medicationType: String? = null
)

enum class QueryType { PREGNANCY_RISK, LATE_PERIOD, CRAMPS_NORMAL, ACNE_REASONS }
enum class StressLevel { LOW, MODERATE, HIGH, EXTREME }
enum class WeightChange { STABLE, SLIGHT_CHANGE, SIGNIFICANT_CHANGE }

@Singleton
class HealthQueryEngine @Inject constructor() {

    fun assess(input: HealthQueryInput): HealthQueryResult = when (input.queryType) {
        QueryType.PREGNANCY_RISK -> assessPregnancyRisk(input)
        QueryType.LATE_PERIOD -> assessLatePeriod(input)
        QueryType.CRAMPS_NORMAL -> assessCrampsNormal(input)
        QueryType.ACNE_REASONS -> assessAcne(input)
    }

    private fun assessPregnancyRisk(input: HealthQueryInput): HealthQueryResult {
        val daysFromLastPeriod = input.lastPeriodDate?.let {
            ChronoUnit.DAYS.between(it, LocalDate.now()).toInt()
        } ?: 14
        val baseOvStart = input.cycleLength - 17
        val baseOvEnd = input.cycleLength - 11
        val ovulationShift = when {
            input.stressLevel == StressLevel.EXTREME -> 5
            input.stressLevel == StressLevel.HIGH -> 3
            input.weightChange == WeightChange.SIGNIFICANT_CHANGE -> 4
            input.stressLevel == StressLevel.MODERATE -> 1
            else -> 0
        }
        val adjOvStart = baseOvStart + ovulationShift
        val adjOvEnd = baseOvEnd + ovulationShift
        val inFertileWindow = daysFromLastPeriod in adjOvStart..(adjOvEnd + 1)
        val protectionUsed = input.usedProtection ?: false
        val onHormonalMed = input.onMedication == true &&
            (input.medicationType?.contains("pill", ignoreCase = true) == true ||
             input.medicationType?.contains("hormonal", ignoreCase = true) == true)

        val riskLevel = when {
            inFertileWindow && !protectionUsed && !onHormonalMed -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
        val shiftNote = if (ovulationShift > 0)
            " Reported stress/weight changes may delay ovulation by ~$ovulationShift days." else ""

        return HealthQueryResult(
            question = "Pregnancy Risk Assessment",
            riskLevel = riskLevel,
            headline = when (riskLevel) {
                RiskLevel.LOW -> "Pregnancy risk appears low at this time"
                RiskLevel.MEDIUM -> "You may be in or near your fertile window"
                RiskLevel.HIGH -> "Higher risk — consult a doctor"
            },
            explanation = "Based on your last period ($daysFromLastPeriod days ago) and a ${input.cycleLength}-day cycle, your estimated fertile window is days $adjOvStart to $adjOvEnd.$shiftNote ${if (protectionUsed) "You reported using protection, which significantly reduces risk." else ""}",
            actionItems = buildList {
                if (riskLevel == RiskLevel.MEDIUM) {
                    add("Take a pregnancy test if your period is more than 5 days late")
                    add("Speak with a gynecologist if you have concerns")
                }
                add("Track ovulation with BBT or LH strips for more accuracy")
                add("Log your cycles consistently for better predictions")
                if (ovulationShift > 0) add("Managing stress and maintaining a healthy weight improves cycle regularity")
            }
        )
    }

    private fun assessLatePeriod(input: HealthQueryInput): HealthQueryResult {
        val daysFromLastPeriod = input.lastPeriodDate?.let {
            ChronoUnit.DAYS.between(it, LocalDate.now()).toInt()
        } ?: 30
        val daysLate = daysFromLastPeriod - input.cycleLength
        val stressDelay = when (input.stressLevel) {
            StressLevel.EXTREME -> 14; StressLevel.HIGH -> 7; StressLevel.MODERATE -> 3; else -> 0
        }
        val weightDelay = when (input.weightChange) {
            WeightChange.SIGNIFICANT_CHANGE -> 10; WeightChange.SLIGHT_CHANGE -> 3; else -> 0
        }
        val likelyDelay = stressDelay + weightDelay
        val riskLevel = when {
            daysLate <= 5 -> RiskLevel.LOW
            daysLate <= 10 && likelyDelay >= 5 -> RiskLevel.LOW
            daysLate <= 10 -> RiskLevel.MEDIUM
            daysLate > 10 && likelyDelay > 0 -> RiskLevel.MEDIUM
            else -> RiskLevel.HIGH
        }
        return HealthQueryResult(
            question = "Late Period Assessment",
            riskLevel = riskLevel,
            headline = when {
                daysLate <= 0 -> "Your period has not started yet - not officially late"
                daysLate <= 5 -> "Slightly late - this is very common"
                daysLate <= 10 -> "Moderately late - likely explainable by lifestyle"
                else -> "Significantly late - consider a test or doctor visit"
            },
            explanation = "Your period is approximately $daysLate day${if (daysLate == 1) "" else "s"} late based on your ${input.cycleLength}-day cycle. ${if (likelyDelay > 0) "Reported stress/weight changes can delay menstruation by up to $likelyDelay days by disrupting the hypothalamic-pituitary-ovarian axis." else ""}",
            actionItems = buildList {
                if (daysLate > 5) add("Take a pregnancy test if sexually active")
                if (input.stressLevel != null && input.stressLevel != StressLevel.LOW) add("Practice stress-reduction techniques: meditation, yoga, adequate sleep")
                if (input.weightChange == WeightChange.SIGNIFICANT_CHANGE) add("Speak with your doctor about weight-related hormonal changes")
                add("Occasional late periods (up to 1 week) are normal for most people")
                if (daysLate > 10) add("Consult a gynecologist - conditions like PCOS, thyroid issues, or POI can cause irregular cycles")
            }
        )
    }

    private fun assessCrampsNormal(input: HealthQueryInput): HealthQueryResult {
        val daysFromPeriod = input.lastPeriodDate?.let {
            ChronoUnit.DAYS.between(it, LocalDate.now()).toInt()
        } ?: 2
        val isFirstDays = daysFromPeriod in 0..3
        return HealthQueryResult(
            question = "Are My Cramps Normal?",
            riskLevel = RiskLevel.LOW,
            headline = if (isFirstDays) "Cramping in the first 1-3 days is physiologically normal"
                       else "Mid-cycle cramping can have several causes",
            explanation = "Menstrual cramps (dysmenorrhea) are caused by prostaglandins that trigger uterine contractions to shed the lining. They peak in the first 24-48 hours. ${if (!isFirstDays) "Cramping outside your period window may be from ovulation (mittelschmerz), digestive causes, or conditions like endometriosis." else ""}",
            actionItems = listOf(
                "Anti-inflammatory diet: reduce processed foods, dairy, red meat during your cycle",
                "Heat therapy: a warm compress at 40C improves blood flow and relaxes uterine muscles",
                "Magnesium-rich foods (dark chocolate, leafy greens, nuts) reduce prostaglandin production",
                "Yoga poses: child's pose, cat-cow, and supine twist reduce pelvic tension",
                "If cramps are severe enough to miss work or school, see a doctor to rule out endometriosis or fibroids"
            )
        )
    }

    private fun assessAcne(input: HealthQueryInput): HealthQueryResult {
        val daysFromLastPeriod = input.lastPeriodDate?.let {
            ChronoUnit.DAYS.between(it, LocalDate.now()).toInt()
        } ?: 14
        val isPreMenstrual = daysFromLastPeriod > (input.cycleLength - 10)
        return HealthQueryResult(
            question = "Why Am I Breaking Out?",
            riskLevel = RiskLevel.LOW,
            headline = if (isPreMenstrual) "Pre-menstrual hormonal acne is extremely common"
                       else "Mid-cycle breakouts may be ovulation-related",
            explanation = "${if (isPreMenstrual) "In the luteal phase, progesterone rises and can stimulate sebaceous glands to overproduce oil, clogging pores. Testosterone also spikes just before menstruation." else "Around ovulation, estrogen peaks then drops, and rising LH can trigger temporary oil production."} Stress elevates cortisol, which amplifies androgen activity and worsens breakouts.",
            actionItems = listOf(
                "Gentle double-cleanse routine; avoid over-washing as it strips the protective barrier",
                "Niacinamide serum reduces sebum production and inflammation",
                "Reduce dairy and high-glycemic foods in the week before your period",
                "Zinc supplements (25-40mg per day) shown in studies to reduce hormonal acne",
                "If acne is cystic or nodular, consult a dermatologist about hormonal therapy options"
            )
        )
    }
}
