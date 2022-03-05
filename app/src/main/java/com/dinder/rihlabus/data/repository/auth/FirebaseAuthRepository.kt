package com.dinder.rihlabus.data.repository.auth

import com.dinder.rihlabus.common.Response
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(): AuthRepository {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    override fun login(mobile: String): Response<User> {
        TODO("Not yet implemented")
    }

    override fun register(name:String, mobile: String, company: Company): Response<User> {
        TODO("Not yet implemented")
    }
}