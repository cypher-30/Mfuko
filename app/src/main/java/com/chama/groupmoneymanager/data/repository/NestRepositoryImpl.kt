package com.chama.groupmoneymanager.data.repository

import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.local.TokenManager
import com.chama.groupmoneymanager.data.remote.CreateNestRequest
import com.chama.groupmoneymanager.data.remote.JoinNestRequest
import com.chama.groupmoneymanager.data.remote.MemberStatusDto
import com.chama.groupmoneymanager.data.remote.NestApiService
import com.chama.groupmoneymanager.data.remote.NestResponse
import javax.inject.Inject

class NestRepositoryImpl @Inject constructor(
    private val api: NestApiService,
    private val tokenManager: TokenManager
) : NestRepository {

    override suspend fun getNestMembers(nestId: Long): Resource<List<MemberStatusDto>> {
        return try {
            val response = api.getNestMembers(nestId)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to fetch members")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown network error occurred")
        }
    }

    override suspend fun createNest(name: String, amount: Double): Resource<NestResponse> {
        return try {
            // Note: the server expects field "name" (not "nestName") in the request body.
            // CreateNestRequest uses @SerializedName("name") — see AppDtos.kt.
            val request = CreateNestRequest(nestName = name, contributionAmount = amount)
            val response = api.createNest(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                // Persist the newly created nest ID so all screens know the current nest.
                tokenManager.saveCurrentNestId(body.nestId)
                Resource.Success(body)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to create nest")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun joinNest(code: String): Resource<NestResponse> {
        return try {
            val request = JoinNestRequest(inviteCode = code)
            val response = api.joinNest(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                tokenManager.saveCurrentNestId(body.nestId)
                Resource.Success(body)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to join nest")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}
