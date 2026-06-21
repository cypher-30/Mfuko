package com.chama.mfuko.data.repository

import com.chama.mfuko.core.util.Resource
import com.chama.mfuko.data.remote.ContributionApiService
import com.chama.mfuko.data.remote.RecordContributionRequest
import javax.inject.Inject

class ContributionRepositoryImpl @Inject constructor(
    private val api: ContributionApiService
) : ContributionRepository {

    // ✅ ADD THIS FUNCTION
    override suspend fun recordContribution(nestId: Long, userId: Long, amount: Double): Resource<Unit> {
        return try {
            val request = RecordContributionRequest(nestId = nestId, userId = userId, amount = amount)
            val response = api.recordContribution(request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Could not record contribution")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}