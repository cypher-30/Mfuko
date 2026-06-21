package com.chama.mfuko.data.repository

import com.chama.mfuko.core.util.Resource
import com.chama.mfuko.data.remote.MemberStatusDto
import com.chama.mfuko.data.remote.NestResponse

interface NestRepository {
    suspend fun createNest(request: String, amount: Double): Resource<NestResponse>
    suspend fun joinNest(request: String): Resource<NestResponse>
    suspend fun getNestMembers(nestId: Long): Resource<List<MemberStatusDto>>
}