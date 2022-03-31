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

class AddTripUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(trip: Trip): Flow<Result<Boolean>> = flow {
        userRepository.get(firebaseAuth.currentUser?.uid!!).collect {
            when (it) {
                Result.Loading -> emit(Result.Loading)
                is Result.Error -> emit(Result.Error(it.message))
                is Result.Success -> {
                    tripRepository.addTrip(
                        trip.copy(
                            from = it.value.location,
                            company = it.value.company
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
}
