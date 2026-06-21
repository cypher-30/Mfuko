package com.chama.groupmoneymanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * An in-app notification for one user.
 * type: "contribution_confirmed" | "loan_approved" | "loan_rejected" | "reminder" | "announcement"
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val nestId: Long,
    val type: String,
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
