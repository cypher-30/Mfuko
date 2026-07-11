package com.chama.mfuko.di

import android.content.Context
import com.chama.mfuko.BuildConfig
import com.chama.mfuko.data.local.MfukoDatabase
import com.chama.mfuko.data.local.TokenManager
import com.chama.mfuko.data.local.dao.ContributionDao
import com.chama.mfuko.data.local.dao.CycleDao
import com.chama.mfuko.data.local.dao.LoanDao
import com.chama.mfuko.data.local.dao.MembershipDao
import com.chama.mfuko.data.local.dao.NestDao
import com.chama.mfuko.data.local.dao.NotificationDao
import com.chama.mfuko.data.local.dao.UserDao
import com.chama.mfuko.data.remote.*
import com.chama.mfuko.data.repository.*
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

    // ── Repositories — local (offline) or network (Phase 7 remote sync),
    // ── selected by BuildConfig.USE_REMOTE ──────────────────────────────────

    @Provides @Singleton
    fun provideAuthRepository(
        localImpl: LocalAuthRepositoryImpl,
        networkImpl: AuthRepositoryImpl
    ): AuthRepository = if (BuildConfig.USE_REMOTE) networkImpl else localImpl

    @Provides @Singleton
    fun provideUserRepository(
        localImpl: LocalUserRepositoryImpl,
        networkImpl: UserRepositoryImpl
    ): UserRepository = if (BuildConfig.USE_REMOTE) networkImpl else localImpl

    @Provides @Singleton
    fun provideNestRepository(
        localImpl: LocalNestRepositoryImpl,
        networkImpl: NestRepositoryImpl
    ): NestRepository = if (BuildConfig.USE_REMOTE) networkImpl else localImpl

    @Provides @Singleton
    fun provideContributionRepository(
        localImpl: LocalContributionRepositoryImpl,
        networkImpl: ContributionRepositoryImpl
    ): ContributionRepository = if (BuildConfig.USE_REMOTE) networkImpl else localImpl

    @Provides @Singleton
    fun provideLoanRepository(
        localImpl: LocalLoanRepositoryImpl,
        networkImpl: LoanRepositoryImpl
    ): LoanRepository = if (BuildConfig.USE_REMOTE) networkImpl else localImpl
}
