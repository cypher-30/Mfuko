package com.chama.groupmoneymanager.data.local

import com.chama.groupmoneymanager.data.local.dao.ContributionDao
import com.chama.groupmoneymanager.data.local.dao.CycleDao
import com.chama.groupmoneymanager.data.local.dao.LoanDao
import com.chama.groupmoneymanager.data.local.dao.MembershipDao
import com.chama.groupmoneymanager.data.local.dao.NestDao
import com.chama.groupmoneymanager.data.local.dao.UserDao
import com.chama.groupmoneymanager.data.local.entities.ContributionEntity
import com.chama.groupmoneymanager.data.local.entities.CycleEntity
import com.chama.groupmoneymanager.data.local.entities.LoanEntity
import com.chama.groupmoneymanager.data.local.entities.MembershipEntity
import com.chama.groupmoneymanager.data.local.entities.NestEntity
import com.chama.groupmoneymanager.data.local.entities.UserEntity
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds the local Room database with realistic demo data.
 * Call [seedIfEmpty] once; it's a no-op when data already exists.
 *
 * Demo nest structure:
 *   • Manager : Demo User      (phone = "demo",        bypass hash)
 *   • Member  : Amina Wanjiku  (phone = "0712345678",  password = "password123")
 *   • Member  : Brian Otieno   (phone = "0723456789",  password = "password123")
 *
 *   Cycle   : June 2026  — KES 1 500 due per member
 *   Status  : Amina = paid, Brian = partial (KES 750), Demo User = unpaid
 *   Loans   : Demo User has a pending loan (KES 5 000)
 *              Amina has an active loan (KES 10 000, 10 months remaining)
 */
@Singleton
class DemoSeeder @Inject constructor(
    private val userDao: UserDao,
    private val nestDao: NestDao,
    private val membershipDao: MembershipDao,
    private val cycleDao: CycleDao,
    private val contributionDao: ContributionDao,
    private val loanDao: LoanDao
) {
    /**
     * Seeds demo data if the demo account doesn't already exist.
     * Returns the demo user's database id.
     *
     * Checks specifically for the demo phone ("demo") rather than using getUserCount(),
     * so real registered accounts don't block demo seeding.
     */
    suspend fun seedIfEmpty(): Long {
        userDao.getUserByPhone(DEMO_PHONE)?.let { existingDemo ->
            // Demo already seeded — return existing demo user id.
            return existingDemo.id
        }

        // ── Users ────────────────────────────────────────────────────────────
        val demoUserId = userDao.insertUser(
            UserEntity(name = "Demo User", phone = DEMO_PHONE, passwordHash = "DEMO")
        )
        val aminaId = userDao.insertUser(
            UserEntity(
                name = "Amina Wanjiku",
                phone = "0712345678",
                passwordHash = LocalAuthManager.hashPassword("password123")
            )
        )
        val brianId = userDao.insertUser(
            UserEntity(
                name = "Brian Otieno",
                phone = "0723456789",
                passwordHash = LocalAuthManager.hashPassword("password123")
            )
        )

        // ── Nest ─────────────────────────────────────────────────────────────
        val nestId = nestDao.insertNest(
            NestEntity(
                name = "Mama Mboga Chama",
                inviteCode = DEMO_INVITE,
                managerId = demoUserId,
                contributionAmount = CONTRIBUTION_AMOUNT
            )
        )

        // ── Memberships ───────────────────────────────────────────────────────
        membershipDao.insertMembership(MembershipEntity(demoUserId, nestId, "manager"))
        membershipDao.insertMembership(MembershipEntity(aminaId, nestId, "member"))
        membershipDao.insertMembership(MembershipEntity(brianId, nestId, "member"))

        // ── June 2026 cycle ───────────────────────────────────────────────────
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.JUNE, 1, 0, 0, 0); cal.set(Calendar.MILLISECOND, 0)
        val startDate = cal.timeInMillis
        cal.set(2026, Calendar.JUNE, 30, 23, 59, 59)
        val endDate = cal.timeInMillis

        val cycleId = cycleDao.insertCycle(
            CycleEntity(
                nestId = nestId,
                name = "June 2026",
                startDate = startDate,
                endDate = endDate,
                amountDuePerMember = CONTRIBUTION_AMOUNT,
                status = "open"
            )
        )

        // ── Contributions ─────────────────────────────────────────────────────
        // Amina — fully paid
        contributionDao.insertContribution(
            ContributionEntity(
                cycleId = cycleId,
                userId = aminaId,
                amountPaid = CONTRIBUTION_AMOUNT,
                datePaid = System.currentTimeMillis(),
                status = "paid"
            )
        )
        // Brian — partial
        contributionDao.insertContribution(
            ContributionEntity(
                cycleId = cycleId,
                userId = brianId,
                amountPaid = 750.0,
                datePaid = System.currentTimeMillis(),
                status = "partial"
            )
        )
        // Demo User — unpaid (no row needed; absence = unpaid)

        // ── Loans ─────────────────────────────────────────────────────────────
        // Demo User — pending loan (KES 5 000, flat 10%, 6 months)
        loanDao.insertLoan(
            LoanEntity(
                nestId = nestId,
                userId = demoUserId,
                principalAmount = 5_000.0,
                interestRate = 10.0,
                interestType = "flat",
                termMonths = 6,
                status = "pending",
                outstandingBalance = 5_000.0
            )
        )
        // Amina — active loan (KES 10 000, reducing 12%, 12 months)
        val totalInterest = 10_000.0 * 0.12  // simplified flat for seed
        loanDao.insertLoan(
            LoanEntity(
                nestId = nestId,
                userId = aminaId,
                principalAmount = 10_000.0,
                interestRate = 12.0,
                interestType = "reducing",
                termMonths = 12,
                status = "active",
                totalInterestAmount = totalInterest,
                totalRepayableAmount = 10_000.0 + totalInterest,
                outstandingBalance = 8_500.0,  // 2 months repaid
                approvalDate = System.currentTimeMillis()
            )
        )

        return demoUserId
    }

    /** Returns the database id of the demo nest (keyed by its stable invite code). */
    suspend fun getDemoNestId(): Long? =
        nestDao.getNestByInviteCode(DEMO_INVITE)?.id

    companion object {
        const val DEMO_PHONE = "demo"
        const val DEMO_INVITE = "DEMO01"
        private const val CONTRIBUTION_AMOUNT = 1_500.0
    }
}
