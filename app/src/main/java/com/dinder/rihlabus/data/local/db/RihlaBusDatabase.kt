package com.dinder.rihlabus.data.local.db

import android.content.Context
import androidx.room.*
import com.dinder.rihlabus.common.Constants.DATABASE_NAME
import com.dinder.rihlabus.data.local.UserDao
import com.dinder.rihlabus.data.model.User
import com.dinder.rihlabus.utils.CompanyConverter
import com.dinder.rihlabus.utils.DestinationConverter

@Database(
    entities = [User::class],
    version = 3,
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(from = 2, to = 3)]
)
@TypeConverters(CompanyConverter::class, DestinationConverter::class)
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
