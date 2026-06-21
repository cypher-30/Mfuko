package com.chama.groupmoneymanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A contribution period / cycle. e.g. "June 2026".
 * startDate / endDate are stored as epoch milliseconds.
 * status: "open" (accepting contributions) or "closed".
 */
@Entity(tableName = "cycles")
data class CycleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nestId: Long,
    /** Human-readable label: "June 2026", "Q2 2026", etc. */
    val name: String,
    val startDate: Long,
    val endDate: Long,
    val amountDuePerMember: Double,
    /** "open" or "closed" */
    val status: String
)
