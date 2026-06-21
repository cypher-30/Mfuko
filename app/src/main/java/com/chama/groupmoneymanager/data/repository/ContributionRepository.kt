package com.chama.groupmoneymanager.data.repository

import com.chama.groupmoneymanager.core.util.Resource

interface ContributionRepository {
    // ✅ ADD THIS FUNCTION
    suspend fun recordContribution(nestId: Long, userId: Long, amount: Double): Resource<Unit>
}