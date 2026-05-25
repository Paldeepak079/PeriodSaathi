package com.deepak.periodsaathi.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.deepak.periodsaathi.data.dao.AccessoryDao
import com.deepak.periodsaathi.data.dao.ChallengeDao
import com.deepak.periodsaathi.data.dao.CycleDao
import com.deepak.periodsaathi.data.dao.JournalDao
import com.deepak.periodsaathi.data.dao.PurchaseDao
import com.deepak.periodsaathi.data.dao.ReminderDao
import com.deepak.periodsaathi.data.dao.SettingsDao
import com.deepak.periodsaathi.data.dao.HabitDao
import com.deepak.periodsaathi.data.model.AccessoryEntity
import com.deepak.periodsaathi.data.model.ChallengeProgressEntity
import com.deepak.periodsaathi.data.model.CycleEntry
import com.deepak.periodsaathi.data.model.CycleSettings
import com.deepak.periodsaathi.data.model.JournalEntry
import com.deepak.periodsaathi.data.model.PurchaseRecord
import com.deepak.periodsaathi.data.model.Reminder
import com.deepak.periodsaathi.data.model.HabitCompletion

@Database(
    entities = [
        CycleEntry::class,
        CycleSettings::class,
        JournalEntry::class,
        Reminder::class,
        AccessoryEntity::class,
        ChallengeProgressEntity::class,
        PurchaseRecord::class,
        HabitCompletion::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PeriodSaathiDatabase : RoomDatabase() {

    abstract fun cycleDao(): CycleDao
    abstract fun settingsDao(): SettingsDao
    abstract fun journalDao(): JournalDao
    abstract fun reminderDao(): ReminderDao
    abstract fun accessoryDao(): AccessoryDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: PeriodSaathiDatabase? = null

        // Migration from version 3 to 4: add indices on date columns
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create index on CycleEntry table for date column if not exists
                database.execSQL("CREATE INDEX IF NOT EXISTS idx_cycle_entry_date ON cycle_entries (date)")
                // Create index on JournalEntry table for date column if not exists
                database.execSQL("CREATE INDEX IF NOT EXISTS idx_journal_entry_date ON journal_entries (date)")
            }
        }

        fun getInstance(context: android.content.Context): PeriodSaathiDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PeriodSaathiDatabase::class.java,
                    "period_saathi_db"
                )
                    .addMigrations(MIGRATION_3_4)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

