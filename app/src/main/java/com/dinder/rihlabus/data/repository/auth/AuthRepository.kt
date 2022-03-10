package com.dinder.rihlabus.data.repository.auth

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.User
import com.google.firebase.auth.AuthCredential

interface AuthRepository {
    val isLoggedIn: Boolean
    val isRegistered: Boolean
    suspend fun login(credential: AuthCredential): Result<Boolean, AuthError>
    suspend fun register(credential: AuthCredential, name: String, company: Company): Result<Boolean, AuthError>
}