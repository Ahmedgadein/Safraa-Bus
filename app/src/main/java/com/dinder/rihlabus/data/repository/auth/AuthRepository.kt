package com.dinder.rihlabus.data.repository.auth

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.User
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val state: StateFlow<AuthRepoState>
    suspend fun login(credential: AuthCredential)
    suspend fun register(credential: AuthCredential, name: String, company: Company)
}