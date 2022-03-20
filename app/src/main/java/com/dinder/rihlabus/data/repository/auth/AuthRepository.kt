package com.dinder.rihlabus.data.repository.auth

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.User
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val state: StateFlow<AuthRepoState>
    suspend fun login(credential: AuthCredential, phoneNumber: String)
    suspend fun register(
        user: User
    ): Flow<Result<Boolean>>
}