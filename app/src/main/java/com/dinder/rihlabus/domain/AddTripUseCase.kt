package com.dinder.rihlabus.domain

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.data.remote.trip.TripRepository
import com.dinder.rihlabus.data.remote.user.UserRepository
import com.mixpanel.android.mpmetrics.MixpanelAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import javax.inject.Inject

class AddTripUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val tripRepository: TripRepository
) {

    @Inject
    lateinit var mixpanel: MixpanelAPI

    suspend operator fun invoke(trip: Trip): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        userRepository.user.collect { user ->
            user?.let {
                val props = JSONObject().apply {
                    put("User", it.name)
                    put("From", user.location?.name)
                    put("To", trip.to?.name)
                    put("Company", it.company?.name)
                    put("Price", trip.price)
                    put("departure", trip.departure)
                    put("Seats count", trip.seats.size)
                }
                mixpanel.track("Add trip", props)

                tripRepository.addTrip(
                    trip
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
