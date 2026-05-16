# S14 — CI/CD + CODE QUALITY

> **Prerequisites:** S00 active · S13 complete · all tests passing
> **Output:** 5 files — GitHub Actions CI, Detekt config, Fastfile, backup rules, data extraction rules
> **Build check:** Push to GitHub → CI must turn green.

---

## REPORT INSTRUCTION
After completing this section AND setting up CI, create:
`masterprompts/reports/S14_report.md`

Include:
- 📄 All 5 files created (checklist)
- ✅ GitHub Actions CI: green/yellow/red on first push
- 🔍 Detekt: violations found (list), violations fixed
- ⚡ Total CI time (lint + tests + build)
- 📦 Debug APK artifact uploaded (yes/no)
- 🔒 Secrets configured in GitHub (list names — not values)
- ❌ Any CI failures and root cause

---

## PROMPT

Create complete CI/CD pipeline and code quality configuration.
Write all files completely.

---

### FILE 1: `.github/workflows/android_ci.yml`

```yaml
name: Period Saathi CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  lint_and_detekt:
    name: 🔍 Lint & Detekt
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - uses: gradle/actions/setup-gradle@v3
        with:
          gradle-home-cache-cleanup: true

      - name: Create local.properties
        run: |
          echo "sdk.dir=$ANDROID_HOME" >> local.properties
          echo "supabase.url=${{ secrets.SUPABASE_URL }}" >> local.properties
          echo "supabase.anon.key=${{ secrets.SUPABASE_ANON_KEY }}" >> local.properties
          echo "razorpay.key.id=${{ secrets.RAZORPAY_KEY_ID }}" >> local.properties
          echo "admob.app.id=${{ secrets.ADMOB_APP_ID }}" >> local.properties

      - name: Run Detekt
        run: ./gradlew detekt

      - name: Run Android Lint
        run: ./gradlew lint

      - name: Upload lint results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: lint-results
          path: app/build/reports/lint-results-debug.html

  unit_tests:
    name: 🧪 Unit Tests
    runs-on: ubuntu-latest
    needs: lint_and_detekt
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - uses: gradle/actions/setup-gradle@v3

      - name: Create local.properties
        run: |
          echo "sdk.dir=$ANDROID_HOME" >> local.properties
          echo "supabase.url=${{ secrets.SUPABASE_URL }}" >> local.properties
          echo "supabase.anon.key=${{ secrets.SUPABASE_ANON_KEY }}" >> local.properties
          echo "razorpay.key.id=${{ secrets.RAZORPAY_KEY_ID }}" >> local.properties
          echo "admob.app.id=${{ secrets.ADMOB_APP_ID }}" >> local.properties

      - name: Run unit tests
        run: ./gradlew testDebugUnitTest

      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: app/build/reports/tests/testDebugUnitTest/

  build_debug:
    name: 🔨 Build Debug APK
    runs-on: ubuntu-latest
    needs: unit_tests
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - uses: gradle/actions/setup-gradle@v3

      - name: Create local.properties
        run: |
          echo "sdk.dir=$ANDROID_HOME" >> local.properties
          echo "supabase.url=${{ secrets.SUPABASE_URL }}" >> local.properties
          echo "supabase.anon.key=${{ secrets.SUPABASE_ANON_KEY }}" >> local.properties
          echo "razorpay.key.id=${{ secrets.RAZORPAY_KEY_ID }}" >> local.properties
          echo "admob.app.id=${{ secrets.ADMOB_APP_ID }}" >> local.properties

      - name: Build debug APK
        run: ./gradlew assembleDebug

      - name: Upload debug APK
        uses: actions/upload-artifact@v4
        with:
          name: debug-apk
          path: app/build/outputs/apk/debug/
          retention-days: 7

  build_release:
    name: 🚀 Build Release AAB
    runs-on: ubuntu-latest
    needs: unit_tests
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - uses: gradle/actions/setup-gradle@v3

      - name: Setup signing + secrets
        run: |
          echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > keystore.jks
          echo "sdk.dir=$ANDROID_HOME" >> local.properties
          echo "supabase.url=${{ secrets.SUPABASE_URL }}" >> local.properties
          echo "supabase.anon.key=${{ secrets.SUPABASE_ANON_KEY }}" >> local.properties
          echo "razorpay.key.id=${{ secrets.RAZORPAY_KEY_ID }}" >> local.properties
          echo "admob.app.id=${{ secrets.ADMOB_APP_ID }}" >> local.properties
          echo "keystore.path=../keystore.jks" >> local.properties
          echo "keystore.password=${{ secrets.KEYSTORE_PASSWORD }}" >> local.properties
          echo "key.alias=${{ secrets.KEY_ALIAS }}" >> local.properties
          echo "key.password=${{ secrets.KEY_PASSWORD }}" >> local.properties

      - name: Build release bundle (AAB)
        run: ./gradlew bundleRelease

      - name: Upload AAB
        uses: actions/upload-artifact@v4
        with:
          name: release-aab-${{ github.run_number }}
          path: app/build/outputs/bundle/release/
          retention-days: 30

      - name: Clean up keystore
        if: always()
        run: rm -f keystore.jks
```

---

### FILE 2: `detekt.yml`

```yaml
build:
  maxIssues: 0
  excludeCorrectable: false
  weights:
    complexity: 2
    coroutines: 2
    empty-blocks: 1
    exceptions: 1
    naming: 1
    performance: 2
    potential-bugs: 2
    style: 1

config:
  validation: true

complexity:
  LongMethod:
    threshold: 60
    ignoreAnnotated: ['Composable', 'Preview']
  LargeClass:
    threshold: 600
  CyclomaticComplexity:
    threshold: 15
  LongParameterList:
    threshold: 8
    ignoreAnnotated: ['Composable']
  TooManyFunctions:
    thresholdInClasses: 15
    thresholdInObjects: 10
    thresholdInEnums: 5

style:
  MaxLineLength:
    maxLineLength: 120
    excludeImportStatements: true
    excludeCommentStatements: true
  MagicNumber:
    active: false  # Too strict for Compose pixel values
  WildcardImport:
    active: true
    excludeImports: ['java.util.*', 'kotlinx.coroutines.*']
  UnusedImports:
    active: true
  ForbiddenComment:
    active: true
    comments: ['FIXME:', 'STOPSHIP:']
    allowedPatterns: ['TODO:']

naming:
  FunctionNaming:
    active: true
    functionPattern: '[a-z][a-zA-Z0-9]*'
    excludes: ['**/*Test*', '**/*Spec*']
  ClassNaming:
    active: true
  MatchingDeclarationName:
    active: true

performance:
  SpreadOperator:
    active: true
  ForEachOnRange:
    active: true

potential-bugs:
  LateinitUsage:
    active: true
    excludeAnnotatedProperties: ['Inject', 'MockK', 'Mock']
  UnnecessarySafeCall:
    active: true

coroutines:
  SuspendFunWithFlowReturnType:
    active: true
  GlobalCoroutineUsage:
    active: true
  RedundantSuspendModifier:
    active: true
  InjectDispatcher:
    active: true

exceptions:
  SwallowedException:
    active: true
  TooGenericExceptionCaught:
    active: true
    exceptionNames: ['Exception', 'Throwable']
    allowedExceptionNameRegex: '_|(ignore|expected).*'
```

---

### FILE 3: `fastlane/Fastfile`

```ruby
default_platform(:android)

platform :android do
  
  before_all do
    # Ensure local.properties is set (on CI, set via environment)
  end

  desc "Run tests only"
  lane :test do
    gradle(
      task: "test",
      build_type: "Debug",
      print_command: true
    )
  end

  desc "Build debug APK"
  lane :debug do
    gradle(
      task: "assemble",
      build_type: "Debug",
      print_command: false
    )
  end

  desc "Build and upload to Play Store internal track"
  lane :beta do
    gradle(
      task: "bundle",
      build_type: "Release",
      print_command: false,
      properties: {
        "android.injected.signing.store.file" => ENV["KEYSTORE_PATH"],
        "android.injected.signing.store.password" => ENV["KEYSTORE_PASSWORD"],
        "android.injected.signing.key.alias" => ENV["KEY_ALIAS"],
        "android.injected.signing.key.password" => ENV["KEY_PASSWORD"],
      }
    )
    
    upload_to_play_store(
      track: "internal",
      aab: "app/build/outputs/bundle/release/app-release.aab",
      release_status: "draft",
      skip_upload_screenshots: true,
      skip_upload_images: true
    )
  end

  desc "Promote internal build to production with staged rollout"
  lane :production do
    upload_to_play_store(
      track: "internal",
      track_promote_to: "production",
      rollout: "0.1",  # Start with 10% rollout
      release_status: "inProgress"
    )
  end

  desc "Increment versionCode"
  lane :bump_version do
    android_set_version_code(
      gradle_file: "app/build.gradle.kts"
    )
  end

  error do |lane, exception|
    # Notification on failure (can integrate with Slack)
    puts "Lane #{lane} failed with: #{exception.message}"
  end
end
```

---

### FILE 4: `res/xml/backup_rules.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<full-backup-content>
  <!-- Exclude encrypted database (managed separately) -->
  <exclude domain="database" path="period_saathi.db"/>
  <exclude domain="database" path="period_saathi.db-shm"/>
  <exclude domain="database" path="period_saathi.db-wal"/>
  <!-- Include encrypted preferences -->
  <include domain="sharedpref" path="period_saathi_secure_prefs.xml"/>
  <!-- Exclude any temp files -->
  <exclude domain="cache" path="."/>
  <exclude domain="external-cache" path="."/>
</full-backup-content>
```

---

### FILE 5: `res/xml/data_extraction_rules.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<data-extraction-rules>
  <cloud-backup>
    <exclude domain="database" path="period_saathi.db"/>
    <exclude domain="database" path="period_saathi.db-shm"/>
    <exclude domain="database" path="period_saathi.db-wal"/>
    <!-- Allow encrypted preferences to backup -->
    <include domain="sharedpref" path="period_saathi_secure_prefs.xml"/>
  </cloud-backup>
  <device-transfer>
    <!-- Don't transfer health DB to new device via local transfer -->
    <exclude domain="database" path="period_saathi.db"/>
  </device-transfer>
</data-extraction-rules>
```

---

## GITHUB SECRETS TO CONFIGURE

Add these secrets in GitHub → Settings → Secrets → Actions:

| Secret Name | Description |
|-------------|-------------|
| `SUPABASE_URL` | Your Supabase project URL |
| `SUPABASE_ANON_KEY` | Supabase anon/public key |
| `RAZORPAY_KEY_ID` | Razorpay Test Key ID (use production for main branch) |
| `ADMOB_APP_ID` | AdMob App ID from Google AdMob console |
| `KEYSTORE_BASE64` | Base64 encoded keystore: `base64 -w 0 keystore.jks` |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias (e.g. "period-saathi") |
| `KEY_PASSWORD` | Key password |

---

## AFTER COMPLETION
Commit and push to `develop` branch.
Verify GitHub Actions starts and all 3 jobs (lint, test, build_debug) pass.
Write the report.
