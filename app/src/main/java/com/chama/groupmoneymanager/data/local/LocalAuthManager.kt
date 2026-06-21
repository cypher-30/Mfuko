package com.chama.groupmoneymanager.data.local

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Local password hashing using SHA-256 + a random salt.
 *
 * Stored format: "SALT:HASH"
 * The special value "DEMO" bypasses verification for the demo account.
 */
object LocalAuthManager {

    /**
     * Hash a plain-text password, returning "salt:sha256hash".
     */
    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val hash = sha256("$salt:$password")
        return "$salt:$hash"
    }

    /**
     * Verify a plain-text password against a stored "salt:hash" string.
     * Returns true for the demo bypass value "DEMO" regardless of password.
     */
    fun verifyPassword(password: String, stored: String): Boolean {
        if (stored == "DEMO") return true          // demo account always passes
        val parts = stored.split(":")
        if (parts.size != 2) return false
        val (salt, hash) = parts
        return sha256("$salt:$password") == hash
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private fun generateSalt(): String {
        val bytes = ByteArray(12)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
