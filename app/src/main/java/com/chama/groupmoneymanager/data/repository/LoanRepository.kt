package com.chama.groupmoneymanager.data.repository

import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.remote.LoanDetailsResponse
import com.chama.groupmoneymanager.data.remote.LoanResponse

interface LoanRepository {
    // ✅ FIX: Corrected the function signature
    suspend fun requestLoan(nestId: Long, amount: Double, termMonths: Int): Resource<LoanResponse>

    suspend fun approveLoan(loanId: Long): Resource<Unit>
    suspend fun getNestLoans(nestId: Long): Resource<List<LoanDetailsResponse>>
    suspend fun repayLoan(loanId: Long, amount: Double): Resource<Unit>
    suspend fun rejectLoan(loanId: Long): Resource<Unit>
}