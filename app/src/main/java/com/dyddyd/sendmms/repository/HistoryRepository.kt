package com.dyddyd.sendmms.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dyddyd.sendmms.repository.data.History
import com.dyddyd.sendmms.repository.room.database.MyDatabase
import kotlinx.coroutines.*

class HistoryRepository(application: Application) {

    private val myDatabase = MyDatabase.getInstance(application)!!
    private val historyDao = myDatabase.historyDao()
    private val allHistories: LiveData<List<History>> = historyDao.getAllHistory()

    fun getAll(): LiveData<List<History>> = allHistories

    fun getHistory(id: Int) = historyDao.getHistory(id)

    fun getCount() = historyDao.getCount()

    suspend fun insert(item: History) {
        withContext(Dispatchers.IO) {
            historyDao.insertHistory(item)
        }
    }

    suspend fun delete(item: History) {
        withContext(Dispatchers.IO) {
            historyDao.deleteHistory(item)
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            historyDao.deleteAllHistory()
        }
    }

}