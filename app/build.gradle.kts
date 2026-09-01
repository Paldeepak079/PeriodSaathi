import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kover)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

fun getLocalProperty(key: String): String {
    return localProperties.getProperty(key) ?: project.findProperty(key)?.toString() ?: ""
}

android {
    namespace = "com.deepak.periodsaathi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.deepak.periodsaathi"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SUPABASE_URL", "\"${getLocalProperty("supabase.url")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${getLocalProperty("supabase.anon.key")}\"")
        buildConfigField("String", "STRIPE_PUBLISHABLE_KEY", "\"${getLocalProperty("stripe.publishable.key")}\"")
        buildConfigField("String", "ADMOB_APP_ID", "\"${getLocalProperty("admob.app.id")}\"")
        buildConfigField("String", "ADMOB_BANNER_ID", "\"${getLocalProperty("admob.banner.id")}\"")
        buildConfigField("String", "ADMOB_INTERSTITIAL_ID", "\"${getLocalProperty("admob.interstitial.id")}\"")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"${getLocalProperty("google.web.client.id")}\"")
        buildConfigField("String", "GOOGLE_ANDROID_CLIENT_ID", "\"${getLocalProperty("google.android.client.id")}\"")
        buildConfigField("String", "SENTRY_DSN", "\"${getLocalProperty("sentry.dsn")}\"")

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }

        manifestPlaceholders["admobAppId"] = getLocalProperty("admob.app.id").ifBlank { "ca-app-pub-3940256099942544~3347511713" }
    }

    signingConfigs {
        create("release") {
            val path = getLocalProperty("keystore.path")
            if (path.isNotBlank()) {
                val ksFile = file(path)
                if (ksFile.exists()) {
                    storeFile = ksFile
                    storePassword = getLocalProperty("keystore.password")
                    keyAlias = getLocalProperty("key.alias")
                    keyPassword = getLocalProperty("key.password")
                } else {
                    val rootKsFile = rootProject.file(path)
                    if (rootKsFile.exists()) {
                        storeFile = rootKsFile
                        storePassword = getLocalProperty("keystore.password")
                        keyAlias = getLocalProperty("key.alias")
                        keyPassword = getLocalProperty("key.password")
                    }
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val path = getLocalProperty("keystore.path")
            val ksFile = if (path.isNotBlank()) file(path) else null
            if (ksFile?.exists() == true || (path.isNotBlank() && rootProject.file(path).exists())) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        debug {
            isMinifyEnabled = false
            versionNameSuffix = "-debug"
            val path = getLocalProperty("keystore.path")
            val ksFile = if (path.isNotBlank()) file(path) else null
            if (ksFile?.exists() == true || (path.isNotBlank() && rootProject.file(path).exists())) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = signingConfigs.getByName("debug")
            }
        }
    }

    bundle {
        language {
            enableSplit = true
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    sourceSets {
        getByName("main") {
            java.setSrcDirs(listOf("src/main/java"))
        }
    }

    firebaseCrashlytics {
        mappingFileUploadEnabled = false
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.ui.text.google.fonts)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Activity + Core
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    // Room + SQLCipher
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.sqlcipher)
    implementation(libs.sqlite.ktx)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Supabase
    implementation(libs.supabase.gotrue.kt)
    implementation(libs.supabase.postgrest.kt)
    implementation(libs.supabase.realtime.kt)
    implementation(libs.ktor.client.android)

    // Google Sign-In
    implementation(libs.play.services.auth)
    // Credential Manager (modern Google Sign-In — no main thread freeze)
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Stripe SDK
    implementation(libs.stripe.android)

    // AdMob
    implementation(libs.play.services.ads)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)

    // Sentry
    implementation(libs.sentry.android)

    // Biometrics
    implementation(libs.androidx.biometric)

    // Security
    implementation(libs.security.crypto)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Gson
    implementation(libs.gson)

    // Accompanist
    implementation(libs.accompainist.systemuicontroller)
    implementation(libs.accompainist.permissions)

    // Glance (Widgets)
    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)

    // PDF generation
    implementation(libs.itext7.core)

    // Lottie animations (bio-visualization engine)
    implementation(libs.lottie.compose)

    // Media3 ExoPlayer for yoga video sessions
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
