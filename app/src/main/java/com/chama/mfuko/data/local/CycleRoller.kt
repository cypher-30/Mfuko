package com.chama.mfuko.data.local

import com.chama.mfuko.data.local.dao.CycleDao
import com.chama.mfuko.data.local.dao.NestDao
import com.chama.mfuko.data.local.entities.CycleEntity
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ensures every nest always has a current, non-expired open [CycleEntity] to record
 * contributions against — creating the first cycle for a brand-new nest, and rolling
 * an expired cycle into a new one based on the nest's [com.chama.mfuko.data.local.entities.NestEntity.cycleDurationDays].
 */
@Singleton
class CycleRoller @Inject constructor(
    private val nestDao: NestDao,
    private val cycleDao: CycleDao
) {
    suspend fun ensureOpenCycle(nestId: Long): CycleEntity? {
        val nest = nestDao.getNestById(nestId).first() ?: return null
        val openCycle = cycleDao.getOpenCycle(nestId)

        val now = System.currentTimeMillis()
        if (openCycle != null && openCycle.endDate >= now) return openCycle

        if (openCycle != null) {
            cycleDao.updateCycle(openCycle.copy(status = "closed"))
        }

        val newCycle = CycleEntity(
            nestId = nestId,
            name = cycleName(now),
            startDate = now,
            endDate = now + nest.cycleDurationDays * MILLIS_PER_DAY,
            amountDuePerMember = nest.contributionAmount,
            status = "open"
        )
        val newCycleId = cycleDao.insertCycle(newCycle)
        return newCycle.copy(id = newCycleId)
    }

    private fun cycleName(epochMillis: Long): String =
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(epochMillis))

    companion object {
        private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000
    }
}
