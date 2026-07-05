package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class PurchaseRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: String,
    val purchaseDate: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val stripePaymentIntentId: String? = null
)
