package com.dyddyd.sendmms.repository.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.dyddyd.sendmms.repository.HistoryRepository
import kotlinx.coroutines.*

class HistoryViewModel(application: Application): AndroidViewModel(application) {
    private val repository = HistoryRepository(application)
    private val allHistories: LiveData<List<History>> = repository.getAll()

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun isEmpty(): Boolean {
        if(allHistories.value == null || allHistories.value!!.isEmpty()) {
            return true
        }
        return false
    }

    fun getAll(): LiveData<List<History>> {
        return allHistories
    }

    fun insert(item: History) = viewModelScope.launch {
        repository.insert(item)
    }

    fun delete(item: History) = viewModelScope.launch {
        repository.delete(item)
    }

    fun getCount() = runBlocking(Dispatchers.IO) {
        repository.getCount()
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun getRepoInfo(id: Int): History = runBlocking(Dispatchers.IO) {
        repository.getHistory(id)
    }
}