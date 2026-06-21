package com.chama.mfuko.data.repository

import com.chama.mfuko.core.util.Resource
import com.chama.mfuko.data.remote.DashboardResponse

interface UserRepository {
    suspend fun getDashboard(): Resource<DashboardResponse>
}