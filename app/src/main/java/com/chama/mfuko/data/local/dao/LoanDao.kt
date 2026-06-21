package com.chama.mfuko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chama.mfuko.data.local.entities.LoanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity): Long

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Query("SELECT * FROM loans WHERE nestId = :nestId ORDER BY requestDate DESC")
    fun getLoansForNest(nestId: Long): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE id = :loanId LIMIT 1")
    suspend fun getLoanById(loanId: Long): LoanEntity?

    @Query("SELECT * FROM loans WHERE userId = :userId AND nestId = :nestId ORDER BY requestDate DESC")
    fun getLoansForUser(userId: Long, nestId: Long): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE userId = :userId AND nestId = :nestId AND status = 'active' ORDER BY requestDate DESC LIMIT 1")
    suspend fun getActiveLoanForUser(userId: Long, nestId: Long): LoanEntity?
}
