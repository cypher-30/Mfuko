package com.chama.mfuko.data.repository

import com.chama.mfuko.core.util.Resource
import com.chama.mfuko.data.remote.AuthRequest
import com.chama.mfuko.data.remote.AuthResponse
import com.chama.mfuko.data.remote.RegisterRequest
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun registerUser(request: RegisterRequest): Resource<AuthResponse>
    suspend fun loginUser(request: AuthRequest): Resource<AuthResponse>

    /**
     * Seeds demo data (if not already present) and logs in as the demo account.
     * The demo user is a manager of a pre-populated nest ("Mama Mboga Chama").
     * No password or registration required.
     */
    suspend fun loginDemo(): Resource<AuthResponse>

    suspend fun logout()

    fun getAuthToken(): Flow<String?>
}