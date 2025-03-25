package com.ohmz.remindersapp.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Custom WorkerFactory that enables Hilt dependency injection for Workers
 */
@Singleton
class HiltWorkerFactory @Inject constructor(
    private val reminderNotificationWorkerFactory: ReminderNotificationWorker.Factory
) : WorkerFactory() {
    
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            ReminderNotificationWorker::class.java.name -> 
                reminderNotificationWorkerFactory.create(appContext, workerParameters)
            else -> null
        }
    }
}