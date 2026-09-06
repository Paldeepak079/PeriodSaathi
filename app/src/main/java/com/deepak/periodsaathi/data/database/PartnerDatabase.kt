package com.deepak.periodsaathi.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.deepak.periodsaathi.data.dao.FriendDao
import com.deepak.periodsaathi.data.dao.PartnerDao
import com.deepak.periodsaathi.data.model.FriendEntity
import com.deepak.periodsaathi.data.model.PartnerConnectionEntity
import com.deepak.periodsaathi.data.model.QuizAnswerEntity

@Database(
    entities = [
        PartnerConnectionEntity::class,
        QuizAnswerEntity::class,
        FriendEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PartnerDatabase : RoomDatabase() {

    abstract fun partnerDao(): PartnerDao
    abstract fun friendDao(): FriendDao

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
                    .fallbackToDestructiveMigrationOnDowngrade(false)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
