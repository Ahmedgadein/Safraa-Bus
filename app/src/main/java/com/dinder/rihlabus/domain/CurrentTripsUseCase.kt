package com.dinder.rihlabus.domain

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.data.remote.repository.trip.TripRepository
import com.dinder.rihlabus.data.remote.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CurrentTripsUseCase @Inject constructor(
    private val tripRepository: TripRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<Result<List<Trip>>> = flow {
        emit(Result.Loading)
        userRepository.user.collect { user ->
            user?.let {
                tripRepository.getCurrentTrips(
                    user.company,
                    user.location
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
