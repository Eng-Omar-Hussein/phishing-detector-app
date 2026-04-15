package com.emad.phishingdetector.di

import android.app.Application
import androidx.room.Room
import com.emad.data.local.AppDatabase
import com.emad.data.local.SessionManager
import com.emad.data.local.dao.EmailDao
import com.emad.data.local.dao.UserDao
import com.emad.data.remote.AuthInterceptor
import com.emad.data.remote.PythonApiService
import com.emad.data.remote.RetrofitClient
import com.emad.data.repository.AuthRepositoryImpl
import com.emad.data.repository.EmailRepositoryImpl
import com.emad.domain.repository.AuthRepository
import com.emad.domain.repository.EmailRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ------------------------------------
    // 1. Database Dependencies
    // ------------------------------------
    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "secure_email_db"
        ).fallbackToDestructiveMigration() // Wipes DB if you change the schema (Good for dev)
            .build()
    }

    @Provides
    @Singleton
    fun provideEmailDao(db: AppDatabase): EmailDao = db.emailDao()

    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    // ------------------------------------
    // 2. Network Dependencies
    // ------------------------------------

    // AuthInterceptor is automatically provided because it has @Inject in its class!

    @Provides
    @Singleton
    fun providePythonApiService(authInterceptor: AuthInterceptor): PythonApiService {
        // Uses the helper object we created earlier
        return RetrofitClient.create(authInterceptor)
    }

    // ------------------------------------
    // 3. Repository Bindings
    // (Connecting Domain Interface to Data Implementation)
    // ------------------------------------

    @Provides
    @Singleton
    fun provideEmailRepository(
        api: PythonApiService,
        dao: EmailDao
    ): EmailRepository {
        return EmailRepositoryImpl(api, dao)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: PythonApiService,
        sessionManager: SessionManager,
        userDao: UserDao
    ): AuthRepository {
        return AuthRepositoryImpl(api, sessionManager, userDao)
    }
}