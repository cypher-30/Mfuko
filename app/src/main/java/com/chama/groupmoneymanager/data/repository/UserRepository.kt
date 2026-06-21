package com.chama.groupmoneymanager.data.repository

import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.remote.DashboardResponse

interface UserRepository {
    suspend fun getDashboard(): Resource<DashboardResponse>
}