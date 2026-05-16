# Play Store Release Steps

## Step 1: Increment Version

Update `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = <increment by 1>
    versionName = "1.0.<build_number>"
}
```

**Version Code Rules:**
- Must be an integer
- Must be higher than the previous release
- Increment by 1 for each release

**Version Name Format:**
- `Major.Minor.Patch` (e.g., 1.0.0 → 1.0.1 → 1.1.0 → 2.0.0)

## Step 2: Generate Signed App Bundle (AAB)

```bash
# Clean build
./gradlew clean

# Generate release AAB
./gradlew bundleRelease
```

Output location:
```
app/build/outputs/bundle/release/app-release.aab
```

## Step 3: Sign the AAB (Manual Alternative)

If using Android Studio:
1. Build → Generate Signed Bundle / APK
2. Select "Android App Bundle"
3. Select your keystore file (`period-saathi-key.jks`)
4. Enter keystore password and key alias
5. Select "release" build variant
6. Click "Finish"

## Step 4: Verify the AAB

```bash
# Check the AAB exists and check its size
ls -lh app/build/outputs/bundle/release/app-release.aab
```

## Step 5: Upload to Play Console

1. Go to [Google Play Console](https://play.google.com/console/)
2. Select Period Saathi app
3. Navigate to **Production** track
4. Click **Create new release**
5. Upload `app-release.aab`
6. Fill in release notes (see below)

## Step 6: Release Notes (What's New)

### Template for First Release

```
🌸 Welcome to Period Saathi!

• Track your period, symptoms, and mood with ease
• Smart predictions for cycles and fertile windows
• Cramp relief yoga flow and wellness remedies
• Beautiful glassmorphism design
• Privacy-first — all data stays on your device
• Biometric lock for extra privacy
• Bilingual: English & Hindi
• 100% free, no ads, no internet required
```

### Template for Updates

```
What's new in v<version>:

• New feature: <feature description>
• Bug fixes: <fix description>
• Performance improvements
• Updated translations
```

## Step 7: Review & Submit

1. Verify all uploaded assets (icon, screenshots, feature graphic)
2. Verify content rating
3. Verify target audience (Women 13+)
4. Click **Submit for review**
5. **First release:** 1-3 days for review
6. **Updates:** typically 1-24 hours

## Quick Reference

```bash
# Full release sequence
cd PeriodSaathi/

# 1. Update versionCode + versionName in app/build.gradle.kts

# 2. Clean and build
./gradlew clean bundleRelease

# 3. Find the AAB
ls -la app/build/outputs/bundle/release/app-release.aab

# 4. Upload to Play Console
#    - Production → Create new release
#    - Upload AAB
#    - Add release notes
#    - Submit for review
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| `BUILD FAILED` | Run `./gradlew clean` then try again |
| Keystore not found | Check `local.properties` paths are correct |
| App not compatible | Check `minSdk` in build.gradle.kts |
| Review rejected | Check Play Console for policy violation details |
| Version code error | Increment `versionCode` to be higher than previous |