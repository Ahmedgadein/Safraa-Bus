package com.dinder.rihlabus.domain

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.data.remote.trip.TripRepository
import com.dinder.rihlabus.data.remote.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class AddTripUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(trip: Trip): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        userRepository.user.collect { user ->
            user?.let {
                tripRepository.addTrip(
                    trip.copy(
                        id = UUID.randomUUID().mostSignificantBits,
                        from = user.location!!,
                        company = user.company!!
                    )
                )
                    .collect { result ->
                        when (result) {
                            Result.Loading -> Unit
                            is Result.Error -> emit(Result.Error(result.message))
                            is Result.Success -> emit(Result.Success(true))
                        }
                    }
            }
        }
    }
}
