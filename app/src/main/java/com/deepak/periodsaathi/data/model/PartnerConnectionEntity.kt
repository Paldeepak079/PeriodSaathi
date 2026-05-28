package com.deepak.periodsaathi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partner_connection")
data class PartnerConnectionEntity(
    @PrimaryKey
    val id: String = "active_connection",
    val inviteCode: String,
    val partnerUserId: String,
    val partnerName: String,
    val status: String, // PENDING, CONNECTED, REVOKED
    val connectedAt: Long,
    val isPrimary: Boolean // true = primary tracker sharing access; false = partner viewing read-only cycle details
)
