package com.dinder.rihlabus.data.remote.destination

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Destination
import kotlinx.coroutines.flow.Flow

interface DestinationRepository {
    fun getDestinations(): Flow<Result<List<Destination>>>
}
