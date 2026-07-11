package com.chama.mfuko.data.repository

import com.chama.mfuko.core.util.Resource
import com.chama.mfuko.data.local.TokenManager
import com.chama.mfuko.data.remote.AuthApiService
import com.chama.mfuko.data.remote.AuthRequest
import com.chama.mfuko.data.remote.AuthResponse
import com.chama.mfuko.data.remote.RegisterRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun registerUser(request: RegisterRequest): Resource<AuthResponse> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                tokenManager.saveUserSession(body.userId, body.name, body.token)
                Resource.Success(body)
            } else {
                Resource.Error(response.message() ?: "Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }

    override suspend fun loginUser(request: AuthRequest): Resource<AuthResponse> {
        return try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                tokenManager.saveUserSession(body.userId, body.name, body.token)
                Resource.Success(body)
            } else {
                Resource.Error("Invalid phone number or password")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }

    /** Demo login is not supported in remote mode — use LocalAuthRepositoryImpl instead. */
    override suspend fun loginDemo(): Resource<AuthResponse> =
        Resource.Error("Demo login is only available in offline mode.")

    override suspend fun logout() {
        tokenManager.clearAll()
    }

    override fun getAuthToken(): Flow<String?> {
        return tokenManager.getToken()
    }
}