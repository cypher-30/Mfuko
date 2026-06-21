package com.chama.mfuko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chama.mfuko.data.local.entities.NestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNest(nest: NestEntity): Long

    @Update
    suspend fun updateNest(nest: NestEntity)

    @Query("SELECT * FROM nests WHERE id = :nestId LIMIT 1")
    fun getNestById(nestId: Long): Flow<NestEntity?>

    @Query("SELECT * FROM nests WHERE inviteCode = :code LIMIT 1")
    suspend fun getNestByInviteCode(code: String): NestEntity?

    @Query("SELECT COUNT(*) FROM nests")
    suspend fun getNestCount(): Int
}
