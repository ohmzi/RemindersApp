package com.ohmz.remindersapp.receiver

import androidx.work.WorkManager
import com.ohmz.remindersapp.domain.repository.ReminderRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt entry point for the BootReceiver
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface BootReceiverEntryPoint {
    fun reminderRepository(): ReminderRepository
    fun workManager(): WorkManager
}