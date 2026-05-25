package com.deepak.periodsaathi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deepak.periodsaathi.data.model.PurchaseRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {

    @Query("SELECT * FROM purchases WHERE isActive = 1")
    fun getActivePurchases(): Flow<List<PurchaseRecord>>

    @Query("SELECT * FROM purchases WHERE productId = :productId AND isActive = 1 LIMIT 1")
    suspend fun getActivePurchaseByProductId(productId: String): PurchaseRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseRecord): Long

    @Query("UPDATE purchases SET isActive = 0 WHERE productId = :productId")
    suspend fun deactivatePurchase(productId: String)

    @Query("DELETE FROM purchases")
    suspend fun deleteAll()
}
