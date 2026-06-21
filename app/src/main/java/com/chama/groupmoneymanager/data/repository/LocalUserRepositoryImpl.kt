package com.chama.groupmoneymanager.data.repository

import com.chama.groupmoneymanager.core.util.Resource
import com.chama.groupmoneymanager.data.local.CycleRoller
import com.chama.groupmoneymanager.data.local.TokenManager
import com.chama.groupmoneymanager.data.local.dao.ContributionDao
import com.chama.groupmoneymanager.data.local.dao.LoanDao
import com.chama.groupmoneymanager.data.local.dao.MembershipDao
import com.chama.groupmoneymanager.data.local.entities.LoanEntity
import com.chama.groupmoneymanager.data.remote.ContributionStatusDto
import com.chama.groupmoneymanager.data.remote.DashboardResponse
import com.chama.groupmoneymanager.data.remote.LoanStatusDto
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Offline-first [UserRepository] implementation backed by Room.
 * Assembles a [DashboardResponse] from local entities.
 *
 * Replaces [UserRepositoryImpl] (network) as the active binding in [di.AppModule].
 */
class LocalUserRepositoryImpl @Inject constructor(
    private val tokenManager: TokenManager,
    private val cycleRoller: CycleRoller,
    private val contributionDao: ContributionDao,
    private val loanDao: LoanDao,
    private val membershipDao: MembershipDao
) : UserRepository {

    override suspend fun getDashboard(): Resource<DashboardResponse> {
        return try {
            val userId = tokenManager.getUserId().first()
                ?: return Resource.Error("Not logged in.")
            val nestId = tokenManager.getCurrentNestId().first()

            if (nestId == 0L) {
                // User registered but hasn't joined or created a nest yet.
                return Resource.Success(DashboardResponse(null, null, null, null))
            }

            // ── Contribution status ───────────────────────────────────────────
            val openCycle = cycleRoller.ensureOpenCycle(nestId)
            val contributionStatus = if (openCycle != null) {
                val contribution = contributionDao.getContribution(openCycle.id, userId)
                ContributionStatusDto(
                    amountDue  = openCycle.amountDuePerMember,
                    amountPaid = contribution?.amountPaid ?: 0.0,
                    dueDate    = formatEpoch(openCycle.endDate)
                )
            } else null

            // ── Active loan status ────────────────────────────────────────────
            val activeLoan = loanDao.getActiveLoanForUser(userId, nestId)
            val loanStatus = activeLoan?.let {
                LoanStatusDto(
                    loanId           = it.id,
                    outstandingBalance = it.outstandingBalance,
                    nextDueDate      = null    // Phase 5: compute from term + approvalDate
                )
            }

            // ── User role ─────────────────────────────────────────────────────
            val role = membershipDao.getUserRole(userId, nestId)

            Resource.Success(
                DashboardResponse(
                    contributionStatus   = contributionStatus,
                    loanStatus           = loanStatus,
                    penaltyStatus        = null,    // Phase 5
                    userRole             = role,
                    financialHealthScore = computeHealthScore(contributionStatus, activeLoan)
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load dashboard.")
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun formatEpoch(epochMillis: Long): String =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(epochMillis))

    /**
     * 0–100 score averaging this cycle's contribution payment rate with the
     * active loan's repayment rate (full marks on either axis when there's
     * nothing due — no open cycle, or no active loan).
     */
    private fun computeHealthScore(contributionStatus: ContributionStatusDto?, activeLoan: LoanEntity?): Int {
        val contributionComponent = if (contributionStatus == null || contributionStatus.amountDue <= 0.0) {
            100.0
        } else {
            (contributionStatus.amountPaid / contributionStatus.amountDue).coerceIn(0.0, 1.0) * 100.0
        }

        val loanComponent = if (activeLoan == null) {
            100.0
        } else {
            val total = activeLoan.totalRepayableAmount ?: activeLoan.outstandingBalance
            if (total <= 0.0) 100.0
            else ((total - activeLoan.outstandingBalance) / total).coerceIn(0.0, 1.0) * 100.0
        }

        return ((contributionComponent + loanComponent) / 2.0).roundToInt().coerceIn(0, 100)
    }
}
