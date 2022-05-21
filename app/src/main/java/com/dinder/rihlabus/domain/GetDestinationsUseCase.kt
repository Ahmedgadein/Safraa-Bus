package com.dinder.rihlabus.domain

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Destination
import com.dinder.rihlabus.data.remote.destination.DestinationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDestinationsUseCase @Inject constructor(private val repository: DestinationRepository) {
    suspend operator fun invoke(): Flow<Result<List<Destination>>> = flow {
        repository.getDestinations().collect { result ->
            when (result) {
                Result.Loading -> emit(Result.Loading)
                is Result.Error -> emit(Result.Error(result.message))
                is Result.Success -> emit(Result.Success(result.value))
            }
        }
    }
}
