package com.dinder.rihlabus.data.repository.trip

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.utils.SeatState
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    suspend fun addTrip(trip: Trip): Flow<Result<Boolean>>
    suspend fun getCurrentTrips(company: String, location: String): Flow<Result<List<Trip>>>
    suspend fun getTrip(id: Long): Flow<Result<Trip>>
    suspend fun updateSeatState(tripId: Long, seatNumber: Int, state: SeatState): Flow<Result<Boolean>>
}
