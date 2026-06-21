package com.chama.groupmoneymanager.data.repository

import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.remote.DashboardResponse
import com.chama.groupmoneymanager.data.remote.UserApiService
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService
) : UserRepository {
    override suspend fun getDashboard(): Resource<DashboardResponse> {
        return try {
            val response = apiService.getDashboard()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to load dashboard.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
}