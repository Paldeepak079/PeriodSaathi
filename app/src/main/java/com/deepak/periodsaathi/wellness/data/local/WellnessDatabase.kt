package com.deepak.periodsaathi.wellness.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.deepak.periodsaathi.wellness.data.local.entities.CoinTransactionEntity
import com.deepak.periodsaathi.wellness.data.local.entities.SolutionEntity
import com.deepak.periodsaathi.wellness.data.local.entities.TipEntity
import com.deepak.periodsaathi.wellness.data.local.entities.UserWellnessStatsEntity
import com.deepak.periodsaathi.wellness.data.local.entities.WellnessLogEntity

@Database(
    entities = [
        WellnessLogEntity::class,
        TipEntity::class,
        CoinTransactionEntity::class,
        UserWellnessStatsEntity::class,
        SolutionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class WellnessDatabase : RoomDatabase() {
    abstract fun wellnessDao(): WellnessDao
}
