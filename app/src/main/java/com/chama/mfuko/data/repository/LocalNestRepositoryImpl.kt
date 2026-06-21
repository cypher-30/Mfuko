package com.chama.mfuko.data.repository

import com.chama.mfuko.core.util.Resource
import com.chama.mfuko.data.local.CycleRoller
import com.chama.mfuko.data.local.TokenManager
import com.chama.mfuko.data.local.dao.ContributionDao
import com.chama.mfuko.data.local.dao.MembershipDao
import com.chama.mfuko.data.local.dao.NestDao
import com.chama.mfuko.data.local.dao.UserDao
import com.chama.mfuko.data.local.entities.MembershipEntity
import com.chama.mfuko.data.local.entities.NestEntity
import com.chama.mfuko.data.remote.MemberStatusDto
import com.chama.mfuko.data.remote.NestResponse
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Offline-first [NestRepository] implementation backed by Room.
 * Replaces [NestRepositoryImpl] (network) as the active binding in [di.AppModule].
 */
class LocalNestRepositoryImpl @Inject constructor(
    private val tokenManager: TokenManager,
    private val userDao: UserDao,
    private val nestDao: NestDao,
    private val membershipDao: MembershipDao,
    private val cycleRoller: CycleRoller,
    private val contributionDao: ContributionDao
) : NestRepository {

    override suspend fun createNest(request: String, amount: Double): Resource<NestResponse> {
        return try {
            val userId = tokenManager.getUserId().first()
                ?: return Resource.Error("Not logged in.")

            val inviteCode = generateUniqueInviteCode()
            val nestId = nestDao.insertNest(
                NestEntity(
                    name = request,
                    inviteCode = inviteCode,
                    managerId = userId,
                    contributionAmount = amount
                )
            )
            membershipDao.insertMembership(MembershipEntity(userId, nestId, "manager"))
            tokenManager.saveCurrentNestId(nestId)

            Resource.Success(NestResponse(nestId = nestId, nestName = request, inviteCode = inviteCode))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create nest.")
        }
    }

    override suspend fun joinNest(request: String): Resource<NestResponse> {
        return try {
            val userId = tokenManager.getUserId().first()
                ?: return Resource.Error("Not logged in.")

            val nest = nestDao.getNestByInviteCode(request)
                ?: return Resource.Error("Invalid invite code.")

            membershipDao.insertMembership(MembershipEntity(userId, nest.id, "member"))
            tokenManager.saveCurrentNestId(nest.id)

            Resource.Success(NestResponse(nestId = nest.id, nestName = nest.name, inviteCode = nest.inviteCode))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to join nest.")
        }
    }

    override suspend fun getNestMembers(nestId: Long): Resource<List<MemberStatusDto>> {
        return try {
            if (nestId == 0L) return Resource.Success(emptyList())

            val memberships = membershipDao.getMembersOfNest(nestId).first()
            val openCycle = cycleRoller.ensureOpenCycle(nestId)

            val members = memberships.mapNotNull { membership ->
                val user = userDao.getUserById(membership.userId) ?: return@mapNotNull null
                val contribution = openCycle?.let { contributionDao.getContribution(it.id, membership.userId) }
                MemberStatusDto(
                    userId = membership.userId,
                    name = user.name,
                    role = membership.role,
                    amountPaid = contribution?.amountPaid ?: 0.0,
                    totalDue = openCycle?.amountDuePerMember ?: 0.0,
                    status = contribution?.status ?: "unpaid"
                )
            }

            Resource.Success(members)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch members.")
        }
    }

    private suspend fun generateUniqueInviteCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        while (true) {
            val code = (1..6).map { chars.random() }.joinToString("")
            if (nestDao.getNestByInviteCode(code) == null) return code
        }
    }
}
