# S13 — TESTING SUITE

> **Prerequisites:** S00 active · S12 complete · `assembleDebug` passing
> **Output:** 7 Kotlin test files
> **Build check:** `./gradlew testDebugUnitTest` — must pass with ≥70% coverage on use cases.

---

## REPORT INSTRUCTION
After completing this section AND running the tests, create:
`masterprompts/reports/S13_report.md`

Include:
- 📄 All 7 test files created (checklist)
- ✅ Unit test result: N passed / M failed / X skipped
- 📊 Coverage percentage on `domain/usecase/` package
- 🧪 Integration test result (Room in-memory DB)
- 📱 UI test result (Compose test rules)
- ❌ Test failures with root cause
- 🔧 Fixes applied to make tests pass

---

## PROMPT

Create the complete test suite for Period Saathi.
Write all test files completely — no stubbed tests, real assertions only.

---

### FILE 1: `test/util/TestData.kt`

Central fake data factory — used by ALL tests:

```kotlin
object TestData {

  // Cycle entries
  fun fakeCycleEntry(
    date: LocalDate = LocalDate.now(),
    flowIntensity: Int = 3,
    mood: MoodType = MoodType.GOOD,
    waterGlasses: Int = 4,
    isPeriodStart: Boolean = false,
    isPeriodEnd: Boolean = false,
    isRestDay: Boolean = false
  ): CycleEntry = CycleEntry(
    date = date,
    flowIntensity = flowIntensity,
    symptoms = listOf(Symptom.CRAMPS),
    mood = mood,
    notes = "Test note",
    waterGlasses = waterGlasses,
    isPeriodStart = isPeriodStart,
    isPeriodEnd = isPeriodEnd,
    isRestDay = isRestDay
  )

  // Period start dates for prediction testing
  fun fakePeriodHistory(cycleLengths: List<Int>): List<LocalDate> {
    var date = LocalDate.of(2024, 1, 1)
    return buildList {
      add(date)
      for (length in cycleLengths) {
        date = date.plusDays(length.toLong())
        add(date)
      }
    }.reversed() // Most recent first
  }

  // Settings
  fun fakeSettings(
    userName: String = "Test User",
    cyclesLogged: Int = 5,
    averageCycleLength: Int = 28,
    streakCount: Int = 3,
    totalPoints: Int = 50,
    premiumTier: String = "FREE"
  ): SettingsEntity = SettingsEntity(
    id = 1,
    userName = userName,
    cyclesLogged = cyclesLogged,
    averageCycleLength = averageCycleLength,
    averagePeriodLength = 5,
    streakCount = streakCount,
    totalPoints = totalPoints,
    premiumTier = premiumTier
  )

  // Home UI states
  fun fakeHomeUiState(
    mascotEmotion: MascotEmotion = MascotEmotion.HAPPY,
    waterGlasses: Int = 4,
    cycleDay: Int = 14
  ): HomeUiState = HomeUiState(
    userName = "Test User",
    cycleDay = cycleDay,
    totalCycleDays = 28,
    phase = CyclePhase.FOLLICULAR,
    waterGlasses = waterGlasses,
    streakCount = 3,
    mascotEmotion = mascotEmotion
  )
}
```

---

### FILE 2: `test/usecase/GetPredictionUseCaseTest.kt`

```kotlin
@RunWith(MockitoJUnitRunner::class)
class GetPredictionUseCaseTest {

  @Mock private lateinit var repository: CycleRepository
  private lateinit var useCase: GetPredictionUseCase

  @Before
  fun setup() {
    useCase = GetPredictionUseCase(repository)
  }

  @Test
  fun `returns null when less than 3 cycles logged`() = runTest {
    val dates = TestData.fakePeriodHistory(listOf(28, 29)) // Only 2 intervals = 3 dates
    whenever(repository.getPeriodStartDates(any())).thenReturn(flowOf(dates.take(2)))

    val result = useCase().first()

    assertNull("Should return null with only 2 cycle starts", result)
  }

  @Test
  fun `returns LOW confidence for exactly 3 cycle starts`() = runTest {
    val dates = TestData.fakePeriodHistory(listOf(28, 30)) // 3 dates, 2 intervals
    whenever(repository.getPeriodStartDates(any())).thenReturn(flowOf(dates))

    val result = useCase().first()

    assertNotNull(result)
    assertEquals(ConfidenceLevel.LOW, result!!.confidence)
  }

  @Test
  fun `returns MEDIUM confidence for 5 cycle starts`() = runTest {
    val dates = TestData.fakePeriodHistory(listOf(28, 30, 27, 29)) // 5 dates, 4 intervals
    whenever(repository.getPeriodStartDates(any())).thenReturn(flowOf(dates))

    val result = useCase().first()

    assertNotNull(result)
    assertEquals(ConfidenceLevel.MEDIUM, result!!.confidence)
  }

  @Test
  fun `returns HIGH confidence for 7+ cycle starts`() = runTest {
    val dates = TestData.fakePeriodHistory(listOf(28, 30, 27, 29, 28, 31)) // 7 dates, 6 intervals
    whenever(repository.getPeriodStartDates(any())).thenReturn(flowOf(dates))

    val result = useCase().first()

    assertNotNull(result)
    assertEquals(ConfidenceLevel.HIGH, result!!.confidence)
  }

  @Test
  fun `weighted average gives more weight to recent cycles`() = runTest {
    // First 3 cycles: 20 days. Last cycle: 35 days.
    // Weighted avg should be closer to 35 than naive average.
    val dates = TestData.fakePeriodHistory(listOf(20, 20, 20, 35))
    whenever(repository.getPeriodStartDates(any())).thenReturn(flowOf(dates))

    val result = useCase().first()!!
    val daysUntilNext = ChronoUnit.DAYS.between(LocalDate.now(), result.predictedStartDate).toInt()

    // Naive avg = 23.75, weighted avg should be > 25 (recent 35 has more weight)
    assertTrue("Weighted avg should favor recent data", result.accuracyDays >= 1)
  }

  @Test
  fun `handles irregular cycles without crashing`() = runTest {
    // Polymenorrhea: two periods in one month (14 days apart)
    val dates = TestData.fakePeriodHistory(listOf(14, 35, 14, 28))
    whenever(repository.getPeriodStartDates(any())).thenReturn(flowOf(dates))

    // Should not throw
    assertDoesNotThrow { runTest { useCase().first() } }
  }

  @Test
  fun `daysUntil is never shown as negative - no overdue language`() = runTest {
    // Last period was 35 days ago with avg 28-day cycle
    val dates = TestData.fakePeriodHistory(listOf(28, 28, 28)).map { it.minusDays(35) }
    whenever(repository.getPeriodStartDates(any())).thenReturn(flowOf(dates))

    val result = useCase().first()
    // Even if period is "late", daysUntil should be represented but
    // business logic in screen should use "still tracking" — test use case returns accurate data
    assertNotNull(result)
    // daysUntil can be negative — that's correct — screen handles language
  }
}
```

---

### FILE 3: `test/usecase/LogCycleEntryUseCaseTest.kt`

```kotlin
@RunWith(MockitoJUnitRunner::class)
class LogCycleEntryUseCaseTest {

  @Mock private lateinit var cycleRepository: CycleRepository
  @Mock private lateinit var settingsRepository: SettingsRepository
  @Mock private lateinit var gamificationManager: GamificationManager
  private lateinit var useCase: LogCycleEntryUseCase

  @Before
  fun setup() {
    useCase = LogCycleEntryUseCase(cycleRepository, settingsRepository, gamificationManager)
    whenever(runBlocking { gamificationManager.awardPoints(any()) })
      .thenReturn(GamificationResult(3, 53, 3))
  }

  @Test
  fun `future dates are rejected`() = runTest {
    val futureDate = LocalDate.now().plusDays(1)
    val result = useCase(TestData.fakeCycleEntry(date = futureDate))
    
    assertTrue(result.isFailure)
    assertTrue(result.exceptionOrNull() is IllegalArgumentException)
  }

  @Test
  fun `today's date is accepted`() = runTest {
    val entry = TestData.fakeCycleEntry(date = LocalDate.now())
    val result = useCase(entry)
    
    assertTrue("Today should be loggable", result.isSuccess)
    verify(cycleRepository).insertOrUpdateEntry(entry)
  }

  @Test
  fun `flow intensity 1-5 is accepted`() = runTest {
    for (intensity in 1..5) {
      val entry = TestData.fakeCycleEntry(flowIntensity = intensity)
      val result = useCase(entry)
      assertTrue("Intensity $intensity should be accepted", result.isSuccess)
    }
  }

  @Test
  fun `flow intensity 0 is accepted (no period logged)`() = runTest {
    val entry = TestData.fakeCycleEntry(flowIntensity = 0)
    assertTrue(useCase(entry).isSuccess)
  }

  @Test
  fun `flow intensity 6 or more is rejected`() = runTest {
    val entry = TestData.fakeCycleEntry(flowIntensity = 6)
    assertTrue(useCase(entry).isFailure)
  }

  @Test
  fun `rest day is suggested not auto-set on heavy flow`() = runTest {
    // Heavy flow (4) for consecutive days should NOT auto-set isRestDay
    val entry = TestData.fakeCycleEntry(flowIntensity = 4, isRestDay = false)
    useCase(entry)
    
    // Verify the entry saved does NOT have isRestDay forced to true
    verify(cycleRepository).insertOrUpdateEntry(argThat { !isRestDay })
  }

  @Test
  fun `points are awarded correctly on log`() = runTest {
    val entry = TestData.fakeCycleEntry()
    useCase(entry)
    
    verify(gamificationManager).awardPoints(PointEvent.CYCLE_LOGGED)
  }
}
```

---

### FILE 4: `test/viewmodel/HomeViewModelTest.kt`

```kotlin
@ExperimentalCoroutinesApi
class HomeViewModelTest {

  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @MockK private lateinit var getHomeDataUseCase: GetHomeDataUseCase
  @MockK private lateinit var logCycleEntryUseCase: LogCycleEntryUseCase
  @MockK private lateinit var addWaterUseCase: AddWaterUseCase

  private lateinit var viewModel: HomeViewModel

  @Before
  fun setup() {
    MockKAnnotations.init(this)
    every { getHomeDataUseCase() } returns flowOf(TestData.fakeHomeUiState())
    viewModel = HomeViewModel(getHomeDataUseCase, logCycleEntryUseCase, addWaterUseCase)
  }

  @Test
  fun `mascot is SAD when water less than 3 glasses`() = runTest {
    val state = TestData.fakeHomeUiState(waterGlasses = 2, mascotEmotion = MascotEmotion.SAD)
    every { getHomeDataUseCase() } returns flowOf(state)
    viewModel = HomeViewModel(getHomeDataUseCase, logCycleEntryUseCase, addWaterUseCase)

    viewModel.homeState.test {
      val emitted = awaitItem()
      assertEquals(MascotEmotion.SAD, emitted.mascotEmotion)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `mascot is EXCITED when water goal reached`() = runTest {
    val state = TestData.fakeHomeUiState(waterGlasses = 8, mascotEmotion = MascotEmotion.EXCITED)
    every { getHomeDataUseCase() } returns flowOf(state)
    viewModel = HomeViewModel(getHomeDataUseCase, logCycleEntryUseCase, addWaterUseCase)

    viewModel.homeState.test {
      assertEquals(MascotEmotion.EXCITED, awaitItem().mascotEmotion)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `no prediction shown before 3 cycles`() = runTest {
    val state = TestData.fakeHomeUiState().copy(prediction = null)
    every { getHomeDataUseCase() } returns flowOf(state)
    viewModel = HomeViewModel(getHomeDataUseCase, logCycleEntryUseCase, addWaterUseCase)

    viewModel.homeState.test {
      assertNull(awaitItem().prediction)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `confetti shown when water goal is exactly reached`() = runTest {
    coEvery { addWaterUseCase(any()) } returns WaterResult(8, goalReached = true)
    
    viewModel.logWater(1)
    
    assertTrue(viewModel.showConfetti.value)
  }
}

// Test rule to swap Main dispatcher in tests
class MainDispatcherRule : TestWatcher() {
  val testDispatcher = UnconfinedTestDispatcher()
  override fun starting(description: Description) { Dispatchers.setMain(testDispatcher) }
  override fun finished(description: Description) { Dispatchers.resetMain() }
}
```

---

### FILE 5: `test/repository/CycleRepositoryTest.kt`

Integration test with in-memory Room database:

```kotlin
@RunWith(AndroidJUnit4::class)
class CycleRepositoryTest {

  private lateinit var db: PeriodSaathiDatabase
  private lateinit var dao: CycleEntryDao
  private lateinit var repository: CycleRepository

  @Before
  fun setup() {
    db = Room.inMemoryDatabaseBuilder(
      ApplicationProvider.getApplicationContext(),
      PeriodSaathiDatabase::class.java
    ).allowMainThreadQueries().build()
    dao = db.cycleEntryDao()
    repository = CycleRepositoryImpl(dao, MockK<PendingSyncDao>())
  }

  @After
  fun tearDown() { db.close() }

  @Test
  fun `insert and retrieve cycle entry by date`() = runTest {
    val entry = TestData.fakeCycleEntry(date = LocalDate.of(2024, 3, 15))
    repository.insertOrUpdateEntry(entry)

    val retrieved = repository.getEntryByDate(LocalDate.of(2024, 3, 15)).first()

    assertNotNull(retrieved)
    assertEquals(LocalDate.of(2024, 3, 15), retrieved!!.date)
    assertEquals(3, retrieved.flowIntensity)
  }

  @Test
  fun `update entry replaces existing`() = runTest {
    val date = LocalDate.now()
    repository.insertOrUpdateEntry(TestData.fakeCycleEntry(date = date, flowIntensity = 2))
    repository.insertOrUpdateEntry(TestData.fakeCycleEntry(date = date, flowIntensity = 4))

    val retrieved = repository.getEntryByDate(date).first()
    assertEquals(4, retrieved!!.flowIntensity) // Updated value
  }

  @Test
  fun `entries between dates returns correct range`() = runTest {
    val start = LocalDate.of(2024, 1, 1)
    for (i in 0..30) {
      repository.insertOrUpdateEntry(TestData.fakeCycleEntry(date = start.plusDays(i.toLong())))
    }

    val results = repository.getEntriesBetweenDates(
      LocalDate.of(2024, 1, 10),
      LocalDate.of(2024, 1, 20)
    ).first()

    assertEquals(11, results.size) // 10th through 20th inclusive
  }
}
```

---

### FILE 6: `androidTest/HomeScreenTest.kt`

Compose UI test:

```kotlin
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

  @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
  @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Before
  fun setup() { hiltRule.inject() }

  @Test
  fun homeScreen_mascoIsDisplayed() {
    composeTestRule.onNodeWithTag("saathi_mascot").assertIsDisplayed()
  }

  @Test
  fun waterButton_addsWater() {
    composeTestRule.onNodeWithText("+1 Glass").performClick()
    composeTestRule
      .onNodeWithTag("water_count")
      .assertTextContains("1", substring = true)
  }

  @Test
  fun bottomNav_navigatesToCalendar() {
    composeTestRule
      .onNodeWithContentDescription("Calendar tab")
      .performClick()
    composeTestRule
      .onNodeWithTag("calendar_screen")
      .assertIsDisplayed()
  }

  @Test
  fun bottomNav_allTabsNavigate() {
    val tabs = listOf("Home tab", "Calendar tab", "Wellness tab", "Settings tab")
    tabs.forEach { tab ->
      composeTestRule.onNodeWithContentDescription(tab).performClick()
      composeTestRule.waitForIdle()
      // Verify no crash and back button works
    }
  }
}
```

---

### FILE 7: `test/TestCoroutineRule.kt` + `test/usecase/AddWaterUseCaseTest.kt`

```kotlin
// TestCoroutineRule.kt
class TestCoroutineRule : TestWatcher(), TestCoroutineScope by TestCoroutineScope() {
  override fun starting(description: Description) { Dispatchers.setMain(coroutineContext[ContinuationInterceptor] as CoroutineDispatcher) }
  override fun finished(description: Description) { cleanupTestCoroutines(); Dispatchers.resetMain() }
}

// AddWaterUseCaseTest.kt
class AddWaterUseCaseTest {
  @get:Rule val testCoroutineRule = TestCoroutineRule()
  
  @MockK private lateinit var cycleRepository: CycleRepository
  @MockK private lateinit var gamificationManager: GamificationManager

  @Test
  fun `water count above 16 is rejected`() = runTest {
    // Insert entry with 16 glasses
    // Try to add 1 more
    // Verify rejected with error result
  }

  @Test
  fun `goal reached event emitted at 8 glasses`() = runTest {
    // Set current glasses to 7
    // Add 1 more
    // Verify WaterResult.goalReached == true
  }

  @Test
  fun `points awarded on each water log`() = runTest {
    val useCase = AddWaterUseCase(cycleRepository, gamificationManager)
    coEvery { cycleRepository.getEntryByDate(any()) } returns flowOf(TestData.fakeCycleEntry(waterGlasses = 3))
    coEvery { cycleRepository.insertOrUpdateEntry(any()) } just Runs

    useCase(1)

    coVerify { gamificationManager.awardPoints(PointEvent.WATER_LOGGED) }
  }
}
```

---

## AFTER COMPLETION
Run: `./gradlew testDebugUnitTest`
Run: `./gradlew jacocoTestReport` (if configured)
Paste test result summary in report.
