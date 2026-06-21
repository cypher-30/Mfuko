package com.chama.mfuko.data.repository

import com.chama.mfuko.core.util.Resource
import com.chama.mfuko.data.local.CycleRoller
import com.chama.mfuko.data.local.TokenManager
import com.chama.mfuko.data.local.dao.ContributionDao
import com.chama.mfuko.data.local.dao.NotificationDao
import com.chama.mfuko.data.local.entities.ContributionEntity
import com.chama.mfuko.data.local.entities.NotificationEntity
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Offline-first [ContributionRepository] implementation backed by Room.
 * Replaces [ContributionRepositoryImpl] (network) as the active binding in [di.AppModule].
 */
class LocalContributionRepositoryImpl @Inject constructor(
    private val cycleRoller: CycleRoller,
    private val contributionDao: ContributionDao,
    private val notificationDao: NotificationDao,
    private val tokenManager: TokenManager
) : ContributionRepository {

    override suspend fun recordContribution(nestId: Long, userId: Long, amount: Double): Resource<Unit> {
        return try {
            val cycle = cycleRoller.ensureOpenCycle(nestId)
                ?: return Resource.Error("Nest not found.")

            val existing = contributionDao.getContribution(cycle.id, userId)
            val newAmountPaid = (existing?.amountPaid ?: 0.0) + amount
            val status = when {
                newAmountPaid >= cycle.amountDuePerMember -> "paid"
                newAmountPaid > 0.0 -> "partial"
                else -> "unpaid"
            }

            contributionDao.insertContribution(
                ContributionEntity(
                    id = existing?.id ?: 0,
                    cycleId = cycle.id,
                    userId = userId,
                    amountPaid = newAmountPaid,
                    datePaid = System.currentTimeMillis(),
                    status = status
                )
            )

            if (tokenManager.getNotificationsEnabled().first()) {
                notificationDao.insertNotification(
                    NotificationEntity(
                        userId = userId,
                        nestId = nestId,
                        type = "contribution_confirmed",
                        message = "You've successfully contributed KES %.2f.".format(amount)
                    )
                )
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Could not record contribution.")
        }
    }
}
