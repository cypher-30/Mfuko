package com.chama.groupmoneymanager.di

import android.content.Context
import com.chama.groupmoneymanager.BuildConfig
import com.chama.groupmoneymanager.data.local.CycleRoller
import com.chama.groupmoneymanager.data.local.DemoSeeder
import com.chama.groupmoneymanager.data.local.MfukoDatabase
import com.chama.groupmoneymanager.data.local.TokenManager
import com.chama.groupmoneymanager.data.local.dao.ContributionDao
import com.chama.groupmoneymanager.data.local.dao.CycleDao
import com.chama.groupmoneymanager.data.local.dao.LoanDao
import com.chama.groupmoneymanager.data.local.dao.MembershipDao
import com.chama.groupmoneymanager.data.local.dao.NestDao
import com.chama.groupmoneymanager.data.local.dao.NotificationDao
import com.chama.groupmoneymanager.data.local.dao.UserDao
import com.chama.groupmoneymanager.data.remote.*
import com.chama.groupmoneymanager.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ── DataStore / Session ───────────────────────────────────────────────────

    @Provides @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager =
        TokenManager(context)

    // ── Room Database ─────────────────────────────────────────────────────────

    @Provides @Singleton
    fun provideMfukoDatabase(@ApplicationContext context: Context): MfukoDatabase =
        MfukoDatabase.build(context)

    @Provides @Singleton
    fun provideUserDao(db: MfukoDatabase): UserDao = db.userDao()

    @Provides @Singleton
    fun provideNestDao(db: MfukoDatabase): NestDao = db.nestDao()

    @Provides @Singleton
    fun provideMembershipDao(db: MfukoDatabase): MembershipDao = db.membershipDao()

    @Provides @Singleton
    fun provideCycleDao(db: MfukoDatabase): CycleDao = db.cycleDao()

    @Provides @Singleton
    fun provideContributionDao(db: MfukoDatabase): ContributionDao = db.contributionDao()

    @Provides @Singleton
    fun provideLoanDao(db: MfukoDatabase): LoanDao = db.loanDao()

    @Provides @Singleton
    fun provideNotificationDao(db: MfukoDatabase): NotificationDao = db.notificationDao()

    // ── Networking (kept for optional Phase 7 remote sync) ────────────────────

    @Provides @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        }

    @Provides @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor =
        AuthInterceptor(tokenManager)

    @Provides @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    @Provides @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides @Singleton
    fun provideNestApiService(retrofit: Retrofit): NestApiService =
        retrofit.create(NestApiService::class.java)

    @Provides @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides @Singleton
    fun provideContributionApiService(retrofit: Retrofit): ContributionApiService =
        retrofit.create(ContributionApiService::class.java)

    @Provides @Singleton
    fun provideLoanApiService(retrofit: Retrofit): LoanApiService =
        retrofit.create(LoanApiService::class.java)

    // ── Repositories — offline-first (Phase 3) ─────────────────────────────

    /**
     * [AuthRepository] is now backed by Room (local auth + demo seed).
     * The network [AuthRepositoryImpl] is kept at [data/repository/AuthRepositoryImpl.kt]
     * for Phase 7 (remote sync).  Swap the binding below to re-enable remote auth.
     */
    @Provides @Singleton
    fun provideAuthRepository(
        userDao: UserDao,
        tokenManager: TokenManager,
        demoSeeder: DemoSeeder
    ): AuthRepository = LocalAuthRepositoryImpl(userDao, tokenManager, demoSeeder)

    /**
     * [UserRepository] (dashboard) is now backed by Room.
     * The network [UserRepositoryImpl] is kept for Phase 7.
     */
    @Provides @Singleton
    fun provideUserRepository(
        tokenManager: TokenManager,
        cycleRoller: CycleRoller,
        contributionDao: ContributionDao,
        loanDao: LoanDao,
        membershipDao: MembershipDao
    ): UserRepository = LocalUserRepositoryImpl(
        tokenManager, cycleRoller, contributionDao, loanDao, membershipDao
    )

    /**
     * [NestRepository] is now backed by Room (Phase 5).
     * The network [NestRepositoryImpl] is kept for Phase 7 (remote sync).
     */
    @Provides @Singleton
    fun provideNestRepository(
        tokenManager: TokenManager,
        userDao: UserDao,
        nestDao: NestDao,
        membershipDao: MembershipDao,
        cycleRoller: CycleRoller,
        contributionDao: ContributionDao
    ): NestRepository = LocalNestRepositoryImpl(
        tokenManager, userDao, nestDao, membershipDao, cycleRoller, contributionDao
    )

    /**
     * [ContributionRepository] is now backed by Room (Phase 5).
     * The network [ContributionRepositoryImpl] is kept for Phase 7 (remote sync).
     */
    @Provides @Singleton
    fun provideContributionRepository(
        cycleRoller: CycleRoller,
        contributionDao: ContributionDao,
        notificationDao: NotificationDao,
        tokenManager: TokenManager
    ): ContributionRepository = LocalContributionRepositoryImpl(cycleRoller, contributionDao, notificationDao, tokenManager)

    /**
     * [LoanRepository] is now backed by Room (Phase 5).
     * The network [LoanRepositoryImpl] is kept for Phase 7 (remote sync).
     */
    @Provides @Singleton
    fun provideLoanRepository(
        tokenManager: TokenManager,
        nestDao: NestDao,
        loanDao: LoanDao,
        notificationDao: NotificationDao
    ): LoanRepository = LocalLoanRepositoryImpl(tokenManager, nestDao, loanDao, notificationDao)
}
