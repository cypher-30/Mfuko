package com.chama.groupmoneymanager.data.repository

import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.local.DemoSeeder
import com.chama.groupmoneymanager.data.local.LocalAuthManager
import com.chama.groupmoneymanager.data.local.TokenManager
import com.chama.groupmoneymanager.data.local.dao.UserDao
import com.chama.groupmoneymanager.data.local.entities.UserEntity
import com.chama.groupmoneymanager.data.remote.AuthRequest
import com.chama.groupmoneymanager.data.remote.AuthResponse
import com.chama.groupmoneymanager.data.remote.RegisterRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Offline-first implementation of [AuthRepository].
 * All operations read/write the local Room database.
 * No network calls are made.
 *
 * This replaces [AuthRepositoryImpl] as the active binding in [di.AppModule].
 * The network implementation is kept for Phase 7 (remote sync).
 */
class LocalAuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val tokenManager: TokenManager,
    private val demoSeeder: DemoSeeder
) : AuthRepository {

    // ── Register ──────────────────────────────────────────────────────────────

    override suspend fun registerUser(request: RegisterRequest): Resource<AuthResponse> {
        return try {
            if (request.name.isBlank()) return Resource.Error("Name is required.")
            if (request.phone.isBlank()) return Resource.Error("Phone number is required.")
            if (request.password.length < 6) return Resource.Error("Password must be at least 6 characters.")

            if (userDao.getUserByPhone(request.phone) != null) {
                return Resource.Error("An account with this phone number already exists.")
            }

            val entity = UserEntity(
                name         = request.name.trim(),
                phone        = request.phone.trim(),
                passwordHash = LocalAuthManager.hashPassword(request.password)
            )
            val userId = userDao.insertUser(entity)
            if (userId == -1L) return Resource.Error("Registration failed. Please try again.")

            val token = buildLocalToken(userId)
            tokenManager.saveUserSession(userId, entity.name, token)

            Resource.Success(
                AuthResponse(
                    userId = userId,
                    name   = entity.name,
                    phone  = entity.phone,
                    token  = token
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred during registration.")
        }
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    override suspend fun loginUser(request: AuthRequest): Resource<AuthResponse> {
        return try {
            val user = userDao.getUserByPhone(request.phone.trim())
                ?: return Resource.Error("No account found with this phone number.")

            if (!LocalAuthManager.verifyPassword(request.password, user.passwordHash)) {
                return Resource.Error("Incorrect password.")
            }

            val token = buildLocalToken(user.id)
            tokenManager.saveUserSession(user.id, user.name, token)

            Resource.Success(
                AuthResponse(
                    userId = user.id,
                    name   = user.name,
                    phone  = user.phone,
                    token  = token
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred during login.")
        }
    }

    // ── Demo login ────────────────────────────────────────────────────────────

    override suspend fun loginDemo(): Resource<AuthResponse> {
        return try {
            // Seeds demo data (if not already present); returns demo userId.
            val demoUserId = demoSeeder.seedIfEmpty()
            val demoUser   = userDao.getUserById(demoUserId)
                ?: return Resource.Error("Failed to load demo account.")

            val token = buildLocalToken(demoUser.id)
            tokenManager.saveUserSession(demoUser.id, demoUser.name, token)
            // Look up the actual demo nest id (invite code is stable: "DEMO01").
            val demoNestId = demoSeeder.getDemoNestId() ?: 1L
            tokenManager.saveCurrentNestId(demoNestId)

            Resource.Success(
                AuthResponse(
                    userId = demoUser.id,
                    name   = demoUser.name,
                    phone  = demoUser.phone,
                    token  = token
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to start demo session.")
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    override suspend fun logout() {
        tokenManager.clearAll()
    }

    override fun getAuthToken(): Flow<String?> = tokenManager.getToken()

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * A simple local session token — not a real JWT.
     * Format: "local_<userId>_<timestamp>"
     * Used only to gate navigation (non-null = logged in).
     */
    private fun buildLocalToken(userId: Long): String =
        "local_${userId}_${System.currentTimeMillis()}"
}
