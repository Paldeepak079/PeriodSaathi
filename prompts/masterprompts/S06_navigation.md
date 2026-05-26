# S06 — NAVIGATION (FULLY WORKING)

> **Prerequisites:** S00 active · S05 complete · `assembleDebug` passing
> **Output:** 4 Kotlin files — Screen.kt, NavGraph.kt, MainActivity.kt, BottomNavBar.kt
> **Build check:** `./gradlew assembleDebug` — must pass. Navigate between ALL screens in emulator.

---

## REPORT INSTRUCTION
After completing this section AND running the build, create:
`masterprompts/reports/S06_report.md`

Include:
- 📄 All 4 files created (checklist)
- ✅ Build result (SUCCESS / FAILED)
- 🗺️ All 19 routes registered (list them)
- 📱 Bottom nav: all 4 tabs navigating (yes/no)
- 🔙 Back navigation: working on all screens (yes/no)
- ❌ Errors and fixes applied
- ⚠️ Any navigation edge cases handled

---

## PROMPT

Create complete, working navigation for all 19 screens.
Every route must compile and navigate correctly.
Write ALL files completely — no placeholders.

---

### FILE 1: `ui/navigation/Screen.kt`

All 19 routes as `@Serializable` objects — required for Nav3 type-safe routing:

```kotlin
import kotlinx.serialization.Serializable

@Serializable object Splash
@Serializable object Onboarding
@Serializable object Login
@Serializable data class NameSetup(val fromGoogle: Boolean = false)
@Serializable object Home
@Serializable object Calendar
@Serializable object Wellness
@Serializable object PartnerMode
@Serializable object Remedies
@Serializable object YogaFlow
@Serializable object Journal
@Serializable object MoodMap
@Serializable object Insights
@Serializable object Settings
@Serializable object ReportExport
@Serializable object Wardrobe
@Serializable object Challenges
@Serializable object BreathingMode
@Serializable object Payment
@Serializable data class DayLog(val dateEpoch: Long)
```

---

### FILE 2: `ui/navigation/NavGraph.kt`

`PeriodSaathiNavGraph` composable with:
- `NavHostController`, `startDestination: Any = Splash`
- Enter transition: `slideInHorizontally(spring(DampingRatioMediumBouncy)) + fadeIn(300ms)`
- Exit transition: `slideOutHorizontally(-it/3, spring) + fadeOut(200ms)`
- PopEnter + PopExit transitions (mirror of above)

Register all 20 composables (19 screens + DayLog with `toRoute<DayLog>()`):
```kotlin
composable<Splash> { SplashScreen(onNavigate = { navController.navigate(it) { popUpTo<Splash> { inclusive = true } } }) }
composable<Onboarding> { OnboardingScreen(onComplete = { navController.navigate(Login) }) }
composable<Login> { LoginScreen(navController) }
composable<NameSetup> { NameSetupScreen(navController) }
composable<Home> { HomeScreen(navController) }
// ... all remaining screens
composable<DayLog> { backStackEntry ->
  val dayLog = backStackEntry.toRoute<DayLog>()
  DayLogScreen(dateEpoch = dayLog.dateEpoch, navController = navController)
}
```

**IMPORTANT:** For screens not yet implemented, create empty placeholder composables that compile:
```kotlin
@Composable fun PlaceholderScreen(name: String, navController: NavHostController) {
  Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text(name, style = MaterialTheme.typography.headlineLarge)
  }
}
```

---

### FILE 3: `MainActivity.kt` (complete rewrite)

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private lateinit var biometricManager: AppBiometricManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    WindowCompat.setDecorFitsSystemWindows(window, false)
    
    // FLAG_SECURE: prevents screenshots in recent apps
    window.setFlags(
      WindowManager.LayoutParams.FLAG_SECURE,
      WindowManager.LayoutParams.FLAG_SECURE
    )

    biometricManager = AppBiometricManager(this)

    setContent {
      PeriodSaathiTheme {
        val navController = rememberNavController()
        val currentRoute by navController.currentBackStackEntryAsState()
        
        // Show bottom nav only for the 4 main screens
        val showBottomNav = currentRoute?.destination?.route in mainScreenRoutes

        Scaffold(
          bottomBar = {
            AnimatedVisibility(
              visible = showBottomNav,
              enter = slideInVertically(initialOffsetY = { it }),
              exit = slideOutVertically(targetOffsetY = { it })
            ) {
              BottomNavBar(navController = navController)
            }
          },
          contentWindowInsets = WindowInsets.navigationBars
        ) { paddingValues ->
          Box(modifier = Modifier.padding(paddingValues)) {
            PeriodSaathiNavGraph(navController = navController)
          }
        }
      }
    }
  }
}

val mainScreenRoutes = setOf(
  Home::class.qualifiedName,
  Calendar::class.qualifiedName,
  Wellness::class.qualifiedName,
  Settings::class.qualifiedName
)
```

---

### FILE 4: `ui/navigation/BottomNavBar.kt`

Custom bottom navigation — do NOT use `NavigationBar` composable.
Build from scratch with:

**Design:**
- Frosted glass background: Surface with `alpha(0.92f)` + background(CreamWhite)
- Height: 72.dp + WindowInsets.navigationBars

**4 Tabs:**
- Home: `Icons.Rounded.Home` → navigate to Home
- Calendar: `Icons.Rounded.CalendarMonth` → navigate to Calendar
- Wellness: `Icons.Rounded.Favorite` → navigate to Wellness
- More: `Icons.Rounded.GridView` → navigate to Settings

**Animations:**
- Active pill indicator: `animateDpAsState(targetValue = tabXOffset, spring())` slides to active tab
- Icon scale: `animateFloatAsState(if(active) 1.2f else 1.0f, spring())`
- Active: icon color = BlushPink, label visible
- Inactive: icon color 40% opacity, no label

**Interaction:**
- Each tab: `pointerInput` for press scale 0.9 → release 1.0
- `onClick`: `navController.navigate(route) { launchSingleTop = true; restoreState = true; popUpTo(graph.startDestinationId) { saveState = true } }`

**Accessibility:**
- `contentDescription` on each tab icon
- `semantics { role = Role.Tab }`

Include `@Preview` showing all 4 tabs, first tab (Home) active.

---

## AFTER COMPLETION
Run: `./gradlew assembleDebug`
Test in emulator: tap all 4 bottom nav tabs, verify navigation works.
Write the report.
