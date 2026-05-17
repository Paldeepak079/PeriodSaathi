package com.example.periodsaathi.data.di

import android.content.Context
import com.example.periodsaathi.data.dao.CycleDao
import com.example.periodsaathi.data.dao.JournalDao
import com.example.periodsaathi.data.dao.ReminderDao
import com.example.periodsaathi.data.dao.SettingsDao
import com.example.periodsaathi.data.database.PeriodSaathiDatabase
import com.example.periodsaathi.data.datastore.UserPreferences
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
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}
