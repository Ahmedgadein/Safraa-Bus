package com.dinder.rihlabus.domain

import android.content.res.Resources
import com.dinder.rihlabus.R
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.remote.version.AppVersionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateAppUseCase @Inject constructor(
    private val repository: AppVersionRepository,
    private val resources: Resources
) {
    operator fun invoke(): Flow<Result<Boolean>> = flow {
        repository.getAppVersion().collect { result ->
            when (result) {
                is Result.Loading -> emit(Result.Loading)
                is Result.Error -> emit(Result.Error(result.message))
                is Result.Success -> {
                    val currentVersion = resources.getString(R.string.app_version)
                    val shouldUpdateApp =
                        result.value.version != currentVersion && result.value.updateRequired
                    emit(Result.Success(shouldUpdateApp))
                }
            }
        }
    }
}
