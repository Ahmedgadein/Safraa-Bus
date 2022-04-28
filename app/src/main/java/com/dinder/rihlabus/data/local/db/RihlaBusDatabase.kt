package com.dinder.rihlabus.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dinder.rihlabus.common.Constants.DATABASE_NAME
import com.dinder.rihlabus.data.local.UserDao
import com.dinder.rihlabus.data.model.User

@Database(entities = [User::class], version = 1)
abstract class RihlaBusDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: RihlaBusDatabase? = null

        fun getInstance(context: Context): RihlaBusDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): RihlaBusDatabase {
            return Room.databaseBuilder(context, RihlaBusDatabase::class.java, DATABASE_NAME)
                .build()
        }
    }
}
