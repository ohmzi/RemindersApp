package com.ohmz.remindersapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ohmz.remindersapp.data.local.entity.ReminderListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderListDao {
    @Query("SELECT * FROM reminder_lists ORDER BY name ASC")
    fun getAllLists(): Flow<List<ReminderListEntity>>

    @Query("SELECT * FROM reminder_lists WHERE id = :id")
    suspend fun getListById(id: Int): ReminderListEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ReminderListEntity): Long

    @Update
    suspend fun updateList(list: ReminderListEntity)

    @Delete
    suspend fun deleteList(list: ReminderListEntity)
    
    @Query("SELECT * FROM reminder_lists WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultList(): ReminderListEntity?
}