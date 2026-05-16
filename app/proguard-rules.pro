-verbose

# --- Android Standard ---
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# --- Jetpack Compose ---
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keepclassmembers class * {
    @androidx.compose.runtime.Immutable <fields>;
}
-keepclassmembers class * {
    @androidx.compose.runtime.Stable <fields>;
}

# Keep all Composable functions
-keepclassmembers class ** {
    @androidx.compose.runtime.Composable public *;
}

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Entity class ** { *; }
-keepclassmembers @androidx.room.Entity class ** { *; }
-keepclassmembers class ** {
    @androidx.room.ColumnInfo <fields>;
    @androidx.room.PrimaryKey <fields>;
    @androidx.room.ForeignKey <fields>;
    @androidx.room.Ignore <fields>;
    @androidx.room.Embedded <fields>;
    @androidx.room.Relation <fields>;
}
-dontwarn androidx.room.paging.**

# --- Room DAOs ---
-keep @androidx.room.Dao class *
-keepclassmembers @androidx.room.Dao class ** { *; }
-keepclassmembers class ** {
    @androidx.room.Query <methods>;
    @androidx.room.Insert <methods>;
    @androidx.room.Update <methods>;
    @androidx.room.Delete <methods>;
    @androidx.room.Transaction <methods>;
}

# --- Room TypeConverters ---
-keep @androidx.room.TypeConverter class *
-keepclassmembers @androidx.room.TypeConverter class ** { *; }

# --- Hilt ---
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep class dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }
-keepclassmembers class * {
    @dagger.hilt.android.qualifiers.ApplicationContext <fields>;
    @dagger.hilt.android.qualifiers.ActivityContext <fields>;
}
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponentManager { *; }

-keepclassmembers class ** {
    @dagger.hilt.android.EarlyEntryPoint <methods>;
    @dagger.hilt.EntryPoint <methods>;
    @dagger.Provides <methods>;
    @dagger.Binds <methods>;
    @dagger.Module <fields>;
    @javax.inject.Inject <init>(...);
    @javax.inject.Inject <fields>;
    @javax.inject.Singleton <fields>;
}

# Keep Hilt generated classes
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponentManager { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }
-keep class **Hilt_Components { *; }
-keep class **Hilt_* { *; }
-keep class **Dagger* { *; }
-keep class * extends dagger.hilt.internal.definecomponent.DefineComponentClasses { *; }

# --- @Serializable (Kotlinx Serialization) ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class **$$serializer { *; }
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable <fields>;
    @kotlinx.serialization.Serializable <methods>;
}
-keepclassmembers @kotlinx.serialization.Serializable class * { *; }
-keep class * implements kotlinx.serialization.KSerializer { *; }

# --- WorkManager ---
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-keepclassmembers class * extends androidx.work.Worker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keepclassmembers class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-dontwarn androidx.work.impl.background.gcm.**

# --- BroadcastReceiver ---
-keep public class * extends android.content.BroadcastReceiver { *; }
-keepclassmembers class * extends android.content.BroadcastReceiver {
    public void onReceive(android.content.Context, android.content.Intent);
}

# --- Gson ---
-keepattributes Signature
-keepattributes *Annotation*, Signature, InnerClasses
-dontwarn sun.misc.**

-keep class com.example.periodsaathi.data.model.** { *; }
-keepclassmembers class com.example.periodsaathi.data.model.** { *; }

-keep class com.google.gson.** { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep Gson TypeToken
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken { *; }

# --- Kotlin Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# --- DataStore ---
-dontwarn androidx.datastore.**
-keep class androidx.datastore.** { *; }

# --- Material3 ---
-keep class androidx.compose.material3.** { *; }
-dontwarn androidx.compose.material3.**

# --- Navigation ---
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# --- Lifecycle ---
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# --- Keep Serializable classes ---
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# --- Keep ViewModel classes ---
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# --- Coil ---
-dontwarn coil.**
-keep class coil.** { *; }

# --- Lottie ---
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }

# --- Keep application class ---
-keep class com.example.periodsaathi.PeriodSaathiApplication { *; }
-keep class com.example.periodsaathi.PeriodSaathiApplication_HiltComponents { *; }
-keep class * extends com.example.periodsaathi.PeriodSaathiApplication { *; }