package com.chama.groupmoneymanager.data.remote

import com.google.gson.annotations.SerializedName

// DTOs for Nest Management
data class CreateNestRequest(
    @SerializedName("name") val nestName: String,
    val contributionAmount: Double
)

data class JoinNestRequest(
    val inviteCode: String
)

data class NestResponse(
    val nestId: Long,
    val nestName: String,
    val inviteCode: String
)

// DTOs for Loans
data class LoanRequestRequest(
    val nestId: Long,
    val amount: Double,
    val termMonths: Int
)

data class LoanResponse(
    val loanId: Long,
    val nestId: Long,
    val userId: Long,
    val amount: Double,
    val status: String
)

data class LoanDetailsResponse(
    val loanId: Long,
    val userId: Long,
    val principalAmount: Double,
    val termMonths: Int,
    val status: String
)

data class RepayLoanRequest(
    val amount: Double
)

// DTO for Contributions
data class RecordContributionRequest(
    val nestId: Long,
    val userId: Long,
    val amount: Double
)