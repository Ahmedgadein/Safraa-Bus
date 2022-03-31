package com.dinder.rihlabus.domain

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.data.repository.trip.TripRepository
import com.dinder.rihlabus.data.repository.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class CurrentTripsUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<Result<List<Trip>>> = flow {
        userRepository.get(firebaseAuth.currentUser?.uid!!).collect { user ->
            when (user) {
                Result.Loading -> emit(Result.Loading)
                is Result.Error -> emit(Result.Error(user.message))
                is Result.Success -> {
                    tripRepository.getCurrentTrips(
                        user.value.company.name,
                        user.value.company.location
                    ).collect {
                        when (it) {
                            Result.Loading -> Unit
                            is Result.Error -> emit(Result.Error(it.message))
                            is Result.Success -> {
                                emit(Result.Success(it.value))
                            }
                        }
                    }
                }
            }
        }
    }
}
