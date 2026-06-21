package com.chama.groupmoneymanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A member's contribution record for one cycle.
 * amountPaid is cumulative (updated on each partial payment — not a new row per payment).
 * status: "paid" | "partial" | "unpaid"
 */
@Entity(tableName = "contributions")
data class ContributionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cycleId: Long,
    val userId: Long,
    /** Total amount paid so far this cycle. */
    val amountPaid: Double,
    /** Epoch millis of last payment. */
    val datePaid: Long,
    /** "paid" | "partial" | "unpaid" */
    val status: String
)
