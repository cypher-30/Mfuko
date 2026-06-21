package com.chama.mfuko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chama.mfuko.data.local.entities.ContributionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContributionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContribution(contribution: ContributionEntity): Long

    @Query("SELECT * FROM contributions WHERE cycleId = :cycleId AND userId = :userId LIMIT 1")
    suspend fun getContribution(cycleId: Long, userId: Long): ContributionEntity?

    @Query("SELECT * FROM contributions WHERE cycleId = :cycleId")
    fun getContributionsForCycle(cycleId: Long): Flow<List<ContributionEntity>>

    @Query("SELECT COALESCE(SUM(amountPaid), 0.0) FROM contributions WHERE cycleId = :cycleId")
    fun getTotalPaidForCycle(cycleId: Long): Flow<Double>
}
