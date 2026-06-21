package com.chama.groupmoneymanager.data.repository

import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.remote.MemberStatusDto
import com.chama.groupmoneymanager.data.remote.NestResponse

interface NestRepository {
    suspend fun createNest(request: String, amount: Double): Resource<NestResponse>
    suspend fun joinNest(request: String): Resource<NestResponse>
    suspend fun getNestMembers(nestId: Long): Resource<List<MemberStatusDto>>
}