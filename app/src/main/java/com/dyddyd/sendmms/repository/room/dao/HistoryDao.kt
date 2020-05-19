package com.dyddyd.sendmms.repository.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dyddyd.sendmms.repository.data.History

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertHistory(item: History)

    @Delete
    fun deleteHistory(item: History)

    @Query("DELETE FROM mms_history")
    fun deleteAllHistory()

    @Query("SELECT * FROM mms_history ORDER BY dateString DESC")
    fun getAllHistory(): LiveData<List<History>>

    @Query("SELECT * FROM mms_history WHERE :id=id")
    fun getHistory(id: Int): History

    @Query("SELECT COUNT(*) FROM mms_history")
    fun getCount(): Int
}