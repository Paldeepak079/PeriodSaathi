package com.deepak.periodsaathi.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.deepak.periodsaathi.data.dao.AccessoryDao
import com.deepak.periodsaathi.data.dao.ChallengeDao
import com.deepak.periodsaathi.data.dao.CycleDao
import com.deepak.periodsaathi.data.dao.ForumDao
import com.deepak.periodsaathi.data.dao.HabitDao
import com.deepak.periodsaathi.data.dao.JournalDao
import com.deepak.periodsaathi.data.dao.PurchaseDao
import com.deepak.periodsaathi.data.dao.ReminderDao
import com.deepak.periodsaathi.data.dao.SettingsDao
import com.deepak.periodsaathi.data.model.AccessoryEntity
import com.deepak.periodsaathi.data.model.ChallengeProgressEntity
import com.deepak.periodsaathi.data.model.CycleEntry
import com.deepak.periodsaathi.data.model.CycleSettings
import com.deepak.periodsaathi.data.model.ForumComment
import com.deepak.periodsaathi.data.model.ForumPost
import com.deepak.periodsaathi.data.model.HabitCompletion
import com.deepak.periodsaathi.data.model.JournalEntry
import com.deepak.periodsaathi.data.model.PurchaseRecord
import com.deepak.periodsaathi.data.model.Reminder

@Database(
    entities = [
        CycleEntry::class,
        CycleSettings::class,
        JournalEntry::class,
        Reminder::class,
        AccessoryEntity::class,
        ChallengeProgressEntity::class,
        PurchaseRecord::class,
        HabitCompletion::class,
        ForumPost::class,
        ForumComment::class
    ],
    version = 5,
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
    abstract fun forumDao(): ForumDao

    companion object {
        @Volatile
        private var INSTANCE: PeriodSaathiDatabase? = null

        // v3 → v4: add indices on date columns
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE INDEX IF NOT EXISTS idx_cycle_entry_date ON cycle_entries (date)")
                database.execSQL("CREATE INDEX IF NOT EXISTS idx_journal_entry_date ON journal_entries (date)")
            }
        }

        // v4 → v5: add Secret Chats forum tables
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS `forum_posts` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `anonymousAlias` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `content` TEXT NOT NULL,
                        `imageUrl` TEXT,
                        `upvotes` INTEGER NOT NULL DEFAULT 0,
                        `commentCount` INTEGER NOT NULL DEFAULT 0,
                        `category` TEXT NOT NULL DEFAULT 'general',
                        `createdAt` INTEGER NOT NULL,
                        `isUpvotedByMe` INTEGER NOT NULL DEFAULT 0,
                        `synced` INTEGER NOT NULL DEFAULT 0
                    )"""
                )
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS `forum_comments` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `postId` TEXT NOT NULL,
                        `anonymousAlias` TEXT NOT NULL,
                        `content` TEXT NOT NULL,
                        `upvotes` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL,
                        `isUpvotedByMe` INTEGER NOT NULL DEFAULT 0,
                        `synced` INTEGER NOT NULL DEFAULT 0
                    )"""
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS idx_forum_comments_postId ON forum_comments (postId)")
            }
        }

        fun getInstance(context: android.content.Context): PeriodSaathiDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PeriodSaathiDatabase::class.java,
                    "period_saathi_db"
                )
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
