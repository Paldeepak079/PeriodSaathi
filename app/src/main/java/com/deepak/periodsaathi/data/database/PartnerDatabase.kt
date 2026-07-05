package com.deepak.periodsaathi.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.deepak.periodsaathi.data.dao.PartnerDao
import com.deepak.periodsaathi.data.model.PartnerConnectionEntity
import com.deepak.periodsaathi.data.model.QuizAnswerEntity

@Database(
    entities = [
        PartnerConnectionEntity::class,
        QuizAnswerEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PartnerDatabase : RoomDatabase() {

    abstract fun partnerDao(): PartnerDao

    companion object {
        @Volatile
        private var INSTANCE: PartnerDatabase? = null

        fun getInstance(context: Context): PartnerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PartnerDatabase::class.java,
                    "partner_saathi_db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
