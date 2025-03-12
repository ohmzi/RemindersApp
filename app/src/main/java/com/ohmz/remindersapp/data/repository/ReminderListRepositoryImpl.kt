package com.ohmz.remindersapp.data.repository

import com.ohmz.remindersapp.data.local.dao.ReminderListDao
import com.ohmz.remindersapp.data.mapper.toDomainModel
import com.ohmz.remindersapp.data.mapper.toDomainModels
import com.ohmz.remindersapp.data.mapper.toEntity
import com.ohmz.remindersapp.domain.model.ReminderList
import com.ohmz.remindersapp.domain.repository.ReminderListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReminderListRepositoryImpl @Inject constructor(
    private val reminderListDao: ReminderListDao
) : ReminderListRepository {

    override fun getAllLists(): Flow<List<ReminderList>> {
        return reminderListDao.getAllLists().map { it.toDomainModels() }
    }

    override suspend fun getListById(id: Int): ReminderList? {
        return reminderListDao.getListById(id)?.toDomainModel()
    }

    override suspend fun addList(list: ReminderList): Long {
        return reminderListDao.insertList(list.toEntity())
    }

    override suspend fun updateList(list: ReminderList) {
        reminderListDao.updateList(list.toEntity())
    }

    override suspend fun deleteList(list: ReminderList) {
        reminderListDao.deleteList(list.toEntity())
    }

    override suspend fun getOrCreateDefaultList(): ReminderList {
        // Look for an existing default list
        val defaultList = reminderListDao.getDefaultList()
        
        if (defaultList != null) {
            return defaultList.toDomainModel()
        }
        
        // Create a default "Reminders" list if none exists
        val newDefaultList = ReminderList(
            name = "Reminders",
            isDefault = true
        )
        
        val newId = reminderListDao.insertList(newDefaultList.toEntity())
        return newDefaultList.copy(id = newId.toInt())
    }
}