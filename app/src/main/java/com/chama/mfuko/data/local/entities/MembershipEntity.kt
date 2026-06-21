package com.chama.mfuko.data.local.entities

import androidx.room.Entity

/**
 * Many-to-many between User and Nest. A user can be in multiple nests.
 * role: "manager" or "member".
 */
@Entity(
    tableName = "memberships",
    primaryKeys = ["userId", "nestId"]
)
data class MembershipEntity(
    val userId: Long,
    val nestId: Long,
    /** "manager" or "member" */
    val role: String,
    val createdAt: Long = System.currentTimeMillis()
)
