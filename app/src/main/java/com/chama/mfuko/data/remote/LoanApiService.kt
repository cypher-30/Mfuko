package com.chama.mfuko.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LoanApiService {
    @POST("api/loans/request")
    suspend fun requestLoan(@Body request: LoanRequestRequest): Response<LoanResponse>

    @POST("api/loans/{loanId}/approve")
    suspend fun approveLoan(@Path("loanId") loanId: Long): Response<Unit>

    @GET("api/loans/nest/{nestId}")
    suspend fun getNestLoans(@Path("nestId") nestId: Long): Response<List<LoanDetailsResponse>>

    @POST("api/loans/{loanId}/repay")
    suspend fun repayLoan(
        @Path("loanId") loanId: Long,
        @Body request: RepayLoanRequest
    ): Response<Unit>

    @POST("api/loans/{loanId}/reject")
    suspend fun rejectLoan(@Path("loanId") loanId: Long): Response<Unit>
}
