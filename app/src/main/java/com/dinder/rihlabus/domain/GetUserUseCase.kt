package com.dinder.rihlabus.domain

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.User
import com.dinder.rihlabus.data.remote.repository.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Result<User>> = flow {
        emit(Result.Loading)
        userRepository.get(firebaseAuth.uid!!).collect { result ->
            when (result) {
                Result.Loading -> emit(Result.Loading)
                is Result.Error -> emit(Result.Error(message = result.message))
                is Result.Success -> emit(Result.Success(result.value))
            }
        }
    }
}
