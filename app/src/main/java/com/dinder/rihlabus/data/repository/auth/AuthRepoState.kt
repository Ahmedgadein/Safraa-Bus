package com.dinder.rihlabus.data.repository.auth

data class AuthRepoState(
    val isRegistered: Boolean = false,
    val isLoggedIn: Boolean = false,
    val loading: Boolean = false
)
