package com.dinder.rihlabus.data.remote.repository.user

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun get(id: String): Flow<Result<User>>
    fun add(user: User)
    val user: Flow<User?>
}
