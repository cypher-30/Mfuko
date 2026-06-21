package com.chama.groupmoneymanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A loan from the nest's collective pool.
 * interestType: "flat" or "reducing"
 * status: "pending" | "active" | "paid" | "rejected"
 * totalInterestAmount / totalRepayableAmount are computed on approval.
 * All dates are epoch milliseconds (null if not set yet).
 */
@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nestId: Long,
    val userId: Long,
    val principalAmount: Double,
    val interestRate: Double,
    /** "flat" or "reducing" */
    val interestType: String,
    val termMonths: Int,
    /** "pending" | "active" | "paid" | "rejected" */
    val status: String,
    val totalInterestAmount: Double? = null,
    val totalRepayableAmount: Double? = null,
    val outstandingBalance: Double,
    val requestDate: Long = System.currentTimeMillis(),
    val approvalDate: Long? = null,
    val disbursementDate: Long? = null
)
