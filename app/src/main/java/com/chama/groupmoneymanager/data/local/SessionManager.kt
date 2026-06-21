package com.chama.groupmoneymanager.data.local

import com.auth0.android.jwt.JWT
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads the current user's identity from the stored JWT.
 *
 * Deliberately does NOT declare its own DataStore — it delegates to [TokenManager],
 * which is the single owner of the "user_prefs" DataStore file.
 * Having two preferencesDataStore delegates with the same name causes a crash:
 * "There are multiple DataStores active for the same file."
 */
@Singleton
class SessionManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    /**
     * Returns the userId embedded in the stored JWT, or null if there is no
     * valid token (user is logged out, token is malformed/expired).
     */
    suspend fun getUserId(): Long? {
        val token = tokenManager.getToken().firstOrNull() ?: return null
        return try {
            JWT(token).getClaim("userId").asLong()
        } catch (e: Exception) {
            null
        }
    }
}
