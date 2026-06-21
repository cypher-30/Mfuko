package com.chama.mfuko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chama.mfuko.data.local.entities.CycleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycle(cycle: CycleEntity): Long

    @Update
    suspend fun updateCycle(cycle: CycleEntity)

    /** Returns the single open cycle for a nest (the current contribution period). */
    @Query("SELECT * FROM cycles WHERE nestId = :nestId AND status = 'open' ORDER BY startDate DESC LIMIT 1")
    suspend fun getOpenCycle(nestId: Long): CycleEntity?

    @Query("SELECT * FROM cycles WHERE nestId = :nestId ORDER BY startDate DESC")
    fun getCyclesForNest(nestId: Long): Flow<List<CycleEntity>>
}
