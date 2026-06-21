package com.chama.groupmoneymanager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chama.groupmoneymanager.data.local.dao.ContributionDao
import com.chama.groupmoneymanager.data.local.dao.CycleDao
import com.chama.groupmoneymanager.data.local.dao.LoanDao
import com.chama.groupmoneymanager.data.local.dao.MembershipDao
import com.chama.groupmoneymanager.data.local.dao.NestDao
import com.chama.groupmoneymanager.data.local.dao.NotificationDao
import com.chama.groupmoneymanager.data.local.dao.UserDao
import com.chama.groupmoneymanager.data.local.entities.ContributionEntity
import com.chama.groupmoneymanager.data.local.entities.CycleEntity
import com.chama.groupmoneymanager.data.local.entities.LoanEntity
import com.chama.groupmoneymanager.data.local.entities.MembershipEntity
import com.chama.groupmoneymanager.data.local.entities.NestEntity
import com.chama.groupmoneymanager.data.local.entities.NotificationEntity
import com.chama.groupmoneymanager.data.local.entities.UserEntity

/**
 * Mfuko's single Room database.
 *
 * Version history:
 *   1 — Initial schema (Phase 3): User, Nest, Membership, Cycle, Contribution, Loan.
 *   2 — Phase 5: added NestEntity.interestRate / interestType (default loan terms).
 *   3 — Phase 5: added NestEntity.cycleDurationDays (contribution schedule).
 *   4 — Phase 5: added NotificationEntity (in-app notifications).
 *
 * Demo data is NOT seeded here; [DemoSeeder] handles it on demand
 * when the user taps "Continue as demo" so the DB stays empty for real accounts.
 */
@Database(
    entities = [
        UserEntity::class,
        NestEntity::class,
        MembershipEntity::class,
        CycleEntity::class,
        ContributionEntity::class,
        LoanEntity::class,
        NotificationEntity::class,
    ],
    version = 4,
    exportSchema = false
)
abstract class MfukoDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun nestDao(): NestDao
    abstract fun membershipDao(): MembershipDao
    abstract fun cycleDao(): CycleDao
    abstract fun contributionDao(): ContributionDao
    abstract fun loanDao(): LoanDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        const val DB_NAME = "mfuko_database"

        fun build(context: Context): MfukoDatabase =
            Room.databaseBuilder(context, MfukoDatabase::class.java, DB_NAME)
                // Destructive migration is safe in Phase 3 (no live user data yet).
                // Replace with proper Migration objects before shipping to production.
                .fallbackToDestructiveMigration()
                .build()
    }
}
