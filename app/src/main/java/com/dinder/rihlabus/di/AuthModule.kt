package com.dinder.rihlabus.di

import com.dinder.rihlabus.data.repository.auth.AuthRepository
import com.dinder.rihlabus.data.repository.auth.FirebaseAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@InstallIn(SingletonComponent::class)
@ExperimentalCoroutinesApi
object AuthModule {
    @Provides
    fun getAuthRepository(
        ioDispatcher: CoroutineDispatcher
    ): AuthRepository = FirebaseAuthRepository(ioDispatcher)
}