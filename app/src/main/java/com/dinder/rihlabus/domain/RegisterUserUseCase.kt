package com.dinder.rihlabus.domain

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.User
import com.dinder.rihlabus.data.repository.auth.AuthRepository
import com.dinder.rihlabus.data.repository.company.CompanyRepository
import com.dinder.rihlabus.data.repository.location.CompanyLocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val companyRepository: CompanyRepository,
    private val locationRepository: CompanyLocationRepository
) {
    suspend operator fun invoke(
        user: User,
        company: String,
        location: String
    ): Flow<Result<Boolean>> = flow {
        companyRepository.upsert(company).collect { companyResult ->
            when (companyResult) {
                Result.Loading -> emit(Result.Loading)
                is Result.Error -> emit(Result.Error(companyResult.message))
                is Result.Success -> locationRepository.upsert(location, company)
                    .collect { locationResult ->
                        when (locationResult) {
                            Result.Loading -> Unit
                            is Result.Error -> emit(Result.Error(locationResult.message))
                            is Result.Success -> authRepository.register(
                                user.copy(company = company, location = location)
                            ).collect {
                                when (it) {
                                    Result.Loading -> Unit
                                    is Result.Error -> emit(Result.Error(it.message))
                                    is Result.Success -> emit(Result.Success(true))
                                }
                            }
                        }
                    }
            }
        }
    }
}
