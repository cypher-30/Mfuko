package com.chama.mfuko.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single owner of the "user_prefs" DataStore file.
 *
 * IMPORTANT: Only ONE class in the process should declare a
 * preferencesDataStore delegate for a given file name.
 * SessionManager must NOT declare its own — it reads through this class.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val AUTH_TOKEN_KEY      = stringPreferencesKey("auth_token")
        private val CURRENT_NEST_ID_KEY = longPreferencesKey("current_nest_id")
        private val USER_ID_KEY         = longPreferencesKey("user_id")
        private val USER_NAME_KEY       = stringPreferencesKey("user_name")
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
    }

    // ── Auth token ────────────────────────────────────────────────────────────

    fun getToken(): Flow<String?> =
        context.dataStore.data.map { it[AUTH_TOKEN_KEY] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[AUTH_TOKEN_KEY] = token }
    }

    suspend fun deleteToken() {
        context.dataStore.edit { it.remove(AUTH_TOKEN_KEY) }
    }

    // ── User identity (for local/offline auth) ────────────────────────────────

    /** Returns the currently logged-in user's Room id, or null if not logged in. */
    fun getUserId(): Flow<Long?> =
        context.dataStore.data.map { prefs -> prefs[USER_ID_KEY] }

    /** Returns the currently logged-in user's display name, or null. */
    fun getUserName(): Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[USER_NAME_KEY] }

    /** Persists user identity alongside the session token. Used by local auth. */
    suspend fun saveUserSession(userId: Long, name: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[AUTH_TOKEN_KEY] = token
            prefs[USER_ID_KEY]    = userId
            prefs[USER_NAME_KEY]  = name
        }
    }

    // ── Current nest ─────────────────────────────────────────────────────────

    /**
     * Returns the ID of the nest the user most recently joined or created.
     * Returns 0L when no nest has been selected yet.
     */
    fun getCurrentNestId(): Flow<Long> =
        context.dataStore.data.map { it[CURRENT_NEST_ID_KEY] ?: 0L }

    suspend fun saveCurrentNestId(nestId: Long) {
        context.dataStore.edit { it[CURRENT_NEST_ID_KEY] = nestId }
    }

    /** Called on logout to wipe all persisted state. */
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

    // ── Settings ──────────────────────────────────────────────────────────────

    fun getNotificationsEnabled(): Flow<Boolean> =
        context.dataStore.data.map { it[NOTIFICATIONS_ENABLED_KEY] ?: true }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS_ENABLED_KEY] = enabled }
    }
}
