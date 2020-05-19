package com.dyddyd.sendmms.repository.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dyddyd.sendmms.repository.data.History
import com.dyddyd.sendmms.repository.room.dao.HistoryDao

@Database(entities = [History::class], version = 1)
abstract class MyDatabase: RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    companion object {
        private var INSTANCE: MyDatabase? = null

        fun getInstance(context: Context): MyDatabase? {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, MyDatabase::class.java, "myDatabase.db")
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return INSTANCE
        }
    }
}