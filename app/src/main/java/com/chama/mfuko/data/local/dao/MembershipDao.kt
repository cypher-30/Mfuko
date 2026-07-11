package com.chama.mfuko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chama.mfuko.data.local.entities.MembershipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MembershipDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMembership(membership: MembershipEntity)

    @Query("SELECT * FROM memberships WHERE userId = :userId AND nestId = :nestId LIMIT 1")
    suspend fun getMembership(userId: Long, nestId: Long): MembershipEntity?

    @Query("SELECT role FROM memberships WHERE userId = :userId AND nestId = :nestId LIMIT 1")
    suspend fun getUserRole(userId: Long, nestId: Long): String?

    @Query("SELECT * FROM memberships WHERE nestId = :nestId")
    fun getMembersOfNest(nestId: Long): Flow<List<MembershipEntity>>

    /** Returns a nest the user already belongs to, or null if they belong to none. */
    @Query("SELECT nestId FROM memberships WHERE userId = :userId LIMIT 1")
    suspend fun getFirstNestIdForUser(userId: Long): Long?

    @Query("DELETE FROM memberships WHERE userId = :userId AND nestId = :nestId")
    suspend fun deleteMembership(userId: Long, nestId: Long)
}
