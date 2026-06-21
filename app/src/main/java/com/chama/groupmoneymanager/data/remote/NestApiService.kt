package com.chama.groupmoneymanager.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NestApiService {
    // ✅ ADD THESE TWO FUNCTIONS
    @POST("api/nests/create")
    suspend fun createNest(@Body request: CreateNestRequest): Response<NestResponse>

    @POST("api/nests/join")
    suspend fun joinNest(@Body request: JoinNestRequest): Response<NestResponse>

    @GET("api/nests/{nestId}/members")
    suspend fun getNestMembers(@Path("nestId") nestId: Long): Response<List<MemberStatusDto>>
}