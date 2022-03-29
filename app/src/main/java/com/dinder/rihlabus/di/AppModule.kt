package com.dinder.rihlabus.di

import com.dinder.rihlabus.data.repository.auth.AuthRepository
import com.dinder.rihlabus.data.repository.auth.FirebaseAuthRepository
import com.dinder.rihlabus.data.repository.trip.TripRepository
import com.dinder.rihlabus.data.repository.trip.TripRepositoryImpl
import com.dinder.rihlabus.data.repository.user.UserRepository
import com.dinder.rihlabus.data.repository.user.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@InstallIn(SingletonComponent::class)
@ExperimentalCoroutinesApi
object AppModule {
    @Provides
    fun getIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun getFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun getAuthRepository(
        ioDispatcher: CoroutineDispatcher,
        firebaseAuth: FirebaseAuth,
    ): AuthRepository = FirebaseAuthRepository(ioDispatcher, firebaseAuth)

    @Provides
    fun getTripRepository(
        ioDispatcher: CoroutineDispatcher
    ): TripRepository = TripRepositoryImpl(ioDispatcher)

    @Provides
    fun getUserRepository(
        ioDispatcher: CoroutineDispatcher
    ): UserRepository = UserRepositoryImpl(ioDispatcher)
}
