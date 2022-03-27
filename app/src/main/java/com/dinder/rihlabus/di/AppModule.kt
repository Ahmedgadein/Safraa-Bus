package com.dinder.rihlabus.di

import com.dinder.rihlabus.data.repository.auth.AuthRepository
import com.dinder.rihlabus.data.repository.auth.FirebaseAuthRepository
import com.dinder.rihlabus.data.repository.trip.TripRepository
import com.dinder.rihlabus.data.repository.trip.TripRepositoryImpl
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
    fun getAuthRepository(
        ioDispatcher: CoroutineDispatcher
    ): AuthRepository = FirebaseAuthRepository(ioDispatcher)

    @Provides
    fun getTripRepository(
        ioDispatcher: CoroutineDispatcher
    ): TripRepository = TripRepositoryImpl(ioDispatcher)
}