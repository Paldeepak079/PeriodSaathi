package com.deepak.periodsaathi.wellness.data.local

import com.deepak.periodsaathi.wellness.data.local.entities.SolutionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SolutionRepository @Inject constructor(
    private val dao: WellnessDao
) {
    val allSolutions: Flow<List<SolutionEntity>> = dao.getAllSolutionsSorted()

    fun getByCategory(category: String): Flow<List<SolutionEntity>> =
        dao.getSolutionsByCategory(category)

    fun getBySymptom(symptom: String): Flow<List<SolutionEntity>> =
        dao.getSolutionsBySymptom(symptom)

    fun getBySeverity(severity: String): Flow<List<SolutionEntity>> =
        dao.getSolutionsBySeverity(severity)

    fun getFiltered(category: String, symptom: String, severity: String): Flow<List<SolutionEntity>> =
        dao.getFilteredSolutions(category, symptom, severity)

    suspend fun toggleFavorite(id: String, fav: Boolean) =
        dao.toggleFavorite(id, fav)

    suspend fun seedSolutions() {
        dao.insertSolutions(allSeedSolutions)
    }

    suspend fun getCount(): Int {
        var count = 0
        dao.getAllSolutionsSorted().collect { count = it.size }
        return count
    }

    companion object {
        val allSeedSolutions = listOf(
            // ===== REMEDIES =====
            solution("remedy_cramps_ginger", "Ginger Tea", "Soothing herbal tea for menstrual cramps", "🫚", "remedy", "cramps", "all", 0, "beginner", "all",
            """["1-inch fresh ginger root", "2 cups water", "1 tbsp honey", "1 tsp lemon juice"]""",
            """["Peel and slice ginger thinly", "Boil water in a saucepan", "Add ginger slices and simmer 10 min", "Strain into a cup", "Add honey and lemon", "Sip slowly while warm"]""",
            """["Reduces prostaglandins that cause pain", "Anti-inflammatory properties", "Warms the body and improves circulation", "Natural muscle relaxant for cramps"]"""),
            solution("remedy_cramps_hotbag", "Hot Water Bag", "Gentle heat therapy for lower abdomen", "🌊", "remedy", "cramps", "mild", 15, "beginner", "all",
            """["Hot water bag or heating pad", "Towel for protection"]""",
            """["Fill hot water bag with warm water (not boiling)", "Wrap in a thin towel", "Place on lower abdomen for 15 min", "Relax and breathe deeply"]""",
            """["Relaxes uterine muscles", "Increases blood flow to pelvic area", "Reduces pain sensation", "Promotes relaxation"]"""),
            solution("remedy_cramps_magnesium", "Magnesium-Rich Foods", "Natural muscle relaxant foods", "🥑", "remedy", "cramps", "moderate", 0, "beginner", "all",
            """["Bananas", "Dark chocolate (70%+)", "Almonds and pumpkin seeds", "Spinach and leafy greens", "Avocado"]""",
            """["Snack on a banana with almond butter", "Have a small piece of dark chocolate", "Add spinach to your meals", "Eat a handful of pumpkin seeds"]""",
            """["Magnesium relaxes muscle contractions", "Reduces severity of cramps", "Improves sleep quality", "Boosts mood naturally"]"""),
            solution("remedy_cramps_childpose", "Child's Pose (Balasana)", "Gentle yoga stretch for pelvic relief", "🧘", "remedy", "cramps", "all", 5, "beginner", "low",
            """["Yoga mat or soft surface"]""",
            """["Kneel on the mat with big toes touching", "Sit back on your heels", "Extend arms forward on the ground", "Rest forehead on the mat", "Breathe deeply for 5 breaths", "Repeat 3 times"]""",
            """["Gently stretches lower back", "Releases pelvic tension", "Calms the nervous system", "Improves blood circulation"]"""),

            solution("remedy_bloating_fennel", "Fennel Tea", "Digestive aid for bloating relief", "🌿", "remedy", "bloating", "all", 0, "beginner", "all",
            """["1 tsp fennel seeds", "1 cup hot water", "1 tsp honey (optional)"]""",
            """["Crush fennel seeds lightly", "Add to a cup of hot water", "Steep for 5-7 minutes", "Strain and add honey if desired", "Drink warm after meals"]""",
            """["Reduces intestinal gas", "Relaxes digestive muscles", "Anti-inflammatory", "Freshens breath"]"""),
            solution("remedy_bloating_mint", "Mint Water", "Cooling digestive refresher", "🍃", "remedy", "bloating", "all", 0, "beginner", "all",
            """["Fresh mint leaves (5-6)", "1 liter water", "Lemon slices (optional)"]""",
            """["Wash mint leaves thoroughly", "Add to a pitcher of water", "Add lemon slices", "Refrigerate for 1 hour", "Sip throughout the day"]""",
            """["Soothes digestive tract", "Reduces bloating naturally", "Hydrates the body", "Provides antioxidants"]"""),
            solution("remedy_bloating_salt", "Reduce Salt Intake", "Dietary adjustment for water retention", "🧂", "remedy", "bloating", "moderate", 0, "beginner", "all",
            """["Check food labels for sodium", "Use herbs instead of salt", "Avoid processed foods"]""",
            """["Read nutrition labels for hidden sodium", "Season food with herbs and spices", "Choose fresh over canned foods", "Drink more water to flush excess sodium", "Limit salty snacks"]""",
            """["Reduces water retention", "Lowers blood pressure", "Decreases bloating", "Improves overall health"]"""),
            solution("remedy_bloating_walking", "Light Walking", "Gentle movement for digestion", "🚶", "remedy", "bloating", "mild", 10, "beginner", "low",
            """["Comfortable shoes", "Quiet outdoor or indoor space"]""",
            """["Stand up straight", "Start with slow, steady steps", "Breathe deeply as you walk", "Swing arms gently", "Continue for 10-15 min"]""",
            """["Stimulates digestion", "Releases trapped gas", "Improves circulation", "Boosts mood"]"""),

            solution("remedy_headache_hydrate", "Hydration", "Water for tension headaches", "💧", "remedy", "headache", "mild", 0, "beginner", "all",
            """["2-3 glasses of water", "Electrolyte drink (optional)"]""",
            """["Stop what you're doing", "Slowly drink 2 glasses of water", "Rest for 10 minutes", "If available, sip an electrolyte drink", "Avoid screens during rest"]""",
            """["Dehydration is a top headache cause", "Restores fluid balance", "Increases oxygen to brain", "No side effects"]"""),
            solution("remedy_headache_darkroom", "Dark Room Rest", "Sensory relief for headaches", "🌑", "remedy", "headache", "moderate", 20, "beginner", "low",
            """["Dark quiet room", "Comfortable pillow", "Eye mask (optional)"]""",
            """["Find a dark, quiet room", "Lie down comfortably", "Close curtains or wear eye mask", "Breathe slowly and deeply", "Rest for 20 minutes"]""",
            """["Reduces sensory overload", "Lowers stress response", "Allows muscles to relax", "Natural headache relief"]"""),
            solution("remedy_headache_peppermint", "Peppermint Oil", "Cooling topical headache relief", "🌱", "remedy", "headache", "mild", 0, "beginner", "all",
            """["2-3 drops peppermint essential oil", "1 tsp carrier oil (coconut/jojoba)"]""",
            """["Mix peppermint oil with carrier oil", "Apply to temples and forehead", "Gently massage in circular motion", "Close eyes and relax for 5 min", "Avoid contact with eyes"]""",
            """["Cooling sensation distracts from pain", "Increases blood flow", "Relieves tension naturally", "No medication needed"]"""),

            solution("remedy_fatigue_iron", "Iron-Rich Foods", "Energy-boosting nutritional support", "🥩", "remedy", "fatigue", "all", 0, "beginner", "normal",
            """["Spinach and leafy greens", "Lean red meat or lentils", "Fortified cereals", "Vitamin C source (orange/citrus)"]""",
            """["Include leafy greens in meals", "Pair iron foods with vitamin C for absorption", "Have a small handful of nuts", "Cook in cast iron pans when possible", "Avoid tea/coffee with iron meals"]""",
            """["Combats iron deficiency anemia", "Increases energy naturally", "Improves concentration", "Supports immune function"]"""),
            solution("remedy_fatigue_nap", "Short Power Nap", "Restorative 20-minute break", "😴", "remedy", "fatigue", "all", 20, "beginner", "low",
            """["Quiet space", "Comfortable surface", "Alarm set for 20 min"]""",
            """["Set an alarm for exactly 20 minutes", "Find a comfortable position", "Close eyes and breathe deeply", "Relax muscles progressively", "Wake up when alarm rings"]""",
            """["Restores alertness without grogginess", "Improves cognitive function", "Boosts immune system", "Reduces stress"]"""),
            solution("remedy_fatigue_yoga", "Gentle Yoga Flow", "Energizing restorative yoga", "🧘", "remedy", "fatigue", "mild", 15, "beginner", "low",
            """["Yoga mat", "Comfortable clothing"]""",
            """["Start in child's pose for 5 breaths", "Move to cat-cow stretches", "Flow into downward dog", "Step forward to standing fold", "Roll up slowly to mountain pose"]""",
            """["Increases blood circulation", "Releases endorphins", "Raises energy naturally", "Clears mental fog"]"""),

            // ===== YOGA =====
            solution("yoga_cramps_child", "Child's Pose", "Pelvic release stretch", "🧘", "yoga", "cramps", "all", 5, "beginner", "low",
            """["Yoga mat"]""",
            """["Kneel on mat with toes together", "Sit back on heels", "Extend arms forward, forehead down", "Breathe deeply for 1 minute", "Return slowly"]""",
            """["Releases lower back", "Relaxes pelvic floor", "Calms nervous system"]"""),
            solution("yoga_cramps_pelvic", "Pelvic Tilt Stretch", "Core and pelvic release", "🧘", "yoga", "cramps", "all", 5, "beginner", "low",
            """["Yoga mat", "Pillow (optional)"]""",
            """["Lie on back with knees bent", "Feet flat on floor hip-width apart", "Slowly tilt pelvis upward", "Hold for 5 breaths", "Release and repeat 5 times"]""",
            """["Eases lower back tension", "Strengthens pelvic muscles", "Reduces menstrual pain"]"""),
            solution("yoga_cramps_butterfly", "Butterfly Pose", "Hip-opening relaxation", "🦋", "yoga", "cramps", "mild", 5, "beginner", "low",
            """["Yoga mat"]""",
            """["Sit with spine straight", "Bring soles of feet together", "Let knees fall open", "Hold feet with hands", "Gently flap knees like butterfly wings"]""",
            """["Opens hips and groin", "Stimulates abdominal organs", "Reduces menstrual discomfort"]"""),

            solution("yoga_bloating_twist", "Seated Spinal Twist", "Digestive massage twist", "🌀", "yoga", "bloating", "all", 5, "intermediate", "normal",
            """["Yoga mat"]""",
            """["Sit with legs extended", "Cross right foot over left knee", "Twist torso to the right", "Place left elbow outside right knee", "Hold for 5 breaths, switch sides"]""",
            """["Massages digestive organs", "Relieves gas and bloating", "Improves spine mobility"]"""),
            solution("yoga_bloating_wind", "Wind-Relieving Pose", "Targeted gas release", "💨", "yoga", "bloating", "all", 3, "beginner", "low",
            """["Yoga mat"]""",
            """["Lie on back", "Hug right knee to chest", "Hold for 5 breaths", "Repeat with left knee", "Hug both knees to chest"]""",
            """["Directly relieves gas", "Massages colon", "Gentle lower back stretch"]"""),

            solution("yoga_fatigue_sun", "Sun Salutation", "Energizing flow sequence", "☀️", "yoga", "fatigue", "mild", 10, "intermediate", "normal",
            """["Yoga mat"]""",
            """["Stand at front of mat", "Reach arms up and back", "Fold forward", "Step back to plank", "Lower to cobra/upward dog", "Push back to downward dog", "Step forward and rise"]""",
            """["Full body energizer", "Improves circulation", "Builds heat and vitality"]"""),

            solution("yoga_headache_neck", "Neck Release Stretch", "Tension relief for headache", "🔄", "yoga", "headache", "mild", 5, "beginner", "low",
            """["Yoga mat or chair"]""",
            """["Sit comfortably", "Slowly tilt right ear to right shoulder", "Hold 30 seconds", "Return to center", "Repeat left side", "Gently roll neck in circles"]""",
            """["Releases neck tension", "Improves blood flow to head", "Reduces headache intensity"]"""),

            // ===== EXERCISE =====
            solution("exercise_cramps_walk", "Gentle Walking", "Low-impact circulation boost", "🚶", "exercise", "cramps", "mild", 15, "beginner", "low",
            """["Comfortable shoes"]""",
            """["Start slow, warm up for 2 min", "Walk at comfortable pace", "Swing arms naturally", "Breathe deeply", "Cool down for 2 min"]""",
            """["Increases pelvic blood flow", "Releases endorphins", "Gentle on the body", "Can be done anytime"]"""),
            solution("exercise_fatigue_walk", "Brisk Walking", "Energy-boosting cardio", "🚶", "exercise", "fatigue", "mild", 20, "intermediate", "normal",
            """["Walking shoes", "Water bottle"]""",
            """["Warm up for 3 min slow pace", "Increase to brisk pace for 15 min", "Maintain steady breathing", "Cool down for 2 min", "Stretch afterwards"]""",
            """["Boosts energy levels", "Improves cardiovascular health", "Releases feel-good endorphins", "Clears mental fog"]"""),

            solution("exercise_bloating_light", "Light Stretching Routine", "Gentle full-body stretch", "🤸", "exercise", "bloating", "all", 10, "beginner", "low",
            """["Yoga mat"]""",
            """["Standing side stretches (each side 30s)", "Standing forward fold (30s)", "Cat-cow stretches (1 min)", "Seated forward fold (30s)", "Gentle spinal twist (each side 30s)"]""",
            """["Stimulates digestion", "Relieves muscle tension", "Reduces water retention"]"""),

            solution("exercise_headache_neck", "Neck & Shoulder Release", "Tension relief exercises", "💆", "exercise", "headache", "all", 5, "beginner", "all",
            """["Chair or comfortable seat"]""",
            """["Shoulder rolls - 10 forward, 10 backward", "Neck rotations - 5 each side", "Chin tucks - hold 5s, repeat 5 times", "Shoulder shrugs - hold 5s, release"]""",
            """["Releases upper body tension", "Improves blood circulation", "Prevents tension headaches"]"""),

            // ===== BREATHING =====
            solution("breathing_cramps_deep", "Deep Belly Breathing", "Calming breath for pain relief", "💨", "breathing", "cramps", "all", 3, "beginner", "all",
            """["Quiet space", "Comfortable seat"]""",
            """["Sit or lie down comfortably", "Place one hand on belly", "Inhale slowly through nose for 4 counts", "Feel belly rise", "Exhale slowly through mouth for 6 counts", "Repeat for 3 minutes"]""",
            """["Activates parasympathetic system", "Reduces pain perception", "Lowers stress hormones", "Oxygenates blood"]"""),
            solution("breathing_all_478", "4-7-8 Breathing", "Relaxation breath technique", "🌬️", "breathing", "general", "all", 2, "beginner", "all",
            """["Quiet space"]""",
            """["Sit with back straight", "Exhale completely", "Inhale through nose for 4 counts", "Hold breath for 7 counts", "Exhale through mouth for 8 counts", "Repeat 4 times"]""",
            """["Immediately calms nervous system", "Reduces anxiety", "Helps with sleep", "Lowers heart rate"]"""),
            solution("breathing_headache_alternate", "Alternate Nostril Breathing", "Balancing breath for headache", "🌪️", "breathing", "headache", "mild", 3, "intermediate", "all",
            """["Quiet space", "Comfortable seat"]""",
            """["Sit comfortably", "Close right nostril with thumb", "Inhale through left for 4 counts", "Close left nostril, hold 2 counts", "Release right, exhale 6 counts", "Inhale right, close, exhale left"]""",
            """["Balances brain hemispheres", "Reduces headache intensity", "Calms mind", "Improves focus"]"""),

            // ===== DIET =====
            solution("diet_cramps_warm", "Warm Nourishing Foods", "Comfort foods for period", "🥣", "diet", "cramps", "all", 0, "beginner", "all",
            """["Oats or porridge", "Ginger", "Turmeric", "Dark leafy greens", "Warm milk"]""",
            """["Start day with warm oatmeal", "Add ginger and turmeric to meals", "Include leafy greens in lunch", "Have warm milk before bed", "Avoid cold foods and drinks"]""",
            """["Warm foods improve circulation", "Turmeric reduces inflammation", "Iron-rich greens replenish stores", "Comforting and grounding"]"""),
            solution("diet_bloating_lowsalt", "Low-Sodium Meal Plan", "Anti-bloat diet guide", "🥗", "diet", "bloating", "all", 0, "beginner", "all",
            """["Fresh vegetables", "Lean proteins", "Whole grains", "Herbs and spices", "Lots of water"]""",
            """["Choose fresh over processed", "Season with herbs not salt", "Eat potassium-rich bananas", "Include probiotic yogurt", "Drink water between meals"]""",
            """["Reduces water retention", "Balances electrolytes", "Supports digestion", "Increases energy"]"""),
            solution("diet_fatigue_ironboost", "Iron-Boost Meal Plan", "Energy-restoring nutrition", "🥩", "diet", "fatigue", "all", 0, "beginner", "all",
            """["Lean red meat or lentils", "Spinach and kale", "Citrus fruits", "Nuts and seeds", "Fortified cereals"]""",
            """["Have iron-rich breakfast", "Pair with vitamin C for absorption", "Snack on nuts and seeds", "Include protein in every meal", "Stay hydrated throughout"]""",
            """["Restores iron levels", "Improves energy naturally", "Supports red blood cell production", "Boosts immune function"]"""),

            // ===== HYDRATION =====
            solution("hydration_general_8glasses", "8-Glass Hydration Goal", "Daily water intake tracker", "💧", "hydration", "general", "all", 0, "beginner", "all",
            """["Water bottle (1 liter)", "Phone timer or reminder app"]""",
            """["Fill bottle in the morning", "Drink 1 glass every hour", "Set reminders on phone", "Refill bottle 2 times per day"]""",
            """["Flushes toxins", "Improves skin health", "Reduces headache risk", "Boosts metabolism"]"""),
            solution("hydration_cramps_warmwater", "Warm Water Therapy", "Soothing hydration for cramps", "🫖", "hydration", "cramps", "all", 0, "beginner", "all",
            """["Warm water", "Honey (optional)", "Lemon slice (optional)"]""",
            """["Boil water and let cool slightly", "Pour into a mug", "Add honey and lemon if desired", "Sip slowly", "Drink 2-3 cups during cramps"]""",
            """["Warmth relaxes muscles", "Honey provides natural energy", "Lemon adds vitamin C", "Hydration reduces cramping"]"""),

            // ===== MOOD =====
            solution("mood_all_journal", "Gratitude Journaling", "Mood-boosting writing practice", "📝", "mood", "general", "all", 5, "beginner", "all",
            """["Notebook or journal", "Pen"]""",
            """["Write 3 things you're grateful for", "Describe one positive moment today", "Write how you feel right now", "Read it back to yourself"]""",
            """["Shifts focus to positive", "Reduces anxiety", "Improves emotional awareness", "Builds resilience over time"]"""),
            solution("mood_cramps_selfcare", "Self-Care Ritual", "Loving-kindness practice", "🌸", "mood", "cramps", "all", 10, "beginner", "low",
            """["Cozy blanket", "Herbal tea", "Soft music", "Journal"]""",
            """["Create a cozy space", "Make your favorite herbal tea", "Play soft relaxing music", "Wrap in a warm blanket", "Write down affirmations"]""",
            """["Reduces stress hormones", "Creates safety and comfort", "Validates your feelings", "Promotes self-compassion"]"""),

            // ===== SLEEP =====
            solution("sleep_all_winddown", "Evening Wind-Down Routine", "Better sleep preparation", "🌙", "sleep", "general", "all", 30, "beginner", "low",
            """["Dim lights", "Warm bath or shower", "Herbal tea (chamomile)", "Book or journal"]""",
            """["Dim lights 1 hour before bed", "Take a warm bath or shower", "Drink caffeine-free herbal tea", "Read a physical book", "Avoid screens for 30 min before sleep"]""",
            """["Signals body to produce melatonin", "Lowers core temperature for sleep", "Reduces screen blue light exposure", "Calms racing thoughts"]"""),
            solution("sleep_cramps_rest", "Restorative Sleep for Period", "Sleep support during menstruation", "😴", "sleep", "cramps", "all", 0, "beginner", "low",
            """["Heating pad", "Extra pillow", "Comfortable pajamas", "Dark room"]""",
            """["Use heating pad on lower back", "Place pillow between knees", "Sleep on side in fetal position", "Keep room cool and dark", "Wear loose comfortable clothing"]""",
            """["Side sleeping improves circulation", "Heat reduces cramping", "Fetal position relaxes abdominal muscles", "Quality sleep aids hormone balance"]"""),
            solution("sleep_fatigue_hygiene", "Sleep Hygiene for Energy", "Quality sleep practices", "🌟", "sleep", "fatigue", "all", 0, "beginner", "all",
            """["Consistent bedtime", "Dark cool room", "No screens 1h before bed"]""",
            """["Go to bed same time every night", "Keep bedroom dark and cool", "No caffeine after 4 PM", "No phones in bed", "Wake up same time every morning"]""",
            """["Regulates circadian rhythm", "Deepens sleep quality", "Increases daytime energy", "Improves mood stability"]""")
        )

        private fun solution(
            id: String,
            title: String,
            subtitle: String,
            emoji: String,
            category: String,
            symptomType: String,
            severity: String,
            durationMinutes: Int,
            difficulty: String,
            energyLevel: String,
            ingredients: String,
            steps: String,
            benefits: String
        ) = SolutionEntity(
            id = id,
            title = title,
            subtitle = subtitle,
            emoji = emoji,
            category = category,
            symptomType = symptomType,
            severity = severity,
            durationMinutes = durationMinutes,
            difficulty = difficulty,
            energyLevel = energyLevel,
            ingredients = ingredients,
            steps = steps,
            benefits = benefits
        )
    }
}
