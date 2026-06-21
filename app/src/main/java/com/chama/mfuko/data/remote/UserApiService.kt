package com.chama.mfuko.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface UserApiService {
    @GET("api/me/dashboard")
    suspend fun getDashboard(): Response<DashboardResponse>
}