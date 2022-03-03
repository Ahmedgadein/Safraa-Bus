package com.dinder.rihlabus.data.repository.auth

import com.dinder.rihlabus.common.Response
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.User

class FirebaseAuthRepository : AuthRepository {
    override val isLoggedIn: Boolean
        get() = true

    override fun login(mobile: String): Response<User> {
        TODO("Not yet implemented")
    }

    override fun register(name:String, mobile: String, company: Company): Response<User> {
        TODO("Not yet implemented")
    }
}