package com.dinder.rihlabus.data.repository.auth

import com.dinder.rihlabus.common.Response
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.User

interface AuthRepository {
    val isLoggedIn: Boolean
    fun login(mobile: String): Response<User>
    fun register(name: String, mobile: String, company: Company): Response<User>
}