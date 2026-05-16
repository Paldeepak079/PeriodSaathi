package com.example.periodsaathi.data

import androidx.compose.ui.graphics.Color

data class Remedy(
    val id: String,
    val name: String,
    val emoji: String,
    val frontColor: Color,
    val backColor: Color,
    val ingredients: List<String>,
    val steps: List<String>,
    val helpfulFor: List<String>,
    val prepTime: String
)

data class YogaPose(
    val id: String,
    val name: String,
    val description: String,
    val durationSeconds: Int,
    val inhaleSeconds: Int,
    val exhaleSeconds: Int,
    val benefits: List<String>,
    val emoji: String
)

object RemediesData {

    val remedies = listOf(
        Remedy(
            id = "ginger_tea",
            name = "Ginger Tea",
            emoji = "🫚",
            frontColor = Color(0xFFFFE0B2),
            backColor = Color(0xFFFFF3E0),
            ingredients = listOf("Fresh ginger (1 inch)", "Water (1 cup)", "Honey (optional)", "Lemon (optional)"),
            steps = listOf(
                "Grate or slice fresh ginger",
                "Boil water and add ginger",
                "Simmer for 5-10 minutes",
                "Strain and add honey/lemon"
            ),
            helpfulFor = listOf("Cramps", "Nausea", "Inflammation"),
            prepTime = "10 mins"
        ),
        Remedy(
            id = "jaggery_sesame",
            name = "Jaggery + Sesame",
            emoji = "🍯",
            frontColor = Color(0xFFD7CCC8),
            backColor = Color(0xFFEFEBE9),
            ingredients = listOf("Jaggery (1 small piece)", "Roasted sesame seeds (1 tbsp)", "Peanuts (optional)"),
            steps = listOf(
                "Roast sesame seeds until golden",
                "Grind jaggery into small pieces",
                "Mix jaggery with sesame",
                "Eat slowly for iron boost"
            ),
            helpfulFor = listOf("Fatigue", "Iron deficiency", "Energy"),
            prepTime = "5 mins"
        ),
        Remedy(
            id = "fennel_water",
            name = "Fennel Seeds Water",
            emoji = "🌿",
            frontColor = Color(0xFFC8E6C9),
            backColor = Color(0xFFE8F5E9),
            ingredients = listOf("Fennel seeds (1 tsp)", "Hot water (1 cup)"),
            steps = listOf(
                "Crush fennel seeds slightly",
                "Add to hot water",
                "Steep for 5-7 minutes",
                "Strain and drink warm"
            ),
            helpfulFor = listOf("Bloating", "Digestion", "Gas"),
            prepTime = "7 mins"
        ),
        Remedy(
            id = "turmeric_milk",
            name = "Turmeric Milk",
            emoji = "🥛",
            frontColor = Color(0xFFFFF9C4),
            backColor = Color(0xFFFFFDE7),
            ingredients = listOf("Milk (1 cup)", "Turmeric powder (1/2 tsp)", "Black pepper (pinch)", "Honey (1 tsp)"),
            steps = listOf(
                "Warm milk in a pan",
                "Add turmeric and pepper",
                "Simmer for 2-3 minutes",
                "Add honey and stir"
            ),
            helpfulFor = listOf("Inflammation", "Sleep", "Immunity"),
            prepTime = "5 mins"
        ),
        Remedy(
            id = "ajwain_water",
            name = "Ajwain Water",
            emoji = "🌱",
            frontColor = Color(0xFFBBDEFB),
            backColor = Color(0xFFE3F2FD),
            ingredients = listOf("Ajwain/Carom seeds (1 tsp)", "Water (1 cup)", "Black salt (pinch)"),
            steps = listOf(
                "Dry roast ajwain slightly",
                "Boil water with ajwain",
                "Steep for 5 minutes",
                "Add black salt, strain"
            ),
            helpfulFor = listOf("Cramps", "Digestion", "Bloating"),
            prepTime = "8 mins"
        ),
        Remedy(
            id = "dark_chocolate",
            name = "Dark Chocolate",
            emoji = "🍫",
            frontColor = Color(0xFFBCAAA4),
            backColor = Color(0xFFD7CCC8),
            ingredients = listOf("Dark chocolate (70%+ cocoa)", "Nuts (optional)"),
            steps = listOf(
                "Choose 70%+ dark chocolate",
                "Break into small pieces",
                "Enjoy slowly with nuts",
                "Limit to 1-2 oz daily"
            ),
            helpfulFor = listOf("Mood", "Magnesium", "Cravings"),
            prepTime = "1 min"
        )
    )

    val yogaPoses = listOf(
        YogaPose(
            id = "child_pose",
            name = "Child's Pose",
            description = "A restorative posture that gently stretches the lower back and hips, calming the nervous system.",
            durationSeconds = 60,
            inhaleSeconds = 4,
            exhaleSeconds = 6,
            benefits = listOf("Relieves back tension", "Calms mind", "Reduces stress"),
            emoji = "🧘"
        ),
        YogaPose(
            id = "cat_cow",
            name = "Cat-Cow Flow",
            description = "Dynamic movement to improve pelvic blood circulation and spinal flexibility.",
            durationSeconds = 90,
            inhaleSeconds = 4,
            exhaleSeconds = 4,
            benefits = listOf("Improves circulation", "Stretches spine", "Eases cramps"),
            emoji = "🐱"
        ),
        YogaPose(
            id = "supine_twist",
            name = "Supine Twist",
            description = "Gentle spinal rotation that massages internal organs and releases tension.",
            durationSeconds = 60,
            inhaleSeconds = 4,
            exhaleSeconds = 6,
            benefits = listOf("Detoxifies", "Releases spine", "Aids digestion"),
            emoji = "🔄"
        ),
        YogaPose(
            id = "legs_up_wall",
            name = "Legs Up Wall",
            description = "Inverted pose that promotes blood flow to the uterus and relaxes the nervous system.",
            durationSeconds = 120,
            inhaleSeconds = 4,
            exhaleSeconds = 8,
            benefits = listOf("Reduces swelling", "Relieves cramps", "Promotes relaxation"),
            emoji = "🦵"
        ),
        YogaPose(
            id = "savasana",
            name = "Savasana",
            description = "Final relaxation pose allowing complete rest and integration of the practice.",
            durationSeconds = 60,
            inhaleSeconds = 4,
            exhaleSeconds = 8,
            benefits = listOf("Deep rest", "Reduces anxiety", "Lowers blood pressure"),
            emoji = "😴"
        )
    )
}