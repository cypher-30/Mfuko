package com.chama.groupmoneymanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A Nest (group / chama). Created locally on "Create Nest" or seeded via demo.
 */
@Entity(tableName = "nests")
data class NestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    /** 6-character code used to join; e.g. "DEMO01". */
    val inviteCode: String,
    val managerId: Long,
    /** Default contribution amount per cycle, in KES. */
    val contributionAmount: Double,
    /** Default annual interest rate (percent) applied when a manager approves a loan. */
    val interestRate: Double = 10.0,
    /** "flat" or "reducing" */
    val interestType: String = "flat",
    /** Length of one contribution cycle, in days. 30 ≈ monthly. */
    val cycleDurationDays: Int = 30,
    val createdAt: Long = System.currentTimeMillis()
)
