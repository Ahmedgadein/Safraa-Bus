package com.dinder.rihlabus.domain

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.remote.company.CompanyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCompaniesUseCase @Inject constructor(private val repository: CompanyRepository) {
    suspend operator fun invoke(): Flow<Result<List<Company>>> = flow {
        repository.getCompanies().collect { result ->
            when (result) {
                Result.Loading -> emit(Result.Loading)
                is Result.Error -> emit(Result.Error(result.message))
                is Result.Success -> emit(Result.Success(result.value))
            }
        }
    }
}
