package com.dinder.rihlabus.data.remote.auth

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.User
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun isLoggedIn(): Flow<Result<Boolean>>
    suspend fun isRegistered(phoneNumber: String): Flow<Result<Boolean>>
    suspend fun login(credential: AuthCredential, phoneNumber: String): Flow<Result<Boolean>>
    suspend fun register(
        user: User
    ): Flow<Result<Boolean>>

    suspend fun updateToken(id: String, token: String): Boolean
}
