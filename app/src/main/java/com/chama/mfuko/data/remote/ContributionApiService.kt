package com.chama.mfuko.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ContributionApiService {
    // ✅ ADD THIS FUNCTION
    @POST("api/contributions/record")
    suspend fun recordContribution(@Body request: RecordContributionRequest): Response<Unit>
}