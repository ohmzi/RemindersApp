package com.ohmz.remindersapp.di

import com.ohmz.remindersapp.domain.repository.ReminderRepository
import com.ohmz.remindersapp.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetRemindersUseCase(repository: ReminderRepository): GetRemindersUseCase {
        return GetRemindersUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddReminderUseCase(repository: ReminderRepository): AddReminderUseCase {
        return AddReminderUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateReminderUseCase(repository: ReminderRepository): UpdateReminderUseCase {
        return UpdateReminderUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteReminderUseCase(repository: ReminderRepository): DeleteReminderUseCase {
        return DeleteReminderUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideToggleReminderCompletionUseCase(repository: ReminderRepository): ToggleReminderCompletionUseCase {
        return ToggleReminderCompletionUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetRemindersByTypeUseCase(repository: ReminderRepository): GetRemindersByTypeUseCase {
        return GetRemindersByTypeUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideClearCompletedRemindersUseCase(repository: ReminderRepository): ClearCompletedRemindersUseCase {
        return ClearCompletedRemindersUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideToggleReminderFavoriteUseCase(repository: ReminderRepository): ToggleReminderFavoriteUseCase {
        return ToggleReminderFavoriteUseCase(repository)
    }
}