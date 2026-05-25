# Changing Logos & Images

## App Launcher Icon (pslogo.png)

The main app icon shown on the home screen and launcher.

**To replace:**
1. Place your new `pslogo.png` at `app/src/main/res/drawable/pslogo.png`
2. Also update `pslogo.png` at the project root (used by build scripts)
3. For different screen densities, add variants:
   - `app/src/main/res/mipmap-mdpi/ic_launcher.png` — 48×48 px
   - `app/src/main/res/mipmap-hdpi/ic_launcher.png` — 72×72 px
   - `app/src/main/res/mipmap-xhdpi/ic_launcher.png` — 96×96 px
   - `app/src/main/res/mipmap-xxhdpi/ic_launcher.png` — 144×144 px
   - `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png` — 192×192 px
   - Same sizes for `ic_launcher_round.png`
4. Or update `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` to point to a different drawable

## In-App Logo (Period Saathi Branding)

The `"Period Saathi"` text shown in:
- **LoginScreen.kt** (line 89-96): Header text
- **SplashScreen.kt** (line 183-190): Typewriter animation title

**To add a logo image instead of text:**
1. Add your image to `app/src/main/res/drawable/` (e.g. `brand_logo.png`)
2. In `LoginScreen.kt`, replace the `Text("Period Saathi", ...)` block with:
   ```kotlin
   Image(
       painter = painterResource(id = R.drawable.brand_logo),
       contentDescription = "Period Saathi",
       modifier = Modifier.height(40.dp)
   )
   ```
3. In `SplashScreen.kt`, replace `displayedText` `Text` with the same `Image`
4. Add import: `import androidx.compose.ui.res.painterResource`

## Saathi Mascot (Login & Splash Screens)

The pink character drawn via `SaathiMascot` composable in:
- `app/src/main/java/com/deepak/periodsaathi/ui/components/SaathiMascot.kt`
- Used in `LoginScreen.kt` (glass circle) and `SplashScreen.kt`

**To replace with a static image:**
1. Add your image (e.g. `saathi_logo.png`) to `app/src/main/res/drawable/`
2. In `LoginScreen.kt`, replace `SaathiMascot(...)` with:
   ```kotlin
   Image(
       painter = painterResource(id = R.drawable.saathi_logo),
       contentDescription = "Saathi mascot",
       modifier = Modifier.size(140.dp)
   )
   ```
3. Repeat in `SplashScreen.kt`
4. Add import: `import androidx.compose.ui.res.painterResource`

## Google Sign-In Button Icon

Drawn via `GoogleLogoIcon()` composable in `LoginScreen.kt` at line 315-368.

**To replace with a static image:**
1. Add a Google logo PNG/SVG to `app/src/main/res/drawable/` (e.g. `google_logo.png`)
2. Replace `GoogleLogoIcon()` call with:
   ```kotlin
   Image(
       painter = painterResource(id = R.drawable.google_logo),
       contentDescription = "Google",
       modifier = Modifier.size(20.dp)
   )
   ```

## Banner Image (project root)

File `banner.png` at project root. Used by GitHub social preview and documentation.

## Notification Icon

`app/src/main/res/drawable/ic_notification.xml` — small monochrome vector shown in Android notification bar. Use a white outline on transparent background.

## Adding New Images to Compose Screens

1. Place the image file in `app/src/main/res/drawable/`
2. Reference in Compose:
   ```kotlin
   import androidx.compose.ui.res.painterResource
   // ...
   Image(
       painter = painterResource(id = R.drawable.your_image_name),
       contentDescription = "description",
       modifier = Modifier.size(24.dp)
   )
   ```
3. Supported formats: PNG, JPEG, WebP, SVG (as XML vector)
4. Naming convention: lowercase with underscores (e.g. `ic_profile.png`, `bg_welcome.png`)
