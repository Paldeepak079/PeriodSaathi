# S12 — SECURITY LAYER

> **Prerequisites:** S00 active · S11 complete · `assembleDebug` passing
> **Output:** 5 Kotlin files + 2 XML files — full security implementation
> **Build check:** `./gradlew assembleDebug` — must pass. Test biometric prompt on emulator.

---

## REPORT INSTRUCTION
After completing this section AND running the build, create:
`masterprompts/reports/S12_report.md`

Include:
- 📄 All 7 files created (checklist — 5 Kotlin + 2 XML)
- ✅ Build result (SUCCESS / FAILED)
- 🔒 FLAG_SECURE verified (screenshots blocked in recent apps)
- 👁️ Biometric prompt appears (yes/no)
- 🕵️ Stealth mode implemented (icon switch + PIN, yes/no)
- 🔐 SQLCipher key in EncryptedSharedPreferences (verified flow, yes/no)
- 📱 Certificate pinning: disabled for debug, enabled for release (yes/no)
- ❌ Errors and fixes applied
- ⚠️ Any security edge cases noted

---

## PROMPT

Create all security files for Period Saathi.
Write every file completely. No shortcuts on security.

---

### FILE 1: `security/AppBiometricManager.kt`

```kotlin
class AppBiometricManager(private val activity: FragmentActivity) {

  enum class BiometricAvailability { AVAILABLE, NOT_ENROLLED, NOT_AVAILABLE }

  fun isBiometricAvailable(): BiometricAvailability {
    val manager = BiometricManager.from(activity)
    return when (manager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
      BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.AVAILABLE
      BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NOT_ENROLLED
      BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.NOT_AVAILABLE
      BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NOT_AVAILABLE
      else -> BiometricAvailability.NOT_AVAILABLE
    }
  }

  fun authenticate(
    onSuccess: () -> Unit,
    onFailed: () -> Unit,
    onError: (String) -> Unit
  ) {
    val executor = ContextCompat.getMainExecutor(activity)
    val callback = object : BiometricPrompt.AuthenticationCallback() {
      override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        onSuccess()
      }
      override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        onFailed()
      }
      override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        onError(errString.toString())
      }
    }

    val prompt = BiometricPrompt(activity, executor, callback)
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
      .setTitle("Wake up Saathi 🌸")
      .setSubtitle("Verify to access Period Saathi")
      .setDescription("Your data is protected")
      .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
      .build()

    prompt.authenticate(promptInfo)
  }

  fun authenticateForSetting(
    settingName: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
      .setTitle("Confirm to change $settingName")
      .setSubtitle("Biometric verification required")
      .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
      .build()
    // Same callback pattern
  }
}
```

---

### FILE 2: `security/StealthModeManager.kt`

```kotlin
@Singleton
class StealthModeManager @Inject constructor(
  @ApplicationContext private val context: Context
) {
  private val prefs: SharedPreferences by lazy { SecurePreferences.getEncryptedPrefs(context) }

  companion object {
    private const val KEY_STEALTH_ENABLED = "stealth_mode_enabled"
    private const val KEY_STEALTH_PIN_HASH = "stealth_pin_hash"
    private const val KEY_DISGUISE_NAME = "disguise_name"
    private const val KEY_DISGUISE_PACKAGE = "disguise_package"
    
    // Activity alias names (must match AndroidManifest.xml)
    private const val REAL_ALIAS = "com.periodsaathi.app.MainActivity"
    private const val STEALTH_ALIAS = "com.periodsaathi.app.StealthAlias"
  }

  fun isStealthModeEnabled(): Boolean = prefs.getBoolean(KEY_STEALTH_ENABLED, false)

  fun enableStealthMode(pin: String, disguiseName: String) {
    val pinHash = hashPin(pin)
    prefs.edit()
      .putBoolean(KEY_STEALTH_ENABLED, true)
      .putString(KEY_STEALTH_PIN_HASH, pinHash)
      .putString(KEY_DISGUISE_NAME, disguiseName)
      .apply()
    switchToStealthAlias()
  }

  fun disableStealthMode(pin: String): Boolean {
    if (!verifyPin(pin)) return false
    prefs.edit()
      .putBoolean(KEY_STEALTH_ENABLED, false)
      .remove(KEY_STEALTH_PIN_HASH)
      .apply()
    switchToRealAlias()
    return true
  }

  fun verifyPin(input: String): Boolean {
    val storedHash = prefs.getString(KEY_STEALTH_PIN_HASH, null) ?: return false
    return hashPin(input) == storedHash
  }

  fun getDisguiseName(): String = prefs.getString(KEY_DISGUISE_NAME, "My Notes") ?: "My Notes"

  private fun hashPin(pin: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val salt = "period_saathi_stealth_salt_2024" // Fixed salt (secure enough for PIN storage)
    val bytes = digest.digest((pin + salt).toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
  }

  private fun switchToStealthAlias() {
    val pm = context.packageManager
    pm.setComponentEnabledSetting(
      ComponentName(context, REAL_ALIAS),
      PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
      PackageManager.DONT_KILL_APP
    )
    pm.setComponentEnabledSetting(
      ComponentName(context, STEALTH_ALIAS),
      PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
      PackageManager.DONT_KILL_APP
    )
  }

  private fun switchToRealAlias() {
    val pm = context.packageManager
    pm.setComponentEnabledSetting(
      ComponentName(context, REAL_ALIAS),
      PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
      PackageManager.DONT_KILL_APP
    )
    pm.setComponentEnabledSetting(
      ComponentName(context, STEALTH_ALIAS),
      PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
      PackageManager.DONT_KILL_APP
    )
  }
}
```

Add to `AndroidManifest.xml`:
```xml
<!-- Stealth mode alias — disguises app as "My Notes" -->
<activity-alias
  android:name=".StealthAlias"
  android:targetActivity=".MainActivity"
  android:label="My Notes"
  android:icon="@drawable/ic_stealth_notes"
  android:enabled="false"
  android:exported="true">
  <intent-filter>
    <action android:name="android.intent.action.MAIN"/>
    <category android:name="android.intent.category.LAUNCHER"/>
  </intent-filter>
</activity-alias>
```

---

### FILE 3: `security/CertificatePinner.kt`

```kotlin
object CertificatePinner {

  /**
   * Create an OkHttpClient with certificate pinning for Supabase.
   * Certificate pinning is DISABLED in debug builds to allow local proxy/testing.
   */
  fun create(isDebug: Boolean): OkHttpClient {
    val builder = OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)

    if (!isDebug) {
      val pinner = okhttp3.CertificatePinner.Builder()
        // Update this hash after fetching: openssl s_client -connect YOUR_PROJECT.supabase.co:443 | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | base64
        .add("*.supabase.co", "sha256/PLACEHOLDER_REPLACE_WITH_REAL_HASH")
        .add("*.supabase.co", "sha256/BACKUP_PIN_HASH") // Backup pin
        .build()
      builder.certificatePinner(pinner)
    }

    return builder.build()
  }
}
```

**File: `res/xml/network_security_config.xml`**
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
  <!-- No cleartext traffic on Supabase domain -->
  <domain-config cleartextTrafficPermitted="false">
    <domain includeSubdomains="true">supabase.co</domain>
    <pin-set expiration="2026-01-01">
      <!-- Get real pin: openssl s_client ... -->
      <pin digest="SHA-256">PLACEHOLDER_REPLACE_WITH_REAL_HASH</pin>
      <pin digest="SHA-256">BACKUP_PLACEHOLDER</pin>
    </pin-set>
  </domain-config>

  <!-- Allow debug proxying for development builds -->
  <debug-overrides>
    <trust-anchors>
      <certificates src="user"/>
      <certificates src="system"/>
    </trust-anchors>
  </debug-overrides>
</network-security-config>
```

Add to AndroidManifest.xml application tag:
`android:networkSecurityConfig="@xml/network_security_config"`

---

### FILE 4: `ui/screens/lock/LockScreen.kt`

```kotlin
@Composable
fun LockScreen(
  onUnlocked: () -> Unit,
  navController: NavHostController
) {
  val context = LocalContext.current
  val activity = context as FragmentActivity
  val biometricManager = remember { AppBiometricManager(activity) }
  
  // State
  var showPinEntry by remember { mutableStateOf(false) }
  var mascotState by remember { mutableStateOf(MascotEmotion.SLEEPING) }
  var isUnlocking by remember { mutableStateOf(false) }
  
  // Star field animation
  val stars = remember { List(50) { 
    Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 3f + 1f)  // x, y, speed
  }}
  
  val starOffset by rememberInfiniteTransition().animateFloat(
    initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(tween(8000))
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0D0A14)) // Deep space dark
  ) {
    // LAYER 1: Star field Canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
      stars.forEach { (x, y, speed) ->
        val adjustedY = (y + starOffset * speed * 0.1f) % 1f
        drawCircle(
          color = Color.White.copy(alpha = 0.6f),
          radius = 2f,
          center = Offset(x * size.width, adjustedY * size.height)
        )
      }
    }

    // LAYER 2: Lock content
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Sleeping mascot with Zzz animations
      Box(contentAlignment = Alignment.Center) {
        SaathiMascot(emotion = mascotState, size = 140.dp, onTap = {})
        // Zzz text animations — 3 "Z" elements, staggered, float upward + fade
        if (mascotState == MascotEmotion.SLEEPING) {
          ZzzAnimation()  // custom composable using Animatable chains
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Period Saathi is resting... 🌙",
        style = MaterialTheme.typography.titleMedium,
        color = Color.White.copy(alpha = 0.7f)
      )

      Spacer(modifier = Modifier.height(40.dp))

      // Biometric button
      if (!showPinEntry) {
        ScaleButton(onClick = {
          biometricManager.authenticate(
            onSuccess = {
              mascotState = MascotEmotion.EXCITED
              isUnlocking = true
              // Radial reveal + navigate
            },
            onFailed = {
              mascotState = MascotEmotion.SAD
              // Screen shake: ShakeAnimation composable
            },
            onError = { showPinEntry = true }
          )
        }) {
          GlassCard(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Rounded.Fingerprint, contentDescription = "Biometric", tint = BlushPink, modifier = Modifier.size(28.dp))
              Spacer(Modifier.width(8.dp))
              Text("Touch to wake Saathi", style = MaterialTheme.typography.bodyLarge, color = Color.White)
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { showPinEntry = true }) {
          Text("Use PIN instead", color = Color.White.copy(0.5f))
        }
      }
    }

    // PIN Entry overlay
    AnimatedVisibility(
      visible = showPinEntry,
      enter = fadeIn() + slideInVertically { it },
      exit = fadeOut() + slideOutVertically { it }
    ) {
      PinEntryScreen(
        onCorrectPin = { onUnlocked() },
        onDismiss = { showPinEntry = false }
      )
    }
  }
}

@Composable
private fun ZzzAnimation() {
  // 3 "Z" letters, each floats upward and fades using Animatable + LaunchedEffect with delay
}
```

---

### FILE 5: `ui/screens/lock/PinEntryScreen.kt`

```kotlin
@Composable
fun PinEntryScreen(
  onCorrectPin: () -> Unit,
  onDismiss: () -> Unit
) {
  val stealthManager: StealthModeManager = hiltViewModel()
  
  var enteredPin by remember { mutableStateOf("") }
  var errorState by remember { mutableStateOf(false) }

  // 4-dot indicator
  Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
    repeat(4) { index ->
      val filled = index < enteredPin.length
      Box(
        modifier = Modifier
          .size(16.dp)
          .clip(CircleShape)
          .background(if (filled) BlushPink else Color.White.copy(0.3f))
          .animateContentSize(spring())
      )
    }
  }

  Spacer(Modifier.height(32.dp))

  // 3×4 numpad
  LazyVerticalGrid(columns = GridCells.Fixed(3)) {
    items(listOf("1","2","3","4","5","6","7","8","9","","0","⌫")) { key ->
      when (key) {
        "" -> Spacer(Modifier)  // empty cell
        "⌫" -> NumberKey(label = key, onClick = {
          if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
        })
        else -> NumberKey(label = key, onClick = {
          if (enteredPin.length < 4) {
            enteredPin += key
            // Each tap: scale bounce 0.9→1.0 + haptic LIGHT
            if (enteredPin.length == 4) {
              // Check PIN
              if (stealthManager.verifyPin(enteredPin)) {
                // Flash green → onCorrectPin()
                onCorrectPin()
              } else {
                // Shake + flash red + clear
                errorState = true
                enteredPin = ""
              }
            }
          }
        })
      }
    }
  }
}

@Composable
private fun NumberKey(label: String, onClick: () -> Unit) {
  // Large circle button, scale bounce on press, haptic LIGHT
  // 72dp minimum size
  ScaleButton(onClick = onClick) {
    Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
      Text(label, style = MaterialTheme.typography.headlineMedium, color = Color.White)
    }
  }
}
```

---

### XML: `res/xml/backup_rules.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<full-backup-content>
  <!-- Exclude encrypted DB from auto-backup -->
  <exclude domain="database" path="period_saathi.db"/>
  <exclude domain="database" path="period_saathi.db-shm"/>
  <exclude domain="database" path="period_saathi.db-wal"/>
  <!-- Include preferences (encrypted) -->
  <include domain="sharedpref" path="period_saathi_prefs.xml"/>
</full-backup-content>
```

### XML: `res/xml/data_extraction_rules.xml` (Android 12+)
```xml
<?xml version="1.0" encoding="utf-8"?>
<data-extraction-rules>
  <cloud-backup>
    <exclude domain="database" path="period_saathi.db"/>
  </cloud-backup>
  <device-transfer>
    <exclude domain="database" path="period_saathi.db"/>
  </device-transfer>
</data-extraction-rules>
```

---

## AFTER COMPLETION
Run: `./gradlew assembleDebug`
Test on emulator: verify biometric prompt appears when LockScreen is navigated to.
Write the report.
