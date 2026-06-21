package com.chama.mfuko.data.remote

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("contributionStatus")
    val contributionStatus: ContributionStatusDto?,
    @SerializedName("loanStatus")
    val loanStatus: LoanStatusDto?,
    @SerializedName("penaltyStatus")
    val penaltyStatus: PenaltyStatusDto?,
    @SerializedName("userRole")
    val userRole: String?,
    /** 0–100. Null when the user hasn't joined a nest yet. */
    @SerializedName("financialHealthScore")
    val financialHealthScore: Int? = null
)

data class ContributionStatusDto(
    @SerializedName("amountDue")
    val amountDue: Double,
    @SerializedName("amountPaid")
    val amountPaid: Double,
    @SerializedName("dueDate")
    val dueDate: String
)

data class LoanStatusDto(
    @SerializedName("loanId")
    val loanId: Long,
    @SerializedName("outstandingBalance")
    val outstandingBalance: Double,
    @SerializedName("nextDueDate")
    val nextDueDate: String?
)

data class PenaltyStatusDto(
    @SerializedName("totalUnpaid")
    val totalUnpaid: Double
)