package com.dinder.rihlabus.di

import android.content.Context
import com.dinder.rihlabus.data.local.UserDao
import com.dinder.rihlabus.data.local.db.RihlaBusDatabase
import com.dinder.rihlabus.data.remote.auth.AuthRepository
import com.dinder.rihlabus.data.remote.auth.FirebaseAuthRepository
import com.dinder.rihlabus.data.remote.company.CompanyRepository
import com.dinder.rihlabus.data.remote.company.CompanyRepositoryImpl
import com.dinder.rihlabus.data.remote.destination.DestinationRepository
import com.dinder.rihlabus.data.remote.destination.DestinationRepositoryImpl
import com.dinder.rihlabus.data.remote.location.CompanyLocationRepository
import com.dinder.rihlabus.data.remote.location.CompanyLocationRepositoryImpl
import com.dinder.rihlabus.data.remote.trip.TripRepository
import com.dinder.rihlabus.data.remote.trip.TripRepositoryImpl
import com.dinder.rihlabus.data.remote.user.UserRepository
import com.dinder.rihlabus.data.remote.user.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@ExperimentalCoroutinesApi
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): RihlaBusDatabase =
        RihlaBusDatabase.getInstance(context)

    @Provides
    fun provideUserDao(database: RihlaBusDatabase): UserDao = database.userDao()

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthRepository(
        ioDispatcher: CoroutineDispatcher,
        firebaseAuth: FirebaseAuth
    ): AuthRepository = FirebaseAuthRepository(ioDispatcher, firebaseAuth)

    @Provides
    fun provideTripRepository(
        ioDispatcher: CoroutineDispatcher
    ): TripRepository = TripRepositoryImpl(ioDispatcher)

    @Provides
    fun provideUserRepository(
        ioDispatcher: CoroutineDispatcher,
        dao: UserDao
    ): UserRepository = UserRepositoryImpl(ioDispatcher, dao)

    @Provides
    fun provideCompanyRepository(
        ioDispatcher: CoroutineDispatcher
    ): CompanyRepository = CompanyRepositoryImpl(ioDispatcher)

    @Provides
    fun provideCompanyLocationRepository(
        ioDispatcher: CoroutineDispatcher
    ): CompanyLocationRepository = CompanyLocationRepositoryImpl(ioDispatcher)

    @Provides
    fun provideDestinationRepository(ioDispatcher: CoroutineDispatcher): DestinationRepository =
        DestinationRepositoryImpl(ioDispatcher)
}
