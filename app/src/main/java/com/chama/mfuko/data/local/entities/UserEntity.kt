package com.chama.mfuko.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Local user record. passwordHash stores "salt:sha256hash" or "DEMO" for the demo user.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["phone"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    /** Format: "salt:sha256hash", or the literal "DEMO" for the demo bypass account. */
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis()
)
