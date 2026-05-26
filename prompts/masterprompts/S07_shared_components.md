# S07 — SHARED COMPONENTS (ALL INTERACTIVE)

> **Prerequisites:** S00 active · S06 complete · `assembleDebug` passing
> **Output:** 10 Kotlin files in `ui/components/` and `ui/util/`
> **Build check:** `./gradlew assembleDebug` — must pass. All @Previews must render.

---

## REPORT INSTRUCTION
After completing this section AND running the build, create:
`masterprompts/reports/S07_report.md`

Include:
- 📄 All 10 files created (checklist)
- ✅ Build result (SUCCESS / FAILED)
- 🎨 All @Previews rendering in Android Studio (yes/no per component)
- ⚡ Haptic feedback implemented in all interactive components (yes/no)
- 🌸 Spring animations working (yes/no)
- ❌ Errors and fixes applied
- ⚠️ Any Canvas rendering issues

---

## PROMPT

Create every shared UI component. ALL must be:
1. Fully interactive (real onClick, real state changes)
2. Have `@Preview` with realistic fake data
3. Have proper haptic feedback on interactions
4. Have spring physics animations

Write ALL files completely — no placeholders.

---

### FILE 1: `ui/util/HapticFeedback.kt`

```kotlin
// Haptic modifier — combines clickable + haptic in one
fun Modifier.hapticClick(
  type: HapticFeedbackType = HapticFeedbackType.LongPress,
  onClick: () -> Unit
): Modifier

// Enum for haptic intensity
enum class HapticType { LIGHT, MEDIUM, HEAVY, SUCCESS, ERROR }

// Platform haptic function using VibrationEffect:
// LIGHT: createOneShot(30ms, 80 amplitude)
// MEDIUM: createOneShot(60ms, 150 amplitude)
// HEAVY: createOneShot(100ms, 255 amplitude)
// SUCCESS: createWaveform([0,30,50,30], [-1,200,-1,150], -1)
// ERROR: createWaveform([0,50,30,50,30,50], [-1,255,-1,255,-1,255], -1)
fun performHaptic(context: Context, type: HapticType)
```

---

### FILE 2: `ui/components/ScaleButton.kt`

Reusable composable wrapping any content with press-scale animation:

```kotlin
@Composable
fun ScaleButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  content: @Composable BoxScope.() -> Unit
)
```

Animation logic:
- Detect press via `interactionSource`
- `val scale by animateFloatAsState(if(pressed) 0.94f else 1.0f, animationSpec = if(pressed) tween(120, easing=FastOutLinearInEasing) else spring(dampingRatio=0.6f, stiffness=500f))`
- Apply: `Modifier.graphicsLayer { scaleX = scale; scaleY = scale }`
- Shadow reduction on press: `graphicsLayer { shadowElevation = if(pressed) 2f else 8f }`

---

### FILE 3: `ui/components/GlassCard.kt`

```kotlin
@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  shape: Shape = CardShape,
  onClick: (() -> Unit)? = null,
  enabled: Boolean = true,
  tint: Color = Color.Transparent,
  content: @Composable ColumnScope.() -> Unit
)
```

Implementation:
- `Surface(shape=shape, color=Color.White.copy(alpha=0.45f), border=BorderStroke(1.dp, Color.White.copy(alpha=0.6f)), modifier=modifier.shadow(8.dp, shape, ambientColor=BlushPink.copy(0.18f), spotColor=BlushPink.copy(0.18f)))`
- Wrap in `ScaleButton` if `onClick != null`
- Add tint overlay `Box` if `tint != Color.Transparent`
- Include @Preview for light AND dark theme

---

### FILE 4: `ui/components/PrimaryButton.kt`

```kotlin
@Composable
fun PrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isLoading: Boolean = false,
  enabled: Boolean = true,
  icon: ImageVector? = null
)
```

States:
- NORMAL: gradient background BlushPink → DeepRose
- LOADING: `CircularProgressIndicator` replaces text (same button size)
- DISABLED: 50% opacity + not clickable

Use `AnimatedContent(targetState = isLoading)` for text ↔ loading swap.
Wrap in `ScaleButton`. Haptic: MEDIUM on tap.
MinHeight: 56.dp, minWidth: 120.dp.
Include @Preview for all 3 states.

---

### FILE 5: `ui/components/PastelChip.kt`

```kotlin
@Composable
fun PastelChip(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  color: Color = BlushPink,
  leadingIcon: ImageVector? = null,
  trailingIcon: ImageVector? = null
)
```

- Selected: gradient fill (using `color` param) + `animateFloatAsState` scale to 1.03
- Unselected: white with 1dp border `color.copy(alpha=0.4f)`
- Animate background color: `animateColorAsState(spring)`
- Height: 36.dp, horizontal padding: 12.dp
- Include @Preview grid of chips, some selected

---

### FILE 6: `ui/components/CycleRing.kt`

Canvas-based donut ring:

```kotlin
@Composable
fun CycleRing(
  currentDay: Int,
  totalDays: Int,
  phase: CyclePhase,
  modifier: Modifier = Modifier,
  size: Dp = 180.dp
)
```

Canvas drawing:
- Background arc: 220-degree arc, grey, strokeWidth 16.dp
- Progress arc: animated from 0 → `(currentDay/totalDays * 220)`, gradient `Brush.sweepGradient(phase.colors)`
- Glow: second arc same path, `Paint` with `BlurMaskFilter(16f, NORMAL)`
- Pulsing glow: `infiniteTransition.animateFloat` for glow alpha 0.3↔0.8
- Center: `currentDay` (large Nunito) + "Day" label (small Poppins)
- Phase emoji below center text

Use `animateFloatAsState(spring)` for progress on `currentDay` change.
Include @Preview.

---

### FILE 7: `ui/components/WaterRingWithWave.kt`

```kotlin
@Composable
fun WaterRingWithWave(
  currentGlasses: Int,
  totalGlasses: Int = 8,
  modifier: Modifier = Modifier,
  size: Dp = 200.dp,
  onGoalReached: () -> Unit = {}
)
```

Canvas with wave animation:
- Background ring: grey, 12dp stroke
- Progress arc: BabyBlue gradient
- Wave inside circle:
  - `val phase by infiniteTransition.animateFloat(0f, 2*PI.toFloat(), 1800ms linear)`
  - `fillLevel = currentGlasses / totalGlasses.toFloat()`
  - Draw sine wave Path at fillLevel height
  - Fill with `BabyBlue.copy(alpha=0.6f)`
  - Second wave: phase offset by PI, alpha 0.4
- Center: "N/M" text + 💧 emoji

When `currentGlasses == totalGlasses` → call `onGoalReached()`.
Include @Preview with 4/8 glasses state.

---

### FILE 8: `ui/components/SaathiMascot.kt`

Full Canvas-based mascot:

```kotlin
enum class MascotEmotion { HAPPY, SAD, SLEEPING, EXCITED, PAIN, LISTENING, HUGGING }

@Composable
fun SaathiMascot(
  emotion: MascotEmotion = MascotEmotion.HAPPY,
  size: Dp = 120.dp,
  onTap: () -> Unit = {},
  showTip: Boolean = false,
  tipText: String = ""
)
```

Draw in `Canvas { DrawScope }`:
- Body: RoundRect with blush-to-coral gradient
- Arms: two rounded Rect stubs

Eyes per emotion (use drawOval, drawArc):
- HAPPY: filled ovals + highlight dots + cheek blush circles
- SAD: drooped corners + teardrop (animated downward loop)
- SLEEPING: curved arc lines (closed eyes)
- EXCITED: large round eyes + highlight
- PAIN: compressed ovals (squinting)
- LISTENING: normal eyes + two side bumps

Mouth per emotion (drawArc):
- HAPPY/EXCITED: upward arc
- SAD: downward arc
- SLEEPING: gentle small arc
- PAIN: slightly open downward

ANIMATIONS:
- Idle float: `infiniteTransition.animateFloat(-6f, 6f, 3000ms)` as `translationY`
- Sleeping Zzz: 3 Text elements animated upward + fade, staggered 700ms
- Sad teardrop: `Animatable` Y offset loops downward
- Excited bounce: `infiniteTransition.animateFloat(0f, -8f, 600ms)`
- Click bounce: `val scale = Animatable(1f)` → launch{ animateTo(1.3f) → animateTo(1f) }

SPEECH BUBBLE:
- Custom `Path` shape: rounded rect + triangular tail pointing down
- Typewriter text animation (add 1 char per 50ms)
- Appears above mascot on click (state: `showTip`)

Include 7 @Preview composables — one per emotion.

---

### FILE 9: `ui/components/ConfettiOverlay.kt`

Physics-based 60-particle confetti:

```kotlin
@Composable
fun ConfettiOverlay(
  visible: Boolean,
  onComplete: () -> Unit = {}
)
```

```kotlin
data class Particle(
  var x: Float, var y: Float,
  var vx: Float, var vy: Float,
  val color: Color,
  val size: Float,
  var rotation: Float,
  val rotationSpeed: Float
)
```

- Initialize 60 particles at random X along top, random velocities
- `withFrameNanos` loop in `LaunchedEffect(visible)`:
  - `p.x += p.vx * dt`; `p.y += p.vy * dt`; `p.vy += GRAVITY(980) * dt`; `p.rotation += ...`
- Draw each as rounded rect on Canvas
- Colors: BlushPink, SoftLavender, BabyBlue, ButterYellow, MintGreen
- Auto-dismiss when all particles below screen bottom → call `onComplete()`

---

### FILE 10: `ui/components/ShimmerSkeleton.kt`

Reusable shimmer loading placeholders:

```kotlin
// Shimmer brush definition
val shimmerColors = listOf(Color.White.copy(0.2f), Color.White.copy(0.5f), Color.White.copy(0.2f))
val translateAnim by rememberInfiniteTransition().animateFloat(0f, 1000f, 1500ms linear)
val brush = Brush.linearGradient(colors=shimmerColors, start=Offset(translateAnim-300f, 0f), end=Offset(translateAnim, 0f))

@Composable fun ShimmerBox(modifier: Modifier, shape: Shape = SmallCardShape)
@Composable fun ShimmerHomeScreen()    // full home screen layout placeholder
@Composable fun ShimmerCalendarGrid() // calendar grid placeholder
@Composable fun ShimmerCard(height: Dp = 120.dp)  // single card placeholder
```

---

## AFTER COMPLETION
Run: `./gradlew assembleDebug`
Open Android Studio: verify all @Previews render without errors.
Write the report.
