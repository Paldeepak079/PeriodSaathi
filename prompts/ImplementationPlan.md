# Period Saathi — Premium Feature Implementation Plan

## Overview

Transform the Period Saathi app into a premium commercial product by implementing a Flo-inspired interactive onboarding flow, multi-tier paywall (Google Play Billing + Razorpay), gamified gift upgrade engine, and anonymous community feed.

**Current Codebase:** Native Android (Kotlin + Jetpack Compse, MVVM + Clean Architecture, Hilt DI, Room DB, DataStore, Material 3)

**Total estimated effort:** 13-18 days across 4 phases.

---

## Navigation Flow (After Implementation)

```
Splash → Lock (optional) → InteractiveOnboarding → ProcessingSplash → Paywall
         → GiftUpgrade → Login/NameSetup → Home (with InsightCarousel)
                                           → Community (replaces "More" tab)
                                           → Calendar
                                           → Wellness
         └─── Settings accessible via Community screen gear icon ───┘
```

---

## Phase 1: Interactive Onboarding Questionnaire

**Goal:** Replace the existing 3-page static onboarding with a dynamic multi-step survey engine. The Saathi mascot reacts to each question type. All responses stored in DataStore for personalization.

### Data Layer

#### 1.1 `data/model/UserOnboardingData.kt` (NEW)

```kotlin
data class OnboardingResponse(
    val goals: List<String> = emptyList(),          // "track_cycle", "get_pregnant", "track_pregnancy"
    val birthControl: String? = null,                // "none", "iud", "pill", "implant", "condoms", "other"
    val cycleLength: Int? = null,                    // 21-45 days
    val periodLength: Int? = null,                   // 2-10 days
    val lastPeriodStart: String? = null,             // "2026-05-10"
)
```

Also add a sealed class for question types:

```kotlin
sealed class OnboardingQuestion {
    data class SingleChoice(
        val id: String,
        val title: String,
        val subtitle: String,
        val options: List<String>,
        val mascotEmotion: MascotEmotion
    ) : OnboardingQuestion()

    data class MultiChoice(
        val id: String,
        val title: String,
        val subtitle: String,
        val options: List<String>,
        val mascotEmotion: MascotEmotion
    ) : OnboardingQuestion()
}
```

#### 1.2 `data/datastore/UserPreferences.kt` (EDIT)

Add the following DataStore keys:

```kotlin
val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
val USER_GOALS = stringPreferencesKey("user_goals")              // CSV: "track_cycle,get_pregnant"
val BIRTH_CONTROL = stringPreferencesKey("birth_control")
val CYCLE_LENGTH = intPreferencesKey("cycle_length")
val PERIOD_LENGTH = intPreferencesKey("period_length")
val ONBOARDING_STEP = intPreferencesKey("onboarding_step")       // for resume support
```

Add corresponding getters/setters:

```kotlin
val onboardingCompleted: Flow<Boolean>
suspend fun saveOnboardingResponse(response: OnboardingResponse)
suspend fun setOnboardingCompleted()
```

### UI Layer

#### 1.3 `ui/screens/onboarding/OnboardingScreen.kt` (REWRITE)

Recreate as a dynamic multi-step wizard using `HorizontalPager(PageSize.Fill)`.

**Structure:**

```
Box(fillMaxSize, Background) {
    // Animated blob per question (color mapped to emotion)
    // Slide-in/out mascot at top with emotion animation
    HorizontalPager(state, pageCount = questions.size) { page ->
        OnboardingQuestionPage(question = questions[page])
    }
    // Bottom: "Skip" text button, dot indicators, "Next"/"Continue" CTA
    // Last page: "Start Your Journey" → navigates to ProcessingSplash
}
```

**Question flow:**

| Step | Screen | Type | Mascot Emotion |
|------|--------|------|----------------|
| 1 | "Join X users tracking their wellness journey" | MultiChoice (Track Cycle, Get Pregnant, Track Pregnancy) | HAPPY |
| 2 | "Are you currently using birth control?" | SingleChoice (None, IUD, Pill, Implant, Condoms, Other) | LISTENING |
| 3 | "How long is your typical cycle?" | SingleChoice (21-24, 25-28, 29-32, 33-35, 36-45, Not sure) | LISTENING |
| 4 | "How many days does your period usually last?" | SingleChoice (2-3, 4-5, 6-7, 8-10, Not sure) | LISTENING |
| 5 | "When did your last period start?" | Date picker card | HAPPY |
| 6 | "You're all set!" | Summary + "Start Your Journey" CTA | EXCITED |

**Animation specs:**
- Page transitions: `snap()` (precise control) with `graphicsLayer { alpha = progress; translationX = ... }` for parallax
- Option selection: scale 1.0→1.05 with `spring(dampingRatio = 0.5f)`, gradient outline via `animateBorderAsState`
- Dot indicators: existing pattern (width 8→20dp, spring bouncy)
- Mascot emotion transition: `animateColorAsState` for body color shift, emotion changes instantly on page change

#### 1.4 `ui/screens/onboarding/OnboardingViewModel.kt` (NEW)

```kotlin
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    data class OnboardingUiState(
        val currentStep: Int = 0,
        val totalSteps: Int = 6,
        val questions: List<OnboardingQuestion> = defaultQuestions(),
        val response: OnboardingResponse = OnboardingResponse(),
        val mascotEmotion: MascotEmotion = MascotEmotion.HAPPY,
        val isComplete: Boolean = false
    )

    fun selectOption(questionId: String, option: String)
    fun deselectOption(questionId: String, option: String)
    fun setDate(dateEpoch: Long)
    fun goToNextStep()
    suspend fun completeOnboarding(): Boolean  // saves to DataStore, returns success
}
```

#### 1.5 `ui/components/QuestionCard.kt` (NEW)

```kotlin
@Composable
fun QuestionCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    // GlassCard wrapper with gradient border
    // Title: Nunito ExtraBold 24sp, Primary color
    // Subtitle: Poppins Regular 14sp, OnSurfaceVariant
    // Content slot for options
}
```

#### 1.6 `ui/components/OptionPill.kt` (NEW)

```kotlin
@Composable
fun OptionPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // RoundedCornerShape(28dp) glass-style pill
    // When selected: Primary background with white text, gradient border glow
    // When deselected: White background with OnSurfaceVariant text
    // ScaleButton wrapper for press animation
}
```

#### 1.7 `ui/components/MascotReactionGuide.kt` (NEW)

```kotlin
@Composable
fun MascotReactionGuide(
    emotion: MascotEmotion,
    size: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    // SaathiMascot with emotion
    // Floating speech bubble below with contextual encouragement
    // Glassmorphism bubble with tail, auto-hides after 4s, re-shows on emotion change
}
```

### Navigation Impact

#### 1.8 `ui/navigation/Screen.kt` (EDIT)

No new route needed — repurpose existing `Onboarding` object.

#### 1.9 `ui/navigation/NavGraph.kt` (EDIT)

Update the `Onboarding` composable to pass navigation to `ProcessingSplash` instead of `Login`:

```kotlin
composable<Onboarding> {
    val viewModel: OnboardingViewModel = hiltViewModel()
    OnboardingScreen(
        viewModel = viewModel,
        onComplete = {
            navController.navigate(ProcessingSplash) {
                popUpTo<Onboarding> { inclusive = true }
            }
        },
        onSkip = {
            navController.navigate(ProcessingSplash) {
                popUpTo<Onboarding> { inclusive = true }
            }
        }
    )
}
```

---

## Phase 2: Processing Splash + Multi-Tier Paywall

**Goal:** After onboarding, show an animated "Analyzing responses..." splash screen that transitions to a premium subscription paywall with Google Play Billing + Razorpay support.

### Data Layer

#### 2.7 `data/model/PurchaseRecord.kt` (EDIT)

Add fields to support subscriptions:

```kotlin
@Entity(tableName = "purchase_records")
data class PurchaseRecord(
    @PrimaryKey val productId: String,
    val razorpayPaymentId: String? = null,
    val isActive: Boolean = true,
    // New fields:
    val provider: String = "RAZORPAY",         // "RAZORPAY" or "GOOGLE_PLAY"
    val purchaseToken: String? = null,          // Google Play token
    val subscriptionExpiry: Long? = null,       // epoch millis
    val autoRenewing: Boolean = false,
    val purchaseTime: Long = System.currentTimeMillis()
)
```

#### 2.8 `data/dao/PurchaseDao.kt` (EDIT)

Add subscription query:

```kotlin
@Query("SELECT * FROM purchase_records WHERE isActive = 1 AND (subscriptionExpiry IS NULL OR subscriptionExpiry > :now)")
fun getActiveSubscriptions(now: Long): Flow<List<PurchaseRecord>>

@Query("SELECT * FROM purchase_records WHERE productId = :productId AND isActive = 1")
fun isProductActive(productId: String): Flow<PurchaseRecord?>
```

### UI Layer

#### 2.1 `ui/screens/paywall/ProcessingSplashScreen.kt` (NEW)

**Visual spec:**
- Full screen (no back button), warm cream background with soft radial blush blob
- Center: glowing circular ring (reuse `CycleRing` glow logic) with animated `lerp` progress 0→100% over 2.5 seconds
- Inner circle: pulsing feather/drop icon (Canvas-drawn SVG-style)
- Below: dynamic text label that shifts at progress thresholds:
  - 0-30%: "Analyzing your responses..."
  - 30-60%: "Understanding your cycle..."
  - 60-90%: "Predicting your best days..."
  - 90-100%: "Preparing your dashboard..."
- On 100%: ring glows bright, checkmark appears, auto-navigate to Paywall after 500ms

**Implementation approach:**
```kotlin
@Composable
fun ProcessingSplashScreen(
    onComplete: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    val progressLabel by remember(progress) {
        derivedStateOf { getProgressLabel(progress) }
    }

    LaunchedEffect(Unit) {
        // Animate from 0 to 1 over 2.5s with LinearEasing
        progress = animateFloatAsState(...)
        delay(3000)
        onComplete()
    }

    Box(fillMaxSize, Background) {
        // Glow ring (Canvas)
        // Icon in center (pulsing alpha)
        // Dynamic text label (AnimatedContent with fade)
    }
}
```

#### 2.2 `ui/screens/paywall/PaywallScreen.kt` (NEW)

**Layout (top to bottom):**
1. **Header**: "Unlock Your Full Journey" + subtitle about premium features
2. **Mascot**: Small SaathiMascot (EXCITED) with "Go Premium 💕" speech bubble
3. **Plan cards**:
   - **Yearly Plan (Recommended)**: ₹999/yr — highlighted card with gold border, "Save 58%" badge, price breakdown (₹83/mo)
   - **Monthly Plan**: ₹199/mo — standard card
   - **Lifetime**: ₹2,499 — standard card with "Best Value" badge
4. **Free Trial toggle**: "Start with 7 days free" — custom Material3 switch that auto-selects yearly when enabled
5. **Features list**: 4-5 bullet points (AI insights, community access, advanced tracking, etc.)
6. **CTA Button**: "Start Free Trial" / "Subscribe Now" (PrimaryButton)
7. **Footer**: "Terms", "Privacy", "Restore Purchases"

**Scrollable** using `LazyColumn` with appropriate `contentPadding`.

#### 2.3 `ui/screens/paywall/PaywallViewModel.kt` (NEW)

```kotlin
@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val purchaseDao: PurchaseDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    data class PaywallUiState(
        val selectedPlan: SubscriptionPlan = SubscriptionPlan.YEARLY,
        val freeTrialEnabled: Boolean = true,
        val isLoading: Boolean = false,
        val isSubscribed: Boolean = false,
        val error: String? = null
    )

    enum class SubscriptionPlan(
        val id: String,
        val price: String,
        val pricePaise: Int,         // for Razorpay
        val pricePerMonth: String,
        val sku: String              // Google Play SKU
    ) {
        YEARLY("saathi_yearly", "₹999", 99900, "₹83", "saathi_yearly_2026"),
        MONTHLY("saathi_monthly", "₹199", 19900, "₹199", "saathi_monthly_2026"),
        LIFETIME("saathi_lifetime", "₹2,499", 249900, "—", "saathi_lifetime_2026")
    }

    private val _uiState = MutableStateFlow(PaywallUiState())
    val uiState: StateFlow<PaywallUiState> = _uiState.asStateFlow()

    fun selectPlan(plan: SubscriptionPlan)
    fun toggleFreeTrial()
    fun onSubscribe(activity: Activity)  // dispatch to Google Play or Razorpay
    fun restorePurchases()
}
```

#### 2.4 `ui/screens/paywall/SubscriptionCard.kt` (NEW)

```kotlin
@Composable
fun SubscriptionCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    isRecommended: Boolean,
    discount: String? = null,         // "Save 58%"
    pricePerMonth: String? = null,    // "₹83/mo"
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    // GlassCard with conditional gold border (2dp, WarmGold)
    // When selected: scale 1.02, primary tint overlay
    // "Recommended" chip: WarmGold background, black text
    // "Save X%" chip: MintGreen background, black text
    // Price in large Bold text (24sp)
    // Price per month in smaller label below
}
```

#### 2.5 `ui/screens/paywall/FreeTrialToggle.kt` (NEW)

```kotlin
@Composable
fun FreeTrialToggle(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Row with Switch + "Start with 7 days free" label
    // Label side: small text "Then ₹999/year" in OnSurfaceVariant
    // Switch uses Primary color track when enabled
}
```

#### 2.6 `ui/components/LerpProgressIndicator.kt` (NEW)

```kotlin
@Composable
fun LerpProgressIndicator(
    progress: Float,          // 0f..1f
    modifier: Modifier = Modifier,
    glowColor: Color = Primary,
    trackColor: Color = PrimaryContainer,
    strokeWidth: Dp = 6.dp
) {
    // Canvas-drawn arc ring
    // Glow effect: draw larger semi-transparent arc behind main arc
    // Color sweeps from BlushPink → DeepRose → WarmGold as progress increases
    // Reuses pattern from CycleRing.kt
}
```

### Navigation Impact

#### 2.9 `ui/navigation/Screen.kt` (EDIT)

Add routes:

```kotlin
@Serializable object ProcessingSplash
@Serializable object Paywall
```

#### 2.10 `ui/navigation/NavGraph.kt` (EDIT)

```kotlin
composable<ProcessingSplash> { ProcessingSplashScreen(
    onComplete = { navController.navigate(Paywall) { popUpTo<ProcessingSplash> { inclusive = true } } }
) }
composable<Paywall> { PaywallScreen(
    onSubscribe = { navController.navigate(GiftUpgrade) },
    onSkip = { navController.navigate(Login) { popUpTo<Paywall> { inclusive = true } } }
) }
```

---

## Phase 3: Gamified Gift Upgrade Engine

**Goal:** After plan selection, show a 3-tier mystery box (Standard → Rare → Epic) with particle bursts and discount reveal. Converts users by offering 24% OFF.

### UI Layer

#### 3.1 `ui/screens/paywall/GiftUpgradeModal.kt` (NEW)

**Visual spec (full-screen, modal background dark overlay):**

**State 1 — Standard Box:**
- Center: golden/yellow gift box with ribbon bow (Canvas-drawn)
- Subtle shimmer gradient animation on the bow
- Text above: "🎁 You've got a surprise gift!"
- Text below: "Tap to upgrade your reward"
- Up-arrow button: "→" (scale on tap)

**State 2 — Rare Box:**
- Box rumbles (oscillating X translation, 3 cycles, 300ms)
- Color shift animation: yellow → deep neon violet (1s)
- Ribbon transforms to glowing style
- Text updates: "✨ Rare upgrade unlocked!"
- Extra step revealed: "Tap again for Epic reward!"
- Up-arrow button pulses with glow

**State 3 — Epic Box:**
- Box lid bursts open (scale + translation animation, lid flies up and fades)
- Particle burst: 120 golden stars radiating from center
- Light-burst rays: 8-12 white/gold lines radiating outward from behind the box, fading over 1.5s
- Floating percentage badges: "24%", "OFF" — each floats in with spring, stays visible
- Background dark overlay lightens to reveal pricing card

**Final screen — Discount Presentation:**
- Glassmorphism card slides up from bottom
- Original price: ₹999 — crossed out with animated strike-through line (drawPath from left to right)
- Discounted price: ₹759 — scales in (0.5x → 1.0x, spring) with "24% OFF" badge
- "Claim This Offer" PrimaryButton
- Small text: "Limited time offer"

**ViewModel:**

```kotlin
@HiltViewModel
class GiftUpgradeViewModel @Inject constructor() : ViewModel() {

    data class GiftUiState(
        val tier: GiftTier = GiftTier.STANDARD,
        val showParticles: Boolean = false,
        val showDiscount: Boolean = false,
        val originalPrice: String = "₹999",
        val discountedPrice: String = "₹759",
        val discountPercent: Int = 24
    )

    enum class GiftTier { STANDARD, RARE, EPIC }

    fun onTapUpgrade()
    fun onClaimOffer()  // navigates to payment processing
}
```

#### 3.3 `ui/components/ParticleBurstOverlay.kt` (NEW)

```kotlin
@Composable
fun ParticleBurstOverlay(
    visible: Boolean,
    particleCount: Int = 120,
    colors: List<Color> = listOf(WarmGold, ButterYellow, Color.White),
    onComplete: () -> Unit = {}
) {
    // Extends ConfettiOverlay pattern but with:
    // - Circular burst from center (not top)
    // - Star shapes (drawPath with 5-point star)
    // - Golden color palette + white highlights
    // - Radial light rays: thick lines from center fading outward
    // - Particles have initial explosion velocity, then gravity + fade
}
```

#### 3.4 `ui/components/StrikeThroughPrice.kt` (NEW)

```kotlin
@Composable
fun StrikeThroughPrice(
    originalPrice: String,
    discountedPrice: String,
    discountPercent: Int,
    showDiscounted: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = CenterVertically) {
        // Original price: draws red line through text using Canvas
        // Animated reveal: line draws from left to right (drawLine with animated progress)
        // Discounted price: larger, bold, deep rose color, spring scale animation
        // Badge: "24% OFF" in MintGreen pill
    }
}
```

### Navigation Impact

No new route needed — `GiftUpgradeModal` is a dialog/modal shown from `PaywallScreen`.

```kotlin
// In NavGraph:
composable<Paywall> {
    PaywallScreen(
        onSubscribe = { /* show gift upgrade as dialog */ },
        ...
    )
}
```

---

## Phase 4: Community Feed (Replaces "More" Tab)

**Goal:** Build a full anonymous community forum with posts, image uploads, comments, and likes. Replaces the "More" bottom nav tab. Settings becomes accessible via a gear icon within the Community screen.

### Data Layer

#### 4.1 `data/model/CommunityPost.kt` (NEW)

```kotlin
@Entity(
    tableName = "community_posts",
    indices = [Index(value = ["timestamp", "category"])]
)
data class CommunityPost(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val content: String,
    val imageUri: String? = null,           // local file URI
    val category: String = "general",       // "cycle_talk", "symptoms", "pregnancy", "wellness", "ask_saathi"
    val timestamp: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isAnonymous: Boolean = true,
    val anonymousColor: Int = randomAvatarColor(),  // color index for avatar
    val isLikedByCurrentUser: Boolean = false
)
```

#### 4.2 `data/model/CommunityComment.kt` (NEW)

```kotlin
@Entity(
    tableName = "community_comments",
    foreignKeys = [ForeignKey(
        entity = CommunityPost::class,
        parentColumns = ["id"],
        childColumns = ["postId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["postId"])]
)
data class CommunityComment(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val postId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isAnonymous: Boolean = true
)
```

#### 4.3 `data/dao/CommunityDao.kt` (NEW)

```kotlin
@Dao
interface CommunityDao {
    @Query("SELECT * FROM community_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<CommunityPost>>

    @Query("SELECT * FROM community_posts WHERE category = :category ORDER BY timestamp DESC")
    fun getPostsByCategory(category: String): Flow<List<CommunityPost>>

    @Query("SELECT * FROM community_posts ORDER BY likeCount DESC, timestamp DESC")
    fun getPopularPosts(): Flow<List<CommunityPost>>

    @Query("SELECT * FROM community_comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getComments(postId: String): Flow<List<CommunityComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: CommunityPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommunityComment)

    @Query("UPDATE community_posts SET likeCount = likeCount + 1 WHERE id = :postId")
    suspend fun incrementLike(postId: String)

    @Query("UPDATE community_posts SET likeCount = CASE WHEN likeCount > 0 THEN likeCount - 1 ELSE 0 END WHERE id = :postId")
    suspend fun decrementLike(postId: String)

    @Query("UPDATE community_posts SET commentCount = commentCount + 1 WHERE id = :postId")
    suspend fun incrementCommentCount(postId: String)

    @Delete
    suspend fun deletePost(post: CommunityPost)
}
```

#### 4.4 `data/database/PeriodSaathiDatabase.kt` (EDIT)

```kotlin
@Database(
    entities = [
        CycleEntry::class, CycleSettings::class, JournalEntry::class,
        Reminder::class, AccessoryEntity::class, ChallengeProgressEntity::class,
        PurchaseRecord::class, HabitCompletion::class,
        CommunityPost::class, CommunityComment::class   // NEW
    ],
    version = 5,  // bumped from 4
    exportSchema = false
)
abstract class PeriodSaathiDatabase : RoomDatabase() {
    abstract fun communityDao(): CommunityDao  // NEW
    // ... existing DAOs

    companion object {
        // Add migration 4→5:
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS community_posts (
                        id TEXT NOT NULL PRIMARY KEY,
                        content TEXT NOT NULL,
                        imageUri TEXT,
                        category TEXT NOT NULL DEFAULT 'general',
                        timestamp INTEGER NOT NULL,
                        likeCount INTEGER NOT NULL DEFAULT 0,
                        commentCount INTEGER NOT NULL DEFAULT 0,
                        isAnonymous INTEGER NOT NULL DEFAULT 1,
                        anonymousColor INTEGER NOT NULL DEFAULT 0
                    )
                """)
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS community_comments (
                        id TEXT NOT NULL PRIMARY KEY,
                        postId TEXT NOT NULL,
                        content TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        isAnonymous INTEGER NOT NULL DEFAULT 1,
                        FOREIGN KEY (postId) REFERENCES community_posts(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS idx_comments_postId ON community_comments(postId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS idx_posts_timestamp ON community_posts(timestamp)")
                database.execSQL("CREATE INDEX IF NOT EXISTS idx_posts_category ON community_posts(category)")
            }
        }

        // Add MIGRATION_4_5 to the builder chain
    }
}
```

### UI Layer

#### 4.5 `ui/screens/community/CommunityScreen.kt` (NEW)

**Layout:**
```
Scaffold(
    topBar = {
        TopAppBar("Community")
        // Gear icon → navigates to Settings
    },
    floatingActionButton = {
        FAB(icon = Create) → PostEditor
    }
) {
    Column {
        // Category Tabs
        TabRow {
            categories.forEach { Tab(text = it.name, selected, onClick) }
        }

        // Post Feed
        LazyColumn {
            items(posts) { PostCard(it) }
        }
    }
}
```

**Category tabs:** "Popular 🔥", "Cycle Talk 🌸", "Symptoms 💊", "Pregnancy 🤰", "Wellness 🧘", "Ask Saathi 💬"

**Sorting:** Popular = by like count, others = by timestamp.

#### 4.6 `ui/screens/community/CommunityViewModel.kt` (NEW)

```kotlin
@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val communityDao: CommunityDao
) : ViewModel() {

    data class CommunityUiState(
        val posts: List<CommunityPost> = emptyList(),
        val selectedCategory: String = "popular",
        val isLoading: Boolean = false,
        val showPostEditor: Boolean = false
    )

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    fun selectCategory(category: String)
    fun toggleLike(postId: String)
    fun deletePost(post: CommunityPost)
    fun onNewPostResult(post: CommunityPost)  // called from editor
}
```

#### 4.7 `ui/screens/community/PostCard.kt` (NEW)

```kotlin
@Composable
fun PostCard(
    post: CommunityPost,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(shape = RoundedCornerShape(20.dp)) {
        Column(padding = 16.dp) {
            // Row 1: Anonymous avatar (circle with color + "?") + category tag + timestamp
            Row {
                // Circle(32dp) with background = anonymousColor from color palette
                // Category chip: small PastelChip
                // Timestamp: "2h ago" relative format
            }

            Spacer(8.dp)

            // Row 2: Content text (expandable)
            ExpandableText(
                text = post.content,
                maxLines = 4,
                "Continue reading..."
            )

            // Row 3: Image (if present)
            if (post.imageUri != null) {
                AsyncImage or rememberImage(
                    model = post.imageUri,
                    contentScale = Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(8.dp)

            // Row 4: Action toolbar
            Row(horizontalArrangement = spacedBy(24.dp)) {
                // Like button: heart icon, scale animation on tap
                IconButton(onClick = onLike) {
                    AnimatedContent(targetState = post.likeCount > 0) { liked ->
                        Icon(
                            if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            tint = if (liked) DeepRose else OnSurfaceVariant,
                            modifier = Modifier
                                .graphicsLayer { scaleX = if (justLiked) 1.3f else 1f }
                        )
                    }
                    Text(post.likeCount.toString())
                }

                // Comment button
                IconButton(onClick = onComment) {
                    Icon(Icons.Outlined.ChatBubbleOutline, ...)
                    Text(post.commentCount.toString())
                }

                // Share button
                IconButton(onClick = onShare) {
                    Icon(Icons.Outlined.Share, ...)
                }
            }
        }
    }
}
```

#### 4.8 `ui/screens/community/PostEditorScreen.kt` (NEW)

```kotlin
@Composable
fun PostEditorScreen(
    onPost: (CommunityPost) -> Unit,
    onBack: () -> Unit
) {
    var content by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCategory by remember { mutableStateOf("cycle_talk") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "New Post",
                navigationIcon = { IconButton(←) },
                actions = { TextButton("Post") { onPost(...) } }
            )
        }
    ) {
        Column {
            // Category dropdown/exposed dropdown menu
            ExposedDropdownMenuBox(
                selectedCategory,
                categories
            )

            // Image preview (if selected) + remove button
            // Image picker button

            // Text field (auto-focus, multiline, fills remaining space)
            BasicTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.fillMaxSize().padding(16.dp),
                placeholder = "Share your thoughts... (anonymous post)"
            )
        }
    }
}
```

#### 4.9 `ui/screens/community/CommentSheet.kt` (NEW)

```kotlin
@Composable
fun CommentSheet(
    postId: String,
    onDismiss: () -> Unit,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    // ModalBottomSheet
    val comments by viewModel.getCommentsForPost(postId).collectAsState(initial = emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        // Header: "Comments (12)"
        // Comments list: LazyColumn with comment items
        // Each comment: anonymous avatar + text + timestamp

        // Bottom input row:
        Row {
            TextField(modifier = Modifier.weight(1f), placeholder = "Add a comment...")
            IconButton(Icons.Filled.Send) { viewModel.addComment(postId, text) }
        }
    }
}
```

#### 4.10 `ui/components/InsightCarousel.kt` (NEW)

**Home Dashboard integration** — horizontal scrollable carousel inserted below the HeroCard section in `HomeScreen.kt`.

```kotlin
@Composable
fun InsightCarousel(
    onLogSymptoms: () -> Unit,
    onFertilityCalculator: () -> Unit,
    onSymptomForecast: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Text("Quick Insights", style = ...)
        LazyRow(horizontalArrangement = spacedBy(12.dp)) {
            item {
                InsightCard("Log Symptoms", Icons.Rounded.MedicalServices, BlushPink, onLogSymptoms)
            }
            item {
                InsightCard("Fertility Calculator", Icons.Rounded.CalendarMonth, SoftLavender, onFertilityCalculator)
            }
            item {
                InsightCard("Symptom Forecast", Icons.Rounded.TrendingUp, BabyBlue, onSymptomForecast)
            }
        }
    }
}

@Composable
fun InsightCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    GlassCard(width = 140.dp, shape = RoundedCornerShape(20.dp), onClick = onClick) {
        Column(horizontalAlignment = CenterHorizontally, padding = 16.dp) {
            Box(circle, background = color.copy(0.2f)) { Icon(icon, tint = color) }
            Text(title, style = ...)
        }
    }
}
```

### Navigation Impact

#### 4.11 `ui/navigation/Screen.kt` (EDIT)

```kotlin
@Serializable object Community
@Serializable object PostEditor
```

#### 4.12 `ui/navigation/NavGraph.kt` (EDIT)

```kotlin
composable<Community> { CommunityScreen(
    onNavigateToSettings = { navController.navigate(Settings) },
    onNavigateToPostEditor = { navController.navigate(PostEditor) }
) }
composable<PostEditor> { PostEditorScreen(
    onPost = { navController.popBackStack() },
    onBack = { navController.popBackStack() }
) }
```

#### 4.13 `ui/navigation/BottomNavBar.kt` (EDIT)

Replace "More" tab with "Community":

```kotlin
val bottomNavTabs = listOf(
    NavTab("Home", Icons.Rounded.Home, Home, "Home tab"),
    NavTab("Calendar", Icons.Rounded.CalendarMonth, Calendar, "Calendar tab"),
    NavTab("Wellness", Icons.Rounded.Favorite, Wellness, "Wellness tab"),
    NavTab("Community", Icons.Rounded.Forum, Community, "Community tab"),
)
```

#### 4.14 `MainActivity.kt` (EDIT)

Update `mainScreenRoutes`:

```kotlin
val mainScreenRoutes = setOf(
    Home::class.qualifiedName,
    Calendar::class.qualifiedName,
    Wellness::class.qualifiedName,
    Community::class.qualifiedName   // was Settings
)
```

Settings is now accessed from Community screen's gear icon (or remains navigable via backstack).

---

## Home Dashboard Enhancement

#### Insert `InsightCarousel` into `HomeScreen.kt` (EDIT)

After `HeroCard` (around line 167), add:

```kotlin
item {
    InsightCarousel(
        onLogSymptoms = { onNavigateToDayLog(System.currentTimeMillis()) },
        onFertilityCalculator = { onNavigateToCalendar() },
        onSymptomForecast = { onNavigateToInsights() },
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
```

This does NOT replace existing content — it adds a new section. The `HomeScreen.kt` already has `onNavigateToInsights` as a parameter (line 74), so it will be wired through from `NavGraph.kt`.

---

## File Change Summary

### New Files (24)

| # | Path | Phase |
|---|------|-------|
| 1 | `data/model/UserOnboardingData.kt` | P1 |
| 2 | `data/model/CommunityPost.kt` | P4 |
| 3 | `data/model/CommunityComment.kt` | P4 |
| 4 | `data/dao/CommunityDao.kt` | P4 |
| 5 | `ui/screens/onboarding/OnboardingViewModel.kt` | P1 |
| 6 | `ui/screens/paywall/ProcessingSplashScreen.kt` | P2 |
| 7 | `ui/screens/paywall/PaywallScreen.kt` | P2 |
| 8 | `ui/screens/paywall/PaywallViewModel.kt` | P2 |
| 9 | `ui/screens/paywall/SubscriptionCard.kt` | P2 |
| 10 | `ui/screens/paywall/FreeTrialToggle.kt` | P2 |
| 11 | `ui/screens/paywall/GiftUpgradeModal.kt` | P3 |
| 12 | `ui/screens/paywall/GiftUpgradeViewModel.kt` | P3 |
| 13 | `ui/screens/community/CommunityScreen.kt` | P4 |
| 14 | `ui/screens/community/CommunityViewModel.kt` | P4 |
| 15 | `ui/screens/community/PostCard.kt` | P4 |
| 16 | `ui/screens/community/PostEditorScreen.kt` | P4 |
| 17 | `ui/screens/community/CommentSheet.kt` | P4 |
| 18 | `ui/components/QuestionCard.kt` | P1 |
| 19 | `ui/components/OptionPill.kt` | P1 |
| 20 | `ui/components/MascotReactionGuide.kt` | P1 |
| 21 | `ui/components/LerpProgressIndicator.kt` | P2 |
| 22 | `ui/components/ParticleBurstOverlay.kt` | P3 |
| 23 | `ui/components/StrikeThroughPrice.kt` | P3 |
| 24 | `ui/components/InsightCarousel.kt` | P4 |

### Modified Files (13)

| # | Path | Phase | Change |
|---|------|-------|--------|
| 1 | `data/datastore/UserPreferences.kt` | P1 | Add onboarding keys/accessors |
| 2 | `data/model/PurchaseRecord.kt` | P2 | Add subscription fields |
| 3 | `data/dao/PurchaseDao.kt` | P2 | Add subscription queries |
| 4 | `data/database/PeriodSaathiDatabase.kt` | P4 | Add Community entities, bump version, migration |
| 5 | `ui/screens/onboarding/OnboardingScreen.kt` | P1 | Rewrite with dynamic wizard |
| 6 | `ui/screens/home/HomeScreen.kt` | P4 | Insert InsightCarousel |
| 7 | `ui/navigation/Screen.kt` | P2/P4 | Add ProcessingSplash, Paywall, Community, PostEditor |
| 8 | `ui/navigation/NavGraph.kt` | P1/P2/P4 | Wire all new routes |
| 9 | `ui/navigation/BottomNavBar.kt` | P4 | Replace "More" with "Community" |
| 10 | `MainActivity.kt` | P4 | Update mainScreenRoutes |

---

## Database Migration Strategy

**Current version:** 4
**Target version:** 5

Migration 4→5 is purely additive (new tables, no destructive changes):

```kotlin
private val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS community_posts (...)")
        database.execSQL("CREATE TABLE IF NOT EXISTS community_comments (...)")
        database.execSQL("CREATE INDEX ...")
    }
}

// Add to builder:
Room.databaseBuilder(...)
    .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
    ...
```

No data loss. No column changes on existing tables. If using fallbackToDestructiveMigration(), remove it before release.

---

## Testing Strategy

| Phase | What to Test | Method |
|-------|-------------|--------|
| P1 | All question flows, mascot emotion mapping, DataStore persistence | Unit tests on ViewModel + manual QA |
| P2 | Progress animation timing, plan selection, trial toggle, purchase flow | Manual + unit tests on ViewModel |
| P3 | Tier transition animations, particle burst, discount strikethrough | Visual QA + animation timing tests |
| P4 | CRUD posts/comments, image attachment, like/unlike, category filtering | Unit tests on DAO + ViewModel, manual UI |

---

## Dependencies to Add

For Google Play Billing:

```toml
# In gradle/libs.versions.toml
billing = "7.0.0"
billing-ktx = { module = "com.android.billingclient:billing-ktx", version.ref = "billing" }
```

For image loading in community posts (if not already using Coil):

```toml
coil = "2.6.0"
coil-compose = { module = "io.coil-kt:coil-compose", version.ref = "coil" }
```

---

## Appendix: Mascot Emotion Mapping

| Context | Mascot Emotion |
|---------|---------------|
| Track Cycle goal | HAPPY |
| Get Pregnant goal | LISTENING |
| Track Pregnancy goal | HUGGING |
| Birth control questions | LISTENING |
| Cycle/period length questions | LISTENING |
| Last period date | HAPPY |
| Onboarding complete | EXCITED |
| Processing splash | LISTENING |
| Paywall screen | EXCITED (with "Go Premium" bubble) |
| Gift Standard | HAPPY |
| Gift Rare | EXCITED |
| Gift Epic | EXCITED (with star sparkle animation) |
| Community feed | LISTENING (reading posts) |
| Post creation | HAPPY |
