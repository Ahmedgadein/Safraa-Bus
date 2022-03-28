package com.dinder.rihlabus.data.repository.user

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun get(id: String): Flow<Result<User>>
}