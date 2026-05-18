package com.deepak.periodsaathi.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.deepak.periodsaathi.data.dao.CycleDao
import com.deepak.periodsaathi.data.dao.JournalDao
import com.deepak.periodsaathi.data.dao.ReminderDao
import com.deepak.periodsaathi.data.dao.SettingsDao
import com.deepak.periodsaathi.data.model.CycleEntry
import com.deepak.periodsaathi.data.model.CycleSettings
import com.deepak.periodsaathi.data.model.JournalEntry
import com.deepak.periodsaathi.data.model.Reminder

@Database(
    entities = [
        CycleEntry::class,
        CycleSettings::class,
        JournalEntry::class,
        Reminder::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PeriodSaathiDatabase : RoomDatabase() {

    abstract fun cycleDao(): CycleDao
    abstract fun settingsDao(): SettingsDao
    abstract fun journalDao(): JournalDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: PeriodSaathiDatabase? = null

        fun getInstance(context: android.content.Context): PeriodSaathiDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PeriodSaathiDatabase::class.java,
                    "period_saathi_db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

