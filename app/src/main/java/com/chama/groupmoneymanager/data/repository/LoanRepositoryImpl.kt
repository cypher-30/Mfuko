package com.chama.groupmoneymanager.data.repository

import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.remote.LoanApiService
import com.chama.groupmoneymanager.data.remote.LoanDetailsResponse
import com.chama.groupmoneymanager.data.remote.LoanRequestRequest
import com.chama.groupmoneymanager.data.remote.LoanResponse
import com.chama.groupmoneymanager.data.remote.RepayLoanRequest
import javax.inject.Inject

class LoanRepositoryImpl @Inject constructor(
    private val apiService: LoanApiService
) : LoanRepository {

    // ✅ FIX: Correctly implemented the new function signature
    override suspend fun requestLoan(nestId: Long, amount: Double, termMonths: Int): Resource<LoanResponse> {
        return try {
            val request = LoanRequestRequest(nestId, amount, termMonths)
            val response = apiService.requestLoan(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to request loan")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun approveLoan(loanId: Long): Resource<Unit> {
        return try {
            val response = apiService.approveLoan(loanId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to approve loan")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun getNestLoans(nestId: Long): Resource<List<LoanDetailsResponse>> {
        return try {
            val response = apiService.getNestLoans(nestId)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to fetch loans")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun repayLoan(loanId: Long, amount: Double): Resource<Unit> {
        return try {
            val response = apiService.repayLoan(loanId, RepayLoanRequest(amount))
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to repay loan: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun rejectLoan(loanId: Long): Resource<Unit> {
        return try {
            val response = apiService.rejectLoan(loanId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to reject loan")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

}