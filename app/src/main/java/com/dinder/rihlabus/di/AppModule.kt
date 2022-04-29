package com.dinder.rihlabus.di

import android.content.Context
import com.dinder.rihlabus.data.local.UserDao
import com.dinder.rihlabus.data.local.db.RihlaBusDatabase
import com.dinder.rihlabus.data.remote.repository.auth.AuthRepository
import com.dinder.rihlabus.data.remote.repository.auth.FirebaseAuthRepository
import com.dinder.rihlabus.data.remote.repository.company.CompanyRepository
import com.dinder.rihlabus.data.remote.repository.company.CompanyRepositoryImpl
import com.dinder.rihlabus.data.remote.repository.location.CompanyLocationRepository
import com.dinder.rihlabus.data.remote.repository.location.CompanyLocationRepositoryImpl
import com.dinder.rihlabus.data.remote.repository.trip.TripRepository
import com.dinder.rihlabus.data.remote.repository.trip.TripRepositoryImpl
import com.dinder.rihlabus.data.remote.repository.user.UserRepository
import com.dinder.rihlabus.data.remote.repository.user.UserRepositoryImpl
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
    fun getDatabase(@ApplicationContext context: Context): RihlaBusDatabase =
        RihlaBusDatabase.getInstance(context)

    @Provides
    fun getUserDao(database: RihlaBusDatabase): UserDao = database.userDao()

    @Provides
    fun getIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun getFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun getAuthRepository(
        ioDispatcher: CoroutineDispatcher,
        firebaseAuth: FirebaseAuth
    ): AuthRepository = FirebaseAuthRepository(ioDispatcher, firebaseAuth)

    @Provides
    fun getTripRepository(
        ioDispatcher: CoroutineDispatcher
    ): TripRepository = TripRepositoryImpl(ioDispatcher)

    @Provides
    fun getUserRepository(
        ioDispatcher: CoroutineDispatcher,
        dao: UserDao
    ): UserRepository = UserRepositoryImpl(ioDispatcher, dao)

    @Provides
    fun getCompanyRepository(
        ioDispatcher: CoroutineDispatcher
    ): CompanyRepository = CompanyRepositoryImpl(ioDispatcher)

    @Provides
    fun getCompanyLocationRepository(
        ioDispatcher: CoroutineDispatcher
    ): CompanyLocationRepository = CompanyLocationRepositoryImpl(ioDispatcher)
}
