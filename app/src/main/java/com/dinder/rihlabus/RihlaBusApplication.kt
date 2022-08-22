package com.dinder.rihlabus

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.dinder.rihlabus.services.RefreshTokenWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class RihlaBusApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        updateToken()
    }

    private fun updateToken() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicRefreshTokenWork = PeriodicWorkRequest.Builder(
            RefreshTokenWorker::class.java,
            14,
            TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        val uniqueWorkName = "refresh-token-work"

        WorkManager
            .getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRefreshTokenWork
            )
    }
}
