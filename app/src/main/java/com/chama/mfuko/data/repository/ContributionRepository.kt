package com.chama.mfuko.data.repository

import com.chama.mfuko.core.util.Resource

interface ContributionRepository {
    // ✅ ADD THIS FUNCTION
    suspend fun recordContribution(nestId: Long, userId: Long, amount: Double): Resource<Unit>
}