package com.dinder.rihlabus.di

import com.dinder.rihlabus.MainActivity
import com.dinder.rihlabus.data.repository.auth.AuthRepository
import com.dinder.rihlabus.data.repository.auth.FirebaseAuthRepository
import com.dinder.rihlabus.ui.login.LoginFragment
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindFirebaseAuthRepository(
        authRepository: FirebaseAuthRepository
    ): AuthRepository
}