package com.deepak.periodsaathi.wellness.di

import android.content.Context
import androidx.room.Room
import com.deepak.periodsaathi.wellness.data.local.SolutionRepository
import com.deepak.periodsaathi.wellness.data.local.WellnessDao
import com.deepak.periodsaathi.wellness.data.local.WellnessDatabase
import com.deepak.periodsaathi.wellness.data.remote.WellnessFirestoreService
import com.deepak.periodsaathi.wellness.ui.wellness.utils.WellnessSoundManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WellnessModule {

    @Provides
    @Singleton
    fun provideWellnessDatabase(
        @ApplicationContext context: Context
    ): WellnessDatabase {
        return Room.databaseBuilder(
            context,
            WellnessDatabase::class.java,
            "period_saathi_wellness_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideWellnessDao(
        database: WellnessDatabase
    ): WellnessDao {
        return database.wellnessDao()
    }

    @Provides
    @Singleton
    fun provideSolutionRepository(
        dao: WellnessDao
    ): SolutionRepository {
        return SolutionRepository(dao)
    }

    @Provides
    @Singleton
    fun provideWellnessSoundManager(
        @ApplicationContext context: Context
    ): WellnessSoundManager {
        return WellnessSoundManager(context)
    }

    @Provides
    @Singleton
    fun provideWellnessFirestoreService(
        @ApplicationContext context: Context
    ): WellnessFirestoreService {
        return WellnessFirestoreService(context)
    }
}
