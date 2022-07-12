package com.dinder.rihlabus.data.remote.version

import com.dinder.rihlabus.common.Result
import kotlinx.coroutines.flow.Flow

interface AppVersionRepository {
    fun getAppVersion(): Flow<Result<String>>
}
