package com.example.periodsaathi.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.periodsaathi.data.dao.CycleDao
import com.example.periodsaathi.data.dao.JournalDao
import com.example.periodsaathi.data.dao.ReminderDao
import com.example.periodsaathi.data.dao.SettingsDao
import com.example.periodsaathi.data.model.CycleEntry
import com.example.periodsaathi.data.model.CycleSettings
import com.example.periodsaathi.data.model.JournalEntry
import com.example.periodsaathi.data.model.Reminder

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
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
