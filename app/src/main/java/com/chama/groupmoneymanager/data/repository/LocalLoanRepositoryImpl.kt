package com.chama.groupmoneymanager.data.repository

import com.chama.groupmoneymanager.core.util.LoanInterestCalculator
import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.local.TokenManager
import com.chama.groupmoneymanager.data.local.dao.LoanDao
import com.chama.groupmoneymanager.data.local.dao.NestDao
import com.chama.groupmoneymanager.data.local.dao.NotificationDao
import com.chama.groupmoneymanager.data.local.entities.LoanEntity
import com.chama.groupmoneymanager.data.local.entities.NotificationEntity
import com.chama.groupmoneymanager.data.remote.LoanDetailsResponse
import com.chama.groupmoneymanager.data.remote.LoanResponse
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Offline-first [LoanRepository] implementation backed by Room.
 * Replaces [LoanRepositoryImpl] (network) as the active binding in [di.AppModule].
 *
 * Interest is calculated at approval time using the nest's default rate/type,
 * via [LoanInterestCalculator] (flat vs. reducing-balance).
 */
class LocalLoanRepositoryImpl @Inject constructor(
    private val tokenManager: TokenManager,
    private val nestDao: NestDao,
    private val loanDao: LoanDao,
    private val notificationDao: NotificationDao
) : LoanRepository {

    override suspend fun requestLoan(nestId: Long, amount: Double, termMonths: Int): Resource<LoanResponse> {
        return try {
            val userId = tokenManager.getUserId().first()
                ?: return Resource.Error("Not logged in.")
            val nest = nestDao.getNestById(nestId).first()
                ?: return Resource.Error("Nest not found.")

            val loanId = loanDao.insertLoan(
                LoanEntity(
                    nestId = nestId,
                    userId = userId,
                    principalAmount = amount,
                    interestRate = nest.interestRate,
                    interestType = nest.interestType,
                    termMonths = termMonths,
                    status = "pending",
                    outstandingBalance = amount
                )
            )

            Resource.Success(LoanResponse(loanId = loanId, nestId = nestId, userId = userId, amount = amount, status = "pending"))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to request loan.")
        }
    }

    override suspend fun approveLoan(loanId: Long): Resource<Unit> {
        return try {
            val loan = loanDao.getLoanById(loanId)
                ?: return Resource.Error("Loan not found.")

            val breakdown = LoanInterestCalculator.calculate(
                principal = loan.principalAmount,
                annualRatePercent = loan.interestRate,
                termMonths = loan.termMonths,
                interestType = loan.interestType
            )

            loanDao.updateLoan(
                loan.copy(
                    status = "active",
                    totalInterestAmount = breakdown.totalInterestAmount,
                    totalRepayableAmount = breakdown.totalRepayableAmount,
                    outstandingBalance = breakdown.totalRepayableAmount,
                    approvalDate = System.currentTimeMillis()
                )
            )

            if (tokenManager.getNotificationsEnabled().first()) {
                notificationDao.insertNotification(
                    NotificationEntity(
                        userId = loan.userId,
                        nestId = loan.nestId,
                        type = "loan_approved",
                        message = "Your loan request for KES %.2f was approved.".format(loan.principalAmount)
                    )
                )
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to approve loan.")
        }
    }

    override suspend fun rejectLoan(loanId: Long): Resource<Unit> {
        return try {
            val loan = loanDao.getLoanById(loanId)
                ?: return Resource.Error("Loan not found.")

            loanDao.updateLoan(loan.copy(status = "rejected"))

            if (tokenManager.getNotificationsEnabled().first()) {
                notificationDao.insertNotification(
                    NotificationEntity(
                        userId = loan.userId,
                        nestId = loan.nestId,
                        type = "loan_rejected",
                        message = "Your loan request for KES %.2f was rejected.".format(loan.principalAmount)
                    )
                )
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to reject loan.")
        }
    }

    override suspend fun repayLoan(loanId: Long, amount: Double): Resource<Unit> {
        return try {
            val loan = loanDao.getLoanById(loanId)
                ?: return Resource.Error("Loan not found.")

            val newOutstanding = (loan.outstandingBalance - amount).coerceAtLeast(0.0)
            loanDao.updateLoan(
                loan.copy(
                    outstandingBalance = newOutstanding,
                    status = if (newOutstanding <= 0.0) "paid" else "active"
                )
            )

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to repay loan.")
        }
    }

    override suspend fun getNestLoans(nestId: Long): Resource<List<LoanDetailsResponse>> {
        return try {
            val loans = loanDao.getLoansForNest(nestId).first().map {
                LoanDetailsResponse(
                    loanId = it.id,
                    userId = it.userId,
                    principalAmount = it.principalAmount,
                    termMonths = it.termMonths,
                    status = it.status
                )
            }
            Resource.Success(loans)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch loans.")
        }
    }
}
