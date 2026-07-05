package com.deepak.periodsaathi.data.di

import android.content.Context
import com.deepak.periodsaathi.data.dao.AccessoryDao
import com.deepak.periodsaathi.data.dao.ChallengeDao
import com.deepak.periodsaathi.data.dao.CycleDao
import com.deepak.periodsaathi.data.dao.ForumDao
import com.deepak.periodsaathi.data.dao.HabitDao
import com.deepak.periodsaathi.data.dao.JournalDao
import com.deepak.periodsaathi.data.dao.PurchaseDao
import com.deepak.periodsaathi.data.dao.ReminderDao
import com.deepak.periodsaathi.data.dao.PartnerDao
import com.deepak.periodsaathi.data.dao.RemedyDao
import com.deepak.periodsaathi.data.dao.SettingsDao
import com.deepak.periodsaathi.data.database.PeriodSaathiDatabase
import com.deepak.periodsaathi.data.database.PartnerDatabase
import com.deepak.periodsaathi.data.datastore.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PeriodSaathiDatabase {
        return PeriodSaathiDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun providePartnerDatabase(@ApplicationContext context: Context): PartnerDatabase {
        return PartnerDatabase.getInstance(context)
    }

    @Provides
    fun providePartnerDao(database: PartnerDatabase): PartnerDao {
        return database.partnerDao()
    }

    @Provides
    fun provideCycleDao(database: PeriodSaathiDatabase): CycleDao {
        return database.cycleDao()
    }

    @Provides
    fun provideSettingsDao(database: PeriodSaathiDatabase): SettingsDao {
        return database.settingsDao()
    }

    @Provides
    fun provideJournalDao(database: PeriodSaathiDatabase): JournalDao {
        return database.journalDao()
    }

    @Provides
    fun provideReminderDao(database: PeriodSaathiDatabase): ReminderDao {
        return database.reminderDao()
    }

    @Provides
    fun provideAccessoryDao(database: PeriodSaathiDatabase): AccessoryDao {
        return database.accessoryDao()
    }

    @Provides
    fun provideChallengeDao(database: PeriodSaathiDatabase): ChallengeDao {
        return database.challengeDao()
    }

    @Provides
    fun providePurchaseDao(database: PeriodSaathiDatabase): PurchaseDao {
        return database.purchaseDao()
    }

    @Provides
    fun provideHabitDao(database: PeriodSaathiDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    fun provideForumDao(database: PeriodSaathiDatabase): ForumDao {
        return database.forumDao()
    }

    @Provides
    fun provideRemedyDao(database: PeriodSaathiDatabase): RemedyDao {
        return database.remedyDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}

